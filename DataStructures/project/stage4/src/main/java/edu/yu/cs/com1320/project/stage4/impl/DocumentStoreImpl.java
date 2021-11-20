package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DocumentStoreImpl implements DocumentStore {
    private final HashTableImpl<URI, DocumentImpl> hashTable = new HashTableImpl<>();
    private Stack<Undoable> undoStack = new StackImpl<>();
    private final Trie<Document> trie = new TrieImpl<>();
    private final MinHeapImpl heap = new MinHeapImpl();
    private Integer maxDocumentCount = null;
    private Integer maxDocumentBytes = null;
    private int documentsCount = 0;
    private int totalSize = 0;

    public DocumentStoreImpl() {

    }

    private void removeDocumentFromHeap(Document document) {
        document.setLastUseTime(0);
        heap.reHeapify(document);
        heap.remove();
    }

    private int getDocumentsCount() {
        return documentsCount;
    }

    private int getTotalSize() {
        return totalSize;
    }

    private void cleanStack(URI uri) {
        Stack<Undoable> tmpStack = new StackImpl<>();
        Undoable command;

        while (true) {
            command = undoStack.pop();
            if (command == null) {
                break; // done walking over the stack
            }

            if (command instanceof GenericCommand) {
                if (!((GenericCommand) command).getTarget().equals(uri)) {
                    // if it is not our uri, return it later to the stack
                    tmpStack.push(command);
                }
            } else {
                CommandSet<Document> newCommandSet = new CommandSet<>();
                for (Iterator<GenericCommand> it = ((CommandSet) command).iterator(); it.hasNext(); ) {
                    GenericCommand item = it.next();
                    if (!item.getTarget().equals(uri)) {
                        newCommandSet.add(item);
                    }
                }
            }
        }

        // Restore the stack
        command = (GenericCommand) tmpStack.pop();
        while (command != null) {
            undoStack.push(command);
            command = (GenericCommand) tmpStack.pop();
        }
    }

    private void removeOverSize() {
        while (maxDocumentCount != null && getDocumentsCount() > maxDocumentCount) {
            Document document = heap.remove();
            heap.insert(document);
            this.removeDocument(document.getKey());
            cleanStack(document.getKey());
        }

        while (maxDocumentBytes != null && getTotalSize() > maxDocumentBytes) {
            Document document = heap.remove();
            heap.insert(document);
            this.removeDocument(document.getKey());
            cleanStack(document.getKey());
        }
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (format == null) {
            throw new IllegalArgumentException();
        }

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

        // If document with same uri existed before, remove it from trie and heap
        if (result != null) {
            for (String word : result.getWords()) {
                trie.delete(word, result);
            }
            removeDocumentFromHeap(result);
            command = new GenericCommand<>(uri, uri_ -> {
                for (String word : result.getWords()) {
                    trie.put(word, result);
                }
                heap.insert(result);
                return hashTable.put(uri_, result) != null;
            });
        } else {
            // Add all word in document to trie
            for (String word : document.getWords()) {
                trie.put(word, document);
            }
            documentsCount++;
            totalSize += getDocumentSize(document);
            heap.insert(document);
            command = new GenericCommand<>(uri, uri_ -> {
                for (String word : document.getWords()) {
                    trie.delete(word, document);
                }
                removeDocumentFromHeap(document);
                documentsCount--;
                totalSize -= getDocumentSize(document);
                return hashTable.put(uri_, result) != null;
            });
        }

        undoStack.push(command);

        removeOverSize();

        if (result != null) {
            return result.hashCode();
        }
        return 0;
    }

    private int getDocumentSize(Document document) {
        if (document.getDocumentBinaryData() != null) {
            return document.getDocumentBinaryData().length;
        }

        if (document.getDocumentTxt() != null) {
            return document.getDocumentTxt().getBytes().length;
        }

        throw new IllegalStateException();
    }

    private DocumentImpl removeDocument(URI uri) {
        DocumentImpl result = hashTable.put(uri, null);

        if (result != null) {
            removeDocumentFromHeap(result);
            for (String word : result.getWords()) {
                trie.delete(word, result);
            }
            documentsCount--;
            totalSize -= getDocumentSize(result);
        }

        // Undo delete document
        GenericCommand<URI> command = new GenericCommand<>(uri, uri_ -> {
            for (String word : result.getWords()) {
                trie.put(word, result);
            }
            result.setLastUseTime(System.nanoTime());
            heap.insert(result);
            documentsCount++;
            totalSize += getDocumentSize(result);
            return hashTable.put(uri_, result) != null;
        });
        undoStack.push(command);
        return result;
    }

    @Override
    public Document getDocument(URI uri) {
        Document result = hashTable.get(uri);
        if (result != null) {
            result.setLastUseTime(System.nanoTime());
        }
        return result;
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
        removeOverSize();
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
        removeOverSize();
    }

    @Override
    public List<Document> search(String keyword) {
        List<Document> documents = trie.getAllSorted(keyword, (doc1, doc2) -> {
            if (doc1.wordCount(keyword) < doc2.wordCount(keyword)) {
                return 1;
            } else if (doc2.wordCount(keyword) < doc1.wordCount(keyword)) {
                return -1;
            }
            return 0;
        });

        long lastUseTime = System.nanoTime();
        for (Document document : documents) {
            document.setLastUseTime(lastUseTime);
            this.heap.reHeapify(document);
        }
        return documents;
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


    private Set<URI> deleteDocuments(Set<Document> documents) {
        HashSet<URI> result = new HashSet<>();
        CommandSet<Document> commandSet = new CommandSet<>();

        for (Document doc : documents) {

            DocumentImpl r = hashTable.put(doc.getKey(), null);

            if (r != null) {
                removeDocumentFromHeap(r);
                for (String word : r.getWords()) {
                    trie.delete(word, r);
                }
                documentsCount--;
                totalSize -= getDocumentSize(r);
            }

            // Undo delete document
            GenericCommand<Document> command = new GenericCommand<>(doc, doc_ -> {
                for (String word : r.getWords()) {
                    trie.put(word, r);
                }
                r.setLastUseTime(System.nanoTime());
                heap.insert(r);
                documentsCount++;
                totalSize += getDocumentSize(r);

                return hashTable.put(doc_.getKey(), r) != null;
            });
            commandSet.addCommand(command);
            result.add(doc.getKey());
        }

        undoStack.push(commandSet);

        return result;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<Document> documents = trie.deleteAll(keyword);
        return deleteDocuments(documents);
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<Document> documents = trie.deleteAllWithPrefix(keywordPrefix);
        return deleteDocuments(documents);
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        this.maxDocumentCount = limit;
        this.removeOverSize();
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxDocumentBytes = limit;
        this.removeOverSize();
    }
}
