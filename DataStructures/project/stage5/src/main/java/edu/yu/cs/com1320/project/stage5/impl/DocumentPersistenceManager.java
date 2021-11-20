package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;


/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private final File baseDir;
    private final DocumentSerializer documentSerializer = new DocumentSerializer();
    private final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    private final Gson gson;


    private class DocumentSerializer implements JsonSerializer<Document> {
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();
            object.addProperty("URI", document.getKey().toString());

            if (document.getDocumentTxt() != null) {
                object.addProperty("text", document.getDocumentTxt());
            }

            if (document.getDocumentBinaryData() != null) {
                object.addProperty("binary", Base64.getEncoder().encodeToString(document.getDocumentBinaryData()));
            }

            object.add("word_count_map", gson.toJsonTree(document.getWordMap()));
            return object;
        }
    }

    private class DocumentDeserializer implements JsonDeserializer<DocumentImpl> {
        @Override
        public DocumentImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{
            URI uri = null;
            DocumentImpl document;

            try {
                JsonElement jsonUri = ((JsonObject) jsonElement).get("URI");
                uri = new URI(jsonUri.getAsString());
            } catch (URISyntaxException e) {
                return null;
            }

            if (((JsonObject) jsonElement).get("text") != null) {
                document = new DocumentImpl(
                        uri,
                        ((JsonObject) jsonElement).get("text").getAsString()
                );

            } else {
                String base64string = ((JsonObject) jsonElement).get("bytes").getAsString();
                byte[] bytes = Base64.getDecoder().decode(base64string);
                document = new DocumentImpl(uri, bytes);
            }

            JsonElement words = ((JsonObject) jsonElement).get("words");
            Object deserialize = jsonDeserializationContext.deserialize(words, HashMap.class);
            document.setWordMap((HashMap) deserialize);
            return document;

        }
    }

    public DocumentPersistenceManager(File baseDir){
        if (baseDir == null) {
            this.baseDir = new File(System.getProperty("user.dir"));
        } else {
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            this.baseDir = baseDir;
        }

        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentSerializer());
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer());
        gson = gsonBuilder.create();

    }

    private File getFile(URI uri) {
        return new File((uri.getHost() + uri.getPath() + ".json"));
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        String json = gson.toJson(val);
        File file = getFile(uri);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        FileReader reader = new FileReader(getFile(uri));
        Document doc = gson.fromJson(reader, DocumentImpl.class);
        reader.close();
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        return false;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri = new URI("http://edu.yu.cs/com1320/project/doc1");
        DocumentImpl doc = new DocumentImpl(uri, "test document");
        dpm.serialize(uri, doc);
        dpm.deserialize(uri);
    }
}
