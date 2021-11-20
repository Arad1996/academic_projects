package edu.yu.introtoalgs;

/**
 * Enhances the Queue enqueue() and dequeue() API with a O(1) max()
 * method and O(1) size().  The dequeue() method is amortized O(1),
 * enqueue() is amortized O(1).  The implementation is O(n) in space.
 *
 * @author Avraham Leff
 */

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class MaxQueue {
    static class MaxHeap {
        private int[] elements;
        private int size;

        public MaxHeap() {
            this.size = 0;
            elements = new int[2];
            elements[0] = Integer.MAX_VALUE;
        }

        private int parent(int pos) {
            return pos / 2;
        }

        private int leftChild(int pos) {
            return (2 * pos);
        }

        private int rightChild(int pos) {
            return (2 * pos) + 1;
        }

        private void swap(int i, int j) {
            int tmp;
            tmp = elements[i];
            elements[i] = elements[j];
            elements[j] = tmp;
        }

        private void downHeapify(int pos) {
            if (pos > (size / 2) && pos <= size)
                return;

            if (elements[pos] < elements[leftChild(pos)] ||
                    elements[pos] < elements[rightChild(pos)]) {

                if (elements[leftChild(pos)] > elements[rightChild(pos)]) {
                    swap(pos, leftChild(pos));
                    downHeapify(leftChild(pos));
                } else {
                    swap(pos, rightChild(pos));
                    downHeapify(rightChild(pos));
                }
            }
        }

        private void heapifyUp(int pos) {
            int temp = elements[pos];
            while (pos > 0 && temp > elements[parent(pos)]) {
                elements[pos] = elements[parent(pos)];
                pos = parent(pos);
            }
            elements[pos] = temp;
        }

        public void insert(int element) {
            if (size >= elements.length - 1) {
                doubleArraySize();
            }

            elements[++size] = element;

            int current = size;
            heapifyUp(current);
        }

        public String toString() {
            String result = "";
            for (int i = 1; i <= size / 2; i++) {
                result += (elements[i] + ": L- " + elements[2 * i] + " R- " + elements[2 * i + 1]);
                result += "\n";
            }
            return result;
        }

        public int extractMax() {
            int max = elements[1];
            elements[1] = elements[size--];
            elements[size+1] = 0;
            downHeapify(1);
            return max;
        }

        public int getMax() {
            return elements[1];
        }

        public boolean isEmpty() {
            return size == 0;
        }

        protected void doubleArraySize() {
            int[] newArray = new int[this.elements.length * 2];
            System.arraycopy(this.elements, 0, newArray, 0, size + 1);
            this.elements = newArray;
        }
    }

    ArrayDeque<Integer> queue;
    MaxHeap heap;
    HashMap<Integer, Integer> counts;

    /**
     * No-argument constructor: students may not add any other constructor for
     * this class
     */
    public MaxQueue() {
        this.queue = new ArrayDeque<>();
        this.heap = new MaxHeap();
        this.counts = new HashMap<>();
    }

    /**
     * Insert the element with FIFO semantics
     * Add the x to max heap for easy max retrieval
     * Increase count of x by one in the counts hash map
     *
     * @param x the element to be inserted.
     */
    public void enqueue(int x) {
        queue.add(x);
        int valuesCount = counts.getOrDefault(x, 0);
        counts.put(x, valuesCount + 1);

        if (valuesCount == 0) {
            heap.insert(x);
        }
    }

    /**
     * Dequeue an element with FIFO semantics.
     *
     * @return the element that satisfies the FIFO semantics if the queue is not
     * empty.
     * @throws NoSuchElementException if the queue is empty
     */
    public int dequeue() {
        if (queue.size() == 0) {
            throw new NoSuchElementException();
        }
        int maxValue = max();
        int resultElement = queue.removeLast();     // O(1)

        // Reduce the count of items int the counts hash table
        int prevCount = counts.get(resultElement);      // O(1)
        counts.put(resultElement, prevCount - 1);       // O(1)

        // We should remove the max value from the heap if we have dequeued it
        // and there are no more equal values in the queue
        if (resultElement == maxValue && prevCount <= 1) {
            heap.extractMax();      // O(log(n))

            // Perform maintenance on the heap. Make sure the max value still exist in the queue.
            // If it does not exist, extract it
            while (!heap.isEmpty() && counts.get(heap.getMax()) == 0) {       // Amortized O(log(n))
                heap.extractMax();
            }
        }

        return resultElement;
    }

    /**
     * Returns the number of elements in the queue
     *
     * @return number of elements in the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns the element with the maximum value
     * Time complexity is O(1) because of the implementation of the MaxHeap:
     * we just return the first value in the array
     *
     * @return the element with the maximum value
     * @throws NoSuchElementException if the queue is empty
     */
    public int max() {
        return heap.getMax();
    }
}