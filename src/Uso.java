import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Uso {
    int numUso, duracionEstaciones, primeraEstacion; //PrimeraEstacion: 0 Otoño, 1 Primavera, 2 cualquiera
    float productividadTotal;
    float fosforo;
    float[] productividad, fosforoEstacion;
    String nombre;
    List<Integer> siguientesUsos;

    public Uso (int nUso, int dEstaciones, int primeraEstacion,  float fosforo, float prod[],float fosforoEstacion[], String nombre,List<Integer>  sigUsos){
        this.numUso=nUso;
        this.duracionEstaciones=dEstaciones;
        this.primeraEstacion=primeraEstacion;
        this.fosforoEstacion=fosforoEstacion;
        this.fosforo=fosforo;
        this.productividad=prod;
        this.productividadTotal=0;
        for (int iEstacion = 0; iEstacion < dEstaciones; iEstacion++) {
            this.productividadTotal+= this.productividad[iEstacion];
        }

        this.nombre= nombre;
        this.siguientesUsos=sigUsos;
    }

    //TODO: Implementar siguienteUso para otras distribuciones

    public static int siguienteUsoUniforme(int usoOriginal){
        //Devuelve la posicion en el array de usos del siguiente uso
        //System.out.println("\t\tCantidad de Posibles siguientes usos: "+Constantes.usos[usoOriginal].siguientesUsos.size());
        int numeroUniforme= 0;  //En caso de tener solo un siguiente uso selecciono ese
        if (Constantes.usos[usoOriginal].siguientesUsos.size()>1){ //En caso de que tenga mas de un uso
            numeroUniforme=Constantes.uniforme.nextInt (Constantes.usos[usoOriginal].siguientesUsos.size()-1); // elijo uno uniforme entre cero y la cantidad de siguientes usos -1
        }
        //System.out.println("\t\tSiguiente Uso: "+numeroUniforme);
        int siguienteUso= Constantes.usos[usoOriginal].siguientesUsos.get(numeroUniforme);//Posicion en el array coincide con el numero de Uso
        return siguienteUso;
    }

    public static int siguienteUsoRuletaFosforo(int usoOriginal){
        //Devuelve la posicion en el array de usos del siguiente uso
        //System.out.println("\t\tCantidad de Posibles siguientes usos: "+Constantes.usos[usoOriginal].siguientesUsos.size());
        boolean encontre =false;
        int siguienteUso=0;
        float fosforoMaximo=0, fosforoSorteado=0,fosforoAcumulado=0;

        //Calculo el maximo fosforo entre los que sortear
        for (int iUsoSiguiente = 0; iUsoSiguiente < Constantes.usos[usoOriginal].siguientesUsos.size(); iUsoSiguiente++) {
            fosforoMaximo+=Constantes.usos[iUsoSiguiente].fosforo;
        }
        //En caso de que tenga mas de un uso sorteo un valor entre el fosforo maximo
        if (Constantes.usos[usoOriginal].siguientesUsos.size()>1){
            fosforoSorteado=Constantes.uniforme.nextFloat()*fosforoMaximo; // elijo uno uniforme entre cero y el fosforo maximo
        }
        //Veo a que uso corresponde el fosforo
        for (int iUsoSiguiente = 0; iUsoSiguiente < Constantes.usos[usoOriginal].siguientesUsos.size() && !encontre; iUsoSiguiente++) {
            //Sumo el fosforo del uso actual al acumulado
            fosforoAcumulado+=Constantes.usos[iUsoSiguiente].fosforo;
            //Chequeo si el fosforo sorteado pertenece al uso actual
            if (fosforoSorteado<fosforoAcumulado){
                siguienteUso=Constantes.usos[usoOriginal].siguientesUsos.get(iUsoSiguiente);//Posicion en el array coincide con el numero de Uso
                encontre=true;
            }
        }
        //System.out.println("\t\tSiguiente Uso: "+numeroUniforme);
        return siguienteUso;
    }

    public static int siguienteUsoRuletaProduccion(int usoOriginal){
        //Devuelve la posicion en el array de usos del siguiente uso
        //System.out.println("\t\tCantidad de Posibles siguientes usos: "+Constantes.usos[usoOriginal].siguientesUsos.size());
        boolean encontre =false;
        int siguienteUso=0;
        float produccionMaxima=0, produccionSorteada=0,produccionAcumulada=0;

        //Calculo la maxima produccion entre los que sortear
        for (int iUsoSiguiente = 0; iUsoSiguiente < Constantes.usos[usoOriginal].siguientesUsos.size(); iUsoSiguiente++) {
            produccionMaxima+=Constantes.usos[iUsoSiguiente].productividadTotal;
        }
        //En caso de que tenga mas de un uso sorteo un valor entre la productividad maxima
        if (Constantes.usos[usoOriginal].siguientesUsos.size()>1){
            produccionSorteada=Constantes.uniforme.nextFloat()*produccionMaxima; // elijo uno uniforme entre cero y el fosforo maximo
        }
        //Veo a que uso corresponde el fosforo
        for (int iUsoSiguiente = 0; iUsoSiguiente < Constantes.usos[usoOriginal].siguientesUsos.size() && !encontre; iUsoSiguiente++) {
            //Sumo la productividad actual al acumulado
            produccionAcumulada+=Constantes.usos[iUsoSiguiente].productividadTotal;
            //Chequeo si la productividad sorteado pertenece al uso actual
            if (produccionSorteada<produccionAcumulada){
                siguienteUso=Constantes.usos[usoOriginal].siguientesUsos.get(iUsoSiguiente);//Posicion en el array coincide con el numero de Uso
                encontre=true;
            }
        }
        //System.out.println("\t\tSiguiente Uso: "+numeroUniforme);
        return siguienteUso;
    }

    public void imprimirUso(){
        System.out.printf("\t("+this.nombre+" , "+this.numUso+" , "+this.duracionEstaciones+" , "+this.primeraEstacion+" , "+ this.fosforo +" , {");
        //Imprimo todos los elementos de productividad
        for (int i = 0; i < this.productividad.length; i++) {
            if (i!=0){
                System.out.printf(",");
            }
            System.out.printf(String.valueOf(this.productividad[i]));
        }
        System.out.printf("},{");

        for (int i = 0; i < this.fosforoEstacion.length; i++) {
            if (i!=0){
                System.out.printf(",");
            }
            System.out.printf(String.valueOf(this.fosforoEstacion[i]));
        }
        System.out.println("},");
        System.out.printf("\t\t{");

        for (int i = 0; i < this.siguientesUsos.size(); i++) {
            if (i!=0){
                System.out.printf(",");
            }
            System.out.printf(String.valueOf(this.siguientesUsos.get(i)));
        }
        System.out.println("})");
    }

    public static void imprimirUsos(){
        for (int i = 0; i < Constantes.usos.length; i++) {
            Constantes.usos[i].imprimirUso();
        }
    }


    public static String getEstacionUso(int numero){
        switch (numero)
        {
            case 0:
            case 1:
            case 2:
            case 3:
                return " (1° año)";
            case 4:
            case 5:
            case 6:
            case 7:
                return " (2° año)";
            case 8:
            case 9:
            case 10:
            case 11:
                return " (3° año)";
            case 12:
            case 13:
            case 14:
            case 15:
                return " (4° año)";
            default:
                return "No match";
        }
    }

    public static int indiceUso(String nombre){
        switch(nombre)
        {
            case "Alfalfa":
                return 1;
            case "FE+TB+L":
                return 2;
            case "TR+Cebadilla":
                return 3;
            case "TR+TB+Raigrás":
                return 4;
            case "Lotus Puro":
                return 5;
            case "Raigrás+TB+L":
                return 6;
            case "Achicoria":
                return 7;
            case "Moha":
                return 8;
            case "Sorgo Forrajero":
                return 9;
            case "Avena Pastoreo":
                return 10;
            case "Avena+Raigrás Temp":
                return 11;
            case "Maiz":
                return 12;
            case "Campo Natural":
                return 13;
            case "Rastrojo":
                return 14;
            default:
                System.out.println("no match");
                return 0;
        }
    }
    public static int[]  usoYDuracion(String usoOriginal){
        int[] usoYDuracion= new int [2];

        String[] campos = usoOriginal.split("\\(");
        /*for (int iCampos = 0; iCampos < campos.length; iCampos++) {
            System.out.print("UsoOriginal["+iCampos+"]: "+campos[iCampos]);
        }
        System.out.print("\n");
        */
        switch(campos[0]) {

            case "Alfalfa ":
                usoYDuracion[0]=1;
                break;
            case "FE+TB+L ":
                usoYDuracion[0]= 2;
                break;
            case "TR+Cebadilla ":
                usoYDuracion[0]= 3;
                break;
            case "TR+TB+Raigrás ":
                usoYDuracion[0]= 4;
                break;
            case "Lotus Puro ":
                usoYDuracion[0]= 5;
                break;
            case "Raigrás+TB+L ":
                usoYDuracion[0]= 6;
                break;
            case "Achicoria ":
                usoYDuracion[0]= 7;
                break;
            case "Moha ":
                usoYDuracion[0]= 8;
                break;
            case "Sorgo Forrajero ":
                usoYDuracion[0]= 9;
                break;
            case "Avena Pastoreo ":
                usoYDuracion[0]= 10;
                break;
            case "Avena+Raigrás Temp ":
                usoYDuracion[0]= 11;
                break;
            case "Maiz ":
                usoYDuracion[0]= 12;
                break;
            case "Campo Natural":
                usoYDuracion[0]= 13;
                break;
            case "Rastrojo ":
                usoYDuracion[0]= 14;
                break;
            default:
                System.out.println("no match");
                break;
        }
        if((usoYDuracion[0]==1)||(usoYDuracion[0]==2)){
             switch (campos[1]){
                 case "1° año)":
                 case "1Â° aÃ±o)":
                     usoYDuracion[1]=12;
                     break;
                 case "2° año)":
                 case "2°año)":
                 case "2Â° aÃ±o)":
                 case "2Â°aÃ±o)":
                     usoYDuracion[1]=8;
                     break;
                 case "3° año)":
                 case "3Â° aÃ±o)":
                     usoYDuracion[1]=4;
                     break;
                 case "4° año)":
                 case "4Â° aÃ±o)":
                     usoYDuracion[1]=0;
                     break;
                 default:
                     System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                     break;
             }
         } else if ((usoYDuracion[0]==3)||(usoYDuracion[0]==4)){
            switch (campos[1]){
                case "1° año)":
                case "1Â° aÃ±o)":
                    usoYDuracion[1]=4;
                    break;
                case "2° año)":
                case "2°año)":
                case "2Â° aÃ±o)":
                case "2Â°aÃ±o)":
                    usoYDuracion[1]=0;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                    System.out.println("Con el texto: "+usoOriginal);
                    System.out.println("Parseado: "+campos[0]+" || "+ campos[1]);


                    break;
            }
        } else if ((usoYDuracion[0]==5)||(usoYDuracion[0]==6)){
            switch (campos[1]){
                case "1° año)":
                case "1Â° aÃ±o)":
                    usoYDuracion[1]=8;
                    break;
                case "2° año)":
                case "2°año)":
                case "2Â° aÃ±o)":
                case "2Â°aÃ±o)":
                    usoYDuracion[1]=4;
                    break;
                case "3° año)":
                case "3Â° aÃ±o)":
                    usoYDuracion[1]=0;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                    break;
            }
        } else if (usoYDuracion[0]==7){
            switch (campos[1]){
                case "1° año)":
                case "1Â° aÃ±o)":
                    usoYDuracion[1]=7;
                    break;
                case "2° año)":
                case "2°año)":
                case "2Â° aÃ±o)":
                case "2Â°aÃ±o)":
                    usoYDuracion[1]=0;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                    break;
            }
        } else if (usoYDuracion[0]==8){
            switch (campos[1]){
                case "anual)":
                    usoYDuracion[1]=0;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                    break;
            }
        } else if (usoYDuracion[0]==9){
            switch (campos[1]){
                case "anual)":
                    usoYDuracion[1]=1;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]);
                    break;
            }
        }
        else if ((usoYDuracion[0]>10)){
            usoYDuracion[1]=0;
            /*switch (campos[1]){
                case "anual)":
                    usoyDuracion[1]=0;
                    break;
                default:
                    System.out.println("No corresponde la duracion. Para Uso: "+usoYDuracion[0]+" y duracion: " +usoYDuracion[1]");
                    break;
            }*/
        }
        //System.out.println("\tPara el usoOriginal:"+usoOriginal+" Devuelvo uso: "+usoyDuracion[0]+" y duracion: "+usoyDuracion[1]);
        return usoYDuracion;
    }



    public static Uso[] cargarUsos(){
        Uso [] listaUsos= new Uso[Constantes.cantUsos];
        float[] productividadUso, fosforoEstacion;
        List<Integer> siguientesUsos;
        //Siguientes usos 0 reservado para el inicio random
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,14));
        fosforoEstacion = new float[] {0};
        productividadUso= new float[] {0};
        listaUsos[0]= new Uso(0, 0,0, 0, productividadUso,fosforoEstacion, "Reservado",siguientesUsos);

        //Cargo los usos reales

        //Pastura Perenne 1 ---- Alfalfa
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(2,3,4,5,6,7,8,9,10,11,14));
        fosforoEstacion = new float[] {0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f};
        productividadUso= new float[] {0, 0, 3025, 2475, 3000, 1200, 4200, 3600, 1600, 800, 3200, 2400, 700, 350, 3500,2450};

        listaUsos[1]= new Uso(1, 16,0, 4.64f, productividadUso,fosforoEstacion, "Alfalfa",siguientesUsos);

        //Pastura Perenne 2 ---- FE+TB+L
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,3,4,5,6,7,8,9,10,11,14));
        productividadUso= new float[] {0, 450, 3150, 900, 2500, 1500, 4000, 2000, 1400, 1120, 3500, 980, 700, 650, 3000,650};
        listaUsos[2]= new Uso(2, 16, 0,4.64f, productividadUso,fosforoEstacion, "FE+TB+L",siguientesUsos);

        //Pastura Perenne 3 ---- TR+Cebadilla
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,4,5,6,7,8,9,10,11,14));
        productividadUso= new float[] {0, 2000, 4400, 1600, 2800, 2000, 4200, 1000};
        fosforoEstacion = new float[] {0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f};
        listaUsos[3]= new Uso(3, 8,0, 2.32f, productividadUso,fosforoEstacion, "TR+Cebadilla", siguientesUsos);

        //Pastura Perenne 4 ---- TR+TB+Raigras
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,5,6,7,8,9,10,11,14));
        productividadUso= new float[] {1161, 2212, 3768, 1483, 2176, 1780, 3495, 1483};
        listaUsos[4]= new Uso(4, 8, 0,2.32f, productividadUso,fosforoEstacion, "TR+TB+Raigrás",siguientesUsos);

        //Pastura Perenne 5 ---- Lotus Puro
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,6,7,8,9,10,11,14));
        productividadUso= new float[] {558, 1075, 2127, 962, 1318, 1107, 2826, 1387, 1134, 828, 2340, 971};
        fosforoEstacion = new float[] {0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f};
        listaUsos[5]= new Uso(5, 12, 0,3.48f, productividadUso,fosforoEstacion, "Lotus Puro", siguientesUsos);

        //Pastura Perenne 6 ---- Raigras+TB+L
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,5,7,8,9,10,11,14));
        productividadUso= new float[] {775, 1811, 3159, 816, 1840, 1749, 3342, 1365, 1318, 1287, 2523, 999};
        listaUsos[6]= new Uso(6, 12, 0,3.48f, productividadUso,fosforoEstacion, "Raigrás+TB+L",siguientesUsos);

        //Pastura Perenne 7 ---- Achicoria
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,8,9,10,11,14));
        productividadUso= new float[] {65, 232, 2653, 2849, 1572, 2342, 3193};
        fosforoEstacion = new float[] {0.29f,0.29f,0.29f,0.29f,0.29f,0.29f,0.29f};
        listaUsos[7]= new Uso(7, 7, 0, 2.03f,productividadUso, fosforoEstacion,"Achicoria" ,siguientesUsos);

        //Verdeo Verano 8 ---- Moha
        siguientesUsos = new  ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,7,10,11,14));
        productividadUso= new float[] {0, 5000};
        fosforoEstacion = new float[] {0.58f,0.58f};
        listaUsos[8]= new Uso(8, 2, 1, 1.16f,productividadUso,fosforoEstacion,"Moha", siguientesUsos);

        //Verdeo Verano 9 ---- Sorgo Forrajero
        siguientesUsos = new  ArrayList<Integer> (Arrays.asList(1,2,5,7,8,9,12,14));
        productividadUso= new float[] {0, 9687, 3568};
        fosforoEstacion = new float[] {0f,0.58f,0.58f};
        listaUsos[9]= new Uso(9, 3, 1, 1.16f,productividadUso, fosforoEstacion, "Sorgo Forrajero",siguientesUsos);

        //Verdeo Verano 10 ---- Avena Pastoreo
        siguientesUsos = new  ArrayList<Integer> (Arrays.asList(1,2,5,7,8,9,12,14));
        productividadUso= new float[] {1625, 3250, 1625};
        fosforoEstacion = new float[] {0.387f, 0.387f,0.387f};
        listaUsos[10]= new Uso(10, 3, 0, 1.16f,productividadUso, fosforoEstacion, "Avena Pastoreo",siguientesUsos);

        //Verdeo Verano 11 ---- Avena+Raigras Temp.
        siguientesUsos = new  ArrayList<Integer> (Arrays.asList(8,9,12,14));
        productividadUso= new float[] {2083, 2211, 2735};
        listaUsos[11]= new Uso(11, 3, 0, 1.16f,productividadUso,fosforoEstacion,"Avena+Raigrás Temp", siguientesUsos);

        //Cultivo Verano 12 ---- Maiz
        siguientesUsos = new  ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,7,10,11,14));
        productividadUso= new float[] {0, 12882};
        fosforoEstacion = new float[] {1.115f,1.115f};
        listaUsos[12]= new Uso(12, 2, 1, 2.230f,productividadUso,fosforoEstacion, "Maiz", siguientesUsos);

        //Campo Natural 13 ---- Campo Natural
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13, 14));
        productividadUso= new float[] {675,425,1039,932};
        fosforoEstacion = new float[] {0.06f,0.06f};
        listaUsos[13]= new Uso(13, 4, 2, 0.24f,productividadUso, fosforoEstacion,"Campo Natural", siguientesUsos);

        //Rastrojo 14 ---- Rastrojo
        //Usa el mismo uso que el anterior
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12, 14));
        productividadUso= new float[] {0,0,0,0};//
        fosforoEstacion = new float[] {0.244f,0.244f, 0.244f,0.244f};
        listaUsos[14]= new Uso(14, 2, 2, 0.975f,productividadUso,fosforoEstacion, "Rastrojo", siguientesUsos);

        return listaUsos;
    }

    public static Uso[] cargarUsosTest() {
        /*
        cantPixelesTest = 10;
        cantEstacionesTest = 8;
        cantAniosTest = cantEstacionesTest/4;
        cantUsosTest= 5;
        cantProductoresTest= 2;
        topeFosforoAnualTest= new float[cantAniosTest];*/

        Uso[] listaUsos = new Uso[Constantes.cantUsos]; //4 en el test 1
        float[] productividadUso, fosforoEstacion;
        List<Integer> siguientesUsos;

        //Siguientes usos 0 reservado para el inicio random
        siguientesUsos = new ArrayList<Integer> (Arrays.asList(1,2,3,4));
        productividadUso= new float[] {0};
        fosforoEstacion = new float[] {0f};
        listaUsos[0]= new Uso(0, 0,0, 0, productividadUso, fosforoEstacion, "Cero",siguientesUsos);

        //Cargo los usos del test

        //Siguientes usos  y productividad para las UsoTest1
        siguientesUsos = new ArrayList<Integer>(Arrays.asList(3));
        productividadUso = new float[]{10};
        fosforoEstacion = new float[] {10f};
        listaUsos[1] = new Uso(1, 1, 2, 10f, productividadUso,fosforoEstacion, "Uno",siguientesUsos);

        //Siguientes usos  y productividad para las UsoTest2
        siguientesUsos = new ArrayList<Integer>(Arrays.asList(3,4));
        productividadUso = new float[]{20, 20};
        fosforoEstacion = new float[] {10f, 10f};
        listaUsos[2] = new Uso(2, 2, 2, 20, productividadUso, fosforoEstacion,"Dos",siguientesUsos);

        //Siguientes usos  y productividad para las UsoTest3
        siguientesUsos = new ArrayList<Integer>(Arrays.asList(1));
        productividadUso = new float[]{30, 30, 30};
        fosforoEstacion = new float[] {10f,10f, 10f};
        listaUsos[3] = new Uso(3, 3, 2, 30, productividadUso, fosforoEstacion,"Tres",siguientesUsos);

        //Siguientes usos  y productividad para las UsoTest4
        siguientesUsos = new ArrayList<Integer>(Arrays.asList(4));
        productividadUso = new float[]{40, 40, 40, 40};
        fosforoEstacion = new float[] {10f,10f, 10f,10f};
        listaUsos[4] = new Uso(4, 4, 2, 40, productividadUso,fosforoEstacion,"Cuatro", siguientesUsos);
    return listaUsos;
    }

}