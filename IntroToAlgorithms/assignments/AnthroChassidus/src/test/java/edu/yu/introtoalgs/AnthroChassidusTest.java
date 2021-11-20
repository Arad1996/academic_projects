package edu.yu.introtoalgs;

import org.junit.*;

import java.util.Arrays;


public class AnthroChassidusTest {
    @Test
    public void testMinimal() {
        // There are 50 different people in the group, and the only fact I know is
        // that person 0 and person 1 share the same chassidus
        final int n = 50;
        final int[] a = {0};
        final int[] b = {1};

        final AnthroChassidus ac = new AnthroChassidus(n, a, b);
        Assert.assertEquals("wrong # of Chassidus instances", ac.getLowerBoundOnChassidusTypes(), 49);
        Assert.assertEquals("wrong # of people who share person #0's Chassidus", ac.nShareSameChassidus(0), 2);
        Assert.assertEquals("wrong # of people who share person #1's Chassidus", ac.nShareSameChassidus(1), 2);
    }

    @Test
    public void testMaximal() {
        final int n = 50;
        int[] a = new int[n - 1];
        int[] b = new int[n - 1];

        for (int i = 0; i < n - 1; i++) {
            a[i] = 0;
            b[i] = i + 1;
        }

        final AnthroChassidus ac = new AnthroChassidus(n, a, b);
        Assert.assertEquals("wrong # of Chassidus instances", ac.getLowerBoundOnChassidusTypes(), 1);
        Assert.assertEquals("wrong # of people who share person #0's Chassidus", ac.nShareSameChassidus(0), 50);
        Assert.assertEquals("wrong # of people who share person #1's Chassidus", ac.nShareSameChassidus(1), 50);
    }

    @Test
    public void test6Pairs() {
        final int n = 50;
        int[] a = {0,1,2,3,4,5};
        int[] b = {1,2,3,4,3,6};


        final AnthroChassidus ac = new AnthroChassidus(n, a, b);
        Assert.assertEquals("wrong # of Chassidus instances", ac.getLowerBoundOnChassidusTypes(), 45);
        Assert.assertEquals("wrong # of people who share person #0's Chassidus", ac.nShareSameChassidus(0), 5);
        Assert.assertEquals("wrong # of people who share person #1's Chassidus", ac.nShareSameChassidus(5), 2);
    }
}