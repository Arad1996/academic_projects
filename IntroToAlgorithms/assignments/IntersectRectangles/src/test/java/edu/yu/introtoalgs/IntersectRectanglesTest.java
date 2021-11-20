package edu.yu.introtoalgs;

import org.junit.*;

import static edu.yu.introtoalgs.IntersectRectangles.NO_INTERSECTION;

public class IntersectRectanglesTest {
    @Test
    public void testIntersect() {
        IntersectRectangles.Rectangle D = new IntersectRectangles.Rectangle(0, 0, 2, 5);
        IntersectRectangles.Rectangle F = new IntersectRectangles.Rectangle(2, 0, 2, 5);
        IntersectRectangles.Rectangle G = new IntersectRectangles.Rectangle(1, 1, 1, 1);

        Assert.assertEquals(
                new IntersectRectangles.Rectangle(2, 0, 0, 5),
                IntersectRectangles.intersect(D, F)
        );

        Assert.assertEquals(
                new IntersectRectangles.Rectangle(2, 0, 0, 5),
                IntersectRectangles.intersect(F, D)
        );

        Assert.assertEquals(
                D,
                IntersectRectangles.intersect(D, D)
        );

        Assert.assertEquals(
                F,
                IntersectRectangles.intersect(F, F)
        );

        Assert.assertEquals(
                G,
                IntersectRectangles.intersect(D, G)

        );

    }

    @Test
    public void testNotIntersect() {
        IntersectRectangles.Rectangle D = new IntersectRectangles.Rectangle(0, 0, 2, 5);
        IntersectRectangles.Rectangle F = new IntersectRectangles.Rectangle(3, 0, 2, 5);

        Assert.assertEquals(
                NO_INTERSECTION,
                IntersectRectangles.intersect(D, F)
        );

        Assert.assertEquals(
                NO_INTERSECTION,
                IntersectRectangles.intersect(F, D)
        );
    }
}