package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapImplTest {

    @Test
    void reHeapify() throws URISyntaxException {
        MinHeapImpl minHeap = new MinHeapImpl();
        DocumentImpl document = new DocumentImpl(new URI("http://test.com"), "This is a test! test it is a good test");
        DocumentImpl document1 = new DocumentImpl(new URI("http://test1.com"), "This is a test1! test it is a good test1");
        DocumentImpl document2 = new DocumentImpl(new URI("http://test2.com"), "This is a test1! test it is a good test2");
        DocumentImpl document3 = new DocumentImpl(new URI("http://test3.com"), "This is a test1! test it is a good test3");
        minHeap.insert(document);
        minHeap.insert(document1);
        minHeap.insert(document2);
        minHeap.insert(document3);
        assertEquals(minHeap.getArrayIndex(document2), 3);
        document.setLastUseTime(System.nanoTime());
        minHeap.reHeapify(document);
        assertEquals(minHeap.getArrayIndex(document), 4);
        assertEquals(minHeap.getArrayIndex(document1), 1);
    }

    @Test
    void doubleArraySize() throws URISyntaxException {
        MinHeapImpl minHeap = new MinHeapImpl();
        DocumentImpl document = new DocumentImpl(new URI("http://test.com"), "This is a test! test it is a good test");
        minHeap.insert(document);
        minHeap.doubleArraySize();
        assertEquals(minHeap.getArrayIndex(document), 1);
    }
}