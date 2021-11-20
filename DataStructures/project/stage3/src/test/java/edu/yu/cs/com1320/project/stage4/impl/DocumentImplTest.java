package edu.yu.cs.com1320.project.stage4.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.jupiter.api.Test;

class DocumentImplTest {
    @Test
    public void testDocumentGeneral() throws URISyntaxException {
        DocumentImpl doc = new DocumentImpl(new URI("http://test.com"), "This is a test! test it is a good test");
        Set<String> words = doc.getWords();
    }
}