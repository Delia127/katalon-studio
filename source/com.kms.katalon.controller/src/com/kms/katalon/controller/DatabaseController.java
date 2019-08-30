package com.kms.katalon.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.db.DatabaseSettings;
import com.kms.katalon.core.db.ListResultSetHandler;
import com.kms.katalon.core.db.SqlRunner;
import com.kms.katalon.core.util.internal.Base64;
import com.kms.katalon.entity.project.ProjectEntity;

public class DatabaseController {

    private static DatabaseController instance;

    public static DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
        }
        return instance;
    }

    /**
     * Get database connection from global settings
     * 
     * @return {@link DatabaseConnection}
     * @throws IOException if <code>[project_dir]/settings/external/database.properties</code> is not found
     */
    public DatabaseConnection getGlobalDatabaseConnection() throws IOException {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        return new DatabaseSettings(currentProject.getFolderLocation()).getDatabaseConnection();
    }

    /**
     * Get Database connection
     * 
     * @param isUsingGlobalDBSetting is using global DB setting
     * @param isSecureUserAccount is user account secure and excluded from {@code connectionUrl}
     * @param user Database user
     * @param password Base64 encrypted password
     * @param connectionUrl JDBC connection URL
     * @return {@link DatabaseConnection}
     * @throws IOException if {@code isUsingGlobalDBSetting} is true and
     * <code>[project_dir]/settings/external/database.properties</code> is not found
     */
    public DatabaseConnection getDatabaseConnection(boolean isUsingGlobalDBSetting, boolean isSecureUserAccount,
            String user, String password, String connectionUrl) throws IOException {
        if (isUsingGlobalDBSetting) {
            return getGlobalDatabaseConnection();
        }

        if (isSecureUserAccount) {
            return new DatabaseConnection(connectionUrl, user, Base64.decode(password));
        }

        return new DatabaseConnection(connectionUrl);
    }
    
    public DatabaseConnection getDatabaseConnection(boolean isUsingGlobalDBSetting, boolean isSecureUserAccount,
            String user, String password, String connectionUrl, String driverClassName) throws IOException {
        if (isUsingGlobalDBSetting) {
            return getGlobalDatabaseConnection();
        }

        if (isSecureUserAccount) {
            return new DatabaseConnection(connectionUrl, user, Base64.decode(password), driverClassName);
        }

        return new DatabaseConnection(connectionUrl, driverClassName);
    }

    public ResultSet query(DatabaseConnection dbConnection, String sqlQuery) throws SQLException {
        return query(dbConnection, sqlQuery, (Object[]) null);
    }

    public ResultSet query(DatabaseConnection dbConnection, String sqlQuery, Object[] params) throws SQLException {
        try (SqlRunner runner = new SqlRunner(dbConnection, sqlQuery, params)) {
            return runner.query();
        }
    }

    public <T> T query(DatabaseConnection dbConnection, String sqlQuery, ListResultSetHandler<T> resultSetHandler)
            throws SQLException {
        return query(dbConnection, sqlQuery, null, resultSetHandler);
    }

    public <T> T query(DatabaseConnection dbConnection, String sqlQuery, Object[] params,
            ListResultSetHandler<T> resultSetHandler) throws SQLException {
        try (SqlRunner runner = new SqlRunner(dbConnection, sqlQuery, params)) {
            return runner.query(resultSetHandler);
        }
    }

}
