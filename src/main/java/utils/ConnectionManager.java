package utils;

import Exceptions.DatabaseUnavailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final String URL = PropertiesUtil.get("db.url");

    private static final String DRIVER = PropertiesUtil.get("db.driver");

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("database unavailable");
        } catch (ClassNotFoundException e) {
            throw new DatabaseUnavailableException("database driver not found");
        }
    }
}
