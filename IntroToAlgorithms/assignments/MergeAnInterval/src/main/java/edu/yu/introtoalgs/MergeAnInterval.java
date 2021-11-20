package edu.yu.introtoalgs;

import java.util.*;

import static java.util.function.Predicate.not;

/**
 * Implements the "Add an Interval To a Set of Intervals" semantics defined in
 * the requirements document.
 *
 * @author Avraham Leff
 */

public class MergeAnInterval {

    /**
     * An immutable class, holds a left and right integer-valued pair that
     * defines a closed interval
     * <p>
     * IMPORTANT: students may not modify the semantics of the "left", "right"
     * instance variables, nor may they use any other constructaor signature.
     * Students may (are encouraged to) add any other methods that they choose,
     * bearing in mind that my tests will ONLY DIRECTLY INVOKE the constructor
     * and the "merge" method.
     */
    public static class Interval implements Comparable<Interval> {
        public final int left;
        public final int right;

        /**
         * Constructor
         *
         * @param l the left endpoint of the interval, may be negative
         * @param r the right endpoint of the interval, may be negative
         * @throws IllegalArgumentException if left is >= right
         */
        public Interval(int l, int r) {
            if (l >= r) {
                throw new IllegalArgumentException();
            }
            this.left = l;
            this.right = r;
        }

        /**
         * Check if intervals intersects with each other.
         * Intervals considered intersecting if any boundary of one of the intervals is inside another interval.
         *
         * @param other interval for comparison
         * @return true if there is any intersection between the intervals false otherwise
         */
        public boolean intersects(Interval other) {
            return this.left <= other.right && this.right >= other.left;
        }

        /**
         * @param other interval
         * @return join of two intervals
         * if intervals do not intersect, return null
         */
        public Interval join(Interval other) {
            if (!intersects(other)) {
                return null;
            }

            int l = Math.min(this.left, other.left);
            int r = Math.max(this.right, other.right);

            return new Interval(l, r);
        }

        public boolean equals(Object other) {
            if (!(other instanceof Interval)) {
                return false;
            }
            return this.compareTo((Interval) other) == 0;
        }

        @Override
        public int compareTo(Interval o) {
            if (this.left > o.left) {
                return 1;
            }
            if (this.left < o.left) {
                return -1;
            }

            return Integer.compare(this.right, o.right);
        }

        @Override
        /*
          Required for sets comparison in tests
         */
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return "[" + left +
                    ", " + right +
                    ']';
        }
    } // Interval class

    /**
     * Merges the new interval into an existing set of disjoint intervals.
     *
     * @param intervals   an set of disjoint intervals (may be empty)
     * @param newInterval the interval to be added
     * @return a new set of disjoint intervals containing the original intervals
     * and the new interval, merging the new interval if necessary into existing
     * interval(s), to preseve the "disjointedness" property.
     * @throws IllegalArgumentException if either parameter is null
     */
    public static Set<Interval> merge(final Set<Interval> intervals, Interval newInterval) {
        Interval[] intervalsArr = new Interval[intervals.size()];
        intervals.toArray(intervalsArr);
        Arrays.sort(intervalsArr); // nlog(n) according to documentations

        // Now we have an array with sorted disjoint intervals
        // The intervals are sorted by left boundary
        int firstIntersectIndex = -1;
        int lastIntersectIndex = -1;

        // Copy the original interval
        Interval joinedInterval = new Interval(newInterval.left, newInterval.right);

        // Find first intersection
        for (int i = 0; i < intervalsArr.length; i++) {
            if (newInterval.intersects(intervalsArr[i])) {
                firstIntersectIndex = i;
                break;
            }
        }

        // If no intersection detected, return copy of original set with added new interval
        if (firstIntersectIndex == -1) {
            HashSet<Interval> result = new HashSet<>(intervals);
            result.add(newInterval);
            return result;
        }

        // Find the last intersecting interval
        for (int i = firstIntersectIndex; i < intervalsArr.length; i++) {
            if (joinedInterval.intersects(intervalsArr[i])) {
                joinedInterval = joinedInterval.join(intervalsArr[i]);
            } else {
                lastIntersectIndex = i - 1;
                break;
            }
        }

        // If lastIntersectIndex is -1, means the last interval in the array intersects with the new interval
        if (lastIntersectIndex == -1) {
            // Update the lastIntersect index to be the last item in the array
            lastIntersectIndex = intervalsArr.length - 1;
        }

        // Leave only not intersecting intervals
        HashSet<Interval> result = new HashSet<>();
        List<Interval> intervals1 = Arrays.asList(Arrays.copyOfRange(intervalsArr, 0, firstIntersectIndex));
        List<Interval> intervals2 = Arrays.asList(Arrays.copyOfRange(intervalsArr, lastIntersectIndex + 1, intervalsArr.length));

        result.addAll(intervals1);
        result.addAll(intervals2);

        // Finally, add the new joined interval
        result.add(joinedInterval);

        return result;
    }
}