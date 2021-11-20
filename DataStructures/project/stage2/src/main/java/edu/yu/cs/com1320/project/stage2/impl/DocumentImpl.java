package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.stage2.Document;

import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements Document {
    private URI uri;
    private String text;
    private byte[] binaryData;

    public DocumentImpl(URI uri, String txt) throws IllegalArgumentException {
        if (uri == null || uri.toString().isEmpty()) throw new IllegalArgumentException();
        if (txt == null || txt.isEmpty())  throw new IllegalArgumentException();

        this.uri = uri;
        this.text = txt;
    }

    public DocumentImpl(URI uri, byte[] binaryData) throws IllegalArgumentException {
        if (uri == null || uri.toString().isEmpty()) throw new IllegalArgumentException();
        if (binaryData == null || (new String(binaryData)).isEmpty())  throw new IllegalArgumentException();

        this.uri = uri;
        this.binaryData = binaryData;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public String getDocumentTxt() {
        return text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return binaryData;
    }

    @Override
    public URI getKey() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }
}
