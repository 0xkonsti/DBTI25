package fhwedel.Mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

public class BibliothekTest {
    private static MongoClient mongoClient;
    private static MongoDatabase db;

    private static MongoCollection<Document> books;
    private static MongoCollection<Document> readers;
    private static MongoCollection<Document> borrows;

    private static Date rueckgabeDatum = new Date(); // Default value for borrow return date

    @BeforeAll
    static void initMongo() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        db = mongoClient.getDatabase("bibliothek");

        books = db.getCollection("books");
        readers = db.getCollection("readers");
        borrows = db.getCollection("borrows");

        books.drop();
        readers.drop();
        borrows.drop();

        try {
            rueckgabeDatum = new SimpleDateFormat("yyyy/MM/dd").parse("2025/12/24");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Document> borrowsList = Arrays.asList(
                new Document("invnr", "B004").append("lnr", "L002").append("rueckgabedatum",
                        rueckgabeDatum),
                new Document("invnr", "B006").append("lnr", "L002").append("rueckgabedatum",
                        rueckgabeDatum),
                new Document("invnr", "B002").append("lnr", "L003").append("rueckgabedatum",
                        rueckgabeDatum),
                new Document("invnr", "B005").append("lnr", "L003").append("rueckgabedatum",
                        rueckgabeDatum),
                new Document("invnr", "B003").append("lnr", "L006").append("rueckgabedatum",
                        rueckgabeDatum),
                new Document("invnr", "B001").append("lnr", "L006").append("rueckgabedatum",
                        rueckgabeDatum));

        borrows.insertMany(borrowsList);
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
                new Document("invnr", new Document("$in",
                        Arrays.asList("B002", "B003", "B004", "B005", "B006"))));
        assertEquals(5, countBooks);

        long countReaders = readers.countDocuments(
                new Document("lnr", new Document("$in",
                        Arrays.asList("L002", "L003", "L004", "L005", "L006"))));
        assertEquals(5, countReaders);
    }

    @Test
    void testB() {
        Document filter = new Document("autor", "Marc-Uwe Kling");
        Document book = books.find(filter).first();

        assertNotNull(book);
        assertEquals("Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers", book.getString("titel"));
        assertEquals("Ullstein", book.getString("verlag"));
        assertEquals("B001", book.getString("invnr"));
        assertEquals("Marc-Uwe Kling", book.getString("autor"));
    }

    @Test
    void testC() {
        long bookCount = books.countDocuments();
        assertEquals(6, bookCount);
    }

    @Test
    void testD() {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.group("$lnr", Accumulators.sum("anzahlBuecher", 1)),
                Aggregates.match(Filters.gt("anzahlBuecher", 1)),
                Aggregates.lookup("readers", "_id", "lnr", "leserinfo"),
                Aggregates.unwind("$leserinfo"),
                Aggregates.project(Projections.fields(
                        Projections.excludeId(),
                        Projections.computed("name", "$leserinfo.name"),
                        Projections.include("anzahlBuecher"))));

        List<Document> result = borrows.aggregate(pipeline).into(new ArrayList<>());

        assertTrue(result.size() > 0);
        for (Document doc : result) {
            assertTrue(doc.containsKey("name"));
            assertTrue(doc.containsKey("anzahlBuecher"));
            assertTrue(doc.getInteger("anzahlBuecher") > 1);
        }
    }

    @Test
    void testE() {
        Document borrow = new Document("lnr", "L001").append("invnr", "B001")
                .append("rueckgabedatum", rueckgabeDatum);
        borrows.insertOne(borrow);

        Document search = borrows.find(borrow).first();
        assertNotNull(search);
        assertEquals("L001", search.getString("lnr"));
        assertEquals("B001", search.getString("invnr"));

        borrows.deleteOne(borrow);

        Document deletedSearch = borrows.find(borrow).first();
        assertEquals(null, deletedSearch, "Borrow record should be deleted");
    }
}
