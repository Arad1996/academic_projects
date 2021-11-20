package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class DocumentImpl implements Document  {
    private URI uri;
    private String text;
    private byte[] binaryData;
    private HashMap<String, Integer> words;
    private long timeInNanoseconds;

    public DocumentImpl(URI uri, String txt) throws IllegalArgumentException {
        if (uri == null || uri.toString().isEmpty()) throw new IllegalArgumentException();
        if (txt == null || txt.isEmpty()) throw new IllegalArgumentException();

        this.uri = uri;
        this.text = txt;
        this.words = countWords(txt);
        this.timeInNanoseconds = System.nanoTime();
    }

    public DocumentImpl(URI uri, byte[] binaryData) throws IllegalArgumentException {
        if (uri == null || uri.toString().isEmpty()) throw new IllegalArgumentException();
        if (binaryData == null || (new String(binaryData)).isEmpty()) throw new IllegalArgumentException();

        this.uri = uri;
        this.binaryData = binaryData;
        this.words = new HashMap<>();
        this.timeInNanoseconds = System.nanoTime();
    }

    private static String filterChars(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    private static HashMap<String, Integer> countWords(String txt) {
        HashMap<String, Integer> map = new HashMap<>();

        for (String word : txt.split("\\s+")) {
            if (word.equals("")) {
                continue;
            }
            String filteredWord = filterChars(word);
            int currentCount = map.getOrDefault(filteredWord, 0);
            map.put(filteredWord, currentCount + 1);
        }

        return map;
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
    public int wordCount(String word) {
        word = filterChars(word);
        return words.getOrDefault(word, 0);
    }

    @Override
    public Set<String> getWords() {
        return words.keySet();
    }

    @Override
    public long getLastUseTime() {
        return timeInNanoseconds;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.timeInNanoseconds = timeInNanoseconds;
    }

    @Override
    public int compareTo(Document o) {
        if (o == null) {
            return 1;
        }
        if (this.timeInNanoseconds > o.getLastUseTime()) {
            return 1;
        } else if (this.timeInNanoseconds < o.getLastUseTime()) {
            return -1;
        }
        return 0;
    }
}
