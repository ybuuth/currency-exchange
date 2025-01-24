package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    public DatabaseConnector(){};

    public Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite::resource:data.db");
    }

    public void enableForeignKeys() throws SQLException, ClassNotFoundException {

        String query  = """
                PRAGMA foreign_keys = ON
                """;
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.execute();
    }
}
