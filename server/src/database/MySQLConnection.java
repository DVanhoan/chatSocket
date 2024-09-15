package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static MySQLConnection instance = null;

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DB_NAME = "chatapp";
    private static final String USER = "root";
    private static final String PASSWORD = 

    private static Connection connection; 

    private MySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (instance == null) {
            instance = new MySQLConnection();
        }
        return instance.connection;
    }

    public static void main(String[] args) {
        System.out.println(MySQLConnection.getConnection());
    }
}
