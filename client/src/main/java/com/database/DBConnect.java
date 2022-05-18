package com.database;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.Objects;

public class DBConnect {
    private Connection connection;
    public boolean connectionError = false;
    private final String DB_URL = "jdbc:mysql://localhost:3306/division";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("DATABASE ERROR");
            alert.setHeaderText("Connection to database failed.");
            alert.setContentText("Can't connect to:\n"+DB_URL);
            alert.showAndWait();
            connectionError = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            connectionError = true;
        }
    }

    public User getQueryUser(String nick, String password) {
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                if (Objects.equals(nick, rs.getString("nick")) && Objects.equals(password, rs.getString("password"))) {
                    return new User(rs.getString("nick"), rs.getString("password"), rs.getString("email"), rs.getString("icon"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmailInDatabase(String email) {
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT email FROM users");

            while (rs.next()) {
                if (Objects.equals(email, rs.getString("email"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertNewUser(String nick, String email, String password, String icon) {
        try {
            PreparedStatement preparedStatement;
            String sql = "INSERT INTO users (nick, email, password,icon) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nick);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, icon);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
