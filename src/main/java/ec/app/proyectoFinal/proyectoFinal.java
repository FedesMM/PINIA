package ec.app.proyectoFinal;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.text.DecimalFormat;
import java.util.*;

import ec.*;
import ec.simple.*;
import ec.vector.*;

import java.util.ArrayList;
import java.util.Random;

import static jdk.nashorn.internal.objects.NativeMath.min;

public class proyectoFinal extends Problem implements SimpleProblemForm
{
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum) {

        if (ind.evaluated)
            return;

        if (!(ind instanceof IntegerMatrixIndividual)) {

            state.output.fatal("Error. No es un vector de enteros!", null);

        } else {
            //consigo el individuo y lo casteo como la clase con la que lo voy a trabajar
            IntegerMatrixIndividual ind2 = (IntegerMatrixIndividual) ind;
            //Consigo la especie del individuo
            IntegerMatrixSpecies t_spe = (IntegerMatrixSpecies) ind2.species;
            //Creo un arreglo en el que tener el genoma del individuo
            int[] genoma = ((IntegerMatrixIndividual) ind).genome;

            //Corroboro que el fitnes de mi individuo extienda de SimpleFitness
            if (!(ind2.fitness instanceof SimpleFitness)) {
                state.output.fatal("Error. No es un SimpleFitness", null);
            }
            //Si llegue hasta aca el individuo es correcto

            int usoACalcular, estacionesDeEsteUso;
            double fosforo=0;
            float [][] productivdadProductores = new float [Constantes.cantProductores][Constantes.cantEstaciones];
            float [][] fosforoProductores = new float [Constantes.cantProductores][Constantes.cantEstaciones];
            RestriccionProductividadProductores restriccionProductividadMinimaEstacion = new RestriccionProductividadProductores();
            RestriccionUsosDistintos restriccionUsosDistintos = new RestriccionUsosDistintos();
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                ArrayList<ArrayList<Integer>> productoresEnUnaEstacion= new ArrayList<>();
                for (Integer iProductor:Constantes.productoresActivos) {
                    ArrayList<Integer> usosDeUnProductor= new ArrayList<>();
                    productoresEnUnaEstacion.add(usosDeUnProductor);
                }
                restriccionUsosDistintos.usosPorEstacionParaCadaProductor.add(productoresEnUnaEstacion);
            }

            //Calculo valores auxiliares
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                ArrayList<Integer> usosDelProductor= new ArrayList<>();
                for (int iPixel = 0; iPixel < Constantes.cantPixeles; iPixel++) {
                    //100*usoACargar+estacionDelUso
                    int indiceGen=iPixel*Constantes.cantEstaciones+iEstacion;
                    usoACalcular = genoma[indiceGen] / 100;
                    estacionesDeEsteUso = genoma[indiceGen] % 100;

                    //Actualizo lo que aporta el uso al fosforo total en esta estacion
                    fosforo += (Constantes.usos[usoACalcular].fosforoEstacion[estacionesDeEsteUso]*Constantes.pixeles[iPixel].superficie);
                    //Actualizo la productividad del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
                    productivdadProductores[Constantes.pixeles[iPixel].productor][iEstacion] +=
                            Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACalcular].productividad[estacionesDeEsteUso];
                    //Actualizo el fosforo del productor due;o del pixel segun la superficie del pixel y la productividad del uso para la estacion del uso
                    fosforoProductores[Constantes.pixeles[iPixel].productor][iEstacion] +=
                            Constantes.pixeles[iPixel].superficie * Constantes.usos[usoACalcular].fosforoEstacion[estacionesDeEsteUso];
                    //Actualizo la cantidad de usos
                    ArrayList<Integer> listaUsos = restriccionUsosDistintos.usosPorEstacionParaCadaProductor.get(iEstacion).get(Constantes.pixeles[iPixel].productor);
                    if(!listaUsos.contains(usoACalcular)) {
                        listaUsos.add(usoACalcular);
                    }
                    //TODO Testear
                    restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[usoACalcular][iEstacion][Constantes.pixeles[iPixel].productor]++;

                }

            }
            //Calculo factibilidad
            //Chequeo Restriccion de Productividad
            //Cheqeuo Restriccion de Uso

            int cantUsosDistintos;
            restriccionUsosDistintos.cumpleRestriccion=true;
            //TODO Seguir de aca:
            for (int iEstacion = 0; iEstacion < Constantes.cantEstaciones; iEstacion++) {
                //Recorro Cada Productor
                for (int iProductor:Constantes.productoresActivos) {
                    //for (int iProductor = 0; iProductor < Constantes.cantProductores ; iProductor++) {
                    cantUsosDistintos = 0;
                    //Calculo cuantos usos distintos tuvo
                    for (int iUso = 0;  iUso< Constantes.cantUsos; iUso++) {
                        if (restriccionUsosDistintos.cantUsosPorEstacionParaCadaProductor[iUso][iEstacion][iProductor] > 0){
                            cantUsosDistintos++;
                        }
                    }
                    if ((cantUsosDistintos < Constantes.productores[iProductor].getMinCantUsos()) ||
                            (cantUsosDistintos > Constantes.maximaCantidadUsos)){
                        restriccionUsosDistintos.cumpleRestriccion=false;
                        restriccionUsosDistintos.cantIncumplimientos++;
                    }
                }
            }
            restriccionUsosDistintos.incumplimientoRelativo=(float) restriccionUsosDistintos.cantIncumplimientos/(float)(Constantes.cantEstaciones*Constantes.cantProductores);



            //TODO BORRAR EVALUACION DESDE ACA
            //Corroboro que el tamaño de los arreglos fitness
            if (tareas.length == genoma.length && tareas.length > empleados.length) {
                //System.out.println("INICIA INDIVIDUO");

                float[] horasDeTrabajoParaCadaEmpleado = new float[t_spe.getCantEmpleados()];
                float[] diasDeTrabajoParaCadaEmpleado = new float[t_spe.getCantEmpleados()];

                //Inicializa las horas trabajadas en cero
                for (int i = 0; i < horasDeTrabajoParaCadaEmpleado.length; i++) {
                    horasDeTrabajoParaCadaEmpleado[i] = (float) 0.0;
                    diasDeTrabajoParaCadaEmpleado[i] = (float) 0.0;
                }
                //Recorro cada Calcula las horas de cada empleado
                for (int i = 0; i < genoma.length; i++) {
                    //Consigo la tarea con la que voy a trabajar
                    Tarea tarea = tareas[i];
                    //System.out.println("genoma["+i+"]= "+genoma[i]);
                    //Encuentro al empleado que tiene asignada esa tarea
                    Empleado empleado = empleados[genoma[i]];
                    //Sumo las horas que le va a tomar al empleado (segun su habilidad) completar el esfuerzo de la tarea
                    //System.out.println("Tarea: "+tarea.toString());
                    //System.out.println("Empleado: "+empleado.toString());
                    float horasTrabajadasPorTarea = tarea.getEsfuerzo() / ((float) 0.5 + empleado.getHabilidad());
                    horasDeTrabajoParaCadaEmpleado[genoma[i]] += horasTrabajadasPorTarea;
                }

                //Con todas las horas cargadas en todos los empleados calculo los dias que trabaja cada uno y cuanto nos cuesta
                for (int i = 0; i < diasDeTrabajoParaCadaEmpleado.length; i++) {
                    Empleado empleado = empleados[i];
                    diasDeTrabajoParaCadaEmpleado[i] = ((int) Math.ceil(horasDeTrabajoParaCadaEmpleado[i] / empleado.getDedicacion()));
                    costoProyecto += diasDeTrabajoParaCadaEmpleado[i] * empleado.getSueldo();
                    //System.out.println("\t Dias trabajados por el empleado "+i+" : "+diasDeTrabajoParaCadaEmpleado[i]);
                    //Guardo la posicion del empleado que trabaje mas dias
                    if (diasDeTrabajoParaCadaEmpleado[i] > diasDeTrabajoParaCadaEmpleado[posMax]) {
                        posMax = i;
                    }
                }

                //Penalizo las soluciones no factibles con un valor que asegure sean peores que las factubles
                if(diasDeTrabajoParaCadaEmpleado[posMax]>t_spe.getF()){
                    //System.out.print("Factibilizo\t");
                    int cantidadFactibilizaciones=0;
                    boolean factible=esFactible(state,  ind2, horasDeTrabajoParaCadaEmpleado, empleados, tareas,t_spe);
                    while(cantidadFactibilizaciones<10 && !factible){
                        //System.out.println("Intento "+cantidadFactibilizaciones+" factibilizar el individuo: "+ind2.genotypeToStringForHumans());
                        factibilizar(state,  ind2, horasDeTrabajoParaCadaEmpleado, empleados, tareas,t_spe);
                        factible=esFactible(state,  ind2, horasDeTrabajoParaCadaEmpleado, empleados, tareas,t_spe);
                        cantidadFactibilizaciones++;
                    }
                    if(!factible){
                        //System.out.print("Se penaliza   \t|\t El individuo demora "+diasDeTrabajoParaCadaEmpleado[posMax]+"/"+t_spe.getF() +" se agrega costo: "+(t_spe.getHorasTotal()*t_spe.getMaxSueldoReal())+ " a su costo: "+ costoProyecto);
                        costoProyecto+=t_spe.getHorasTotal()*t_spe.getMaxSueldoReal()*(diasDeTrabajoParaCadaEmpleado[posMax]/t_spe.getF());
                        //System.out.println(" total: "+costoProyecto);
                    }else{
                        //System.out.println("FUNCIONO el Factibilizo");
                    }



                } else{
                    //System.out.println("NO SE PENALIZA\t|\t El individuo demora "+diasDeTrabajoParaCadaEmpleado[posMax]+"/"+t_spe.getF()+" y tiene costo: "+costoProyecto);
                }
                //System.out.println("\tAl individuo: ");



                boolean ideal = diasDeTrabajoParaCadaEmpleado[posMax] <= t_spe.getF(); // factible??
                //ideal = ideal && (((SimpleFitness) ind2.fitness).getMinFitness() > costoProyecto);
                ideal =  ideal && (t_spe.getMaxSueldoReal()*diasDeTrabajoParaCadaEmpleado[posMax]) == costoProyecto;
                //ideal = ideal || state.generation - state.getBestFitnessGeneration() > (int) state.numGenerations/10;
                //System.out.println("state.generation: " + state.generation + ",getBestFitnessGeneration(): " + state.getBestFitnessGeneration() + ", state.numGenerations: " + state.numGenerations);
                //System.out.println("ideal= "+ideal+"  horasDeTrabajoParaCadaEmpleado[posMax]"+ horasDeTrabajoParaCadaEmpleado[posMax]);
                ((SimpleFitness) ind2.fitness).setFitness(state, costoProyecto * (-1), ideal);
                ind2.evaluated = true;

                //ind.printIndividualForHumans(state,0);
                /*
                System.out.print("\t Dias trabajados: (");

                for (int i = 0; i < horasDeTrabajoParaCadaEmpleado.length; i++) {
                    System.out.print(diasDeTrabajoParaCadaEmpleado[i]+", ");
                }
                System.out.println(")");
                System.out.println("FINALIZA INDIVIDUO");
                */
            }
        }
    }

    private boolean esFactible(EvolutionState state, IntegerMatrixIndividual ind2, float[] horasDeTrabajoParaCadaEmpleado, Empleado[] empleados, Tarea[] tareas, IntegerMatrixSpecies t_spe) {
        boolean factible=true;
        int iEmpleados=0;
        //recorro hasta ver todos los usuarios o encontrar que no es factible
        while(iEmpleados<empleados.length && factible){
            if(Math.ceil(horasDeTrabajoParaCadaEmpleado[iEmpleados] / empleados[iEmpleados].getDedicacion())>t_spe.getF()){
                factible=false;
                //System.out.println("\tNo factible por el empleado: "+iEmpleados);
                //System.out.println("\t\tHoras trabajadas: "+horasDeTrabajoParaCadaEmpleado[iEmpleados]+" Dedicacion diaria: "+empleados[iEmpleados].getDedicacion());
                //System.out.println("\t\tDias trabajados: "+(horasDeTrabajoParaCadaEmpleado[iEmpleados]/empleados[iEmpleados].getDedicacion())+"/"+t_spe.getF());

            }
            else{
                iEmpleados++;
            }
        }
        return factible;
    }

    private void factibilizar(EvolutionState state, IntegerMatrixIndividual ind2, float[] horasDeTrabajoParaCadaEmpleado, Empleado[] empleados, Tarea[] tareas, IntegerMatrixSpecies t_spe) {
        Random r = new Random();
        //Recorro cada empleado


        for (int iEmpleado = 0; iEmpleado < horasDeTrabajoParaCadaEmpleado.length; iEmpleado++) {
            List<Integer> tareasEmpleado=new ArrayList<Integer>();

            //Mientras este pasado de horas
            if (Math.ceil(horasDeTrabajoParaCadaEmpleado[iEmpleado]/empleados[iEmpleado].getDedicacion())>t_spe.getF()){
                //System.out.println("\tIntento corregir al empleado: "+iEmpleado+ " con dedicacion: "+empleados[iEmpleado].getDedicacion()+ " y habilidad: "+empleados[iEmpleado].getHabilidad());
                //System.out.print("\tTareas del empleado ("+(horasDeTrabajoParaCadaEmpleado[iEmpleado]/empleados[iEmpleado].getDedicacion())+"): ");
                //Consigo todas sus tareas
                for (int iTareas = 0; iTareas < tareas.length; iTareas++) {
                    if (ind2.genome[iTareas]==iEmpleado){
                        tareasEmpleado.add(iTareas);
                        //System.out.print(" "+iTareas+"("+tareas[iTareas].getEsfuerzo()+")");
                    }
                }
                //System.out.println("");

                int contadorCorrecciones=0;
                while(Math.ceil(horasDeTrabajoParaCadaEmpleado[iEmpleado]/empleados[iEmpleado].getDedicacion())>t_spe.getF()
                        && contadorCorrecciones<tareas.length
                        && tareasEmpleado.size()>0
                        ){
                    //Elijo tarea al azar
                    int indiceTareaAleatoria = r.nextInt(tareasEmpleado.size());
                    int tareaAleatoria= tareasEmpleado.get(indiceTareaAleatoria);
                    //System.out.print("\t\tIntento reasignar la tarea: "+tareaAleatoria+" ("+tareas[tareaAleatoria].getEsfuerzo()+") pruebo con:");
                    //Actualizo sus horas trabajadas
                    horasDeTrabajoParaCadaEmpleado[iEmpleado]-=tareas[tareaAleatoria].getEsfuerzo()/(0.5+empleados[iEmpleado].getHabilidad());
                    //Elijo un empleado nuevo
                    int iNuevoEmpleado=(iEmpleado+1)%empleados.length;
                    float limiteHoras=empleados[iNuevoEmpleado].getDedicacion()*t_spe.getF();
                    float holgura=limiteHoras-horasDeTrabajoParaCadaEmpleado[iNuevoEmpleado];
                    boolean empleadoDisponible=holgura>= tareas[tareaAleatoria].getEsfuerzo();
                    while(iNuevoEmpleado!=iEmpleado && !empleadoDisponible){
                        limiteHoras=empleados[iNuevoEmpleado].getDedicacion()*t_spe.getF();
                        holgura=limiteHoras-horasDeTrabajoParaCadaEmpleado[iNuevoEmpleado];
                        //System.out.print(" "+iNuevoEmpleado+ "("+holgura+")");
                        //Paso al siguiente empleado
                        iNuevoEmpleado= (iNuevoEmpleado+1)%empleados.length;
                        empleadoDisponible=holgura>= tareas[tareaAleatoria].getEsfuerzo();

                    }
                    //System.out.print("\n");
                    //Le asigno la tarea
                    //System.out.println("\t\tGenoma:"+ind2.genome[tareaAleatoria]);
                    ind2.genome[tareaAleatoria]=iNuevoEmpleado;
                    //System.out.println("\t\tLa asigno al empleado:"+iNuevoEmpleado);
                    //System.out.println("\t\tGenoma:"+ind2.genome[tareaAleatoria]);

                    //Actualizo sus horas trabajadas
                    horasDeTrabajoParaCadaEmpleado[iNuevoEmpleado]+=tareas[tareaAleatoria].getEsfuerzo()/(0.5+empleados[iNuevoEmpleado].getHabilidad());
                    //Retiro la lista de tarea del iEmpleado
                    tareasEmpleado.remove(indiceTareaAleatoria);
                    //
                    contadorCorrecciones++;
                }

            }
        }

        //Encontrar quienes se pasan de F
        //Repartir tareas hasta que dejen de pasarse


    }
}




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
        //Productor.imprimirProductores();Constantes.usos = Uso.cargarUsos();
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


