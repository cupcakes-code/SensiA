package common;


import javafx.scene.control.Alert;

import java.sql.*;



public class DBConnector {

    static Connection dbConnection;

    private static PreparedStatement statement;
    private static ResultSet results;


    public static boolean connectToDB() {
        String dbURL = "jdbc:mysql://localhost:3306/newschema";
        String dbUser = "root";
        String dbPassword = "Anime";

        try {
            System.out.println("Connecting to database...");
            dbConnection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Database connection successful.");
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not connect to database.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }


    public static Connection getDbConnection() {
        return dbConnection;
    }


    public static void closeConnection() throws SQLException {
        dbConnection.close();
        System.out.println("Database connection closed.");
    }



    public static void executeQuery(String query) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (results != null) {
            results.close();
        }

        try {
            statement = dbConnection.prepareStatement(query);
            results = statement.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("SQL Query error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Query Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    public static ResultSet executeQueryResults(String query) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (results != null) {
            results.close();
        }

        try {
            statement = dbConnection.prepareStatement(query);
            return statement.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("SQL Query error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Query Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return null;
        }
    }


    public static void executeUpdate(String update) throws SQLException {
        if (statement != null) {
            statement.close();
        }

        try {
            statement = dbConnection.prepareStatement(update);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Update error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Update Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }



    public static ResultSet getResults() {
        return results;
    }

}
