package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 127;
    private Node<Value> root;

    public static class Node<Value> {
        protected Set<Value> values = new HashSet<>();
        protected Node<Value>[] links = new Node[TrieImpl.alphabetSize];
        protected int storedValues = 0;
    }

    public TrieImpl() {

    }

    @Override
    public void put(String key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (val != null) {
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d) {
        key = key.toLowerCase();
        // create a new node
        if (x == null) {
            x = new Node<>();
        }

        x.storedValues += 1;

        // we've reached the last node in the key,
        // set the value for the key and return the node
        if (d == key.length()) {
            x.values.add(val);
            return x;
        }
        // proceed to the next node in the chain of nodes that
        // forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    @Override
    public List<Value> getAllSorted(String key, Comparator comparator) {
        if (key == null || comparator == null) {
            throw new IllegalArgumentException();
        }

        key = key.toLowerCase();
        Node<Value> x = this.get(this.root, key, 0);
        if (x == null) {
            return new ArrayList<>();
        }
        ArrayList<Value> result = new ArrayList<>(x.values);
        result.sort(comparator);
        return result;
    }

    private Node<Value> get(Node<Value> x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator comparator) {
        if (prefix == "") {
            return new ArrayList<>();
        }
        if (prefix == null || comparator == null) {
            throw new IllegalArgumentException();
        }

        prefix = prefix.toLowerCase();

        // Get the prefix node
        Node x = this.get(this.root, prefix, 0);

        // Prefix not found
        if (x == null) {
            return new ArrayList<>();
        }

        ArrayList<Value> result = new ArrayList<>(getAllSubtreeValues(x));
        result.sort(comparator);
        return result;
    }

    private Set<Value> getAllSubtreeValues(Node<Value> x) {
        Set<Value> result = new HashSet<>();

        if (x.values.size() > 0) {
            result.addAll(x.values);
        }

        for (Node<Value> link : x.links) {
            if (link != null) {
                result.addAll(getAllSubtreeValues(link));
            }
        }
        return result;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("")) {
            return new HashSet<>();
        }
        prefix = prefix.toLowerCase();

        Set<Value> result = deleteAllWithPrefix(this.root, prefix, 0);
        if (result == null) {
            return new HashSet<>();
        }

        return result;
    }

    private Set<Value> deleteAllWithPrefix(Node<Value> x, String prefix, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the prefix
        if (d == prefix.length()) {
            // mark as no values left, later in recursion the node will be deleted
            x.storedValues = 0;
            return new HashSet<>(getAllSubtreeValues(x));
        }

        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = prefix.charAt(d);

        Set<Value> deletedValues = this.deleteAllWithPrefix(x.links[c], prefix, d + 1);

        // prefix not found
        if (deletedValues == null) {
            return null;
        }

        x.storedValues -= deletedValues.size();
        // If sub tree contains zero values, remove the node and it's subtree
        if (x.links[c].storedValues == 0) {
            x.links[c] = null;
        }
        return deletedValues;
    }


    @Override
    public Set<Value> deleteAll(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        key = key.toLowerCase();
        Set<Value> deletedValue = this.deleteAll(this.root, key, 0);
        if (root.storedValues == 0) {
            root = null;
        }
        return deletedValue;
    }

    private Set<Value> deleteAll(Node<Value> x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return new HashSet<>();
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            x.storedValues -= x.values.size();
            Set<Value> deletedValues = x.values;
            x.values = new HashSet<>();
            return deletedValues;
        }

        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);

        Set<Value> deletedValues = this.deleteAll(x.links[c], key, d + 1);
        // if no values deleted do not remove anything
        if (deletedValues.size() == 0) {
            return deletedValues;
        }
        x.storedValues -= deletedValues.size();
        // If sub tree contains zero values, remove the node and it's subtree
        if (x.links[c].storedValues == 0) {
            x.links[c] = null;
        }
        return deletedValues;
    }

    @Override
    public Value delete(String key, Value val) {
        if (key == null || val == null) {
            throw new IllegalArgumentException();
        }

        key = key.toLowerCase();
        Value deletedValue = this.deleteValue(this.root, key, val, 0);
        if (root.storedValues == 0) {
            root = null;
        }
        return deletedValue;
    }

    private Value deleteValue(Node<Value> x, String key, Value val, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            for (Value value: x.values) {
                if (value.equals(val)) {
                    x.values.remove(val);
                    x.storedValues -= 1;
                    return value;
                }
            }
             return null;
        }

        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);

        Value deletedValue = this.deleteValue(x.links[c], key, val, d + 1);
        if (deletedValue != null) {
            // Only if value was removed reduce storedValues count
            x.storedValues -= 1;
        }

        // If sub tree contains zero values, remove the node and it's subtree
        if (x.links[c] != null && x.links[c].storedValues == 0) {
            x.links[c] = null;
        }
        return deletedValue;
    }
}
