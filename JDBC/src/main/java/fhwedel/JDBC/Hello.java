package fhwedel.JDBC;

import java.sql.*;

public class Hello {
    public static void main(String[] args) {
        System.out.println("Hi, JDBC world!");

        JDBC.create_personal();

        JDBC.read_personal();

        JDBC.update_gehalt();

        JDBC.delete_person();

        JDBC.sales_personal();
    }
}
