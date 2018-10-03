import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.toIntExact;

class RestriccionProductividadProductores{
    boolean cumpleRestrccion;
    float mediaDesviacion;
    float maximoDesviacion;
}
class RestriccionUsosDistintos{
    boolean cumpleRestriccion;
    int cantIncumplimientos; //Cantidad de veces que una estacion un productor esta por debajo del minimo o encima del maximo
    float incumplimientoRelativo; //Cantidad de incumplimientos dividido entre todas posibles parejas productor,estacion
    int [][][] cantUsosPorEstacionParaCadaProductor;
}
class RestriccionFosforoAnual{
    boolean cumpleRestriccion;
    float mediaDesviacion;
    float maximoDesviacion;
    float [] fosforoAnual;
    float [][] fosforoAnualPorProductor;
}

public class Solucion {
    float fosforo;
    int[][] matriz; //Uso*100+numero de estacion de ese uso
    float [][] productivdadProductores;
    float areaTotal;

    //Resticcion 1 productividad minima por a;o para cada productor
    RestriccionProductividadProductores restriccionProductividadMinimaAnual;
    //Resticcion 2 productividad minima por estacion para cada productor
    RestriccionProductividadProductores restriccionProductividadMinimaEstacion;
    //Restriccion 3 usos distintos, cuantos productores no la cumplen.
    RestriccionUsosDistintos restriccionUsosDistintos;

    //Version2
    RestriccionFosforoAnual restriccionFosforoAnual;



    /*DEPRECATED
    //Restricciones de fosforo anual
    boolean cumpleRestriccionFAnual;
    int  indiceDesviacionMaximaFosforoAnual; //Indicie en que se encuentra la Maxima superacion del tope de fosforo
    float  desviacionMediaFosforo; //Superacion promedia del tope de fosforo
    float [] fosforoAnual;
    float [][] fosforoAnualPorProductor;


    //Restricciones Productor
    boolean cumpleRestriccionProductores;
    int[]   indiceDesviacionesMaximaProductores; //Indicie en que se encuentra la Maxima superacion del tope de fosforo
    float[]  desviacionesMediaProductores; //Superacion promedia del tope de fosforo

    //Restriccion cantUsos
    int [][] cantUsosProductor;
    int [] cantUsosDistin
    tosProductor;
    */

    //float produccionAcumulada;
    public void imprimirFuncionObjetivo(){
        System.out.println("\tFosforo modulado: "+Constantes.pesoIncumplimientoFosforo * (this.fosforo /(Constantes.maximoIncumplimientoFosforo*this.areaTotal)));
        System.out.println("\tUsos Distintos modulado: "+ Constantes.pesoIncumplimientoUsosDistintos * (this.restriccionUsosDistintos.cantIncumplimientos/ Constantes.maximoIncumplimientoUsosDistintos));
        System.out.println("\tProductividad Anual modulado: "+(-1)*Constantes.pesoIncumplimientoProductividadMinimaAnual * (this.restriccionProductividadMinimaEstacion.maximoDesviacion  /Constantes.maximoIncumplimientoProductividadMinimaAnual));
        System.out.println("\tProductividad Estacion modulado: "+(-1)*Constantes.pesoIncumplimientoProductividadMinimaEstacion * (this.restriccionProductividadMinimaEstacion.maximoDesviacion /Constantes.maximoIncumplimientoProductividadMinimaEstacion));
    }

    public float evaluarFuncionObjetivo(){
        float valor=0;
        valor= Constantes.pesoIncumplimientoFosforo * (this.fosforo /(Constantes.maximoIncumplimientoFosforo*this.areaTotal));
        valor +=  Constantes.pesoIncumplimientoUsosDistintos * (this.restriccionUsosDistintos.cantIncumplimientos/ Constantes.maximoIncumplimientoUsosDistintos);
        valor += -Constantes.pesoIncumplimientoProductividadMinimaAnual * (this.restriccionProductividadMinimaEstacion.maximoDesviacion  /Constantes.maximoIncumplimientoProductividadMinimaAnual);
        valor += -Constantes.pesoIncumplimientoProductividadMinimaEstacion * (this.restriccionProductividadMinimaEstacion.maximoDesviacion /Constantes.maximoIncumplimientoProductividadMinimaEstacion);


        return valor;
    }

    public Solucion (){
        this.fosforo=0;
        //Por la especificacion del lenguaje los array tienen valores 0  al inicializarce
        this.matriz = new int[Constantes.cantPixeles][Constantes.cantEstaciones]; //Uso*100+numero de estacion
        this.productivdadProductores = new float [Constantes.cantProductores][Constantes.cantEstaciones];
        //Restriccion 1
        this.restriccionProductividadMinimaAnual = new RestriccionProductividadProductores();
        this.restriccionProductividadMinimaAnual.cumpleRestrccion=false;
        this.restriccionProductividadMinimaAnual.maximoDesviacion =0;
        this.restriccionProductividadMinimaAnual.mediaDesviacion =0;
        //Restriccion 2
        this.restriccionProductividadMinimaEstacion = new RestriccionProductividadProductores();
        this.restriccionProductividadMinimaEstacion.cumpleRestrccion=false;
        this.restriccionProductividadMinimaEstacion.maximoDesviacion =0;
        this.restriccionProductividadMinimaEstacion.mediaDesviacion =0;
        //Restriccion 3
        this.restriccionUsosDistintos = new RestriccionUsosDistintos();
        this.restriccionUsosDistintos.cumpleRestriccion=false;
        this.restriccionUsosDistintos.cantIncumplimientos =0;
        this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor =
                new int [Constantes.cantUsos][Constantes.cantEstaciones][Constantes.cantProductores];
        
        //Version2

        this.restriccionFosforoAnual= new RestriccionFosforoAnual();
        this.restriccionFosforoAnual.fosforoAnual = new float [Constantes.cantAnios];
        this.restriccionFosforoAnual.fosforoAnualPorProductor= new float[Constantes.cantAnios][Constantes.cantProductores];
        this.restriccionFosforoAnual.cumpleRestriccion=true;
        this.restriccionFosforoAnual.maximoDesviacion=0;
        this.restriccionFosforoAnual.mediaDesviacion=0;

        this.areaTotal=0;
        for (int iProductores = 0; iProductores < Constantes.cantProductores; iProductores++) {
            this.areaTotal+=Constantes.productores[iProductores].areaTotal;
        }


        /*DEPRECATED
        this.cumpleRestriccionProductores=true;
        this.indiceDesviacionesMaximaProductores= new int[Constantes.cantProductores];
        this.desviacionesMediaProductores= new float[Constantes.cantProductores];

        this.cantUsosProductor=new int[Constantes.cantProductores][Constantes.cantUsos];
        this.cantUsosDistintosProductor=new int[Constantes.cantProductores];


         */
    }


    public void imprimirSolucion(){
        System.out.println("\tSOLUCION:");
        System.out.println("\t\tFosforo Total: "+this.fosforo);
        this.imprimirMatriz();
        this.imprimirRestriccionProductividadMinimaEstacion();
        this.imprimirRestriccionProductividadMinimaAnual();
        this.imprimirRestriccionUsosDistintos();
        this.imprimirRestriccionFosforoAnual();
        /*
        this.imprimirProductividadSobreSuperficiePorEstacion();
        this.imprimirFosforoAnual();

        this.imprimirRestriccionFosforoAnual();
        this.imprimirRestriccionUsosPorProductor();

        --Deprecated--
        System.out.println("\t\tRestriccion productores: "+this.cumpleRestriccionProductores);
        this.imprimirRestriccionProductores();
        */
    }

    public void imprimirPixel(int pixel){
    System.out.printf("\t\t\tPixel "+pixel+": {");
    for (int estacion =0; estacion < this.matriz[pixel].length; estacion++){
        System.out.print(this.matriz[pixel][estacion]);
        if (estacion!=(this.matriz[pixel].length-1)){
            System.out.printf(", ");
        }
    }
    System.out.println("}");
    }

    public void imprimirMatriz(){
        System.out.println("\t\tMatriz:");
        for (int pixel =0; pixel< this.matriz.length;pixel++){
            System.out.printf("\t\t\tPixel "+pixel+": {");
            for (int estacion =0; estacion < this.matriz[pixel].length; estacion++){
                System.out.print(this.matriz[pixel][estacion]);
                if (estacion!=(this.matriz[pixel].length-1)){
                    System.out.printf(", ");
                }
            }
            System.out.println("}");
        }
    }


    public void imprimirUsosPorEstacion(){
        System.out.println("\t\tMatriz:");
        for (int pixel =0; pixel< this.matriz.length;pixel++){
            System.out.printf("\t\t\tPixel "+pixel+": {");
            for (int estacion =0; estacion < this.matriz[pixel].length; estacion++){
                System.out.print(this.matriz[pixel][estacion]/100);
                if (estacion!=(this.matriz[pixel].length-1)){
                    System.out.printf(", ");
                }
            }
            System.out.println("}");
        }
    }

    public void imprimirRestriccionProductividadMinimaAnual(){
        System.out.println("\t\tRestriccion Productividad Minima Anual: "+ this.restriccionProductividadMinimaAnual.cumpleRestrccion);
        System.out.println("\t\t\tMaxima Desviacion: "+ this.restriccionProductividadMinimaAnual.maximoDesviacion);
        System.out.println("\t\t\tMedia Desviacion: "+ this.restriccionProductividadMinimaAnual.mediaDesviacion);
    }

    public void imprimirRestriccionProductividadMinimaEstacion(){
        System.out.println("\t\tRestriccion Productividad Minima Estacion: "+ this.restriccionProductividadMinimaEstacion.cumpleRestrccion);
        System.out.println("\t\t\tMaxima Desviacion: "+ this.restriccionProductividadMinimaEstacion.maximoDesviacion);
        System.out.println("\t\t\tMedia Desviacion: "+ this.restriccionProductividadMinimaEstacion.mediaDesviacion);
    }

    public void imprimirRestriccionUsosDistintos(){
        int cantIncumplimientos,cantUsos;
        System.out.println("\t\tRestriccion Usos Distintos: "+ this.restriccionUsosDistintos.cumpleRestriccion);
        System.out.println("\t\t\tCant Incumplimientos: "+ this.restriccionUsosDistintos.cantIncumplimientos);
        System.out.println("\t\t\tIncumplimiento relativo: "+ this.restriccionUsosDistintos.incumplimientoRelativo);


    }
    public void imprimirRestriccionUsosDistintosExpandida(){

        System.out.println("\t\tRestriccion Usos Distintos: "+ this.restriccionUsosDistintos.cumpleRestriccion);
        System.out.println("\t\t\tCant Incumplimientos: "+ this.restriccionUsosDistintos.cantIncumplimientos);
        System.out.println("\t\t\tIncumplimiento relativo: "+ this.restriccionUsosDistintos.incumplimientoRelativo);
        imprimirUsosDisitintosPorEstacion();

    }

    public void imprimirUsosDisitintosPorEstacion(){
        int cantIncumplimientos,cantUsos;

        System.out.println("\t\tUsos distintos por estacion:") ;
        for (int iProductor = 0; iProductor < Constantes.cantProductores ; iProductor++) {
            System.out.print("\t\t\tProductor "+iProductor+": { ") ;
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                cantIncumplimientos=0;
                cantUsos=0;
                for (int iUso = 0; iUso < Constantes.cantUsos; iUso++) {
                    if(this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[iUso][iEstacion][iProductor]>0){
                        cantUsos++;
                    }
                }
                if (iEstacion!=Constantes.cantEstaciones-1){
                    System.out.print(cantUsos+", ");
                }else{
                    System.out.println("}") ;
                }
            }

        }
    }



    public void imprimirProductividadSobreSuperficiePorEstacion(){
        //Imprime la productividad (dividida por su area total) de cada productor en cada estacion
        System.out.println("\t\tProductividad Productores:");
        for (int productor =0; productor< this.productivdadProductores.length;productor++){
            System.out.printf("\t\t\tProductor "+productor+": {");
            for (int estacion =0; estacion < this.productivdadProductores[productor].length; estacion++){
                System.out.print(this.productivdadProductores[productor][estacion]/Constantes.productores[productor].areaTotal);
                if (estacion!=(this.productivdadProductores[productor].length-1)){
                    System.out.printf(", ");
                }
            }
            System.out.println("}");
        }
    }


    //VERSION 2
    public void imprimirFosforoAnual(){
        System.out.print("\t\tFosforo Anual:\n\t\t\t{");
        for (int anio =0; anio < this.restriccionFosforoAnual.fosforoAnual.length;anio++){
            System.out.print(this.restriccionFosforoAnual.fosforoAnual[anio]);
            if (anio!=(this.restriccionFosforoAnual.fosforoAnual.length-1)){
                System.out.printf(", ");
            }
        }
        System.out.println("}");
    }

    public void imprimirRestriccionFosforoAnual(){
        System.out.println("\t\tRestriccion Fosforo Anual: "+this.restriccionFosforoAnual.cumpleRestriccion);
        System.out.println("\t\t\tDesviacion Maxima: "+this.restriccionFosforoAnual.maximoDesviacion);
        System.out.println("\t\t\tDesviacion Media: "+this.restriccionFosforoAnual.mediaDesviacion);
        //DEPRECATED
        // System.out.println("\t\t\tAlcanzada el año: "+ this.indiceDesviacionMaximaFosforoAnual);

    }

    public static Solucion crearSolucion(){
        Solucion solucion = new Solucion();
        int usoACargar, iEstacion=0, estacionActual;
        //Cargar cada uno de los pixeles
        for (int iPixel=0; iPixel<Constantes.cantPixeles; iPixel++){
            solucion.cargarPixel(iPixel);
        }
        solucion.chequearRestricciones();
     return solucion;
    }

    public static Solucion firstImprove(Solucion solucion){
        float funcionObjetivo=solucion.evaluarFuncionObjetivo();
        int pixelRandom, fallos=0;
        int [] pixelACambiar= new int[Constantes.cantEstaciones];
        for (int intentos = 0; intentos < Constantes.cantPixeles ; intentos++) {
            //Sorteo cual cambiar
            pixelRandom= Constantes.uniforme.nextInt(Constantes.cantPixeles-1);
            //System.out.println("\tSorteo el pixel"+pixelRandom);
            //System.out.println("\t\tFO actual"+funcionObjetivo);
            //Respaldo el pixel viejo
            pixelACambiar=solucion.matriz[pixelRandom].clone();
            //Respaldo valores de las restricciones y costos

            //Cambio el pixel sorteado
            solucion.cambiarPixel(pixelRandom);
            //Calculo las restricciones
            solucion.chequearRestricciones();
            //System.out.println("\t\tFO nueva"+solucion.evaluarFuncionObjetivo());
            //En caso de que me sirva lo devuelvo
            if (solucion.evaluarFuncionObjetivo()< funcionObjetivo){
                System.out.println("\tExito, con cantidad de fallos: "+fallos);
                return solucion;
            }else{
                fallos++;
                //Restauro el pixel cambiado
                solucion.matriz[pixelRandom]=pixelACambiar;
                //Restauro los valores respaldados
            }
        }
        System.out.println("\tFracaso con cantidad de fallos: "+fallos);
        return solucion;
    }


    public void chequearRestricciones(){
        this.cumpleRestriccionesProductividad();
        this.cumpleRestriccionFosforoAnual();
        this.cumpleRestriccionUsosDistintos();
    }

    public  void cargarPixel(int iPixel){
        //Carga un nuevo pixel
        int iEstacion=0, iEstacionesCargadas=0, usoACargar, estacionActual, estacionesDeEsteUso, usoYDuracion[];
        usoYDuracion= new int[2];
        //System.out.println("Trabajo con el pixel: "+iPixel);

        //Relleno la estacion 0 del pixel
        String usoOriginal = Constantes.pixeles[iPixel].usoOriginal;
        //Averiguo que pixel tenia
        usoYDuracion=Uso.usoYDuracion(usoOriginal);
        //System.out.println("\tTengo que cargar por el uso original: "+usoYDuracion[0]);
        //System.out.println("\tme faltan : "+usoYDuracion[1]);
        //Completo las estaciones que me faltan
        while(usoYDuracion[1]>0){

            usoACargar=usoYDuracion[0];
            //Calculo la estacion del uso que voy a cargar
            estacionesDeEsteUso=Constantes.usos[usoACargar].duracionEstaciones-usoYDuracion[1];
            //Cargo el uso y la estacion
            this.matriz[iPixel][iEstacion]=100*usoACargar+estacionesDeEsteUso; //Antes usaba estacionActual pero seguro estaba mal
            //Aumento la cantidad de usos del due;o del pixel
            this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoACargar][iEstacion][Constantes.pixeles[iPixel].productor]++;
            //Actualizo valores de la solucion
            /*
            System.out.print("\tIntento actualizar productividad del productor: "+Constantes.pixeles[iPixel].productor+" en la estacion "+iEstacion);
            System.out.print(" Aumentando su valor actual: "+this.productivdadProductores[Constantes.pixeles[iPixel].productor][iEstacion]);
            System.out.print(" segun los valores: "+Constantes.pixeles[iPixel].superficie);
            System.out.print(" usoACargar "+ usoACargar);
            System.out.print(" estacionesDeEsteUso "+ estacionesDeEsteUso);
            System.out.print(" y "+ Constantes.usos[usoACargar].productividad[estacionesDeEsteUso]);
            System.out.println(" Sumando: "+ Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].productividad[estacionesDeEsteUso]);
            */

            //Actualizo la productividad del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
            this.productivdadProductores[Constantes.pixeles[iPixel].productor][iEstacion] +=
                    Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].productividad[estacionesDeEsteUso];
            //Actualizo fosforoAnual segun la estacion actual y el fosforo del Uso
            this.restriccionFosforoAnual.fosforoAnual[iEstacion/4] += Constantes.usos[usoACargar].fosforo*Constantes.pixeles[iPixel].superficie;
            //Actualizo lo que aporta el uso al fosforo total en esta estacion
            this.fosforo+=(Constantes.usos[usoACargar].fosforo*Constantes.pixeles[iPixel].superficie)/Constantes.usos[usoACargar].duracionEstaciones;
            //Aumento el iterador de iEstacion
            iEstacion++;
            //Reduzco la duracion
            usoYDuracion[1]--;
            iEstacionesCargadas++;
        }
        //Averiguo en que momento del plantio estaba
        //Si hay que llenar mas estaciones las lleno
        iEstacion=iEstacionesCargadas;

        usoACargar= Uso.siguienteUsoUniforme(usoYDuracion[0]);
        //System.out.println("Pixel:"+ iPixel+" Estacion:"+ iEstacion+" Uso previo: "+usoYDuracion[0]+" Siguiente uso: "+usoACargar+" Estaciones a cargar: "+Constantes.usos[usoACargar].duracionEstaciones);
        //Para un pixel recorro todas las estaciones
        while (iEstacion < Constantes.cantEstaciones){
            estacionesDeEsteUso=0;
            //Cargo todas las estaciones del uso, deteniendome si llego a cantEstaciones
            while((estacionesDeEsteUso<Constantes.usos[usoACargar].duracionEstaciones) && ((estacionesDeEsteUso+iEstacion)<(Constantes.cantEstaciones))){
                estacionActual=iEstacion+estacionesDeEsteUso;
                //Cargo el uso y las estaciones que llevaNo corresponde la duracion.
                this.matriz[iPixel][estacionActual]=100*usoACargar+estacionesDeEsteUso; //Antes usaba estacionActual pero seguro estaba mal
                //Aumento la cantidad de usos del due;o del pixel
                this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoACargar][estacionActual][Constantes.pixeles[iPixel].productor]++;

                //Actualizo valores de la solucion
                //Actualizo la productividad del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
                this.productivdadProductores[Constantes.pixeles[iPixel].productor][estacionActual]
                        += Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].productividad[estacionesDeEsteUso];
                //Actualizo fosforoAnual segun la estacion actual y el fosforo del Uso
                this.restriccionFosforoAnual.fosforoAnual[estacionActual/4] += Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].fosforo;
                //Actualizo lo que aporta el uso al fosforo total en esta estacion
                this.fosforo+=(Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].fosforo)/Constantes.usos[usoACargar].duracionEstaciones;
                estacionesDeEsteUso++;
            }


            iEstacion=iEstacion + estacionesDeEsteUso; //Actualizo la siguiente estacion con la que trabajar
            //System.out.print("Pixel:"+ iPixel+" Estacion:"+ iEstacion+" Uso previo: "+usoACargar);
            usoACargar= Uso.siguienteUsoUniforme(usoACargar); //Obtengo el siguiente uso a cargar
            //System.out.println(" Siguiente uso: "+usoACargar+" Estaciones a cargar: "+Constantes.usos[usoACargar].duracionEstaciones);
        }
    }

    public void limpiarPixel(int iPixel){
        //Libera un pixel actualizando las variables de restricciones
        System.out.println("Limpiar pixel: "+iPixel);
        int iEstacion=0, usoABorrar, estacionesDeEsteUso, estacionActual, estacionesBorradas=0;

        for (int itEstacion = 0; itEstacion < Constantes.cantEstaciones; itEstacion++) {
            //100*usoACargar+estacionDelUso
            usoABorrar=this.matriz[iPixel][iEstacion]/100;
            estacionesDeEsteUso=this.matriz[iPixel][iEstacion]%100;

            //Libero el uso y las estaciones que lleva
            this.matriz[iPixel][itEstacion]=0; //Antes usaba estacionActual pero seguro estaba mal

            //Reduzco la cantidad de usos del due;o del pixel
            //this.cantUsosProductor[Constantes.pixeles[iPixel].productor][usoABorrar]--;
            this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoABorrar][itEstacion][Constantes.pixeles[iPixel].productor]--;

            //Reduzco la productividad del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
            this.productivdadProductores[Constantes.pixeles[iPixel].productor][itEstacion]
                    -= Constantes.pixeles[iPixel].superficie * Constantes.usos[usoABorrar].productividad[estacionesDeEsteUso];
            //Reduzco fosforoAnual segun la estacion actual y el fosforo del Uso
            this.restriccionFosforoAnual.fosforoAnual[itEstacion/4] -= Constantes.pixeles[iPixel].superficie * Constantes.usos[usoABorrar].fosforo;

            //Actualizo lo que aporta el uso al fosforo total en esta estacion
            this.fosforo-=Constantes.pixeles[iPixel].superficie *Constantes.usos[usoABorrar].fosforo/Constantes.usos[usoABorrar].duracionEstaciones;
        }
    }

    public  void cambiarPixel(int iPixel){
        //Toma un pixel ya cargado y lo cambia limpiando y actualizando variables en una sola recorrida
        int iEstacion=0, iEstacionesCargadas=0, usoACargar, usoABorrar, estacionActual, estacionesDeUsoACargar, estacionesDeUsoABorrar, usoYDuracion[];
        usoYDuracion= new int[2];


        //NO debo modificar la continuacion del uso original ni variar lo que aporta a las restricciones


        //Relleno la estacion 0 del pixel
        String usoOriginal = Constantes.pixeles[iPixel].usoOriginal;
        //Averiguo que pixel tenia
        usoYDuracion=Uso.usoYDuracion(usoOriginal);
        //Calculo la primera estacion posterior al uso original
        iEstacion=usoYDuracion[1];
        //Calculo el siguiente uso a cargar
        usoACargar= Uso.siguienteUsoUniforme(usoYDuracion[0]);
        //Para un pixel recorro todas las estaciones
        while (iEstacion < Constantes.cantEstaciones){
            estacionesDeUsoACargar=0;
            //Cargo todas las estaciones del uso, deteniendome si llego a cantEstaciones
            while((estacionesDeUsoACargar<Constantes.usos[usoACargar].duracionEstaciones) && ((estacionesDeUsoACargar+iEstacion)<(Constantes.cantEstaciones))){
                //Calculo la estacion
                estacionActual=iEstacion+estacionesDeUsoACargar;
                //Obtengo el pixel a borrar
                usoABorrar=this.matriz[iPixel][estacionActual]/100;
                //Y la estacion de el uso
                estacionesDeUsoABorrar=this.matriz[iPixel][estacionActual]%100;

                //Cargo el uso y las estaciones que llevaNo corresponde la duracion.
                this.matriz[iPixel][estacionActual]=100*usoACargar+estacionesDeUsoACargar; //Antes usaba estacionActual pero seguro estaba mal

                //Actualizo valores de la solucion
                //En caso de ser necesario actualizo la cantidad de usos en en estaestacion en este pixel
                if (usoABorrar != usoACargar) {
                    //Actualizo la cantidad de usos
                    this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoABorrar][estacionActual][Constantes.pixeles[iPixel].productor]--;
                    this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoACargar][estacionActual][Constantes.pixeles[iPixel].productor]++;
                    //Actualizo la productividad del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
                    this.productivdadProductores[Constantes.pixeles[iPixel].productor][estacionActual] =
                            this.productivdadProductores[Constantes.pixeles[iPixel].productor][estacionActual]
                                    - Constantes.pixeles[iPixel].superficie * Constantes.usos[usoABorrar].productividad[estacionesDeUsoABorrar]
                                    + Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].productividad[estacionesDeUsoACargar];
                    //Actualizo fosforoAnual segun la estacion actual y el fosforo del Uso
                    this.restriccionFosforoAnual.fosforoAnual[estacionActual/4] =
                            this.restriccionFosforoAnual.fosforoAnual[estacionActual/4]
                                    -Constantes.pixeles[iPixel].superficie * Constantes.usos[usoABorrar].fosforo
                                    +Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].fosforo;
                    //Actualizo lo que aporta el uso al fosforo total en esta estacion
                    this.fosforo= this.fosforo
                            - (Constantes.pixeles[iPixel].superficie * Constantes.usos[usoABorrar].fosforo)/Constantes.usos[usoABorrar].duracionEstaciones
                            + (Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACargar].fosforo)/Constantes.usos[usoACargar].duracionEstaciones;
                }
                estacionesDeUsoACargar++;
            }
            iEstacion=iEstacion + estacionesDeUsoACargar; //Actualizo la siguiente estacion con la que trabajar
            //System.out.print("Pixel:"+ iPixel+" Estacion:"+ iEstacion+" Uso previo: "+usoACargar);
            usoACargar= Uso.siguienteUsoUniforme(usoACargar); //Obtengo el siguiente uso a cargar
            //System.out.println(" Siguiente uso: "+usoACargar+" Estaciones a cargar: "+Constantes.usos[usoACargar].duracionEstaciones);
        }
    }


    public void  cumpleRestriccionesProductividad(){
        float acumuladoAnual, maximaDesviacionAnual=0, mediaDesviacionAnual=0, maximaDesviacionEstacion=0,mediaDesviacionEstacion=0, desviacion, productividadSobreSuperficie=0;
        int incumplimientoEstacion=0, incumplimientoAnual=0;
        this.restriccionProductividadMinimaAnual.cumpleRestrccion=true;
        this.restriccionProductividadMinimaAnual.mediaDesviacion=0;
        this.restriccionProductividadMinimaAnual.maximoDesviacion=0;
        this.restriccionProductividadMinimaEstacion.cumpleRestrccion=true;
        this.restriccionProductividadMinimaEstacion.mediaDesviacion=0;
        this.restriccionProductividadMinimaEstacion.maximoDesviacion=0;

        for (int iProductores = 0; iProductores < Constantes.cantProductores; iProductores++) {
            acumuladoAnual=0;
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                //RESTRICCION ESTACIONARIA
                //Calculo desviacion estacion
                productividadSobreSuperficie=this.productivdadProductores[iProductores][iEstacion]/Constantes.productores[iProductores].areaTotal;
                desviacion=min((productividadSobreSuperficie-Constantes.productores[iProductores].restriccionProduccionEstacion[iEstacion]),0);
                if (desviacion<0){
                    this.restriccionProductividadMinimaEstacion.cumpleRestrccion=false;
                    incumplimientoEstacion++;
                    mediaDesviacionEstacion+=desviacion;
                    maximaDesviacionEstacion=min(desviacion,maximaDesviacionEstacion);
                    //System.out.print("Desviacion estacion "+incumplimientoEstacion+": "+desviacion+" MediaAcumulada: "+mediaDesviacionEstacion+" maximaDesviacionEstacion: "+maximaDesviacionEstacion);
                    //System.out.println(" Productividad"+this.productivdadProductores[iProductores][iEstacion]+" Restriccion "+Constantes.productores[iProductores].restriccionProduccionEstacion[iEstacion]);
                }                //Acumulo la desviacion

                //RESTRICCION ANUAL
                //acumulo la productividad anual
                acumuladoAnual+=this.productivdadProductores[iProductores][iEstacion];
                //Si llegue a fin de año
                if (((iEstacion+1)%4)==0){
                    productividadSobreSuperficie=(acumuladoAnual/ Constantes.productores[iProductores].areaTotal );
                    //Calculo desviacion anual
                    desviacion=
                            min((productividadSobreSuperficie- Constantes.productores[iProductores].restriccionProduccionAnual[(iEstacion)/4]),0);
                    if (desviacion<0){
                        this.restriccionProductividadMinimaAnual.cumpleRestrccion=false;
                        incumplimientoAnual++;
                        mediaDesviacionAnual+=desviacion;
                        maximaDesviacionAnual=min(desviacion,maximaDesviacionAnual);
                        //System.out.println("DesviacionAnual año "+incumplimientoAnual+": "+desviacion+" MediaAnualAcumulada: "+mediaDesviacionAnual);
                    }
                    acumuladoAnual=0;
                }
            }
        }
        if (incumplimientoEstacion > 0){
            //Calculo la media de este productor
            mediaDesviacionEstacion=mediaDesviacionEstacion/incumplimientoEstacion;
            //Me quedo con la mejor de las dos
            this.restriccionProductividadMinimaEstacion.mediaDesviacion=mediaDesviacionEstacion;
            this.restriccionProductividadMinimaEstacion.maximoDesviacion=maximaDesviacionEstacion;
        }
        if (incumplimientoAnual > 0){
            //Calculo la media de este productor
            mediaDesviacionAnual=mediaDesviacionAnual/incumplimientoAnual;
            //Me quedo con la mejor de las dos
            this.restriccionProductividadMinimaAnual.mediaDesviacion=mediaDesviacionAnual;
            this.restriccionProductividadMinimaAnual.maximoDesviacion=maximaDesviacionAnual;
        }
    }

    public  void cumpleRestriccionUsosDistintos(){
        int cantUsosDistintos;
        this.restriccionUsosDistintos.cumpleRestriccion=true;
        this.restriccionUsosDistintos.cantIncumplimientos=0;
        //Recorro Cada estacion
        for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
            //Recorro Cada Productor
            for (int iProductor = 0; iProductor < Constantes.cantProductores ; iProductor++) {
                cantUsosDistintos=0;
                //Calculo cuantos usos distintos tuvo
                for (int iUso = 0;  iUso< Constantes.cantUsos; iUso++) {
                    if (this.restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[iUso][iEstacion][iProductor] > 0){
                        cantUsosDistintos++;
                    }
                }
                if ((cantUsosDistintos < Constantes.minimaCantidadUsos) ||
                        (cantUsosDistintos > Constantes.maximaCantidadUsos)){
                    this.restriccionUsosDistintos.cumpleRestriccion=false;
                    this.restriccionUsosDistintos.cantIncumplimientos++;
                }
            }
        }
        this.restriccionUsosDistintos.incumplimientoRelativo=(float) this.restriccionUsosDistintos.cantIncumplimientos/(float)(Constantes.cantEstaciones*Constantes.cantProductores);
        /*System.out.println("this.restriccionUsosDistintos.cantIncumplimientos: "+this.restriccionUsosDistintos.cantIncumplimientos );
        System.out.println("Constantes.cantEstaciones: "+Constantes.cantEstaciones );
        System.out.println("Constantes.cantProductores: "+Constantes.cantProductores );
        System.out.println("this.restriccionUsosDistintos.incumplimientoRelativo: "+this.restriccionUsosDistintos.incumplimientoRelativo );
*/
    }

    public void cumpleRestriccionFosforoAnual(){
        //Chequeo restriccion cumpleRestriccionFAnual
        this.restriccionFosforoAnual.cumpleRestriccion=true;
        this.restriccionFosforoAnual.maximoDesviacion=0;
        this.restriccionFosforoAnual.mediaDesviacion=0;
        //Para cada an;o
        int cantidadAniosSuperandoFosforo=0;
        for (int iAnio=0; iAnio<Constantes.cantAnios; iAnio++){
            //Chequeo que si la produccion de fosforo es mayor que el tope de Fosforo para ese año
            if (this.restriccionFosforoAnual.fosforoAnual[iAnio] >= Constantes.topeFosforoAnual[iAnio]){
                //En caso de serlo actualizo cumple restriccion y el anio en que esta la desviacion maxima
                if (this.restriccionFosforoAnual.cumpleRestriccion) {
                    this.restriccionFosforoAnual.cumpleRestriccion = false;
                /*DEPRECATED
                    //this.indiceDesviacionMaximaFosforoAnual=iAnio;
                }else{
                    if (this.restriccionFosforoAnual.fosforoAnual[this.indiceDesviacionMaximaFosforoAnual]<this.restriccionFosforoAnual.fosforoAnual[iAnio]){
                        this.indiceDesviacionMaximaFosforoAnual=iAnio;
                    }
                */
                }
                //actualizo los valores para calcular la media
                cantidadAniosSuperandoFosforo++;
                this.restriccionFosforoAnual.mediaDesviacion+= this.restriccionFosforoAnual.fosforoAnual[iAnio]-Constantes.topeFosforoAnual[iAnio];

            }
        }
        //En caso de ser necesario calculo la media
        if (!this.restriccionFosforoAnual.cumpleRestriccion){
            this.restriccionFosforoAnual.mediaDesviacion/=cantidadAniosSuperandoFosforo;
        }
    }


    public void imprimirArchivo(){
        int uso=0;
        String[][] matriz;
        //Creo la matriz de potreros con valores en cero
        matriz = new String [Constantes.cantPotreros][Constantes.cantEstaciones];
        for (int iPotrero = 0; iPotrero < Constantes.cantPotreros; iPotrero++) {
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                matriz[iPotrero][iEstacion]="Reservado";
            }
        }


        //Agrego los pixeles calculados
        for (int iPixel = 0; iPixel< Constantes.pixeles.length; iPixel++) {
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                uso = this.matriz[iPixel][iEstacion] / 100;
                matriz[Constantes.pixeles[iPixel].id-1][iEstacion]=Constantes.usos[uso].nombre;
            }
        }
        //Creo el archivo
        try {
            PrintWriter writer = new PrintWriter("salida.out", "UTF-8");

            //Imprimo la primera fila que marca las estaciones
            writer.println("ID,N,10,0\tEst1\tEst2\tEst3\tEst4\tEst5\tEst6\tEst7\tEst8\tEst9\tEst10\tEst11\tEst12\tEst13\tEst14\tEst15\tEst16");
            //Para cada potrero
            for (int iPotrero = 0; iPotrero < Constantes.cantPotreros; iPotrero++) {
                //Imprimo los usos que se seleccionaron.
                writer.print((iPotrero+1));
                for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                    //Imprimo el uso de cada estacion
                    writer.print("\t" + matriz[iPotrero][iEstacion]);
                }
                writer.println();

            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
    }


    /*--DEPRECATED



        public void imprimirRestriccionProductores(){
        for (int iProductor = 0; iProductor < Constantes.cantProductores; iProductor++) {
            System.out.println("\t\t\tDesviacion Maxima Productor "+iProductor+": "+
                    min(this.productivdadProductores[iProductor][this.indiceDesviacionesMaximaProductores[iProductor]]-
                            Constantes.productores[iProductor].restriccionProduccionEstacion[this.indiceDesviacionesMaximaProductores[iProductor]],0));

            System.out.println("\t\t\t\tAlcanzada la estacion: "+ this.indiceDesviacionesMaximaProductores[iProductor]);
            System.out.println("\t\t\t\tDesviacion Media: "+this.desviacionesMediaProductores[iProductor]);
        }
    }

        public void imprimirRestriccionUsosPorProductor(){
        System.out.println("\t\tRestriccion Usos por productores: "+this.cumpleRestriccionProductores);
        for (int iProductor = 0; iProductor < Constantes.cantProductores; iProductor++) {
            System.out.println("\t\t\tProductor "+iProductor+" CantUsosDistintos "+this.cantUsosDistintosProductor[iProductor]);

        }
    }


*/

}