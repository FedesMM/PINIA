package ec.vector;

import ec.*;
import ec.util.*;


public class IntegerMatrixSpecies extends MatrixSpecies {

    public final static String P_MINGENE = "min-gene";
    public final static String P_MAXGENE = "max-gene";

    public static final double PROB_MUTACION_PONDERADA_FOSFORO = 0.3;
    public static final double PROB_MUTACION_PONDERADA_PRODUCTIVIDAD = 0.3;
    public static final double PROB_MUTACION_PONDERADA_FACTIBLE_PROD = 0.2;
    public static final double PROB_MUTACION_PONDERADA_FACTIBLE_USOS = 0.2;

    public final static String P_NUM_SEGMENTS = "num-segments";

    public final static String P_SEGMENT_TYPE = "segment-type";

    public final static String P_SEGMENT_START = "start";

    public final static String P_SEGMENT_END = "end";

    public final static String P_SEGMENT = "segment";

    public final static String P_MUTATIONTYPE = "mutation-type";

    public final static String P_RANDOM_WALK_PROBABILITY = "random-walk-probability";

    public final static String P_MUTATION_BOUNDED = "mutation-bounded";

    public final static String V_RESET_MUTATION = "reset";

    public final static String V_RANDOM_WALK_MUTATION = "random-walk";

    public final static int C_RESET_MUTATION = 0;

    public final static int C_RANDOM_WALK_MUTATION = 1;

    // TODO: VER clamp en IntegerMatrixSpecies
    /** Min-gene value, per gene.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    protected long[] minGene;

    /** Max-gene value, per gene.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    protected long[] maxGene;


    /** Mutation type, per gene.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    protected int[] mutationType;

    /** The continuation probability for Integer Random Walk Mutation, per gene.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    protected double[] randomWalkProbability;

    /** Whether mutation is bounded to the min- and max-gene values, per gene.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    protected boolean[] mutationIsBounded;

    /** Whether the mutationIsBounded value was defined, per gene.
        Used internally only.
        This array is one longer than the standard genome length.
        The top element in the array represents the parameters for genes in
        genomes which have extended beyond the genome length.  */
    boolean mutationIsBoundedDefined;

    int[] usos;

    public long maxGene(int gene) {
        long[] m = maxGene;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public long minGene(int gene) {
        long[] m = minGene;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public int mutationType(int gene) {
        int[] m = mutationType;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public double randomWalkProbability(int gene) {
        double[] m = randomWalkProbability;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public boolean mutationIsBounded(int gene) {
        boolean[] m = mutationIsBounded;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public boolean inNumericalTypeRange(double geneVal) {
        if (i_prototype instanceof IntegerMatrixIndividual)
            return (geneVal <= Integer.MAX_VALUE && geneVal >= Integer.MIN_VALUE);
        else if (i_prototype instanceof LongVectorIndividual)
            return true;  // geneVal is valid for all longs
        else return false;
    }

    public boolean inNumericalTypeRange(long geneVal) {
        if (i_prototype instanceof IntegerMatrixIndividual)
            return (geneVal <= Integer.MAX_VALUE && geneVal >= Integer.MIN_VALUE);
        else if (i_prototype instanceof LongVectorIndividual)
            return true;  // geneVal is valid for all longs
        else return false;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter def = defaultBase();

        setupGenome(state, base);

        // create the arrays
        minGene = new long[genomeSize + 1];
        maxGene = new long[genomeSize + 1];
        mutationType = fill(new int[genomeSize + 1], -1);
        mutationIsBounded = new boolean[genomeSize + 1];
        randomWalkProbability = new double[genomeSize + 1];


        // LOADING GLOBAL MIN/MAX GENES
        long _minGene = 1;                  // TODO: menor ID de todos los uso
        long _maxGene = 14;                 // TODO: mayor ID de todos los uso
        if (_maxGene < _minGene)
            state.output.fatal("IntegerMatrixSpecies must have a default min-gene which is <= the default max-gene", base.push(P_MAXGENE), def.push(P_MAXGENE));
        fill(minGene, _minGene);
        fill(maxGene, _maxGene);

        /// MUTATION

        String mtype = state.parameters.getStringWithDefault(base.push(P_MUTATIONTYPE), def.push(P_MUTATIONTYPE), null);
        int _mutationType = C_RESET_MUTATION; // mtype.equalsIgnoreCase(V_RESET_MUTATION)
        if (mtype == null)
            state.output.warning("No global mutation type given for IntegerMatrixSpecies, assuming 'reset' mutation", base.push(P_MUTATIONTYPE), def.push(P_MUTATIONTYPE));
        else if (mtype.equalsIgnoreCase(V_RANDOM_WALK_MUTATION))
            _mutationType = C_RANDOM_WALK_MUTATION;

        fill(mutationType, _mutationType);

        if (_mutationType == C_RANDOM_WALK_MUTATION) {
            double _randomWalkProbability = state.parameters.getDoubleWithMax(base.push(P_RANDOM_WALK_PROBABILITY), def.push(P_RANDOM_WALK_PROBABILITY), 0.0, 1.0);
            if (_randomWalkProbability <= 0)
                state.output.fatal("If it's going to use random walk mutation as its global mutation type, IntegerMatrixSpecies must a random walk mutation probability between 0.0 and 1.0.",
                        base.push(P_RANDOM_WALK_PROBABILITY), def.push(P_RANDOM_WALK_PROBABILITY));
            fill(randomWalkProbability, _randomWalkProbability);

            if (!state.parameters.exists(base.push(P_MUTATION_BOUNDED), def.push(P_MUTATION_BOUNDED)))
                state.output.warning("IntegerMatrixSpecies is using gaussian, polynomial, or integer randomwalk mutation as its global mutation type, but " + P_MUTATION_BOUNDED + " is not defined.  Assuming 'true'");
            boolean _mutationIsBounded = state.parameters.getBoolean(base.push(P_MUTATION_BOUNDED), def.push(P_MUTATION_BOUNDED), true);
            fill(mutationIsBounded, _mutationIsBounded);
            mutationIsBoundedDefined = true;
        }

        super.setup(state, base);

        // VERIFY
        for (int x = 0; x < genomeSize + 1; x++) {
            if (maxGene[x] < minGene[x])
                state.output.fatal("IntegerMatrixSpecies must have a min-gene[" + x + "] which is <= the max-gene[" + x + "]");

            // check to see if these longs are within the data type of the particular individual
            if (!inNumericalTypeRange(minGene[x]))
                state.output.fatal("This IntegerMatrixSpecies has a prototype of the kind: " + i_prototype.getClass().getName() +
                        ", but doesn't have a min-gene[" + x + "] value within the range of this prototype's genome's data types");
            if (!inNumericalTypeRange(maxGene[x]))
                state.output.fatal("This IntegerMatrixSpecies has a prototype of the kind: " + i_prototype.getClass().getName() +
                        ", but doesn't have a max-gene[" + x + "] value within the range of this prototype's genome's data types");
        }
    }


    protected void loadParametersForGene(EvolutionState state, int index, Parameter base, Parameter def, String postfix) {

        super.loadParametersForGene(state, index, base, def, postfix);
        
        boolean minValExists = state.parameters.exists(base.push(P_MINGENE).push(postfix), def.push(P_MINGENE).push(postfix));
        boolean maxValExists = state.parameters.exists(base.push(P_MAXGENE).push(postfix), def.push(P_MAXGENE).push(postfix));
        
        if ((maxValExists && !(minValExists)))
            state.output.warning("Max Gene specified but not Min Gene", base.push(P_MINGENE).push(postfix), def.push(P_MINGENE).push(postfix));
                
        if ((minValExists && !(maxValExists)))
            state.output.warning("Min Gene specified but not Max Gene", base.push(P_MAXGENE).push(postfix), def.push(P_MINGENE).push(postfix));

        if (minValExists) {
            long minVal = state.parameters.getLongWithDefault(base.push(P_MINGENE).push(postfix), def.push(P_MINGENE).push(postfix), 0);

            //check if the value is in range
            if (!inNumericalTypeRange(minVal))
                state.output.error("Min Gene Value out of range for data type " + i_prototype.getClass().getName(),
                    base.push(P_MINGENE).push(postfix), base.push(P_MINGENE).push(postfix));
            else minGene[index] = minVal;

            if (dynamicInitialSize)
                state.output.warnOnce("Using dynamic initial sizing, but per-gene or per-segment min-gene declarations.  This is probably wrong.  You probably want to use global min/max declarations.",
                    base.push(P_MINGENE).push(postfix), base.push(P_MINGENE).push(postfix));
        }
            
        if (maxValExists) {
            long maxVal = state.parameters.getLongWithDefault(base.push(P_MAXGENE).push(postfix), def.push(P_MAXGENE).push(postfix), 0);
                
            //check if the value is in range
            if (!inNumericalTypeRange(maxVal))
                state.output.error("Max Gene Value out of range for data type " + i_prototype.getClass().getName(),
                    base.push(P_MAXGENE).push(postfix), base.push(P_MAXGENE).push(postfix));
            else maxGene[index] = maxVal;

            if (dynamicInitialSize)
                state.output.warnOnce("Using dynamic initial sizing, but per-gene or per-segment max-gene declarations.  This is probably wrong.  You probably want to use global min/max declarations.",
                    base.push(P_MAXGENE).push(postfix), base.push(P_MAXGENE).push(postfix));
        }


        /// MUTATION

        String mType = state.parameters.getStringWithDefault(base.push(P_MUTATIONTYPE).push(postfix), def.push(P_MUTATIONTYPE).push(postfix), null);
        int mutType = -1;
        if (mType == null) { }  // we're cool
        else if (mType.equalsIgnoreCase(V_RESET_MUTATION))
            mutType = mutationType[index] = C_RESET_MUTATION; 
        else if (mType.equalsIgnoreCase(V_RANDOM_WALK_MUTATION)) {
            mutType = mutationType[index] = C_RANDOM_WALK_MUTATION;
            state.output.warnOnce("Integer Random Walk Mutation used in IntegerMatrixSpecies.  Be advised that during initialization these genes will only be set to integer values.");
        } else
            state.output.error("IntegerMatrixSpecies given a bad mutation type: " + mType, base.push(P_MUTATIONTYPE).push(postfix), def.push(P_MUTATIONTYPE).push(postfix));


        if (mutType == C_RANDOM_WALK_MUTATION) {
            if (state.parameters.exists(base.push(P_RANDOM_WALK_PROBABILITY).push(postfix),def.push(P_RANDOM_WALK_PROBABILITY).push(postfix))) {
                randomWalkProbability[index] = state.parameters.getDoubleWithMax(base.push(P_RANDOM_WALK_PROBABILITY).push(postfix),def.push(P_RANDOM_WALK_PROBABILITY).push(postfix), 0.0, 1.0);
                if (randomWalkProbability[index] <= 0)
                    state.output.error("If it's going to use random walk mutation as a per-gene or per-segment type, IntegerMatrixSpecies must a random walk mutation probability between 0.0 and 1.0.",
                        base.push(P_RANDOM_WALK_PROBABILITY).push(postfix), def.push(P_RANDOM_WALK_PROBABILITY).push(postfix));

            } else
                state.output.error("If IntegerMatrixSpecies is going to use polynomial mutation as a per-gene or per-segment type, either the global or per-gene/per-segment random walk mutation probability must be defined.",
                    base.push(P_RANDOM_WALK_PROBABILITY).push(postfix), def.push(P_RANDOM_WALK_PROBABILITY).push(postfix));

            if (state.parameters.exists(base.push(P_MUTATION_BOUNDED).push(postfix), def.push(P_MUTATION_BOUNDED).push(postfix))) {
                mutationIsBounded[index] = state.parameters.getBoolean(base.push(P_MUTATION_BOUNDED).push(postfix), def.push(P_MUTATION_BOUNDED).push(postfix), true);

            } else if (!mutationIsBoundedDefined)
                state.output.fatal("If IntegerMatrixSpecies is going to use gaussian, polynomial, or integer random walk mutation as a per-gene or per-segment type, the mutation bounding must be defined.",
                    base.push(P_MUTATION_BOUNDED).push(postfix), def.push(P_MUTATION_BOUNDED).push(postfix));
        }
    }
}

