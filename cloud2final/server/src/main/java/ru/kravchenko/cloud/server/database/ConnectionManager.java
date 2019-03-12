package ru.kravchenko.cloud.server.database;

import lombok.SneakyThrows;

import java.sql.*;

/**
 * @author Roman Kravchenko
 */

public class ConnectionManager implements AutoService {

    public ConnectionManager() { start(); }

    private Connection connection;

    private Statement statement;

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public String getNickname(String login, String password) {
        ResultSet rs = statement.executeQuery(String.format("SELECT * FROM users WHERE login = '%s' AND password = '%s'", login, password));
        if (!rs.next()) return null;
        System.out.println(rs.getString(1));
        System.out.println(rs.getString(2));
        System.out.println(rs.getString(3));
        return rs.getString(1);
    }


    @SneakyThrows
    public boolean chekRegistry(String login, String password) {
        if (login == null || password == null) {
            System.out.println("once field is empty");
            return false;
        }
        ResultSet rs = statement.executeQuery(String.format("SELECT * FROM users WHERE login = '%s' AND password = '%s'", login, password));
        if (!rs.next()) {
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
        disconnect();
    }

    @Override
    public boolean start() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/usercloud", "root", "root");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
