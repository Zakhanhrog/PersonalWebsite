package com.bachnt.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";

    public static Connection getConnection() throws SQLException {
        String dbType = System.getenv("DB_TYPE");
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbName = System.getenv("DB_NAME");

        // Điều kiện kiểm tra bao gồm tất cả các biến cần thiết
        if (dbType != null && dbHost != null && dbPort != null && dbUser != null && dbPassword != null && dbName != null) {
            if ("postgres".equalsIgnoreCase(dbType)) {
                logger.info("DB_TYPE is postgres. Connecting to PostgreSQL.");
                try {
                    Class.forName(POSTGRESQL_DRIVER_CLASS);
                    String connectionUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", dbHost, dbPort, dbName);
                    logger.info("PostgreSQL URL: {}", connectionUrl);
                    return DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
                } catch (ClassNotFoundException e) {
                    logger.error("PostgreSQL JDBC Driver not found.", e);
                    throw new SQLException("PostgreSQL JDBC Driver not found.", e);
                }
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                logger.info("DB_TYPE is mysql. Connecting to MySQL.");
                try {
                    Class.forName(MYSQL_DRIVER_CLASS);
                    String connectionUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", dbHost, dbPort, dbName);
                    logger.info("MySQL URL: {}", connectionUrl);
                    return DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
                } catch (ClassNotFoundException e) {
                    logger.error("MySQL JDBC Driver not found.", e);
                    throw new SQLException("MySQL JDBC Driver not found.", e);
                }
            } else {
                logger.error("DB_TYPE environment variable ('{}') not recognized (expected 'postgres' or 'mysql'). Cannot establish database connection.", dbType);
                throw new SQLException("Unsupported DB_TYPE: " + dbType);
            }
        } else {
            // Log rõ hơn các biến nào bị thiếu
            StringBuilder missingVars = new StringBuilder();
            if (dbType == null) missingVars.append("DB_TYPE, ");
            if (dbHost == null) missingVars.append("DB_HOST, ");
            if (dbPort == null) missingVars.append("DB_PORT, ");
            if (dbName == null) missingVars.append("DB_NAME, ");
            if (dbUser == null) missingVars.append("DB_USER, ");
            if (dbPassword == null) missingVars.append("DB_PASSWORD, ");

            String missing = missingVars.length() > 0 ? missingVars.substring(0, missingVars.length() - 2) : "Unknown";

            logger.warn("One or more database environment variables are not set (Missing: {}). Attempting fallback to local MySQL (THIS SHOULD NOT HAPPEN ON RENDER).", missing);
            return getLocalMySQLConnectionForDev();
        }
    }

    private static Connection getLocalMySQLConnectionForDev() throws SQLException {
        final String LOCAL_MYSQL_URL_STRING = "jdbc:mysql://localhost:3306/personalwebdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        final String LOCAL_MYSQL_USER_STRING = "root";
        final String LOCAL_MYSQL_PASSWORD_STRING = "khanh7679";

        logger.info("Connecting to local MySQL (DEV fallback) with URL: {}", LOCAL_MYSQL_URL_STRING);
        try {
            Class.forName(MYSQL_DRIVER_CLASS);
            return DriverManager.getConnection(LOCAL_MYSQL_URL_STRING, LOCAL_MYSQL_USER_STRING, LOCAL_MYSQL_PASSWORD_STRING);
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found (DEV fallback).", e);
            throw new SQLException("MySQL JDBC Driver not found (DEV fallback).", e);
        }
    }
    // ... (các hàm closeResources giữ nguyên) ...
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing connection: {}", e.getMessage(), e);
            }
        }
    }

    public static void closeStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("Error closing statement: {}", e.getMessage(), e);
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Error closing result set: {}", e.getMessage(), e);
            }
        }
    }

    public static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    public static void closeResources(Connection conn, PreparedStatement stmt) {
        closeStatement(stmt);
        closeConnection(conn);
    }
}