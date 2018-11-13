import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(final String[] args) {
        boolean imprimirConstantesTodoJunto = false, imprimirConstantesUnoAUno = false, imprimirSolucion = false;
        Solucion solucion, solucionOriginal;
        int cantidadSoluciones = 0, cantidadFI = 0;
        Constantes.usos = Uso.cargarUsos();
        //Uso.imprimirUsos();
        //Se cargan primero los productores para que al agregar los pixeles se incerten cuales pertenecen a cada productor
        Constantes.productores = Productor.cargarProductores();
        //Productor.imprimirProductores();
        //String fileName = "potreros.in";
        //Constantes.cantPixeles = 792;
        //Constantes.cantPotreros = 1602;
        //String fileName = "pixeles.in";
        //Constantes.cantPixeles = 18475;
        //Constantes.cantPotreros = 26168;
        //Constantes.pixeles = Pixel.cargarPixeles(fileName);

        Main.testInstancias();

        //Pixel.imprimirPixeles();
        //Productor.imprimirProductores();
        //Main.generarMejorSolucion();

        /*
        solucion = Main.grasp(100, true);
        solucion.imprimirFuncionObjetivo();
        solucion.imprimirSolucion();
        */

        //Main.testFirstImprovement();
        //Main.testLocalSearch();
        //Main.testGRASP();

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

    private static void testInstancias() {
        Solucion solucion;
        String nombreInstancia;
        int cantPixeles=0;
        float fosforosRepeticion[]=new float[30], usosRepeticion[]=new float[30],productividadRepeticion[]=new float[30];
        float fosforoEsperanza[]=new float[30], usosEsperanza[]=new float[30], productividadEsperanza[]=new float[30];
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(6);
        df.setMinimumFractionDigits(6);
        //Para cada instancia
        for (int iInstancia = 0; iInstancia < 30; iInstancia++) {
            nombreInstancia="./Instancias/Intancia "+(iInstancia+1)+".in";
            System.out.println(nombreInstancia+": ");
            cantPixeles=Pixel.contarLineas(nombreInstancia);
            Constantes.cantPixeles=cantPixeles;
            Constantes.cantPotreros=cantPixeles;
            Constantes.pixeles = Pixel.cargarPixeles(nombreInstancia);
            Constantes.maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.productoresActivos.size();

            //Ejecuto el grasp 30 veces para cada instancia
            for (int iRepeticion = 0; iRepeticion < 30; iRepeticion++) {
                System.out.print("\t\tRepeticion "+iRepeticion);
                //Pixel.imprimirPixeles();
                //Productor.imprimirProductores();
                solucion=Main.grasp(1000, false);
                //Cuento las lineas
                //Corro el grasp
                //System.out.println("\tCantPixeles: "+ Constantes.cantPixeles+"\tCantEstaciones: "+Constantes.cantEstaciones);

                //Guardo datos
                solucion.recalcular();
                fosforosRepeticion[iRepeticion]=solucion.evaluarFosforoModulado();
                productividadRepeticion[iRepeticion]=solucion.evaluarIncumplimientoProductividadModulado();
                usosRepeticion[iRepeticion]=solucion.evaluarIncumplimientoUsosDistintosModulado();

                fosforoEsperanza[iInstancia]+=solucion.evaluarFosforoModulado();
                productividadEsperanza[iInstancia]+=solucion.evaluarIncumplimientoProductividadModulado();
                usosEsperanza[iInstancia]+=solucion.evaluarIncumplimientoUsosDistintosModulado();

                //solucion.imprimirFuncionObjetivo();
                System.out.print("\tFosforo Modulado: "+df.format(fosforosRepeticion[iRepeticion]));
                System.out.print("\tProductividad Modulada: "+ df.format(productividadRepeticion[iRepeticion]));
                System.out.println("\tUsos Distintos Modulado: "+ df.format(usosRepeticion[iRepeticion]));
            }
            //Tomo la media
            fosforoEsperanza[iInstancia]=fosforoEsperanza[iInstancia]/30;
            productividadEsperanza[iInstancia]=productividadEsperanza[iInstancia]/30;
            usosEsperanza[iInstancia]=usosEsperanza[iInstancia]/30;
            System.out.print("\tMedia de Fosforo Modulado: "+df.format(fosforoEsperanza[iInstancia]));
            System.out.print("\tMedia de Productividad Modulada: "+ df.format(productividadEsperanza[iInstancia]));
            System.out.println("\tMedia de Usos Distintos Modulado: "+ df.format(usosEsperanza[iInstancia])+"\n\n");

        }

        //Muestro datos

    }

    private static Solucion grasp(int maxCantidad, boolean verbose) {
        Solucion nuevaSolucion, mejorSolucion;
        //System.out.println("GRASP-Cantidad maxima de Soluciones:");
        //int maxCantidad = 0;
        //Scanner reader = new Scanner(System.in);
        //maxCantidad = reader.nextInt();

        mejorSolucion = Main.LocalSearch(Solucion.crearSolucion());
        if (verbose) System.out.println("GRASP-Solucion Original: ");
        mejorSolucion.evaluarFuncionObjetivo();

        for (int iSoluciones = 0; iSoluciones < maxCantidad; iSoluciones++) {
            if (verbose)System.out.println("GRASP-Iteracion: " + iSoluciones);
            nuevaSolucion=Solucion.crearSolucion();
            nuevaSolucion=Main.LocalSearch(nuevaSolucion);

            if (nuevaSolucion.evaluarFuncionObjetivo() < mejorSolucion.evaluarFuncionObjetivo()) {
                if (verbose)System.out.println("GRASP-Actualizo el mejor.");
                if (verbose)System.out.println("GRASP-Mejor Solucion previa: "+ mejorSolucion.evaluarFuncionObjetivo());

                mejorSolucion = nuevaSolucion.clone();
                if (verbose)System.out.println("GRASP-Mejor Solucion actual: "+mejorSolucion.evaluarFuncionObjetivo());
            }else{
                if (verbose)System.out.println("GRASP-Conservo el mejor anterior ");
                if (verbose)System.out.println("GRASP-Mejor Solucion previa: "+ mejorSolucion.evaluarFuncionObjetivo());
            }
            if (verbose)System.out.println();
            if (verbose)System.out.println();
        }

        if (verbose)System.out.println("GRASP-Solucion Final: ");
        return mejorSolucion;

    }


    private static Solucion LocalSearch(Solucion solucion) {
        boolean huboMejora = true;
        int maxCantidadFI = Constantes.cantPixeles, UDprevia = 0;
        float pesoFosforo = 1, pesoProductividad = 1, pesoCantUsos = 1;
        Solucion solucionOriginal = solucion.clone();
        //System.out.println("\tLS-Solucion Original:");
        //solucion.imprimirFuncionObjetivo();

        //Hasta llegar a la cantidad maxima de iteraciones o no tener mejora
        for (int iSoluciones = 0; (iSoluciones < maxCantidadFI) && huboMejora; iSoluciones++) {
            //System.out.println("\tLS-Iteracion: " + (iSoluciones + 1));
            //Busco una mejora
            solucion = Solucion.firstImprove(solucion, pesoFosforo, pesoProductividad, pesoCantUsos);
            //Comparo valores de la mejora
            if (solucionOriginal.evaluarFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos)
                    > solucion.evaluarFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos)) {
                //Obtube mejora
                //Muestro la mejora y los pesos
                //solucionOriginal.imprimirFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos);
                //solucion.imprimirFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos);
                //System.out.println();
                //Actualizo mi solucion original a la acutal
                solucionOriginal=solucion.clone();
                //Actualizo pesos
                pesoFosforo = Solucion.actualizarPesoFosforo(solucionOriginal, solucion, pesoFosforo);
                pesoProductividad = Solucion.actualizarPesoProduccion(solucionOriginal, solucion, pesoProductividad);
                pesoCantUsos = Solucion.actualizarPesoCantUsos(solucionOriginal, solucion, pesoCantUsos);


            } else {
                //No obtuve FirstImprovement
                huboMejora = false;
            }
        }

        /*
        System.out.println();
        System.out.println();
        System.out.println("Solucion antes de los LS:");
        System.out.println("\tSolucion original (con pesos): " + solucionOriginal.evaluarFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos));
        System.out.println("\tSolucion original (sin pesos): " + solucionOriginal.evaluarFuncionObjetivo());
        System.out.println("\tSolucion final (con pesos): " + solucion.evaluarFuncionObjetivo(pesoFosforo, pesoProductividad, pesoCantUsos));
        System.out.println("\tSolucion final (sin pesos): " + solucion.evaluarFuncionObjetivo());
        solucion.chequearRestricciones();
        solucion.imprimirFuncionObjetivo();
        */

        return solucion;


    }

    private static void generarMejorSolucion() {
        int cantidadSoluciones = 0, datos;
        String fileName;
        Solucion solucion, mejorSolucion;
        Scanner reader = new Scanner(System.in);
        System.out.println("Definir semilla: ");
        Constantes.uniforme=  new Random(reader.nextInt());

        System.out.println("Cantidad de soluciones a generar : ");
        cantidadSoluciones = reader.nextInt();

        System.out.println("1 para potreros, 2 para pixeles: ");
        datos = reader.nextInt();

        if (datos%2==0) {
            fileName = "pixeles.in";
            Constantes.cantPixeles = 18475;
            Constantes.cantPotreros = 26168;
        }else{
            fileName = "potreros.in";
            Constantes.cantPixeles = 792;
            Constantes.cantPotreros = 1602;
        }
        Constantes.pixeles = Pixel.cargarPixeles(fileName);

        solucion=Main.grasp(cantidadSoluciones, true);
        solucion.crearArchivoMatriz();
        solucion.crearArchivoMatrizNombreUsoExtendido();
        solucion.crearArchivoCantidadUsos();
        solucion.crearArchivoProductividadSobreAreaTotal();
        solucion.crearArchivoFosforoSobreAreaTotal();
        System.out.println("Mejor solucion:");
        System.out.println("\tFosforo: " + solucion.fosforo);
        solucion.imprimirRestriccionProductividadMinimaEstacion();
        solucion.imprimirRestriccionUsosDistintos();
    }

    private static void testFirstImprovement() {
        Solucion solucionOriginal, solucionNueva;
        solucionOriginal = Solucion.crearSolucion();
        System.out.println("TEST FI\nORIGINAL:");
        solucionOriginal.imprimirFuncionObjetivo();
        System.out.println();

        for (int i = 0; i < 10000; i++) {
            System.out.println("Intento FI: " + i);
            solucionNueva = Solucion.firstImprove(solucionOriginal, 1, 1, 1);
            if (solucionOriginal.evaluarFuncionObjetivo() < solucionNueva.evaluarFuncionObjetivo()) {
                System.out.println("\tFallo FI\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            } else {
                System.out.println("\tExito FI\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            }
        }
    }

    private static void testLocalSearch() {
        Solucion solucionOriginal, solucionNueva;
        solucionOriginal = Solucion.crearSolucion();
        System.out.println("TEST LS\nORIGINAL:");
        solucionOriginal.imprimirFuncionObjetivo();
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.println("Intento LS: " + i);
            solucionNueva = Main.LocalSearch(solucionOriginal);
            if (solucionOriginal.evaluarFuncionObjetivo() < solucionNueva.evaluarFuncionObjetivo()) {
                System.out.println("\tFallo LS\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            } else {
                System.out.println("\tExito LS\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            }
        }


    }

    private static void testGRASP() {
        Solucion solucionOriginal, solucionNueva;
        solucionOriginal = Solucion.crearSolucion();
        System.out.println("TEST GRASP\nORIGINAL:");
        solucionOriginal.imprimirFuncionObjetivo();
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.println("Intento GRASP: " + i);
            solucionNueva = Main.grasp(10, true);
            if (solucionOriginal.evaluarFuncionObjetivo() < solucionNueva.evaluarFuncionObjetivo()) {
                System.out.println("\tFallo GRASP\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            } else {
                System.out.println("\tExito GRASP\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            }
        }
    }


}

