package src.databaseConnection;
import java.sql.*;

public class DataAccess {

    public static Connection conn;
    static String HOST = "localhost";
    static String PORT = "3306";
    static String DB_NAME = "teambach";
    static String databaseURL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;
    static String USER = "root";
    static String PASSWORD = "";

    public DataAccess() {
    }

    public static void setConn() {
        //information in the connection string varies depending on the backend data source
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(databaseURL, USER, PASSWORD);
                System.out.println("Connected to database.");
            }
        } catch (SQLException e) {
            System.out.println("Database Connection Failed");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeCon() throws Exception{
        //explicitly close the connection to ensure that resources are properly deallocated
        if (conn != null){
            conn.close();
        }
    }

}