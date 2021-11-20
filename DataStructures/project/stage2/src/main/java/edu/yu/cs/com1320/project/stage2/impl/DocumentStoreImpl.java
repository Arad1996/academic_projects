package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private final HashTable<URI, DocumentImpl> hashTable;
    private Stack<Command> undoStack = new StackImpl<>();

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        // This is a delete
        if (input == null) {
            DocumentImpl result = hashTable.put(uri, null);

            // Undo delete document
            Command command = new Command(uri, uri_ -> hashTable.put(uri_, result) != null);
            undoStack.push(command);

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

        // The undo command will handle both new uri added and existing uri with new document
        Command command = new Command(uri, uri_ -> hashTable.put(uri_, result) != null);
        undoStack.push(command);

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

    @Override
    public void undo() throws IllegalStateException {
        Command command = undoStack.pop();
        if (command == null) {
            throw new IllegalStateException("The stack is empty");
        }
        command.undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        Stack<Command> tmpStack = new StackImpl<>();
        Command command;

        while (true) {
            command = undoStack.pop();
            if (command == null) {
                throw new IllegalStateException("The stack is empty or URI not found");
            }

            if (command.getUri().equals(uri)) {
                command.undo();
                break;
            } else {
                tmpStack.push(command);
            }
        }

        // Restore the stack
        command = tmpStack.pop();
        while (command != null) {
            undoStack.push(command);
            command = tmpStack.pop();
        }
    }
}
