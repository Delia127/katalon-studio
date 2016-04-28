package com.kms.katalon.core.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Supported Database Types
 * <ul>
 * <li>MySQL</li>
 * <li>SQLServer</li>
 * </ul>
 *
 */
public enum DatabaseType {
    MySQL("com.mysql.jdbc.Driver", 3306), SQLServer("com.microsoft.sqlserver.jdbc.SQLServerDriver", 1433);

    /** Driver class name */
    private String driverClass;

    /** Default connection port number */
    private int defaultPort;

    private DatabaseType(final String driverClass, int defaultPort) {
        this.driverClass = driverClass;
        this.defaultPort = defaultPort;
    }

    public static DatabaseType fromName(String name) {
        for (DatabaseType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * @return List of DatabaseType name
     */
    public static List<String> names() {
        List<String> list = new ArrayList<String>();
        for (DatabaseType type : values()) {
            list.add(type.name());
        }
        return list;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public int getDefaultPort() {
        return defaultPort;
    }
}
