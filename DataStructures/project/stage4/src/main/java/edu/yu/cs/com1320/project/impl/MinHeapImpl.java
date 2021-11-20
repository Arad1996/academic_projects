package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage4.Document;

import java.util.NoSuchElementException;


public class MinHeapImpl extends MinHeap<Document> {
    private static final int initialSize = 10;

    public MinHeapImpl() {
        this.elements = new Document[initialSize];

    }

    @Override
    public void reHeapify(Document element) {
        int i = getArrayIndex(element);

        // element greater then parent
        if (i > 1 && isGreater(i / 2, i)) {
            upHeap(i);
        } // element bigger than left or right child
        else if (i * 2 < count && (
                isGreater(i, i * 2 + 1) ||
                isGreater(i, i * 2 + 2)
        )
        ) {
            downHeap(i);
        }
    }

    @Override
    protected int getArrayIndex(Document element) throws NoSuchElementException {
        for (int i = 0; i < elements.length; i++) {
            if (element.equals(elements[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        Document[] newArray = new Document[initialSize];
        System.arraycopy(this.elements, 0, newArray, 0, elements.length);
    }
}
