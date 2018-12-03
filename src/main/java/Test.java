
import ec.EvolutionState;
import ec.vector.IntegerMatrixIndividual;
import ec.vector.MatrixSpecies;

import java.util.Random;

public class Test {

    public static void main(String [ ] args) {
        int cantPixeles = 5;
        int cantEstaciones = MatrixSpecies.CANT_ESTACIONES;
        int idMinPixel = 1;
        int idMaxPixel = 14;
        IntegerMatrixIndividual ind1 = new IntegerMatrixIndividual();
        ind1.genome = new int[cantEstaciones * cantPixeles];
        IntegerMatrixIndividual ind2 = new IntegerMatrixIndividual();
        ind2.genome = new int[cantEstaciones * cantPixeles];
        Random r = new Random();
        for (int i = 0; i < ind1.genome.length-1; i++) {
            ind1.genome[i] = r.nextInt(idMaxPixel-idMinPixel)+idMinPixel;
            ind2.genome[i] = r.nextInt(idMaxPixel-idMinPixel)+idMinPixel;
        }
        System.out.println(ind1.toString());
        System.out.println(ind2.toString());

        ind1.defaultCrossover(new EvolutionState(), 0, ind2);

        System.out.println(ind1.toString());
        System.out.println(ind2.toString());
    }
}
