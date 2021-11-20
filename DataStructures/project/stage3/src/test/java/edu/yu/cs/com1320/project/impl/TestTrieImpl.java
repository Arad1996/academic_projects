package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestTrieImpl {
    @Test
    void testPut() {
        TrieImpl trie = new TrieImpl<String>();
        trie.put("Test", "Value");
        trie.put("Test", "AnotherValue");
        trie.put("Test1", "Value1");
        trie.put("NotTest", "NotValue1");
    }

    @Test
    void testGet() {
        TrieImpl trie = new TrieImpl<Integer>();
        trie.put("Ar", 3);
        trie.put("AR", 1);
        trie.put("ar", 2);
        trie.put("ar1", 4);
        trie.put("aR1", 5);
        List<Integer> result = trie.getAllSorted("ar", (int1, int2) -> {
            if ((int) int1 < (int) int2) {
                return 1;
            } else if ((int) int2 < (int) int1) {
                return -1;
            }
            return 0;
        });
    }

    @Test
    void testGetAllWithPrefixSorted() {
        TrieImpl trie = new TrieImpl<Integer>();
        trie.put("Ar", 3);
        trie.put("AR", 1);
        trie.put("ar", 2);
        trie.put("ar1", 4);
        trie.put("aR1", 5);
        trie.put("aR11", -2);
        List result = trie.getAllWithPrefixSorted("ar", (int1, int2) -> {
            if ((int) int1 < (int) int2) {
                return 1;
            } else if ((int) int2 < (int) int1) {
                return -1;
            }
            return 0;
        });
    }

    @Test
    void testDeleteKey() {
        TrieImpl trie = new TrieImpl<Integer>();
        trie.put("Ar", 3);
        trie.put("AR", 1);
        trie.put("ar", 2);
        trie.put("ar1", 4);
        trie.put("aR1", 5);
        trie.put("aR11", -2);
        trie.delete("ar1", 4);
        trie.delete("ar1", 5);
        trie.delete("ar11", -2);
    }

    @Test
    void testDeleteAll() {
        TrieImpl trie = new TrieImpl<Integer>();
        trie.put("Ar", 3);
        trie.put("AR", 1);
        trie.put("ar", 2);
        trie.put("ar1", 4);
        trie.put("aR1", 5);
        trie.put("aR11", -2);
        trie.deleteAll("ar");
        trie.deleteAll("arr");
        trie.deleteAll("ar1");
        trie.deleteAll("ar11");
    }

    @Test
    void testDeleteAllWithPrefix() {
        TrieImpl trie = new TrieImpl<Integer>();
        trie.put("Ar", 3);
        trie.put("AR", 1);
        trie.put("ar", 2);
        trie.put("ar1", 4);
        trie.put("aR1", 5);
        trie.put("aR11", -2);
        trie.deleteAllWithPrefix("ar1");
        trie.deleteAllWithPrefix("ar");

        TrieImpl trie1 = new TrieImpl<Integer>();
        trie1.put("Ar", 3);
        trie1.put("AR", 1);
        trie1.put("ar", 2);
        trie1.put("ar1", 4);
        trie1.put("aR1", 5);
        trie1.put("aR11", -2);
        trie1.deleteAllWithPrefix("ar");
    }
}
