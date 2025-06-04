package fhwedel.JDBC;

import java.sql.*;

public class JDBC {

    private static final String CON_STRING = "jdbc:mariadb://localhost:3306/firma";

    private static final String USER = "root";

    private static final String PASSWORD = "password";

    private static final String INSERT_STRING_PERSONAL = "INSERT INTO personal(pnr, name, vorname, geh_stufe, abt_nr, krankenkasse) VALUES(?, ?, ?, ?, ?, ?)";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(CON_STRING, USER, PASSWORD);
    }

    public static void create_personal() {
        try ( Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(INSERT_STRING_PERSONAL)) {

        final String name = "Krause";
        final String firstname = "Henrik";
        final int pnr = 417;
        final String geh_stufe = "it1";
        final String krankenkasse = "tkk";
        final String abt_nr = "d13"; // d13 = Produktion

        stmt.setInt(1, pnr);
        stmt.setString(2, name);
        stmt.setString(3, firstname);
        stmt.setString(4, geh_stufe);
        stmt.setString(5, abt_nr);
        stmt.setString(6, krankenkasse);

        int rowsAffected = stmt.executeUpdate();
        System.out.println("✅ " + rowsAffected + " Zeile(n) eingefügt.");
        } catch (SQLException e) {
            System.err.println("❌ Einfügen Fehlgeschalgen");
            e.printStackTrace();
        }
    }

    public static void read_personal() {
        final String query = "SELECT pnr, name, vorname, geh_stufe, abt_nr, krankenkasse FROM personal";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int pnr = rs.getInt("pnr");
                String name = rs.getString("name");
                String firstname = rs.getString("vorname");
                String gehStufe = rs.getString("geh_stufe");
                String abtNr = rs.getString("abt_nr");
                String krankenkasse = rs.getString("krankenkasse");

                System.out.printf("👤 %d: %s %s | Stufe: %s | Abt: %s | KK: %s%n",
                        pnr, firstname, name, gehStufe, abtNr, krankenkasse);
            }

        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Lesen der Daten");
            e.printStackTrace();
        }
    }
}