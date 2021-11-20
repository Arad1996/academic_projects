package edu.yu.introtoalgs;

import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class MaxQueueTest {
    @Test
    public void testMaxQueue() {
        MaxQueue maxQueue = new MaxQueue();
        maxQueue.enqueue(1);
        maxQueue.enqueue(2);
        maxQueue.enqueue(3);
        maxQueue.enqueue(10);
        maxQueue.enqueue(30);
        maxQueue.enqueue(30);
        maxQueue.enqueue(15);
        maxQueue.enqueue(6);

        while (maxQueue.size() > 0) {
            int expectedMax = naiveMax(maxQueue);
            Assert.assertEquals(expectedMax, maxQueue.max());
            maxQueue.dequeue();
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testMaxQueueThrowsException() {
        MaxQueue maxQueue = new MaxQueue();
        maxQueue.enqueue(1);
        maxQueue.enqueue(15);
        maxQueue.enqueue(10);
        maxQueue.dequeue();
        maxQueue.dequeue();
        maxQueue.dequeue();
        maxQueue.dequeue(); // Should throw and exception
    }

    @Test
    public void testMaxHeap() {
        MaxQueue.MaxHeap heap = new MaxQueue.MaxHeap();
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(12);
        heap.insert(6);
        heap.insert(4);
        System.out.println(heap);
        while (!heap.isEmpty()) {
            System.out.println(heap.extractMax());
        }
    }

    public int naiveMax(MaxQueue maxQueue) {
        int max = 0;
        Iterator<Integer> iterator = maxQueue.queue.iterator();
        while(iterator.hasNext()) {
            int value = iterator.next();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}