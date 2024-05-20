package utils;

import Exceptions.DatabaseUnavailableException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final String URL = PropertiesUtil.get("db.url");

    private static final String DRIVER = PropertiesUtil.get("db.driver");
    private static final HikariDataSource HIKARI_DATA_SOURCE;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(URL);
        config.setDriverClassName(DRIVER);

        HIKARI_DATA_SOURCE = new HikariDataSource(config);
    }

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return HIKARI_DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("database unavailable");
        }
    }
}
