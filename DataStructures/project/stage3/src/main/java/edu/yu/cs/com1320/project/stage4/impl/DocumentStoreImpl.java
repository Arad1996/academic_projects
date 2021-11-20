package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentStoreImpl implements DocumentStore {
    private final HashTable<URI, DocumentImpl> hashTable;
    private Stack<Undoable> undoStack = new StackImpl<>();
    private final Trie<Document> trie = new TrieImpl<>();

    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        // This is a delete
        if (input == null) {
            DocumentImpl result = removeDocument(uri);

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
        GenericCommand<URI> command;

        // If document with same uri existed before, remove it from trie
        if (result != null) {
            for (String word : result.getWords()) {
                trie.delete(word, result);
            }
            command = new GenericCommand<>(uri, uri_ -> {
                for (String word : result.getWords()) {
                    trie.put(word, result);
                }
                return hashTable.put(uri_, result) != null;
            });
        } else {
            // Add all word in document to trie
            for (String word : document.getWords()) {
                trie.put(word, document);
            }
            command = new GenericCommand<>(uri, uri_ -> {
                for (String word : document.getWords()) {
                    trie.delete(word, document);
                }
                return hashTable.put(uri_, result) != null;
            });
        }

        undoStack.push(command);

        if (result != null) {
            return result.hashCode();
        }
        return 0;
    }

    private DocumentImpl removeDocument(URI uri) {
        DocumentImpl result = hashTable.put(uri, null);

        // Undo delete document
        GenericCommand<URI> command = new GenericCommand<>(uri, uri_ -> {
            for (String word : result.getWords()) {
                trie.put(word, result);
            }
            return hashTable.put(uri_, result) != null;
        });
        undoStack.push(command);
        return result;
    }

    @Override
    public Document getDocument(URI uri) {
        return hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        DocumentImpl result = removeDocument(uri);

        if (result == null) {
            return false;
        }
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        Undoable command = undoStack.pop();
        if (command == null) {
            throw new IllegalStateException("The stack is empty");
        }
        command.undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        Stack<Undoable> tmpStack = new StackImpl<>();
        Undoable command;

        while (true) {
            command = undoStack.pop();
            if (command == null) {
                throw new IllegalStateException("The stack is empty or URI not found");
            }

            if (command instanceof GenericCommand) {
                if (((GenericCommand) command).getTarget().equals(uri)) {
                    command.undo();
                    break;
                } else {
                    tmpStack.push(command);
                }
            } else {
                ((CommandSet) command).undo(uri);
            }
        }

        // Restore the stack
        command = (GenericCommand) tmpStack.pop();
        while (command != null) {
            undoStack.push(command);
            command = (GenericCommand) tmpStack.pop();
        }
    }

    @Override
    public List<Document> search(String keyword) {
        return trie.getAllSorted(keyword, (doc1, doc2) -> {
            if (doc1.wordCount(keyword) < doc2.wordCount(keyword)) {
                return 1;
            } else if (doc2.wordCount(keyword) < doc1.wordCount(keyword)) {
                return -1;
            }
            return 0;
        });
    }

    private int countPrefixes(Document doc, String prefix) {
        int result = 0;
        for (String word : doc.getWords()) {
            if (word.startsWith(prefix)) {
                result += doc.wordCount(word);
            }
        }
        return result;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        return trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> {
            if (countPrefixes(doc1, keywordPrefix) < countPrefixes(doc2, keywordPrefix)) {
                return 1;
            } else if (countPrefixes(doc2, keywordPrefix) < countPrefixes(doc1, keywordPrefix)) {
                return -1;
            }
            return 0;
        });
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        HashSet<URI> result = new HashSet<>();
        Set<Document> documents = trie.deleteAll(keyword);
        for (Document doc : documents) {
            this.deleteDocument(doc.getKey());
            result.add(doc.getKey());
        }

        CommandSet<Document> commandSet = new CommandSet<>();
        for (Document document : documents) {
            GenericCommand<Document> command = new GenericCommand<>(document, document_ -> {
                hashTable.put(document.getKey(), (DocumentImpl) document);
                for (String word : document.getWords()) {
                    trie.put(word, document);
                }
                return true;
            });
            commandSet.addCommand(command);
        }

        undoStack.push(commandSet);

        return result;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        HashSet<URI> result = new HashSet<>();
        Set<Document> documents = trie.deleteAllWithPrefix(keywordPrefix);
        for (Document doc : documents) {
            this.deleteDocument(doc.getKey());
            result.add(doc.getKey());
        }

        CommandSet<Document> commandSet = new CommandSet<>();
        for (Document document : documents) {
            GenericCommand<Document> command = new GenericCommand<>(document, document_ -> {
                hashTable.put(document.getKey(), (DocumentImpl) document);
                for (String word : document.getWords()) {
                    trie.put(word, document);
                }
                return true;
            });
            commandSet.addCommand(command);
        }

        undoStack.push(commandSet);

        return result;
    }
}
