import java.util.Scanner;

public class Main {

    public static void main( final String[] args )
    {
        boolean imprimirConstantesTodoJunto=false,imprimirConstantesUnoAUno=false, imprimirSolucion=false;
        Solucion solucion, solucionOriginal;
        int cantidadSoluciones=0, cantidadFI=0;
        Constantes.cargarTopeFosforoAnualTest();
        Constantes.usos= Uso.cargarUsos();
        //Uso.imprimirUsos();
        //Se cargan primero los productores para que al agregar los pixeles se incerten cuales pertenecen a cada productor
        Constantes.productores=Productor.cargarProductores();
        //Productor.imprimirProductores();
        Constantes.pixeles=Pixel.cargarPixeles();
        //Pixel.imprimirPixeles();

        solucion = Main.pruebaGenerarSoluciones();
        solucion.crearArchivoMatriz();
        solucion.crearArchivoCantidadUsos();
        solucion.crearArchivoProductividadSobreAreaTotal();
        //Productor.imprimirProductores();


        /*
        solucion = Solucion.crearSolucion();
        solucionOriginal=solucion.clone();
        solucion.imprimirFuncionObjetivo();
        solucion.recalcular();
        solucion.imprimirFuncionObjetivo();
        solucion = Main.LocalSearch(solucion);
        */





        //solucion.imprimirSolucion();




        //for (int iPixel = 0; iPixel < 1; iPixel++) {
            //System.out.println("ITERADOR: "+iPixel);
            //solucion =new Solucion();
            //solucion =Solucion.crearSolucion();
            //solucion.chequearRestricciones();
            //solucion.imprimirUsosPorEstacion();
            //solucion.imprimirProductividadSobreSuperficiePorEstacion();
            //solucion.imprimirUsosDisitintosPorEstacion();
            //solucion.crearArchivoMatriz();

            //solucion.imprimirProductividadSobreSuperficiePorEstacion();
            //solucion.imprimirSolucion();

            /*System.out.println("LIMPIO PIXEL: "+ iPixel);
            solucion.limpiarPixel(iPixel);
            solucion.chequearRestricciones();
            solucion.imprimirPixel(iPixel);

            solucion.imprimirRestriccionProductividadMinimaAnual();
            solucion.imprimirRestriccionProductividadMinimaEstacion();
            solucion.imprimirRestriccionFosforoAnual();
            solucion.imprimirRestriccionUsosDistintos();
            //solucion.imprimirFosforoAnual();
            //Constantes.imprimirRestriccionesFosforoAnual();
            */
        //}
        //Productor.imprimirProductores();


        /*

        Constantes.pixeles=Pixel.cargarPixelesTest();
        Constantes.productores=Productor.cargarProductoresTest();

        if(imprimirConstantesTodoJunto) {
            System.out.printf("Imprimiendo Usos todos juntos:%n");
            Uso.imprimirUsos();
            System.out.printf("Imprimiendo Pixeles todos juntos:%n");
            Pixel.imprimirPixeles();
            System.out.printf("Imprimiendo Productores todos juntos:%n");
            Productor.imprimirProductores();
        }

        if (imprimirConstantesUnoAUno) {
            System.out.printf("Imprimiendo Usos uno a uno:%n");
            for (int i = 0; i < Constantes.usos.length; i++) {
                Constantes.usos[i].imprimirUso();
            }
            System.out.printf("Imprimiendo Pixeles uno a uno:%n");
            for (int i = 0; i < Constantes.pixeles.length; i++) {
                Constantes.pixeles[i].imprimirPixel();
            }
            System.out.printf("Imprimiendo Productores uno a uno:%n");
            for (int i = 0; i < Constantes.productores.length; i++) {
                Constantes.productores[i].imprimirProductor();
            }
        }


        //SOLUCION
        Constantes.cargarTopeFosforoAnualTest();
        Solucion solucion = new Solucion();
        solucion =new Solucion();
        System.out.printf("Construyo solucion:%n");
        if (imprimirSolucion)solucion.imprimirSolucion();
        solucion.imprimirSolucion();

        System.out.printf("Creo una solucion valida:%n");
        solucion = Solucion.crearSolucion();
        solucion.imprimirSolucion();


        for (int iPixel = 0; iPixel < Constantes.cantPixeles; iPixel++) {
            System.out.println("LIMPIO PIXEL: "+ iPixel);
            solucion.limpiarPixel(iPixel);
            solucion.chequearRestricciones();
            solucion.imprimirSolucion();
            //solucion.imprimirRestriccionProductividadMinimaAnual();
            //solucion.imprimirFosforoAnual();
            //Constantes.imprimirRestriccionesFosforoAnual();
            //solucion.imprimirRestriccionFosforoAnual();
        }
        Uso.imprimirUsos();

        //solucion.cumpleRestriccionesProductividad();
        //System.out.printf("ProductividadMinimaEstacion.mediaDesviacion: "+ solucion.restriccionProductividadMinimaEstacion.mediaDesviacion);
        //solucion.imprimirFosforoAnual();
       */
    }

    private static Solucion LocalSearch(Solucion solucion) {
        int cantidadFI=0, UDprevia=0;
        float FOprevia=0,PMEprevia=0,PMAprevia=0, FOposterior=0, Fprevia=0;
        float FOOriginal=solucion.evaluarFuncionObjetivo();
        System.out.println("Cantidad de First Improvements : ");
        Scanner reader = new Scanner(System.in);
        cantidadFI=reader.nextInt();

        System.out.println("Solucion antes de los FI:");
        System.out.println("\tFuncion Objetivo: "+solucion.evaluarFuncionObjetivo());
        solucion.imprimirFuncionObjetivo();

        for (int iSoluciones = 0; iSoluciones < cantidadFI; iSoluciones++){
            System.out.println("FirstImprovement : "+(iSoluciones+1));
            solucion= Solucion.firstImprove(solucion);
            FOprevia=solucion.evaluarFuncionObjetivo();
            Fprevia=solucion.fosforo;
            UDprevia=solucion.restriccionUsosDistintos.cantIncumplimientos;
            PMEprevia=solucion.restriccionProductividadMinimaEstacion.mediaDesviacion;
            PMAprevia=solucion.restriccionProductividadMinimaAnual.mediaDesviacion;

            //System.out.println("\tFuncion Objetivo: "+FOprevia);
            //solucion.imprimirFuncionObjetivo();
            //System.out.println();
            solucion.recalcular();
            FOposterior=solucion.evaluarFuncionObjetivo();

            if(FOprevia!=FOposterior){
                System.out.println("Encuentro discrepancia entre    FOprevia: "+FOprevia+" y FOposterior: "+FOposterior);
                if (Fprevia!=solucion.fosforo)
                    System.out.println("\tFosforo                       previa: "+Fprevia+" y posterior: "+solucion.fosforo);
                if (UDprevia!=solucion.restriccionUsosDistintos.cantIncumplimientos)
                    System.out.println("\tCantidad Usos Distintos       previa: "+UDprevia+" y posterior: "+solucion.restriccionUsosDistintos.cantIncumplimientos);
                if (PMEprevia!=solucion.restriccionProductividadMinimaEstacion.mediaDesviacion)
                    System.out.println("\tMedia Productividad Estacion  previa: "+PMEprevia+" y posterior: "+solucion.restriccionProductividadMinimaEstacion.mediaDesviacion);
                if (PMAprevia!=solucion.restriccionProductividadMinimaAnual.mediaDesviacion)
                    System.out.println("\tMedia Productividad Anual     previa: "+PMAprevia+" y posterior: "+solucion.restriccionProductividadMinimaAnual.mediaDesviacion);
            }


        }


        System.out.println();
        System.out.println();
        System.out.println("Solucion antes de los FI:");
        System.out.println("\tFuncion Objetivo antes: "+FOOriginal);
        System.out.println("\tFuncion Objetivo despues: "+solucion.evaluarFuncionObjetivo());
        solucion.chequearRestricciones();
        solucion.imprimirFuncionObjetivo();


        return solucion;


    }

    private static Solucion pruebaGenerarSoluciones() {
        int cantidadSoluciones=0;
        Solucion solucion, mejorSolucion;

        System.out.println("Cantidad de soluciones a generar : ");
        Scanner reader = new Scanner(System.in);
        cantidadSoluciones=reader.nextInt();


        //Creo una primera mejor solucion
        mejorSolucion =new Solucion();
        mejorSolucion =Solucion.crearSolucion();
        mejorSolucion.chequearRestricciones();
        System.out.println("Fosforo Solucion origina: "+ mejorSolucion.fosforo);

        for (int iSoluciones = 0; iSoluciones < cantidadSoluciones-1; iSoluciones++) {
            //Creo una nueva solucion
            solucion =new Solucion();
            solucion =Solucion.crearSolucion();
            solucion.chequearRestricciones();
            System.out.println("Fosforo Solucion "+(iSoluciones+2)+" : "+solucion.fosforo);
            System.out.println("Funcion Objetivo: "+solucion.evaluarFuncionObjetivo());
            System.out.println("Area Total: "+solucion.areaTotal);
            //Me quedo con la mejor de ambas
            if (solucion.fosforo<mejorSolucion.fosforo){
                mejorSolucion=solucion;
            }
        }

        return mejorSolucion;
    }
}
