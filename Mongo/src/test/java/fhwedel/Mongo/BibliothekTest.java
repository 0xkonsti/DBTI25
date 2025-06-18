package fhwedel.Mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BibliothekTest {
    private static MongoClient mongoClient;
    private static MongoDatabase db;

    private static MongoCollection<Document> books;
    private static MongoCollection<Document> readers;

    @BeforeAll
    static void initMongo() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        db = mongoClient.getDatabase("bibliothek");

        books = db.getCollection("books");
        readers = db.getCollection("readers");

        books.drop();
        readers.drop();
    }

    @Test
    void testA() {
        Document book = new Document("invnr", "B001")
                .append("autor", "Marc-Uwe Kling")
                .append("titel", "Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers")
                .append("verlag", "Ullstein");

        Document reder = new Document("lnr", "L001")
                .append("name", "Friedrich Funke")
                .append("adresse", "Bahnhofstraße 17, 23758 Oldenburg");

        List<Document> b = Arrays.asList(
                new Document("invnr", "B002")
                        .append("autor", "J.R.R. Tolkien")
                        .append("titel", "Der Herr der Ringe: Die Gefährten")
                        .append("verlag", "Klett-Cotta"),
                new Document("invnr", "B003")
                        .append("autor", "J.K. Rowling")
                        .append("titel", "Harry Potter und die Kammer des Schreckens")
                        .append("verlag", "Carlsen"),
                new Document("invnr", "B004")
                        .append("autor", "Frank Herbert")
                        .append("titel", "Dune – Der Wüstenplanet")
                        .append("verlag", "Heyne"),
                new Document("invnr", "B005")
                        .append("autor", "George Lukas")
                        .append("titel", "Star Wars: Episode IV - A New Hope")
                        .append("verlag", "Lucasfilm"),
                new Document("invnr", "B006")
                        .append("autor", "Stan Lee")
                        .append("titel", "Spider-Man: The Night Gwen Stacy Died")
                        .append("verlag", "Marvel Comics"));

        List<Document> r = Arrays.asList(
                new Document("lnr", "L002")
                        .append("name", "Luke Skywalker")
                        .append("adresse", "Tatooine, Feuchtfarmerstraße 42"),
                new Document("lnr", "L003")
                        .append("name", "Aragorn Elessar")
                        .append("adresse", "Bruchtal 1, Mittelerde"),
                new Document("lnr", "L004")
                        .append("name", "Leia Organa")
                        .append("adresse", "Alderaan Platz 12, Rebellenbasis"),
                new Document("lnr", "L005")
                        .append("name", "Paul Atreides")
                        .append("adresse", "Arrakis, Sietch Tabr 5"),
                new Document("lnr", "L006")
                        .append("name", "Hermione Granger")
                        .append("adresse", "Ottery St. Catchpole 7, England"));

        books.insertOne(book);
        readers.insertOne(reder);

        books.insertMany(b);
        readers.insertMany(r);

        Document bookResult = books.find(new Document("invnr", "B001")).first();
        assertNotNull(bookResult);
        assertEquals("Marc-Uwe Kling", bookResult.getString("autor"));

        Document readerResult = readers.find(new Document("lnr", "L001")).first();
        assertNotNull(readerResult);
        assertEquals("Friedrich Funke", readerResult.getString("name"));

        long countBooks = books.countDocuments(
                new Document("invnr", new Document("$in", Arrays.asList("B002", "B003", "B004", "B005", "B006"))));
        assertEquals(5, countBooks);

        long countReaders = readers.countDocuments(
                new Document("lnr", new Document("$in", Arrays.asList("L002", "L003", "L004", "L005", "L006"))));
        assertEquals(5, countReaders);
    }
}
