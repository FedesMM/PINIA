package ec.vector;

import ec.*;

public abstract class MatrixIndividual extends Individual {

    /** Destructively crosses over the individual with another in some default manner.  In most
     implementations provided in ECJ, one-, two-, and any-point crossover is done with a
     for loop, rather than a possibly more efficient approach like arrayCopy().  The disadvantage
     is that arrayCopy() takes advantage of a CPU's bulk copying.  The advantage is that arrayCopy()
     would require a scratch array, so you'd be allocing and GCing an array for every crossover.
     Dunno which is more efficient.  */
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) { }

    /** Destructively mutates the individual in some default manner.  The default version calls reset()*/
    public void defaultMutate(EvolutionState state, int thread) {
        reset(state,thread);
    }

    /** Initializes the individual. */
    public abstract void reset(EvolutionState state, int thread);

    /** Returns the gene array.  If you know the type of the array, you can cast it and work on
     it directly.  Otherwise, you can still manipulate it in general, because arrays (like
     all objects) respond to clone() and can be manipulated with arrayCopy without bothering
     with their type.  This might be useful in creating special generalized crossover operators
     -- we apologize in advance for the fact that Java doesn't have a template system.  :-(
     The default version returns null. */
    public Object getGenome() {
        return null;
    }

    /** Sets the gene array.  See getGenome().  The default version does nothing.
     */
    public void setGenome(Object gen) { }

    /** Returns the length of the gene array.  By default, this method returns 0. */
    public int genomeLength() {
        return 0;
    }

    /** Initializes the individual to a new size.  Only use this if you need to initialize variable-length individuals. */
    public void reset(EvolutionState state, int thread, int newSize) {
        setGenomeLength(newSize);
        reset(state, thread);
    }

    /** Sets the genome length.  If the length is longer, then it is filled with a default value (likely 0 or false).
     This may or may not be a valid value -- you will need to set appropriate values here.
     The default implementation does nothing; but all subclasses in ECJ implement a subset of this. */
    public void setGenomeLength(int len) { }

    /** Splits the genome into n pieces, according to points, which *must* be sorted.
     pieces.length must be 1 + points.length.  The default form does nothing -- be careful
     not to use this method if it's not implemented!  It should be trivial to implement it
     for your genome -- just like at the other implementations.  */
    public void split(int[] points, Object[] pieces) { }

    /** Joins the n pieces and sets the genome to their concatenation.  The default form does nothing.
     It should be trivial to implement it
     for your genome -- just like at the other implementations.  */
    public void join(Object[] pieces) { }

    /** Clones the genes in pieces, and replaces the genes with their copies.  Does NOT copy the array, but modifies it in place.
     If the VectorIndividual holds numbers or booleans etc. instead of genes, nothing is cloned
     (why bother?). */
    public void cloneGenes(Object piece) { }  // default does nothing.

    public long size() {
        return genomeLength();
    }
}
