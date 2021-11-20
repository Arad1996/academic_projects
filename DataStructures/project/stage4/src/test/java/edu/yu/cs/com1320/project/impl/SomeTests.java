package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

public class SomeTests{
    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    String txt2;

    private URI uri3;
    String txt3;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
    }

    @Test
    public void basicSearchAndOrganizationTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(1, store.search("PiE").size());
        store.search("Pants");
        store.setMaxDocumentCount(2);
        assertEquals(2, store.searchByPrefix("p").size());
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri2);
        });
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri3);
        });
        assertEquals(0, store.search("Pants").size());
        assertEquals(0, store.search("P").size());
        assertEquals(2, store.searchByPrefix("p").size());
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(1, store.search("Pants").size());
        assertEquals(2, store.searchByPrefix("p").size());


        //assertEquals(0, store.searchByPrefix("x").size());
        //assertEquals(3, store.searchByPrefix("pi").size());
        //assertEquals(5, store.search("PiZzA").get(0).wordCount("pizza"));
        //assertEquals(6, store.searchByPrefix("p").get(0).getWords().size());
    }

    @Test
    public void basicPutOverwriteTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.setMaxDocumentBytes(this.txt2.getBytes().length);
        assertEquals(1, store.search("pizza").size());
        store.undo();
        assertEquals(0, store.search("pizza").size());

        //store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        // assertEquals(1, store.search("pizza").size());
    }
    @Test
    public void testDeleteAndDeleteAll() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(2, store.search("pizza").size());
        store.deleteAll("PiZZa");
        assertEquals(0, store.search("pizza").size());
        store.setMaxDocumentCount(2);
        store.undo();
        assertEquals(2, store.search("pizza").size());
        assertEquals(2, store.searchByPrefix("p").size());

        //assertNull(store.getDocument(this.uri1));
        // store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        //store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //assertEquals(2, store.search("pizza").size());
        //assertNotNull(store.getDocument(this.uri1));assertNotNull(store.getDocument(this.uri2));assertNotNull(store.getDocument(this.uri3));
        //store.deleteAllWithPrefix("p");
        //assertNull(store.getDocument(this.uri1));assertNull(store.getDocument(this.uri2));assertNull(store.getDocument(this.uri3));
    }
    /////////////////////////
    @Test
    public void testUndoNoArgs() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.setMaxDocumentCount(2);
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri1);
        });
        store.setMaxDocumentBytes(this.txt1.getBytes().length+this.txt3.getBytes().length);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri2);
        });
        assertEquals(2, store.searchByPrefix("p").size());
        assertEquals(1, store.search("pizza").size());
        store.setMaxDocumentCount(1);
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri3);
        });
        assertEquals(1, store.searchByPrefix("p").size());





    }
    @Test
    public void testUndoWithArgs() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        //store.setMaxDocumentBytes();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(2, store.searchByPrefix("p").size());
        assertThrows(IllegalStateException.class, () -> {
            store.undo(this.uri1);
        });
        assertEquals(0, store.search("Apple").size());


    }
}
