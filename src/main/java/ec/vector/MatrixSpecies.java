package ec.vector;

import ec.util.*;
import java.io.*;
import ec.*;

public class MatrixSpecies extends Species {

    public final static String RUTA_CANT_TAREAS="ruta-cantidad-tareas";
    public final static String RUTA_CANT_EMPLEADOS="ruta-cantidad-empleados";
    public static final String P_VECTORSPECIES = "species";

    public final static String P_CROSSOVERTYPE = "crossover-type";
    public final static String P_CHUNKSIZE = "chunk-size";
    public final static String V_ONE_POINT = "one";
    public final static String V_ONE_POINT_NO_NOP = "one-nonempty";
    public final static String V_TWO_POINT = "two";
    public final static String V_TWO_POINT_NO_NOP = "two-nonempty";
    public final static String V_ANY_POINT = "any";
    public final static String V_LINE_RECOMB = "line";
    public final static String V_INTERMED_RECOMB = "intermediate";
    public final static String V_SIMULATED_BINARY = "sbx";
    public final static String P_CROSSOVER_DISTRIBUTION_INDEX = "crossover-distribution-index";
    public final static String P_MUTATIONPROB = "mutation-prob";
    public final static String P_CROSSOVERPROB = "crossover-prob";
    public final static String P_GENOMESIZE = "genome-size";
    public final static String P_LINEDISTANCE = "line-extension";
    public final static String V_GEOMETRIC = "geometric";
    public final static String P_GEOMETRIC_PROBABILITY = "geometric-prob";
    public final static String V_UNIFORM = "uniform";
    public final static String P_UNIFORM_MIN = "min-initial-size";
    public final static String P_UNIFORM_MAX = "max-initial-size";
    public final static String P_NUM_SEGMENTS = "num-segments";
    public final static String P_SEGMENT_TYPE = "segment-type";
    public final static String P_SEGMENT_START = "start";
    public final static String P_SEGMENT_END = "end";
    public final static String P_SEGMENT = "segment";
    public final static String P_DUPLICATE_RETRIES = "duplicate-retries";

    public final static int C_ONE_POINT = 0;
    public final static int C_ONE_POINT_NO_NOP = 2;
    public final static int C_TWO_POINT = 4;
    public final static int C_TWO_POINT_NO_NOP = 8;
    public final static int C_ANY_POINT = 128;
    public final static int C_LINE_RECOMB = 256;
    public final static int C_INTERMED_RECOMB = 512;
    public final static int C_SIMULATED_BINARY = 1024;

    public final static int C_NONE = 0;
    public final static int C_GEOMETRIC = 1;
    public final static int C_UNIFORM = 2;

    /** How often do we retry until we get a non-duplicate gene? */
    protected int[] duplicateRetries;

    /** Probability that a gene will mutate, per gene.
     This array is one longer than the standard genome length.
     The top element in the array represents the parameters for genes in
     genomes which have extended beyond the genome length.  */
    protected double[] mutationProbability;

    /** Probability that a gene will cross over -- ONLY used in V_ANY_POINT crossover */
    public double crossoverProbability;
    /** What kind of crossover do we have? */
    public int crossoverType;
    /** How big of a genome should we create on initialization? */
    public int genomeColumnLength;
    public int genomeRowLength;
    /** What should the SBX distribution index be? */
    public int crossoverDistributionIndex;
    /** How should we reset the genome? */
    public int genomeResizeAlgorithm;
    /** What's the smallest legal genome? */
    public int minInitialSize;
    /** What's the largest legal genome? */
    public int maxInitialSize;
    /** With what probability would our genome be at least 1 larger than it is now during initialization? */
    public double genomeIncreaseProbability;
    /** How big of chunks should we define for crossover? */
    public int chunksize;
    /** How far along the long a child can be located for line or intermediate recombination */
    public double lineDistance;
    /** Was the initial size determined dynamically? */
    public boolean dynamicInitialSize = false;
    protected int cantTareas = 0;
    protected int cantEmpleados = 0;


    public double mutationProbability(int gene)
    {
        double[] m = mutationProbability;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public int duplicateRetries(int gene)
    {
        int[] m = duplicateRetries;
        if (m.length <= gene)
            gene = m.length - 1;
        return m[gene];
    }

    public Parameter defaultBase()
    {
        return VectorDefaults.base().push(P_VECTORSPECIES);
    }


    protected void setupGenome(final EvolutionState state, final Parameter base)
    {
        Parameter def = defaultBase();
        try {
            String ruta_cant_empleados = state.parameters.getStringWithDefault(base.push(RUTA_CANT_EMPLEADOS), def.push(RUTA_CANT_EMPLEADOS), null);
            //System.out.println("Ruta del archivo de RUTA_CANT_EMPLEADOS: " + ruta_cant_empleados);
            File fin = new File(ruta_cant_empleados);
            FileInputStream fis = new FileInputStream(fin);

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            cantEmpleados = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            state.output.fatal(e + ", error al leer el archivo de cant empleados");
        }

        try {
            String ruta_cant_tareas = state.parameters.getStringWithDefault(base.push(RUTA_CANT_TAREAS), def.push(RUTA_CANT_TAREAS), null);
            //System.out.println("Ruta del archivo de RUTA_CANT_TAREAS: " + ruta_cant_tareas);
            File fin = new File(ruta_cant_tareas);
            FileInputStream fis = new FileInputStream(fin);

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            cantTareas = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            state.output.fatal(e + ", error al leer el archivo de cant tareas");
        }

        //String genomeSizeForm = state.parameters.getString(base.push(P_GENOMESIZE),def.push(P_GENOMESIZE));
        String genomeSizeForm =String.valueOf(cantTareas);

        if (genomeSizeForm == null) // clearly an error
        {
            state.output.fatal("No genome size specified on CantTareas");
        }
        else if (genomeSizeForm.equals(V_GEOMETRIC))
        {
            dynamicInitialSize = true;
            genomeSize = 1;
            genomeResizeAlgorithm = C_GEOMETRIC;
            chunksize = state.parameters.getIntWithDefault(base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE),1);
            if (chunksize != 1)
                state.output.fatal("To use Geometric size initialization, VectorSpecies must have a chunksize of 1",
                        base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE));
            minInitialSize = state.parameters.getInt(base.push(P_UNIFORM_MIN),def.push(P_UNIFORM_MIN), 0);
            if (minInitialSize < 0)
            {
                state.output.warning("Gemoetric size initialization used, but no minimum initial size provided.  Assuming minimum is 0.");
                minInitialSize = 0;
            }
            genomeIncreaseProbability = state.parameters.getDoubleWithMax(base.push(P_GEOMETRIC_PROBABILITY),def.push(P_GEOMETRIC_PROBABILITY),0.0, 1.0);
            if (genomeIncreaseProbability < 0.0 || genomeIncreaseProbability >= 1.0)  // note >=
                state.output.fatal("To use Gemoetric size initialization, the genome increase probability must be >= 0.0 and < 1.0",
                        base.push(P_GEOMETRIC_PROBABILITY),def.push(P_GEOMETRIC_PROBABILITY));
        }
        else if (genomeSizeForm.equals(V_UNIFORM))
        {
            dynamicInitialSize = true;
            genomeSize = 1;
            genomeResizeAlgorithm = C_UNIFORM;
            chunksize = state.parameters.getIntWithDefault(base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE),1);
            if (chunksize != 1)
                state.output.fatal("To use Uniform size initialization, VectorSpecies must have a chunksize of 1",
                        base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE));
            minInitialSize = state.parameters.getInt(base.push(P_UNIFORM_MIN),def.push(P_UNIFORM_MIN),0);
            if (minInitialSize < 0)
                state.output.fatal("To use Uniform size initialization, you must set a minimum initial size >= 0",
                        base.push(P_UNIFORM_MIN),def.push(P_UNIFORM_MIN));
            maxInitialSize = state.parameters.getInt(base.push(P_UNIFORM_MAX),def.push(P_UNIFORM_MAX),0);
            if (maxInitialSize < 0)
                state.output.fatal("To use Uniform size initialization, you must set a maximum initial size >= 0",
                        base.push(P_UNIFORM_MAX),def.push(P_UNIFORM_MAX));
            if (maxInitialSize < minInitialSize)
                state.output.fatal("To use Uniform size initialization, you must set a maximum initial size >= the minimum initial size",
                        base.push(P_UNIFORM_MAX),def.push(P_UNIFORM_MAX));
        }
        else  // it's a number
        {
            //genomeSize = state.parameters.getInt(base.push(P_GENOMESIZE),def.push(P_GENOMESIZE),1);
            genomeSize=cantTareas;
            if (genomeSize==0)
                state.output.fatal("VectorSpecies must have a genome size > 0",
                        base.push(P_GENOMESIZE),def.push(P_GENOMESIZE));

            genomeResizeAlgorithm = C_NONE;

            chunksize = state.parameters.getIntWithDefault(base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE),1);
            if (chunksize <= 0 || chunksize > genomeSize)
                state.output.fatal("VectorSpecies must have a chunksize which is > 0 and < genomeSize",
                        base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE));
            if (genomeSize % chunksize != 0)
                state.output.fatal("VectorSpecies must have a genomeSize which is a multiple of chunksize",
                        base.push(P_CHUNKSIZE),def.push(P_CHUNKSIZE));
        }
    }


    public void setup(final EvolutionState state, final Parameter base)
    {
        Parameter def = defaultBase();
        // We will construct, but NOT set up, a sacrificial individual here.
        // Actual setup is done at the end of this method (in super.setup(...) )
        // The purpose of this sacrificial individual is to enable methods such
        // as inNumericalTypeRange() to run properly, since they require knowledge
        // of which KIND of individual it is

        i_prototype = (Individual)(state.parameters.getInstanceForParameter(
                base.push(P_INDIVIDUAL),def.push(P_INDIVIDUAL),
                Individual. class));

        // this will get thrown away and replaced with a new one during super.setup(...).



        // this might get called twice, I don't think it's a big deal
        setupGenome(state, base);


        // MUTATION

        double _mutationProbability = state.parameters.getDoubleWithMax(base.push(P_MUTATIONPROB), def.push(P_MUTATIONPROB), 0.0, 1.0);
        if (_mutationProbability == -1.0)
            state.output.fatal("Global mutation probability must be between 0.0 and 1.0 inclusive",
                    base.push(P_MUTATIONPROB),def.push(P_MUTATIONPROB));
        mutationProbability = fill(new double[genomeSize + 1], _mutationProbability);

        int _duplicateRetries = state.parameters.getIntWithDefault(base.push(P_DUPLICATE_RETRIES), def.push(P_DUPLICATE_RETRIES), 0);
        if (_duplicateRetries < 0)
        {
            state.output.fatal("Duplicate Retries, if defined, must be a value >= 0", base.push(P_DUPLICATE_RETRIES), def.push(P_DUPLICATE_RETRIES));
        }
        duplicateRetries = fill(new int[genomeSize + 1], _duplicateRetries);

        // CROSSOVER

        String ctype = state.parameters.getStringWithDefault(base.push(P_CROSSOVERTYPE), def.push(P_CROSSOVERTYPE), null);
        crossoverType = C_ONE_POINT;
        if (ctype==null)
            state.output.warning("No crossover type given for VectorSpecies, assuming one-point crossover (\"one\")",
                    base.push(P_CROSSOVERTYPE),def.push(P_CROSSOVERTYPE));
        else if (ctype.equalsIgnoreCase(V_ONE_POINT))
            crossoverType=C_ONE_POINT;  // redundant
        else if (ctype.equalsIgnoreCase(V_ONE_POINT_NO_NOP))
            crossoverType=C_ONE_POINT_NO_NOP;
        else if (ctype.equalsIgnoreCase(V_TWO_POINT))
            crossoverType=C_TWO_POINT;
        else if (ctype.equalsIgnoreCase(V_TWO_POINT_NO_NOP))
            crossoverType=C_TWO_POINT_NO_NOP;
        else if (ctype.equalsIgnoreCase(V_ANY_POINT))
            crossoverType=C_ANY_POINT;
        else if (ctype.equalsIgnoreCase(V_LINE_RECOMB))
            crossoverType=C_LINE_RECOMB;
        else if (ctype.equalsIgnoreCase(V_INTERMED_RECOMB))
            crossoverType=C_INTERMED_RECOMB;
        else if (ctype.equalsIgnoreCase(V_SIMULATED_BINARY))
            crossoverType=C_SIMULATED_BINARY;
        else state.output.fatal("VectorSpecies given a bad crossover type: " + ctype,
                    base.push(P_CROSSOVERTYPE),def.push(P_CROSSOVERTYPE));

        if (crossoverType==C_LINE_RECOMB || crossoverType==C_INTERMED_RECOMB)
        {
            if (!(this instanceof IntegerVectorSpeciesP1E1)/* && !(this instanceof FloatVectorSpecies)*/)
                state.output.fatal("Line and intermediate recombinations are only supported by IntegerVectorSpecies and FloatVectorSpecies", base.push(P_CROSSOVERTYPE), def.push(P_CROSSOVERTYPE));
            lineDistance = state.parameters.getDouble(
                    base.push(P_LINEDISTANCE), def.push(P_LINEDISTANCE), 0.0);
            if (lineDistance==-1.0)
                state.output.fatal("If it's going to use line or intermediate recombination, VectorSpecies needs a line extension >= 0.0  (0.25 is common)", base.push(P_LINEDISTANCE), def.push(P_LINEDISTANCE));
        }
        else lineDistance = 0.0;

        if (crossoverType==C_ANY_POINT)
        {
            crossoverProbability = state.parameters.getDoubleWithMax(
                    base.push(P_CROSSOVERPROB),def.push(P_CROSSOVERPROB),0.0,0.5);
            if (crossoverProbability==-1.0)
                state.output.fatal("If it's going to use any-point crossover, VectorSpecies must have a crossover probability between 0.0 and 0.5 inclusive",
                        base.push(P_CROSSOVERPROB),def.push(P_CROSSOVERPROB));
        }
        else if (crossoverType==C_SIMULATED_BINARY)
        {
            if (true/*!(this instanceof FloatVectorSpecies)*/)
                state.output.fatal("Simulated binary crossover (SBX) is only supported by FloatVectorSpecies", base.push(P_CROSSOVERTYPE), def.push(P_CROSSOVERTYPE));
            crossoverDistributionIndex = state.parameters.getInt(base.push(P_CROSSOVER_DISTRIBUTION_INDEX), def.push(P_CROSSOVER_DISTRIBUTION_INDEX), 0);
            if (crossoverDistributionIndex < 0)
                state.output.fatal("If FloatVectorSpecies is going to use simulated binary crossover (SBX), the distribution index must be defined and >= 0.",
                        base.push(P_CROSSOVER_DISTRIBUTION_INDEX), def.push(P_CROSSOVER_DISTRIBUTION_INDEX));
        }
        else crossoverProbability = 0.0;

        state.output.exitIfErrors();

        if (crossoverType != C_ANY_POINT && state.parameters.exists(base.push(P_CROSSOVERPROB),def.push(P_CROSSOVERPROB)))
            state.output.warning("The 'crossover-prob' parameter may only be used with any-point crossover.  It states the probability that a particular gene will be crossed over.  If you were looking for the probability of crossover happening at *all*, look at the 'likelihood' parameter.",
                    base.push(P_CROSSOVERPROB),def.push(P_CROSSOVERPROB));





        // SEGMENTS

        // Set number of segments to 0 by default
        int numSegments = 0;
        // Now check to see if segments of genes (genes having the same min and
        // max values) exist
        if (state.parameters.exists(base.push(P_NUM_SEGMENTS), def.push(P_NUM_SEGMENTS)))
        {
            if (dynamicInitialSize)
                state.output.warnOnce("Using dynamic initial sizing, but per-segment min/max gene declarations.  This is probably wrong.  You probably want to use global min/max declarations.",
                        base.push(P_NUM_SEGMENTS), def.push(P_NUM_SEGMENTS));

            numSegments = state.parameters.getIntWithDefault(base.push(P_NUM_SEGMENTS),
                    def.push(P_NUM_SEGMENTS), 0);

            if(numSegments == 0)
                state.output.warning(
                        "The number of genome segments has been defined to be equal to 0.\n"
                                + "Hence, no genome segments will be defined.",
                        base.push(P_NUM_SEGMENTS),
                        def.push(P_NUM_SEGMENTS));
            else if(numSegments < 0)
                state.output.fatal(
                        "Invalid number of genome segments: " + numSegments
                                + "\nIt must be a nonnegative value.",
                        base.push(P_NUM_SEGMENTS),
                        def.push(P_NUM_SEGMENTS));

            //read the type of segment definition using the default start value
            String segmentType = state.parameters.getStringWithDefault(base.push(P_SEGMENT_TYPE),
                    def.push(P_SEGMENT_TYPE), P_SEGMENT_START);

            if(segmentType.equalsIgnoreCase(P_SEGMENT_START))
                initializeGenomeSegmentsByStartIndices(state, base, def, numSegments);
            else if(segmentType.equalsIgnoreCase(P_SEGMENT_END))
                initializeGenomeSegmentsByEndIndices(state, base, def, numSegments);
            else
                state.output.fatal(
                        "Invalid specification of genome segment type: " + segmentType
                                + "\nThe " + P_SEGMENT_TYPE + " parameter must have the value of " + P_SEGMENT_START + " or " + P_SEGMENT_END,
                        base.push(P_SEGMENT_TYPE),
                        def.push(P_SEGMENT_TYPE));
        }
        state.output.exitIfErrors();



        // PER-GENE VALUES

        for (int x = 0; x < genomeSize; x++)
        {
            loadParametersForGene(state, x, base, def, "" + x);
        }
        state.output.exitIfErrors();



        // NOW call super.setup(...), which will in turn set up the prototypical individual
        super.setup(state,base);

    }


    /** Called when VectorSpecies is setting up per-gene and per-segment parameters.  The index
     is the current gene whose parameter is getting set up.  The Parameters in question are the
     bases for the gene.  The postfix should be appended to the end of any parameter looked up
     (it often contains a number indicating the gene in question), such as
     state.parameters.exists(base.push(P_PARAM).push(postfix), def.push(P_PARAM).push(postfix)

     <p>If you override this method, be sure to call super(...) at some point, ideally first.
     */
    protected void loadParametersForGene(EvolutionState state, int index, Parameter base, Parameter def, String postfix)
    {
        // our only per-gene parameter is mutation probablity.

        if (state.parameters.exists(base.push(P_MUTATIONPROB).push(postfix), def.push(P_MUTATIONPROB).push(postfix)))
        {
            mutationProbability[index] = state.parameters.getDoubleWithMax(base.push(P_MUTATIONPROB).push(postfix), def.push(P_MUTATIONPROB).push(postfix), 0.0, 1.0);
            if (mutationProbability[index] == -1.0)
                state.output.fatal("Per-gene or per-segment mutation probability must be between 0.0 and 1.0 inclusive",
                        base.push(P_MUTATIONPROB).push(postfix),def.push(P_MUTATIONPROB).push(postfix));
        }

        if (state.parameters.exists(base.push(P_DUPLICATE_RETRIES).push(postfix), def.push(P_DUPLICATE_RETRIES).push(postfix)))
        {
            duplicateRetries[index] = state.parameters.getInt(base.push(P_DUPLICATE_RETRIES).push(postfix), def.push(P_DUPLICATE_RETRIES).push(postfix));
            if (duplicateRetries[index] < 0)
                state.output.fatal("Duplicate Retries for gene " + index + ", if defined must be a value >= 0",
                        base.push(P_DUPLICATE_RETRIES).push(postfix), def.push(P_DUPLICATE_RETRIES).push(postfix));
        }

    }

    /** Looks up genome segments using start indices.  Segments run up to the next declared start index.  */
    protected void initializeGenomeSegmentsByStartIndices(final EvolutionState state,
                                                          final Parameter base,
                                                          final Parameter def,
                                                          int numSegments)
    {
        //loop in reverse order
        int previousSegmentEnd = genomeSize;
        int currentSegmentEnd = 0;

        for (int i = numSegments - 1; i >= 0; i--)
        {
            //check if the segment data exist
            if (state.parameters.exists(base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START),
                    def.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START)))
            {
                //Read the index of the end gene specifying current segment
                currentSegmentEnd = state.parameters.getInt(base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START),
                        def.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START));

            }
            else
            {
                state.output.fatal("Genome segment " + i + " has not been defined!" +
                                "\nYou must specify start indices for " + numSegments + " segment(s)",
                        base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START),
                        base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_START));
            }

            //check if the start index is valid
            if(currentSegmentEnd >= previousSegmentEnd || currentSegmentEnd < 0)
                state.output.fatal(
                        "Invalid start index value for segment " + i + ": " + currentSegmentEnd
                                +  "\nThe value must be smaller than " + previousSegmentEnd +
                                " and greater than or equal to  " + 0);

            //check if the index of the first segment is equal to 0
            if(i == 0 && currentSegmentEnd != 0)
                state.output.fatal(
                        "Invalid start index value for the first segment " + i + ": " + currentSegmentEnd
                                +  "\nThe value must be equal to " + 0);

            //and assign min and max values for all genes in this segment
            for(int j = previousSegmentEnd-1; j >= currentSegmentEnd; j--)
            {
                loadParametersForGene(state, j, base.push(P_SEGMENT).push(""+i), def.push(P_SEGMENT).push(""+i), "");
            }

            previousSegmentEnd = currentSegmentEnd;

        }

    }

    /** Looks up genome segments using end indices.  Segments run from the previously declared end index. */
    protected void initializeGenomeSegmentsByEndIndices(final EvolutionState state,
                                                        final Parameter base,
                                                        final Parameter def,
                                                        int numSegments)
    {
        int previousSegmentEnd = -1;
        int currentSegmentEnd = 0;
        // iterate over segments and set genes values for each segment
        for (int i = 0; i < numSegments; i++)
        {
            //check if the segment data exist
            if (state.parameters.exists(base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END), def.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END)))
            {
                //Read the index of the end gene specifying current segment
                currentSegmentEnd = state.parameters.getInt(base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END),
                        def.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END));

            }
            else
            {
                state.output.fatal("Genome segment " + i + " has not been defined!" +
                                "\nYou must specify end indices for " + numSegments + " segment(s)",
                        base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END),
                        base.push(P_SEGMENT).push(""+i).push(P_SEGMENT_END));
            }

            //check if the end index is valid
            if(currentSegmentEnd <= previousSegmentEnd || currentSegmentEnd >= genomeSize)
                state.output.fatal(
                        "Invalid end index value for segment " + i + ": " + currentSegmentEnd
                                +  "\nThe value must be greater than " + previousSegmentEnd +
                                " and smaller than " + genomeSize);

            //check if the index of the final segment is equal to the genomeSize
            if(i == numSegments - 1 && currentSegmentEnd != (genomeSize-1))
                state.output.fatal(
                        "Invalid end index value for the last segment " + i + ": " + currentSegmentEnd
                                +  "\nThe value must be equal to the index of the last gene in the genome:  " + (genomeSize-1));


            //and assign min and max values for all genes in this segment
            for(int j = previousSegmentEnd+1; j <= currentSegmentEnd; j++)
            {
                loadParametersForGene(state, j, base.push(P_SEGMENT).push(""+i), def.push(P_SEGMENT).push(""+i), "");
            }

            previousSegmentEnd = currentSegmentEnd;
        }
    }


    public Individual newIndividual(final EvolutionState state, int thread)

    {
        VectorIndividual newind = (VectorIndividual)(super.newIndividual(state, thread));

        if (genomeResizeAlgorithm == C_NONE)
            newind.reset( state, thread );
        else if (genomeResizeAlgorithm == C_UNIFORM)
        {
            int size = state.random[thread].nextInt(maxInitialSize - minInitialSize + 1) + minInitialSize;
            newind.reset(state, thread, size);
        }
        else if (genomeResizeAlgorithm == C_GEOMETRIC)
        {
            int size = minInitialSize;
            while(state.random[thread].nextBoolean(genomeIncreaseProbability)) size++;
            newind.reset(state, thread, size);
        }

        return newind;
    }




    // These convenience methods are used by subclasses to fill arrays and check to see if
    // arrays contain certain values.

    /** Utility method: fills the array with the given value and returns it. */
    protected long[] fill(long[] array, long val)
    {
        for(int i =0; i < array.length; i++) array[i] = val;
        return array;
    }

    /** Utility method: fills the array with the given value and returns it. */
    protected int[] fill(int[] array, int val)
    {
        for(int i =0; i < array.length; i++) array[i] = val;
        return array;
    }

    /** Utility method: fills the array with the given value and returns it. */
    protected boolean[] fill(boolean[] array, boolean val)
    {
        for(int i =0; i < array.length; i++) array[i] = val;
        return array;
    }

    /** Utility method: fills the array with the given value and returns it. */
    protected double[] fill(double[] array, double val)
    {
        for(int i =0; i < array.length; i++) array[i] = val;
        return array;
    }

    /** Utility method: returns the first array slot which contains the given value, else -1. */
    protected int contains(boolean[] array, boolean val)
    {
        for(int i =0; i < array.length; i++)
            if (array[i] == val) return i;
        return -1;
    }

    /** Utility method: returns the first array slot which contains the given value, else -1. */
    protected int contains(long[] array, long val)
    {
        for(int i =0; i < array.length; i++)
            if (array[i] == val) return i;
        return -1;
    }

    /** Utility method: returns the first array slot which contains the given value, else -1. */
    protected int contains(int[] array, int val)
    {
        for(int i =0; i < array.length; i++)
            if (array[i] == val) return i;
        return -1;
    }

    /** Utility method: returns the first array slot which contains the given value, else -1. */
    protected int contains(double[] array, double val)
    {
        for(int i =0; i < array.length; i++)
            if (array[i] == val) return i;
        return -1;
    }
}


