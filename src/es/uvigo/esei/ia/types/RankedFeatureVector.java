/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

/**
 * A FeatureVector for which you can efficiently get the feature with
 * highest value, and other ranks.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package es.uvigo.esei.ia.types;


public class RankedFeatureVector extends FeatureVector {

    private static final int SORTINIT = -1;
    int[] rankOrder;
    int sortedTo = SORTINIT; /* Extent of latest sort */

    public RankedFeatureVector(Alphabet dict, int[] indices, double[] values) {
        super(dict, indices, values);
    }

    public RankedFeatureVector(Alphabet dict, double[] values) {
        super(dict, values);
    }

    public RankedFeatureVector(Alphabet dict, DenseVector v) {
        this(dict, v.values);
    }

    public RankedFeatureVector(Alphabet dict, AugmentableFeatureVector v) {
        super(dict, v.indices, v.values, v.size, v.size, true, true, true);
    }

    public RankedFeatureVector(Alphabet dict, SparseVector v) {
        super(dict, v.indices, v.values);
    }

    // xxx This bubble sort is a major inefficiency.
    // Implement a O(n log(n)) method!
    // No longer used!
    protected void setRankOrder() {
        this.rankOrder = new int[values.length];

        for (int i = 0; i < rankOrder.length; i++) {
            rankOrder[i] = i;
            //assert(!Double.isNaN(values[i]));
        }

        // BubbleSort from back
        for (int i = rankOrder.length - 1; i >= 0; i--) {

            //if (i % 1000 == 0)
            //System.out.println ("RankedFeatureVector.setRankOrder i="+i);


            for (int j = 0; j < i; j++)

                if (values[rankOrder[j]] < values[rankOrder[j + 1]]) {

                    // swap
                    int r = rankOrder[j];
                    rankOrder[j] = rankOrder[j + 1];
                    rankOrder[j + 1] = r;
                }
        }
    }

    protected void setRankOrder(int extent, boolean reset) {

        int sortExtent;

        // Set the number of cells to sort, making sure we don't go past the max.
        // Since we are using insertion sort, sorting n-1 sorts the whole array.
        sortExtent = (extent >= values.length) ? values.length - 1 : extent;

        if (sortedTo == SORTINIT || reset) { // reinitialize and sort
            this.rankOrder = new int[values.length];

            for (int i = 0; i < rankOrder.length; i++) {
                rankOrder[i] = i;
                //assert(!Double.isNaN(values[i]));
            }
        }

        // Selection sort
        for (int i = sortedTo + 1; i <= sortExtent; i++) {

            double max = values[rankOrder[i]];
            int maxIndex = i;

            for (int j = i + 1; j < rankOrder.length; j++) {

                if (values[rankOrder[j]] > max) {
                    max = values[rankOrder[j]];
                    maxIndex = j;
                }
            }

            //swap
            int r = rankOrder[maxIndex];
            rankOrder[maxIndex] = rankOrder[i];
            rankOrder[i] = r;
            sortedTo = i;
        }
    }

    protected void setRankOrder(int extent) {
        setRankOrder(extent, false);
    }

    public int getMaxValuedIndex() {

        if (rankOrder == null)
            setRankOrder(0);

        return rankOrder[0];
    }

    public Object getMaxValuedObject() {

        return dictionary.lookupObject(getMaxValuedIndex());
    }

    public int getMaxValuedIndexIn(FeatureSelection fs) {

        if (fs == null)

            return getMaxValuedIndex();

        //assert(fs.getAlphabet() == dictionary);

        // xxx Make this more efficient!  I'm pretty sure that Java BitSet's can do this more efficiently
        int i = 0;

        while (!fs.contains(rankOrder[i])) {
            setRankOrder(i);
            i++;
        }

        //System.out.println ("RankedFeatureVector.getMaxValuedIndexIn feature="
        //+dictionary.lookupObject(rankOrder[i]));
        return rankOrder[i];
    }

    public Object getMaxValuedObjectIn(FeatureSelection fs) {

        return dictionary.lookupObject(getMaxValuedIndexIn(fs));
    }

    public double getMaxValue() {

        if (rankOrder == null)
            setRankOrder(0);

        return values[rankOrder[0]];
    }

    public double getMaxValueIn(FeatureSelection fs) {

        if (fs == null)

            return getMaxValue();

        int i = 0;

        while (!fs.contains(i)) {
            setRankOrder(i);
            i++;
        }

        return values[rankOrder[i]];
    }

    public int getIndexAtRank(int rank) {
        setRankOrder(rank);

        return rankOrder[rank];
    }

    public Object getObjectAtRank(int rank) {
        setRankOrder(rank);

        return dictionary.lookupObject(rankOrder[rank]);
    }

    public double getValueAtRank(int rank) {

        if (values == null)

            return 1.0;

        setRankOrder(rank);

        if (rank >= rankOrder.length) {
            rank = rankOrder.length - 1;
            System.err.println(
                    "rank larger than rankOrder.length. rank = " + rank +
                            "rankOrder.length = " + rankOrder.length);
        }

        if (rankOrder[rank] >= values.length) {
            System.err.println("rankOrder[rank] out of range.");

            return 1.0;
        }

        return values[rankOrder[rank]];
    }

    public int getRank(Object o) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int getRank(int index) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void set(int i, double v) {
        throw new UnsupportedOperationException(RankedFeatureVector.class.getName() +
                " is immutable");
    }

    public interface Factory {
        RankedFeatureVector newRankedFeatureVector(Instance[] ilist, LabelAlphabet labels, Alphabet vocab);
    }

    public interface PerLabelFactory {
        RankedFeatureVector[] newRankedFeatureVectors(Instance[] ilist, LabelAlphabet labels, Alphabet vocab);
    }
}
