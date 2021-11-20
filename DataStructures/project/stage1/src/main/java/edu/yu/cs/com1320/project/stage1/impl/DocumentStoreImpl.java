package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private final HashTable<URI, DocumentImpl> hashTable;

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        // This is a delete
        if (input == null) {
            DocumentImpl result = hashTable.put(uri, null);
            if (result == null) {
                return 0;
            }
            return result.hashCode();

        }

        byte[] bytes = input.readAllBytes();
        DocumentImpl document;

        if (format == DocumentFormat.BINARY) {
            document = new DocumentImpl(uri, bytes);
        } else {
            String string = new String(bytes);
            document = new DocumentImpl(uri, string);
        }

        DocumentImpl result = hashTable.put(uri, document);
        if (result != null) {
            return result.hashCode();
        }
        return 0;
    }

    @Override
    public Document getDocument(URI uri) {
        return hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        DocumentImpl result = hashTable.put(uri, null);

        if (result == null) {
            return false;
        }
        return true;
    }
}
