package ec.vector;

import ec.*;
import ec.util.*;
import java.io.*;

public class IntegerMatrixIndividual extends Individual {

    private static final String P_IntegerMatrixIndividual = "int-mat-ind";
    public int[][] genome;
    public int lenCol;

    public int getCantidadPixeles(){
        return genome.length;
    }

    public int getCantidadEstaciones(){
        if (genome.length >= 1) {
            return genome[0].length;
        }
        return 0;
    }

    public Parameter defaultBase() {
        return VectorDefaults.base().push(P_IntegerMatrixIndividual);
    }

    public Object clone() {
        IntegerMatrixIndividual myObject = (IntegerMatrixIndividual) (super.clone());
        myObject.genome = (int[][])(genome.clone());
        return myObject;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

        Parameter def = defaultBase();
        
        if (!(species instanceof IntegerMatrixSpecies))
            state.output.fatal("IntegerMatrixIndividual requires an IntegerMatrixSpecies", base, def);
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;

        genome = new int[s.genomeColumnLength][s.genomeRowLength];
        lenCol =
    }

    public void defaultCrossover(EvolutionState state, int thread, Individual ind) {
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        IntegerMatrixIndividual i = (IntegerMatrixIndividual) ind;
        int[] tmp;
        int point;

        int minLenCol = Math.min(genome.length, i.genome.length);
        int lenRowInd = (i.genome.length >= 1) ? i.genome[0].length : 0;
        int lenRow = (genome.length >= 1) ? genome[0].length : 0;
        int minLenRow = Math.min(lenRowInd, lenRow);
        if (minLenCol != genome.length || minLenCol != i.genome.length || minLenRow != lenRowInd || minLenRow != lenRow) {
            state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");
        }

        switch (s.crossoverType) {
            case MatrixSpecies.C_ONE_POINT:
                point = state.random[thread].nextInt((minLenCol / s.chunksize));
                for (int x = 0; x < point * s.chunksize; x++) {
                    tmp = i.genome[x];
                    i.genome[x] = genome[x];
                    genome[x] = tmp;
                }
                break;
            case MatrixSpecies.C_TWO_POINT:
                point = state.random[thread].nextInt((minLenCol / s.chunksize));
                int point0 = state.random[thread].nextInt((minLenCol / s.chunksize));
                if (point0 > point) {
                    int p = point0;
                    point0 = point;
                    point = p;
                }
                for (int x = point0 * s.chunksize; x < point * s.chunksize; x++) {
                    tmp = i.genome[x];
                    i.genome[x] = genome[x];
                    genome[x] = tmp;
                }
                break;
            case MatrixSpecies.C_ANY_POINT:
                for (int x = 0; x < minLenCol / s.chunksize; x++) {
                    if (state.random[thread].nextBoolean(s.crossoverProbability)) {
                        for (int y = x * s.chunksize; y < (x + 1) * s.chunksize; y++) {
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
        point0 = 0; point1 = points[0];
        for(int x=0;x<pieces.length;x++) {
            pieces[x] = new int[point1-point0];
            System.arraycopy(genome,point0,pieces[x],0,point1-point0);
            point0 = point1;
            if (x >=pieces.length-2) {
                point1 = genome.length;
            } else {
                point1 = points[x + 1];
            }
        }
    }
    
    /** Joins the n pieces and sets the genome to their concatenation.*/
    public void join(Object[] pieces) {
        int sum = 0;
        for(int x=0;x<pieces.length;x++) {
            sum += ((int[]) (pieces[x])).length;
        }
        int runningSum = 0;
        int[][] newGenome = new int[sum][lenCol];
        for(int x=0;x<pieces.length;x++) {
            System.arraycopy(pieces[x], 0, newGenome, runningSum, ((int[])(pieces[x])).length);
            runningSum += ((int[])(pieces[x])).length;
        }
        // set genome
        genome = newGenome;
    }


    /** Returns a random value from between min and max inclusive.  This method handles
        overflows that complicate this computation.  Does NOT check that
        min is less than or equal to max.  You must check this yourself. */
    public int randomValueFromClosedInterval(int min, int max, MersenneTwisterFast random) {
        if (max - min < 0) { // we had an overflow
            int value = 0;
            do value = random.nextInt();
            while(value < min || value > max);
            return value;
        } else {
            return min + random.nextInt(max - min + 1);
        }
    }


    /** Destructively mutates the individual in some default manner.  The default form
        simply randomizes genes to a uniform distribution from the min and max of the gene values. */
    public void defaultMutate(EvolutionState state, int thread) {
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        for(int x = 0; x < genome.length; x++) {
            if (state.random[thread].nextBoolean(s.mutationProbability(x))) {
                int[] old = genome[x];
                for(int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) {
                    switch(s.mutationType(x)) {
                        case IntegerMatrixSpecies.C_RESET_MUTATION:
                            genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
                            break;
                        case IntegerMatrixSpecies.C_RANDOM_WALK_MUTATION:
                            int min = (int)s.minGene(x);
                            int max = (int)s.maxGene(x);
                            if (!s.mutationIsBounded(x)) {
                                // okay, technically these are still bounds, but we can't go beyond this without weird things happening
                                max = Integer.MAX_VALUE;
                                min = Integer.MIN_VALUE;
                            }
                            do {
                                int n = (int)(state.random[thread].nextBoolean() ? 1 : -1);
                                int g = genome[x];
                                if ((n == 1 && g < max) || (n == -1 && g > min)) {
                                    genome[x] = g + n;
                                } else if ((n == -1 && g < max) || (n == 1 && g > min)) {
                                    genome[x] = g - n;
                                }
                            }
                            while (state.random[thread].nextBoolean(s.randomWalkProbability(x)));
                            break;
                        default:
                            state.output.fatal("In IntegerVectorIndividualP1E1.defaultMutate, default case occurred when it shouldn't have");
                            break;
                        }
                    if (genome[x] != old) break;
                    // else genome[x] = old;  // try again
                }
            }
        }
    }
        
    
    /** Initializes the individual by randomly choosing Integers uniformly from mingene to maxgene. */
    // notice that we bump to longs to avoid overflow errors
    public void reset(EvolutionState state, int thread) {
        IntegerMatrixSpecies s = (IntegerMatrixSpecies) species;
        for (int x = 0; x < genome.length; x++) {
            for (int y = 0; y < lenCol; y++) {
                genome[x][y] = randomValueFromClosedInterval((int) s.minGene(x), (int) s.maxGene(x), state.random[thread]); //TODO: VER minGene(x)
            }
        }
    }

    public int hashCode() {
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x=0; x<genome.length; x++) {
            hash = (hash << 1 | hash >>> 31) ^ genome[x];
        }
        return hash;
    }

    public String genotypeToStringForHumans() {
        StringBuilder s = new StringBuilder();
        for( int i = 0 ; i < genome.length ; i++ ) {
            if (i > 0) {
                s.append(" ");
            }
            s.append(genome[i]);
        }
        return s.toString();
    }
        
    public String genotypeToString()
        {
        StringBuilder s = new StringBuilder();
        s.append( Code.encode( genome.length ) );
        for( int i = 0 ; i < genome.length ; i++ )
            s.append( Code.encode( genome[i] ) );
        return s.toString();
        }
                
    protected void parseGenotype(final EvolutionState state, final LineNumberReader reader) throws IOException {
        // read in the next line.  The first item is the number of genes
        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);
        Code.decode( d );
        
        // of course, even if it *is* an integer, we can't tell if it's a gene or a genome count, argh...
        if (d.type != DecodeReturn.T_INTEGER) {
            state.output.fatal("Individual with genome:\n" + s + "\n... does not have an integer at the beginning indicating the genome count.");
        }
        int lll = (int)(d.l);

        genome = new int[ lll ];

        // read in the genes
        for( int i = 0 ; i < genome.length ; i++ ) {
            Code.decode( d );
            genome[i] = (int)(d.l);
        }
    }

    public boolean equals(Object ind)
        {
        if (ind == null) return false;
        if (!(this.getClass().equals(ind.getClass()))) return false; // SimpleRuleIndividuals are special.
        IntegerVectorIndividualP1E1 i = (IntegerVectorIndividualP1E1)ind;
        if( genome.length != i.genome.length )
            return false;
        for( int j = 0 ; j < genome.length ; j++ )
            if( genome[j] != i.genome[j] )
                return false;
        return true;
        }

    public Object getGenome()
        { return genome; }
    public void setGenome(Object gen)
        { genome = (int[]) gen; }
    public int genomeLength()
        { return genome.length; }
        
    public void writeGenotype(final EvolutionState state,
        final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeInt(genome.length);
        for(int x=0;x<genome.length;x++)
            dataOutput.writeInt(genome[x]);
        }

    public void readGenotype(final EvolutionState state,
        final DataInput dataInput) throws IOException
        {
        int len = dataInput.readInt();
        if (genome==null || genome.length != len)
            genome = new int[len];
        for(int x=0;x<genome.length;x++)
            genome[x] = dataInput.readInt();
        }
    public void cargarGenotype(final EvolutionState state,
                             final int[] nuevoGenotype)
    {
        int len = nuevoGenotype.length;
        if (genome==null || genome.length != len)
            genome = new int[len];
        for(int x=0;x<genome.length;x++)
            genome[x] = nuevoGenotype[x];
    }

    /** Clips each gene value to be within its specified [min,max] range. */
    public void clamp() 
        {
        IntegerVectorSpeciesP1E1 _species = (IntegerVectorSpeciesP1E1)species;
        for (int i = 0; i < genomeLength(); i++)
            {
            int minGene = (int)_species.minGene(i);
            if (genome[i] < minGene)
                genome[i] = minGene;
            else 
                {
                int maxGene = (int)_species.maxGene(i);
                if (genome[i] > maxGene)
                    genome[i] = maxGene;
                }
            }
        }
                
    public void setGenomeLength(int len)
        {
        int[] newGenome = new int[len];
        System.arraycopy(genome, 0, newGenome, 0, 
            genome.length < newGenome.length ? genome.length : newGenome.length);
        genome = newGenome;
        }

    /** Returns true if each gene value is within is specified [min,max] range. */
    public boolean isInRange() 
        {
        IntegerVectorSpeciesP1E1 _species = (IntegerVectorSpeciesP1E1)species;
        for (int i = 0; i < genomeLength(); i++)
            if (genome[i] < _species.minGene(i) ||
                genome[i] > _species.maxGene(i)) return false;
        return true;
        }

    public double distanceTo(Individual otherInd)
        {               
        if (!(otherInd instanceof IntegerVectorIndividualP1E1)) 
            return super.distanceTo(otherInd);  // will return infinity!
                
        IntegerVectorIndividualP1E1 other = (IntegerVectorIndividualP1E1) otherInd;
        int[] otherGenome = other.genome;
        double sumSquaredDistance =0.0;
        for(int i=0; i < other.genomeLength(); i++)
            {
            long dist = this.genome[i] - (long)otherGenome[i];
            sumSquaredDistance += dist*dist;
            }
        return StrictMath.sqrt(sumSquaredDistance);
        }
    }
