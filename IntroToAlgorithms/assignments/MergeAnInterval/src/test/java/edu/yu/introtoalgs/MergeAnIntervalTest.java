package edu.yu.introtoalgs;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class MergeAnIntervalTest {
    @Test
    public void testIntervalCompare() {
        MergeAnInterval.Interval interval1 = new MergeAnInterval.Interval(0, 1);
        MergeAnInterval.Interval interval2 = new MergeAnInterval.Interval(-1, 1);
        MergeAnInterval.Interval interval3 = new MergeAnInterval.Interval(1, 2);
        MergeAnInterval.Interval interval4 = new MergeAnInterval.Interval(-1, 4);
        MergeAnInterval.Interval interval5 = new MergeAnInterval.Interval(-1, 4);

        assertEquals(0, interval4.compareTo(interval5));
        assertEquals(1, interval1.compareTo(interval2));
        assertEquals(-1, interval4.compareTo(interval3));
        assertEquals(-1, interval2.compareTo(interval4));
    }

    @Test
    public void testIntersect() {
        MergeAnInterval.Interval interval1 = new MergeAnInterval.Interval(0, 1);
        MergeAnInterval.Interval interval2 = new MergeAnInterval.Interval(-1, 1);
        MergeAnInterval.Interval interval3 = new MergeAnInterval.Interval(1, 2);
        MergeAnInterval.Interval interval4 = new MergeAnInterval.Interval(-1, 4);
        MergeAnInterval.Interval interval5 = new MergeAnInterval.Interval(-1, 4);
        MergeAnInterval.Interval interval6 = new MergeAnInterval.Interval(3, 4);

        assertTrue(interval1.intersects(interval2));
        assertTrue(interval2.intersects(interval3));
        assertTrue(interval3.intersects(interval4));
        assertTrue(interval4.intersects(interval5));
        assertTrue(interval1.intersects(interval4));
        assertTrue(interval1.intersects(interval5));
        assertTrue(interval5.intersects(interval1));
        assertFalse(interval1.intersects(interval6));
        assertFalse(interval6.intersects(interval2));
    }

    @Test
    public void testMerge() {
        MergeAnInterval.Interval interval1 = new MergeAnInterval.Interval(0, 1);
        MergeAnInterval.Interval interval2 = new MergeAnInterval.Interval(2, 3);
        MergeAnInterval.Interval interval3 = new MergeAnInterval.Interval(4, 5);
        MergeAnInterval.Interval interval4 = new MergeAnInterval.Interval(6, 7);
        MergeAnInterval.Interval interval5 = new MergeAnInterval.Interval(8, 9);
        MergeAnInterval.Interval interval6 = new MergeAnInterval.Interval(10, 11);
        MergeAnInterval.Interval interval7 = new MergeAnInterval.Interval(16, 17);

        HashSet<MergeAnInterval.Interval> intervals = new HashSet<>();
        intervals.add(interval1);
        intervals.add(interval2);
        intervals.add(interval3);
        intervals.add(interval4);
        intervals.add(interval5);
        intervals.add(interval6);
        intervals.add(interval7);

        HashSet<MergeAnInterval.Interval> expectedResult;

        expectedResult = new HashSet<>();
        expectedResult.add(new MergeAnInterval.Interval(-1, 18));
        assertEquals(
                MergeAnInterval.merge(intervals, new MergeAnInterval.Interval(-1, 18)),
                expectedResult
        );

        expectedResult = new HashSet<>(intervals);
        expectedResult.add(new MergeAnInterval.Interval(13, 14));
        assertEquals(
                MergeAnInterval.merge(intervals, new MergeAnInterval.Interval(13, 14)),
                expectedResult
        );

        expectedResult = new HashSet<>();
        expectedResult.add(interval1);
        expectedResult.add(interval2);
        expectedResult.add(new MergeAnInterval.Interval(4, 18));
        assertEquals(
                MergeAnInterval.merge(intervals, new MergeAnInterval.Interval(4, 18)),
                expectedResult
        );

        expectedResult = new HashSet<>();
        expectedResult.add(interval7);
        expectedResult.add(new MergeAnInterval.Interval(-1, 14));
        assertEquals(
                MergeAnInterval.merge(intervals, new MergeAnInterval.Interval(-1, 14)),
                expectedResult
        );

    }
}