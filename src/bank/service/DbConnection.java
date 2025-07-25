package bank.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        try (InputStream input = DbConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new SQLException("Sorry, db.properties file not found in classpath.");
            }

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            return connection;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to load database db.properties.");
        }
    }
}