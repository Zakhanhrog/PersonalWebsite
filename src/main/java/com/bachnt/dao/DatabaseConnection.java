package com.bachnt.dao;

import java.net.URI;
import java.net.URISyntaxException;
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
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbName = System.getenv("DB_NAME"); // Thêm biến này cho tên DB

        if (dbUrl != null && dbUser != null && dbPassword != null) {
            if ("postgres".equalsIgnoreCase(dbType)) {
                logger.info("DB_TYPE is postgres. Attempting to connect to PostgreSQL.");
                try {
                    Class.forName(POSTGRESQL_DRIVER_CLASS);
                    String connectionUrl = String.format("jdbc:postgresql://%s/%s?sslmode=require", dbUrl, dbName);
                    logger.info("Connecting to PostgreSQL with URL: {}", connectionUrl);
                    return DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
                } catch (ClassNotFoundException e) {
                    logger.error("PostgreSQL JDBC Driver not found.", e);
                    throw new SQLException("PostgreSQL JDBC Driver not found.", e);
                }
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                logger.info("DB_TYPE is mysql. Attempting to connect to MySQL.");
                try {
                    Class.forName(MYSQL_DRIVER_CLASS);
                    String connectionUrl = String.format("jdbc:mysql://%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", dbUrl, dbName);
                    logger.info("Connecting to MySQL with URL: {}", connectionUrl);
                    return DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
                } catch (ClassNotFoundException e) {
                    logger.error("MySQL JDBC Driver not found.", e);
                    throw new SQLException("MySQL JDBC Driver not found.", e);
                }
            } else if (dbUrl.startsWith("jdbc:postgresql://")) { // Tương thích ngược với DATABASE_URL của Heroku
                logger.info("DATABASE_URL (DB_URL) for PostgreSQL found. Parsing and connecting.");
                try {
                    Class.forName(POSTGRESQL_DRIVER_CLASS);
                    URI dbUri = new URI(dbUrl.substring("jdbc:".length())); // Bỏ "jdbc:" để URI parse đúng

                    String username = dbUri.getUserInfo().split(":")[0];
                    String passwordFromUri = dbUri.getUserInfo().split(":")[1];
                    // Tái tạo jdbcUrl từ URI, vì dbUrl lúc này là đầy đủ rồi
                    String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

                    if (!jdbcUrl.contains("?")) {
                        jdbcUrl += "?sslmode=require";
                    } else {
                        if (!jdbcUrl.contains("sslmode=")) {
                            jdbcUrl += "&sslmode=require";
                        }
                    }
                    logger.info("Connecting to PostgreSQL with parsed URL: {} , User: {}", jdbcUrl, username);
                    return DriverManager.getConnection(jdbcUrl, username, passwordFromUri);

                } catch (URISyntaxException e) {
                    logger.error("Invalid DATABASE_URL (DB_URL) syntax: {}", dbUrl, e);
                    throw new SQLException("Invalid DATABASE_URL (DB_URL) syntax.", e);
                } catch (ClassNotFoundException e) {
                    logger.error("PostgreSQL JDBC Driver not found.", e);
                    throw new SQLException("PostgreSQL JDBC Driver not found.", e);
                }
            }
            else {
                logger.warn("DB_TYPE environment variable not set or not recognized (expected 'postgres' or 'mysql'). " +
                        "No specific logic for DB_URL format: {}. Attempting fallback to local MySQL (THIS SHOULD NOT HAPPEN ON RENDER).", dbUrl);
                return getLocalMySQLConnectionForDev(); // Chỉ nên gọi khi phát triển local
            }
        } else {
            logger.warn("One or more database environment variables (DB_URL, DB_USER, DB_PASSWORD) are not set. " +
                    "Attempting fallback to local MySQL (THIS SHOULD NOT HAPPEN ON RENDER).");
            return getLocalMySQLConnectionForDev(); // Chỉ nên gọi khi phát triển local
        }
    }

    // Phương thức riêng cho kết nối local MySQL để dễ quản lý
    private static Connection getLocalMySQLConnectionForDev() throws SQLException {
        final String LOCAL_MYSQL_URL_STRING = "jdbc:mysql://localhost:3306/personalwebdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        final String LOCAL_MYSQL_USER_STRING = "root";
        final String LOCAL_MYSQL_PASSWORD_STRING = "khanh7679"; // Hãy cẩn thận với việc hardcode mật khẩu

        logger.info("Connecting to local MySQL (DEV fallback) with URL: {}", LOCAL_MYSQL_URL_STRING);
        try {
            Class.forName(MYSQL_DRIVER_CLASS);
            return DriverManager.getConnection(LOCAL_MYSQL_URL_STRING, LOCAL_MYSQL_USER_STRING, LOCAL_MYSQL_PASSWORD_STRING);
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found (DEV fallback).", e);
            throw new SQLException("MySQL JDBC Driver not found (DEV fallback).", e);
        }
    }


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