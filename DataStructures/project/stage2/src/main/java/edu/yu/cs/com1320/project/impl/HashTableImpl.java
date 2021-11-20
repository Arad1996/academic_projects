package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    private static class HashEntry<Key, Value> {
        private final Key key;
        private final Value value;
        private HashEntry<Key, Value> next;
        private HashEntry<Key, Value> previous;

        HashEntry(Key key, Value value) {
            this(key, value, null, null);
        }

        HashEntry(Key key, Value value, HashEntry<Key, Value> next, HashEntry<Key, Value> previous) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.previous = previous;
        }
    }

    int length;
    int itemsCount;
    HashEntry<Key, Value>[] data;

    public HashTableImpl() {
        length = 2;
        itemsCount = 0;
        data = new HashEntry[length];
    }

    private int getIndex(Key k) {
        int hash = k.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        return hash % length;
    }

    private HashEntry<Key, Value> getHashEntry(Key k) {
        int index = getIndex(k);
        HashEntry<Key, Value> entry = data[index];

        while (entry != null) {
            if (entry.key.equals(k)) {
                return entry;
            }
            entry = entry.next;
        }

        return null;
    }

    @Override
    public Value get(Key k) {
        HashEntry<Key, Value> entry = getHashEntry(k);
        if (entry == null) {
            return null;
        }
        return entry.value;
    }

    @Override
    public Value put(Key k, Value v) {
        // Delete value if value is null
        if (v == null) {
            return removeKey(k);
        }

        // If item with same hash exist already, remove it and then return its value
        Value value = removeKey(k);

        if (itemsCount >= length) {
            resizeData();
        }

        HashEntry<Key, Value> entryHead = data[getIndex(k)];

        // If no entities were created in current index
        if (entryHead == null) {
            data[getIndex(k)] = new HashEntry<>(k, v);

        } else {
            // If entry already exist in current index prepend the new entry
            HashEntry<Key, Value> newEntry = new HashEntry<>(k, v, entryHead, null);
            data[getIndex(k)] = newEntry;
            entryHead.previous = newEntry;
        }
        itemsCount++;
        return value;
    }

    private void resizeData() {
        HashEntry<Key, Value>[] oldData = data;
        length = length * 2;
        itemsCount = 0;
        this.data = new HashEntry[length];
        for (HashEntry<Key, Value> entry : oldData) {
            if (entry != null) {
                do {
                    put(entry.key, entry.value);
                    entry = entry.next;
                } while (entry != null);
            }
        }
    }

    private Value removeKey(Key k) {
        HashEntry<Key, Value> entry = getHashEntry(k);

        // If entry didn't exist, return null
        if (entry == null) {
            return null;
        }

        itemsCount--;

        // When previous object is null, update the entry in the list and reset previous to null
        if (entry.previous == null) {
            if (entry.next != null) {
                entry.next.previous = null;
            }
            data[getIndex(k)] = entry.next;

            // Return current value
            return entry.value;
        }

        // Reverse link next item to previous item
        entry.next.previous = entry.previous;

        // Link previous item to the next one
        entry.previous.next = entry.next;
        return entry.value;
    }

    public static void main(String[] args) {
        HashTable<String, Integer> hashTable = new HashTableImpl<>();
        hashTable.put("test1", 1);
        hashTable.put("test2", 2);
        hashTable.put("test3", 3);
        hashTable.put("test3", 3);
        hashTable.put("test3", null);
        hashTable.put("test3", null);
        hashTable.put("test4", 4);
        hashTable.put("test5", 5);
        hashTable.put("test6", 6);
        hashTable.put("test7", 7);
        Integer item = hashTable.get("test1");
        System.out.println(item);
    }
}
