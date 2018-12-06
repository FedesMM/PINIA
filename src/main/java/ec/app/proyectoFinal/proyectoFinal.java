package ec.app.proyectoFinal;


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
            //Paso del genoma a una solucion, con valores calculados y restricciones chequeadas.
            //System.out.println(genoma.toString());
            Solucion solucion= Solucion.genomaASolucion(genoma);

            // Calculo factibilidad
            if (!solucion.esFactible()) {
                // Intento factibilizar
                solucion = solucion.factibilizar(100, 100);
            }

            float fitness = solucion.evaluarFitness();

            ind2.genome = solucion.solucionAGenoma();
            ((SimpleFitness) ind2.fitness).setFitness(state, fitness * (-1), false);
            ind2.evaluated = true;
        }
    }
}



