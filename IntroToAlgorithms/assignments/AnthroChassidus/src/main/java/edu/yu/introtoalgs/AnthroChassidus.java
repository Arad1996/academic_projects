package edu.yu.introtoalgs;

import java.util.ArrayList;

/**
 * Defines and implements the AnthroChassidus API per the requirements
 * documentation.
 *
 * @author Avraham Leff
 */

public class AnthroChassidus {
    public class WeightedQuickUnionPathCompressionUF {
        /**
         * Based on code from textbook page 228 and improvement from slides
         * As stated the time for n UF operations is "almost linear"
         */
        private int[] id; // parent link
        private int[] size; // size of component for roots (site indexed)
        private int count; // number of components

        public WeightedQuickUnionPathCompressionUF(int N) {
            count = N;
            id = new int[N];

            // Start with each ID in own set
            for (int i = 0; i < N; i++) {
                id[i] = i;
            }

            // Initialize size of all the sets to 1
            size = new int[N];
            for (int i = 0; i < N; i++) {
                size[i] = 1;
            }
        }

        public int count() {
            return count;
        }

        public int getSize(int id) {
            int root = find(id);
            return size[root];
        }

        public boolean connected(int p, int q) {
            return find(p) == find(q);
        }

        private int find(int p) {
            // Follow links to find a root and compress the tree
            ArrayList<Integer> visitedIds = new ArrayList<>();
            while (p != id[p]) {
                visitedIds.add(p);
                p = id[p];
            }

            // Compress the tree
            for (int visitedId : visitedIds) {
                id[visitedId] = p;
            }

            return p;
        }

        public void union(int p, int q) {
            int i = find(p);
            int j = find(q);

            if (i == j)
                return;

            // Make smaller root point to larger one.
            if (size[i] < size[j]) {
                id[i] = j;
                size[j] += size[i];
            } else {
                id[j] = i;
                size[i] += size[j];
            }
            count--;
        }
    }

    /**
     * Constructor.  When the constructor completes, ALL necessary processing
     * for subsequent API calls have been made such that any subsequent call will
     * incur an O(1) cost.
     *
     * @param n the size of the underlying population that we're investigating:
     * need not correspond in any way to the number of people actually
     * interviewed (i.e., the number of elements in the "a" and "b" parameters).
     * Must be greater than 2.
     * @param a interviewed people, element value corresponds to a unique "person
     * id" in the range 0..n-1
     * @param b interviewed people, element value corresponds to a unique "person
     * id" in the range 0..n-1.  Pairs of a_i and b_i entries represent the fact
     * that the corresponding people follow the same Chassidus (without
     * specifying what that Chassidus is).
     */

    WeightedQuickUnionPathCompressionUF wquf;
    int[] sizes;

    public AnthroChassidus(final int n, final int[] a, final int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("a and b should be of the same length");
        }

        sizes = new int[n];

        wquf = new WeightedQuickUnionPathCompressionUF(n);
        for (int i = 0; i < a.length; i++) {
            wquf.union(a[i], b[i]);
        }

        for (int id = 0; id < n; id++) {
            sizes[id] = wquf.getSize(id);
        }
    }

    /**
     * Return the tightest value less than or equal to "n" specifying how many
     * types of Chassidus exist in the population: this answer is inferred from
     * the interviewers data supplied to the constructor
     *
     * @return tightest possible lower bound on the number of Chassidus in the
     * underlying population.
     */
    public int getLowerBoundOnChassidusTypes() {
        return wquf.count();
    }

    /**
     * Return the number of interviewed people who follow the same Chassidus as
     * this person.
     *
     * @param id uniquely identifies the interviewed person
     * @return the number of interviewed people who follow the same Chassidus as
     * this person.
     */
    public int nShareSameChassidus(final int id) {
        return sizes[id];
    }

}