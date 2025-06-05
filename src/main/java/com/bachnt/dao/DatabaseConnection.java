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

    private static final String LOCAL_MYSQL_URL = "jdbc:mysql://localhost:3306/personalwebdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String LOCAL_MYSQL_USER = "root";
    private static final String LOCAL_MYSQL_PASSWORD = "khanh7679";
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";

    public static Connection getConnection() throws SQLException {
        String dbUrlEnv = System.getenv("DATABASE_URL");

        if (dbUrlEnv != null && !dbUrlEnv.isEmpty()) {
            logger.info("DATABASE_URL environment variable found. Attempting to connect to Heroku PostgreSQL.");
            try {
                Class.forName(POSTGRESQL_DRIVER_CLASS);
                URI dbUri = new URI(dbUrlEnv);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

                if (!jdbcUrl.contains("?")) {
                    jdbcUrl += "?sslmode=require";
                } else {
                    if (!jdbcUrl.contains("sslmode=")) {
                        jdbcUrl += "&sslmode=require";
                    }
                }

                logger.info("Connecting to PostgreSQL with URL: {} , User: {}", jdbcUrl, username);
                return DriverManager.getConnection(jdbcUrl, username, password);

            } catch (URISyntaxException e) {
                logger.error("Invalid DATABASE_URL syntax: {}", dbUrlEnv, e);
                throw new SQLException("Invalid DATABASE_URL syntax.", e);
            } catch (ClassNotFoundException e) {
                logger.error("PostgreSQL JDBC Driver not found.", e);
                throw new SQLException("PostgreSQL JDBC Driver not found.", e);
            }
        } else {
            logger.warn("DATABASE_URL environment variable not found. Falling back to local MySQL configuration.");
            try {
                Class.forName(MYSQL_DRIVER_CLASS);
                logger.info("Connecting to local MySQL with URL: {}", LOCAL_MYSQL_URL);
                return DriverManager.getConnection(LOCAL_MYSQL_URL, LOCAL_MYSQL_USER, LOCAL_MYSQL_PASSWORD);
            } catch (ClassNotFoundException e) {
                logger.error("MySQL JDBC Driver not found.", e);
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
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