package com.kms.katalon.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.util.Base64;

/**
 * Database Connection
 */
public class DatabaseConnection {
    private DatabaseType databaseType;

    private String connectionUrl;

    private Connection connection;

    /**
     * Database Connection
     * 
     * @param databaseType {@link DatabaseType}
     * @param connectionUrl JDBC connection URL
     * @see com.kms.katalon.core.db.DatabaseType
     */
    public DatabaseConnection(DatabaseType databaseType, String connectionUrl) {
        this.databaseType = databaseType;
        this.connectionUrl = connectionUrl;
    }

    /**
     * Obtain a connection using the given connection URL with fulfill properties (user and password should be included)
     * 
     * @return the obtained Connection
     * @throws SQLException in case of failure
     */
    public Connection getConnection() throws SQLException {
        return getConnection(new Properties());
    }

    /**
     * Obtain a connection using the given user and password.
     *
     * @param user the name of the user
     * @param password the password to use
     * @return the obtained Connection
     * @throws SQLException in case of failure
     */
    public Connection getConnection(String user, String password) throws SQLException {
        return getConnection(user, password, false);
    }

    /**
     * Obtain a connection using the given user and password.
     *
     * @param user the name of the user
     * @param password the password to use
     * @param isPasswordEncrypted if the given password is encrypted as base64, it will be decrypted
     * @return the obtained Connection
     * @throws SQLException in case of failure
     * @see Base64#decode(String)
     */
    public Connection getConnection(String user, String password, boolean isPasswordEncrypted) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", StringUtils.defaultString(user));

        password = StringUtils.defaultString(password);
        if (isPasswordEncrypted) {
            password = Base64.decode(password);
        }
        properties.setProperty("password", password);
        return getConnection(properties);
    }

    /**
     * Obtain a Connection using the given properties.
     *
     * @param properties the connection properties
     * @return the obtained Connection
     * @throws SQLException in case of failure
     * @see java.sql.DriverManager#getConnection(String, java.util.Properties)
     */
    public Connection getConnection(Properties properties) throws SQLException {
        loadJdbcDriver();

        connection = DriverManager.getConnection(connectionUrl, properties);
        // Disable auto commit
        connection.setAutoCommit(false);
        // Enable read-only
        connection.setReadOnly(true);
        return connection;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public boolean isAlive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close connection, avoid closing if null and hide any SQLExceptions that occur.
     */
    public void close() {
        DbUtils.closeQuietly(connection);
    }

    private void loadJdbcDriver() throws SQLException {
        if (!DbUtils.loadDriver(databaseType.getDriverClass())) {
            throw new SQLException(MessageFormat.format(StringConstants.EXC_CANNOT_LOAD_JDBC_DRIVER,
                    databaseType.getDriverClass()));
        }
    }
}
