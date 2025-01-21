package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.io.File;

public class DatabaseConnector {

    private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    
    private DatabaseConnector(){};

    public static DatabaseConnector getInstance() {
        return INSTANCE;
    }
    
    private static Properties properties;

    static {
        ClassLoader classLoader = DatabaseConnector.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("application.properties");
         properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {

        String pathToBase = properties.getProperty("database.path");
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:data.db");
    }
}
