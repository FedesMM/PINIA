package ec.vector;

import Utils.MatrixUtils;
import Utils.Range;
import ec.*;
import ec.app.p1e1.Empleado;
import ec.app.p1e1.Tarea;
import ec.util.*;
import java.io.*;
import java.util.Collections;
import java.util.List;

public class IntegerMatrixIndividual extends MatrixIndividual {
    
    public static final String P_IntegerMatrixIndividual = "int-vect-ind";
    public int[] genome;

    public int getCantidadPixeles(){
        return (genome != null) ? genome.length : 0;
    }

    public Parameter defaultBase() {
        return VectorDefaults.base().push(P_IntegerMatrixIndividual);
    }

    public Object clone() {
        IntegerMatrixIndividual myObj = (IntegerMatrixIndividual) (super.clone());
        myObj.genome = (int[])(genome.clone());
        return myObj;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

        Parameter def = defaultBase();
        
        if (!(species instanceof IntegerMatrixSpecies)) 
            state.output.fatal("IntegerMatrixIndividual requires an IntegerMatrixSpecies", base, def);
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;

        System.out.print("cant pixeles: " + getCantidadPixeles() + ", cant estaciones: " + IntegerMatrixSpecies.CANT_ESTACIONES);

        genome = new int[s.genomeSize];
    }
                
    public void defaultCrossover(EvolutionState state, int thread, MatrixIndividual ind) {
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        IntegerMatrixIndividual i = (IntegerMatrixIndividual) ind;
        int tmp;
        int point;
        int ini;
        int fin;

        int len = Math.min(genome.length, i.genome.length);
        if (len != genome.length || len != i.genome.length)
            state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");

        switch(s.crossoverType) {
            case MatrixSpecies.C_ONE_POINT:
                point = state.random[thread].nextInt((len / s.chunksize));
                fin = MatrixUtils.getClosestIntSeason(point*s.chunksize);
                for(int x = 0; x < fin; x++) {
                    tmp = i.genome[x];
                    i.genome[x] = genome[x]; 
                    genome[x] = tmp; 
                }
                break;
            case MatrixSpecies.C_TWO_POINT: {
                point = state.random[thread].nextInt((len / s.chunksize));
                int point0 = state.random[thread].nextInt((len / s.chunksize));
                if (point0 > point) {
                    int p = point0;
                    point0 = point;
                    point = p;
                }
                ini = MatrixUtils.getClosestIntSeason(point0*s.chunksize);
                fin = MatrixUtils.getClosestIntSeason(point*s.chunksize);
                for(int x = ini; x < fin; x++) {
                    tmp = i.genome[x];
                    i.genome[x] = genome[x];
                    genome[x] = tmp;
                }
            }
            break;
            case MatrixSpecies.C_ANY_POINT:
                for(int x = 0; x < len/s.chunksize; x += IntegerMatrixSpecies.CANT_ESTACIONES) {
                    if (state.random[thread].nextBoolean(s.crossoverProbability)) {
                        for (int y = x * s.chunksize; y < (x + IntegerMatrixSpecies.CANT_ESTACIONES) * s.chunksize; y++) {
                            tmp = i.genome[y];
                            i.genome[y] = genome[y];
                            genome[y] = tmp;
                        }
                    }
                }
                break;
            }
        }

    /** Splits the genome into n pieces, according to points, which *must* be sorted. 
        pieces.length must be 1 + points.length */
    public void split(int[] points, Object[] pieces) {
        int point0, point1;
        point0 = 0;
        point1 = points[0];
        for(int x = 0; x < pieces.length; x++) {
            Range range = MatrixUtils.pullApartIntsInSeason(point0, point1);
            point0 = range.ini;
            point1 = range.fin;
            pieces[x] = new int[point1-point0];
            System.arraycopy(genome,point0,pieces[x],0,point1-point0);
            point0 = point1;
            if (x >= pieces.length - 2) {
                point1 = genome.length;
            } else {
                point1 = points[x+1];
            }
        }
    }
    
    /** Joins the n pieces and sets the genome to their concatenation.*/
    public void join(Object[] pieces) {
        int sum=0;
        for(int x = 0; x < pieces.length; x++) {
            sum += ((int[]) (pieces[x])).length;
        }
        
        int runningSum = 0;
        int[] newGenome = new int[sum];
        for(int x = 0; x < pieces.length; x++) {
            System.arraycopy(pieces[x], 0, newGenome, runningSum, ((int[])(pieces[x])).length);
            runningSum += ((int[])(pieces[x])).length;
        }
        genome = newGenome;
    }


    /** Returns a random value from between min and max inclusive.  This method handles
        overflows that complicate this computation.  Does NOT check that
        min is less than or equal to max.  You must check this yourself. */
    public int randomValueFromClosedInterval(int min, int max, MersenneTwisterFast random) {
        if (max - min < 0) { // we had an overflow
            int l = 0;
            do l = random.nextInt();
            while(l < min || l > max);
            return l;
        } else {
            return min + random.nextInt(max - min + 1);
        }
    }


    /** Destructively mutates the individual in some default manner.  The default form
        simply randomizes genes to a uniform distribution from the min and max of the gene values. */
    public void defaultMutate(EvolutionState state, int thread) {

        System.out.println("defaultMutate en IntegerMatrixIndividual, cambiar segun nuestro mutate (si es que se usa esta funcion)");

        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        for(int x = 0; x < genome.length; x++)
            if (state.random[thread].nextBoolean(s.mutationProbability(x))) {
                int old = genome[x];
                for(int retries = 0; retries < s.duplicateRetries(x) + 1; retries++)
                    {
                    switch(s.mutationType(x))
                        {
                        case IntegerMatrixSpecies.C_RESET_MUTATION:
                            genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
                            break;
                        case IntegerMatrixSpecies.C_RANDOM_WALK_MUTATION:
                            int min = (int)s.minGene(x);
                            int max = (int)s.maxGene(x);
                            if (!s.mutationIsBounded(x))
                                {
                                // okay, technically these are still bounds, but we can't go beyond this without weird things happening
                                max = Integer.MAX_VALUE;
                                min = Integer.MIN_VALUE;
                                }
                            do
                                {
                                int n = (int)(state.random[thread].nextBoolean() ? 1 : -1);
                                int g = genome[x];
                                if ((n == 1 && g < max) ||
                                    (n == -1 && g > min))
                                    genome[x] = g + n;
                                else if ((n == -1 && g < max) ||
                                    (n == 1 && g > min))
                                    genome[x] = g - n;     
                                }
                            while (state.random[thread].nextBoolean(s.randomWalkProbability(x)));
                            break;
                        default:
                            state.output.fatal("In IntegerMatrixIndividual.defaultMutate, default case occurred when it shouldn't have");
                            break;
                        }
                    if (genome[x] != old) break;
                    // else genome[x] = old;  // try again
                    }
            }
    }
        
    
    /** Initializes the individual by randomly choosing Integers uniformly from mingene to maxgene. */
    // notice that we bump to longs to avoid overflow errors
    public void reset(EvolutionState state, int thread) { // TODO: minGene y maxGene en IntegerMatrixSpecies debería tener los IDs minimos y maximos de los usos.
        System.out.println("reset en IntegerMatrixIndividual, cambiar segun nuestro inicializador (si es que se usa esta funcion)");
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        for(int x = 0; x < genome.length; x++)
            genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
    }

    public int hashCode() { // TODO: dafac is this?
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x=0;x<genome.length;x++)
            hash = ( hash << 1 | hash >>> 31 ) ^ genome[x];

        return hash;
    }

    public String genotypeToStringForHumans() {
        StringBuilder s = new StringBuilder();
        for( int i = 0 ; i < genome.length ; i++ ) {
            if (i > 0) {
                s.append(" ");
                if (i % IntegerMatrixSpecies.CANT_ESTACIONES == 0) {
                    s.append('\n');
                }
            }
            s.append(genome[i]);
        }
        return s.toString();
    }

    public String genotypeToString() {
        StringBuilder s = new StringBuilder();
        s.append( Code.encode( genome.length ) );
        for( int i = 0 ; i < genome.length ; i++ ) {
            if (i % IntegerMatrixSpecies.CANT_ESTACIONES == 0) {
                s.append('\n');
            }
            s.append(Code.encode(genome[i]));
        }
        return s.toString();
    }

    protected void parseGenotype(final EvolutionState state, final LineNumberReader reader) throws IOException {
        // read in the next line.  The first item is the number of genes
        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);
        Code.decode( d );
        
        // of course, even if it *is* an integer, we can't tell if it's a gene or a genome count, argh...
        if (d.type != DecodeReturn.T_INTEGER)  // uh oh
            state.output.fatal("Individual with genome:\n" + s + "\n... does not have an integer at the beginning indicating the genome count.");
        int genotypeLength = (int)(d.l);

        genome = new int[ genotypeLength ];

        // read in the genes
        for( int i = 0 ; i < genome.length ; i++ ) {
            Code.decode( d );
            genome[i] = (int)(d.l);
        }
    }

    public boolean equals(Object ind) {
        if (ind == null)
            return false;

        if (!(this.getClass().equals(ind.getClass())))
            return false; // SimpleRuleIndividuals are special.

        IntegerMatrixIndividual i = (IntegerMatrixIndividual)ind;

        if( genome.length != i.genome.length )
            return false;

        for( int j = 0 ; j < genome.length ; j++ ) {
            if (genome[j] != i.genome[j])
                return false;
        }
        return true;
    }

    public Object getGenome() {
        return genome;
    }

    public void setGenome(Object gen) {
        genome = (int[]) gen;
    }

    public int genomeLength() {
        return genome.length / IntegerMatrixSpecies.CANT_ESTACIONES;
    }
        
    public void writeGenotype(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(genome.length);
        for(int x=0; x < genome.length; x++) {
            dataOutput.writeInt(genome[x]);
        }
    }

    public void readGenotype(final EvolutionState state, final DataInput dataInput) throws IOException {
        int len = dataInput.readInt();
        if (genome==null || genome.length != len)
            genome = new int[len];

        for(int x = 0;x < genome.length; x++) {
            genome[x] = dataInput.readInt();
        }
    }

    public void cargarGenotype(final EvolutionState state, final int[] nuevoGenotype) {
        // TODO: esta func la hicimos nosotros, no es del IntegerVectorSpecies

        int len = nuevoGenotype.length;
        if (genome==null || genome.length != len)
            genome = new int[len];

        for(int x=0;x<genome.length;x++)
            genome[x] = nuevoGenotype[x];
    }

    /** Clips each gene value to be within its specified [min,max] range. */
    public void clamp() {  // TODO: aca se pueden poner las restricciones de usos que se pueden plantar x estacion, haciendo que min
        // y max Gene dependan de la estación, y en vez de retornar un min y un max, que retorne los ids de usos disponibles.

        IntegerMatrixSpecies _species = (IntegerMatrixSpecies)species;
        for (int i = 0; i < genomeLength(); i++) {
            int minGene = (int)_species.minGene(i);
            if (genome[i] < minGene) {
                genome[i] = minGene;
            } else {
                int maxGene = (int)_species.maxGene(i);
                if (genome[i] > maxGene) {
                    genome[i] = maxGene;
                }
            }
        }
    }

    /** Returns true if each gene value is within is specified [min,max] range. */
    public boolean isInRange() {
        IntegerMatrixSpecies _species = (IntegerMatrixSpecies)species;
        for (int i = 0; i < genomeLength(); i++)
            if (genome[i] < _species.minGene(i) || genome[i] > _species.maxGene(i))
                return false;
        return true;
    }

    public double distanceTo(Individual otherInd) {
        if (!(otherInd instanceof IntegerVectorIndividual))
            return super.distanceTo(otherInd);  // will return infinity!

        IntegerVectorIndividual other = (IntegerVectorIndividual) otherInd;
        int[] otherGenome = other.genome;
        double sumSquaredDistance =0.0;
        for(int i=0; i < other.genomeLength(); i++) {
            long dist = this.genome[i] - (long)otherGenome[i];
            sumSquaredDistance += dist*dist;
        }
        return StrictMath.sqrt(sumSquaredDistance);
    }
}