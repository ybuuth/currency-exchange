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
    
    private static Properties PROPERTIES;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        ClassLoader classLoader = DatabaseConnector.class.getClassLoader();
        PROPERTIES = new Properties();
        try (InputStream inputStream = classLoader.getResourceAsStream("WEB-INF/application.properties")) {

            if (inputStream == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
