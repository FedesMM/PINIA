package ec.app.proyectoFinal;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainTest {

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
        //MainTest.generarMejorSolucion();

        //MainTest.testInstancias();
        //MainTest.testFactibilizarProductividad();
        MainTest.testCrearSolucionFactible();

    }

    private static void testCrearSolucionFactible() {
        Solucion solucion, solucionCantUsos, solucionProductividad, solucionFactible;
        String nombreInstancia;
        int cantPixeles, repeticiones=100, profundidad=100;
        int factible=0, cumpleRCantUsos=0, cumpleRProductividad=0;
        int factible2=0, cumpleRCantUsos2=0, cumpleRProductividad2=0;
        int factible3=0, cumpleRCantUsos3=0, cumpleRProductividad3=0;
        int factible4=0, cumpleRCantUsos4=0, cumpleRProductividad4=0;


        int instancia=29;
        System.out.println("testFactibilizarCantUsos");
        for (int iInstancia =0; iInstancia <= instancia; iInstancia++) {
            int maxSoluciones=1000;
            factible=0; cumpleRCantUsos=0; cumpleRProductividad=0;
            factible2=0; cumpleRCantUsos2=0; cumpleRProductividad2=0;
            factible3=0; cumpleRCantUsos3=0; cumpleRProductividad3=0;
            factible4=0; cumpleRCantUsos4=0; cumpleRProductividad4=0;

            nombreInstancia = "./Instancias/Intancia " + (iInstancia + 1) + ".in";
            cantPixeles=Pixel.contarLineas(nombreInstancia);
            System.out.println(nombreInstancia+"\tCantPixeles :"+cantPixeles+ "\t CantSoluciones: "+maxSoluciones);
            //System.out.print("\tSolucion:                    ");
            for (int iSoluciones = 0; iSoluciones< maxSoluciones; iSoluciones++) {
                System.out.print("\rSolucion: "+iSoluciones);
                Constantes.productores = Productor.cargarProductores();
                Constantes.cantPixeles=cantPixeles;
                Constantes.cantPotreros=cantPixeles;
                Constantes.pixeles = Pixel.cargarPixeles(nombreInstancia);
                Constantes.maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.productoresActivos.size();

                //System.out.println("Solucion: "+iSoluciones);
                solucion=Solucion.crearSolucionFactible();
                solucion.recalcular();
                if(solucion.esFactible()) { factible++; }
                if(solucion.restriccionUsosDistintos.cumpleRestriccion) { cumpleRCantUsos++; }
                if(solucion.restriccionProductividadMinimaEstacion.cumpleRestriccion) { cumpleRProductividad++; }

                solucionCantUsos=solucion.clone();
                solucionCantUsos.factibilizarCantUsos();
                solucionCantUsos.recalcular();
                if(solucionCantUsos.esFactible()) { factible2++; }
                if(solucionCantUsos.restriccionUsosDistintos.cumpleRestriccion) { cumpleRCantUsos2++; }
                if(solucionCantUsos.restriccionProductividadMinimaEstacion.cumpleRestriccion) { cumpleRProductividad2++; }

                solucionProductividad=solucion.clone();
                solucionProductividad.factibilizarProductividad();
                solucionProductividad.recalcular();
                if(solucionProductividad.esFactible()) { factible3++; }
                if(solucionProductividad.restriccionUsosDistintos.cumpleRestriccion) { cumpleRCantUsos3++; }
                if(solucionProductividad.restriccionProductividadMinimaEstacion.cumpleRestriccion) { cumpleRProductividad3++; }

                solucionFactible=solucion.clone();
                solucionFactible.recalcular();
                solucionFactible.factibilizar(repeticiones, profundidad);
                solucionFactible.recalcular();
                if(solucionFactible.esFactible()) { factible4++; }
                if(solucionFactible.restriccionUsosDistintos.cumpleRestriccion) { cumpleRCantUsos4++; }
                if(solucionFactible.restriccionProductividadMinimaEstacion.cumpleRestriccion) { cumpleRProductividad4++; }

            }
            System.out.print("\n\tGENERADA: ");
            System.out.print("\t\t\tFACTIBLE: "+factible);
            System.out.print("\tRest Cant Usos: "+cumpleRCantUsos);
            System.out.println("\t\tRest Productividad: "+cumpleRProductividad);
            System.out.print("\tFacCantUsos: ");
            System.out.print("\t\tFACTIBLE: "+factible2);
            System.out.print("\tRest Cant Usos: "+cumpleRCantUsos2);
            System.out.println("\t\tRest Productividad: "+cumpleRProductividad2);
            System.out.print("\tFacProduccion: ");
            System.out.print("\t\tFACTIBLE: "+factible3);
            System.out.print("\tRest Cant Usos: "+cumpleRCantUsos3);
            System.out.println("\t\tRest Productividad: "+cumpleRProductividad3);
            System.out.print("\tFactibilizar: ");
            System.out.print("\t\tFACTIBLE: "+factible4);
            System.out.print("\tRest Cant Usos: "+cumpleRCantUsos4);
            System.out.println("\t\tRest Productividad: "+cumpleRProductividad4);


            //solucion.imprimirMatriz();
            System.out.println("\n---------------------------------------------------------------------------------\n");
        }
    }

    private static void testFactibilizarCantUsos() {
        Solucion solucion, nuevaSolucion;
        String nombreInstancia;
        int cantPixeles;
        int instancia=0;
        System.out.println("testFactibilizarCantUsos");
        for (int iInstancia =instancia; iInstancia <= instancia; iInstancia++) {
           int maxSoluciones=1000;
            for (int iSoluciones = 0; iSoluciones< maxSoluciones; iSoluciones++) {
                Constantes.productores = Productor.cargarProductores();
                nombreInstancia = "./Instancias/Intancia " + (iInstancia + 1) + ".in";
                cantPixeles=Pixel.contarLineas(nombreInstancia);
                System.out.println(nombreInstancia+"\tCantPixeles:"+cantPixeles);
                Constantes.cantPixeles=cantPixeles;
                Constantes.cantPotreros=cantPixeles;
                Constantes.pixeles = Pixel.cargarPixeles(nombreInstancia);
                Constantes.maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.productoresActivos.size();

                System.out.println("Solucion: "+iSoluciones);
                solucion=Solucion.crearSolucion();
                for (;solucion.restriccionUsosDistintos.cumpleRestriccion;) {
                    solucion=Solucion.crearSolucion();
                    solucion.recalcular();
                    solucion.imprimirFuncionObjetivo();

                }
                nuevaSolucion= solucion.clone();
                int maxIter=1000;
                for (int i = 0; (i <maxIter&& !nuevaSolucion.restriccionUsosDistintos.cumpleRestriccion); i++) {
                    nuevaSolucion= solucion.clone();
                    System.out.println("Factibilizacion  :"+i);
                    nuevaSolucion.factibilizarCantUsos();
                    nuevaSolucion.recalcular();
                    if (nuevaSolucion.restriccionUsosDistintos.cumpleRestriccion){
                        System.out.println("FACTIBILIZADO EN LA ITERACION  "+i);
                        nuevaSolucion.imprimirUsosDisitintosPorEstacion();
                        nuevaSolucion.evaluarFuncionObjetivo();
                    }else if (i==maxIter-1) {
                        System.out.println("NO FACTIBILZADO "+i);
                        nuevaSolucion.imprimirUsosDisitintosPorEstacion();
                    }else{
//                    System.out.println("NO FACTIBILZADO "+i);
//                    nuevaSolucion.imprimirUsosDisitintosPorEstacion();
                    }
                }
                System.out.println("\n");
            }

            //solucion.imprimirMatriz();
            System.out.println("\n\n");
        }

    }

    private static void testFactibilizarProductividad() {
        Solucion solucion, nuevaSolucion;
        String nombreInstancia;
        int cantPixeles;
        int instancia=14;
        System.out.println("testFactibilizarProductividad");
        for (int iInstancia =instancia; iInstancia <= instancia; iInstancia++) {
            int maxSoluciones=1000;
            nombreInstancia = "./Instancias/Intancia " + (iInstancia + 1) + ".in";
            cantPixeles=Pixel.contarLineas(nombreInstancia);
            System.out.println(nombreInstancia+"\tCantPixeles:"+cantPixeles);
            Constantes.cantPixeles=cantPixeles;
            Constantes.cantPotreros=cantPixeles;
            for (int iSoluciones = 0; iSoluciones< maxSoluciones; iSoluciones++) {
                Constantes.productores = Productor.cargarProductores();
                Constantes.pixeles = Pixel.cargarPixeles(nombreInstancia);
                Constantes.maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.productoresActivos.size();
                //Pixel.imprimirPixeles();
                System.out.println("SOLUCION: "+iSoluciones+"\nORIGINAL:");
                solucion=Solucion.crearSolucion();
                for (;solucion.restriccionProductividadMinimaEstacion.cumpleRestriccion || solucion.restriccionUsosDistintos.cumpleRestriccion;) {
                    solucion=Solucion.crearSolucion();
                    solucion.recalcular();
                }
                solucion.imprimirFuncionObjetivo();

                nuevaSolucion= solucion.clone();
                int maxBusquedas=100, maxMejoras=100;
                for (int i = 0; (i <maxBusquedas && !nuevaSolucion.esFactible()); i++) {
                    nuevaSolucion= solucion.clone();
                    //System.out.println("Factibilizacion  :"+i);
                    for (int j = 0; (j < maxMejoras &&  !nuevaSolucion.esFactible()) ; j++) {
                        nuevaSolucion.factibilizarProductividad();
                        nuevaSolucion.recalcular();
                    }
                    if (nuevaSolucion.esFactible()){
                        System.out.println("FACTIBILIZADO EN LA ITERACION  "+i);
                        //nuevaSolucion.imprimirProductividadSobreSuperficiePorEstacion();
                        nuevaSolucion.imprimirFuncionObjetivo();
                        if(!nuevaSolucion.restriccionUsosDistintos.cumpleRestriccion){
                            nuevaSolucion.imprimirRestriccionUsosDistintosExpandida();
                            nuevaSolucion.imprimirMatriz();
                        }
                    }else if (i==maxBusquedas-1) {
                        System.out.println("NO FACTIBILIZADO "+i);
                        //nuevaSolucion.imprimirProductividadSobreSuperficiePorEstacion();
                        nuevaSolucion.imprimirFuncionObjetivo();
                    }else{
//                    System.out.println("NO FACTIBILIZADO "+i);
//                    nuevaSolucion.imprimirUsosDisitintosPorEstacion();
                    }
                }
                System.out.println("\n");
            }

            //solucion.imprimirMatriz();
            System.out.println("\n-------------------------------------------------------------------------------------------\n");
        }

    }

    private static void testInstancias() {
        Solucion solucion;
        String nombreInstancia;
        int cantPixeles=0;
        float fosforosRepeticion[]=new float[30], usosRepeticion[]=new float[30],productividadRepeticion[]=new float[30];
        float fosforoEsperanza[]=new float[30], usosEsperanza[]=new float[30], productividadEsperanza[]=new float[30];
        float cantIncumplimientosProductividadRepeticion[]=new float [30], cantIncumplimientosProductividadEsperanza[]=new float[30];
        float cantIncumplimientosUsosRepeticion[]=new float [30], cantIncumplimientosUsosEsperanza[]=new float[30];;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(6);
        df.setMinimumFractionDigits(6);
        //Para cada instancia
        for (int iInstancia = 0; iInstancia < 30; iInstancia++) {
            nombreInstancia="./Instancias/Intancia "+(iInstancia+1)+".in";
            cantPixeles=Pixel.contarLineas(nombreInstancia);
            Constantes.cantPixeles=cantPixeles;
            Constantes.cantPotreros=cantPixeles;
            Constantes.pixeles = Pixel.cargarPixeles(nombreInstancia);
            Constantes.maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.productoresActivos.size();
            System.out.println(nombreInstancia+"\tCantPixeles:"+cantPixeles);
            //Ejecuto el grasp 30 veces para cada instancia
            for (int iRepeticion = 0; iRepeticion < 10; iRepeticion++) {
                System.out.print("\t\tRepeticion "+iRepeticion);
                //Pixel.imprimirPixeles();
                //Productor.imprimirProductores();
                solucion=MainTest.grasp(1000, false);
                //Cuento las lineas
                //Corro el grasp
                //System.out.println("\tCantPixeles: "+ Constantes.cantPixeles+"\tCantEstaciones: "+Constantes.cantEstaciones);

                //Guardo datos
                solucion.recalcular();
                fosforosRepeticion[iRepeticion]=solucion.evaluarFosforoModulado();
                productividadRepeticion[iRepeticion]=solucion.evaluarIncumplimientoProductividadModulado();
                usosRepeticion[iRepeticion]=solucion.evaluarIncumplimientoUsosDistintosModulado();
                cantIncumplimientosUsosRepeticion[iRepeticion]=solucion.restriccionUsosDistintos.cantIncumplimientos;
                cantIncumplimientosProductividadRepeticion[iRepeticion]=solucion.restriccionProductividadMinimaEstacion.cantIncumplimientos;


                fosforoEsperanza[iInstancia]+=solucion.evaluarFosforoModulado();
                productividadEsperanza[iInstancia]+=solucion.evaluarIncumplimientoProductividadModulado();
                usosEsperanza[iInstancia]+=solucion.evaluarIncumplimientoUsosDistintosModulado();
                cantIncumplimientosUsosRepeticion[iRepeticion]+=solucion.restriccionUsosDistintos.cantIncumplimientos;
                cantIncumplimientosProductividadEsperanza[iInstancia]+=solucion.restriccionProductividadMinimaEstacion.cantIncumplimientos;

                //solucion.imprimirFuncionObjetivo();
                System.out.print("\tFosforo Modulado: "+df.format(fosforosRepeticion[iRepeticion]));
                System.out.print("\tProductividad Modulada: "+ df.format(productividadRepeticion[iRepeticion]));
                System.out.print("\tProductividad CantIncumplimientos: "+cantIncumplimientosProductividadRepeticion[iRepeticion]);
                System.out.print("/"+ Constantes.productoresActivos.size()*Constantes.cantEstaciones);
                System.out.print("\tUsos Distintos Modulado: "+ df.format(usosRepeticion[iRepeticion]));
                System.out.print("\tUsos Distintos CantIncumplimientos: "+cantIncumplimientosUsosRepeticion[iRepeticion]);
                System.out.println("/"+ Constantes.productoresActivos.size()*Constantes.cantEstaciones);
            }
            //Tomo la media
            fosforoEsperanza[iInstancia]=fosforoEsperanza[iInstancia]/30;
            productividadEsperanza[iInstancia]=productividadEsperanza[iInstancia]/30;
            usosEsperanza[iInstancia]=usosEsperanza[iInstancia]/30;
            cantIncumplimientosProductividadEsperanza[iInstancia]=cantIncumplimientosProductividadEsperanza[iInstancia]/30;
            cantIncumplimientosUsosEsperanza[iInstancia]=cantIncumplimientosUsosEsperanza[iInstancia]/30;

            System.out.print("\tMedia de Fosforo Modulado: "+df.format(fosforoEsperanza[iInstancia]));
            System.out.print("\tMedia de Productividad Modulada: "+ df.format(productividadEsperanza[iInstancia]));
            System.out.print("\tMedia Productividad CantIncumplimientos: "+cantIncumplimientosProductividadEsperanza[iInstancia]);
            System.out.print("/"+ Constantes.productoresActivos.size()*Constantes.cantEstaciones);
            System.out.print("\tMedia de Usos Distintos Modulado: "+ df.format(usosEsperanza[iInstancia])+"\n\n");
            System.out.print("\tMedia Usos Distintos CantIncumplimientos: "+cantIncumplimientosUsosEsperanza[iInstancia]);
            System.out.println("/"+ Constantes.productoresActivos.size()*Constantes.cantEstaciones);

        }

        //Muestro datos

    }

    private static Solucion grasp(int maxCantidad, boolean verbose) {
        Solucion nuevaSolucion, mejorSolucion;
        //System.out.println("GRASP-Cantidad maxima de Soluciones:");
        //int maxCantidad = 0;
        //Scanner reader = new Scanner(System.in);
        //maxCantidad = reader.nextInt();

        mejorSolucion = MainTest.LocalSearch(Solucion.crearSolucion());
        if (verbose) System.out.println("GRASP-Solucion Original: ");
        mejorSolucion.evaluarFuncionObjetivo();

        for (int iSoluciones = 0; iSoluciones < maxCantidad; iSoluciones++) {
            if (verbose)System.out.println("GRASP-Iteracion: " + iSoluciones);
            nuevaSolucion=Solucion.crearSolucion();
            nuevaSolucion=MainTest.LocalSearch(nuevaSolucion);

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

        solucion=MainTest.grasp(cantidadSoluciones, true);
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
            solucionNueva = MainTest.LocalSearch(solucionOriginal);
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
            solucionNueva = MainTest.grasp(10, true);
            if (solucionOriginal.evaluarFuncionObjetivo() < solucionNueva.evaluarFuncionObjetivo()) {
                System.out.println("\tFallo GRASP\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            } else {
                System.out.println("\tExito GRASP\tFOOriginal=" + solucionOriginal.evaluarFuncionObjetivo() + "\t FONueva=" + solucionNueva.evaluarFuncionObjetivo());
            }
        }
    }


}


