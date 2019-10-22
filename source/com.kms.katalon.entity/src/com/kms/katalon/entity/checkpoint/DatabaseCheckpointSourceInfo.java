package com.kms.katalon.entity.checkpoint;

import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

/**
 * Checkpoint database source info
 */
public class DatabaseCheckpointSourceInfo extends CheckpointSourceInfo {

    private static final long serialVersionUID = 3179900113916086055L;

    /**
     * Using global database settings which declared in
     * <code>[project_path]/settings/external/database.properties</code>
     */
    private Boolean usingGlobalDBSetting;

    /**
     * Secure User Account indicates whether user & password are excluded in {@link #sourceUrl} or not.<br>
     * If user & password are specified in {@link #sourceUrl}, they will be stored as plain text (without
     * encryption).
     */
    private Boolean secureUserAccount;

    /** Database user for login */
    private String user;

    /** Base64 encrypted password */
    private String password;

    /** Database query string */
    private String query;
    
    private String driverClassName;

    public DatabaseCheckpointSourceInfo() {
        this(StringConstants.EMPTY, false, false, null, null, StringConstants.EMPTY, null);
    }

    /**
     * Checkpoint database source info
     * 
     * @param sourceUrl Database connection URL
     * @param usingGlobalDBSetting is using global DB setting
     * @param secureUserAccount is secure user account
     * @param user DB user
     * @param password DB password
     * @param query DB SQL query
     */
    public DatabaseCheckpointSourceInfo(String sourceUrl, boolean usingGlobalDBSetting, boolean secureUserAccount, String user,
            String password, String query, String driverClassName) {
        setSourceUrl(sourceUrl);
        setSourceType(DataFileDriverType.DBData);
        this.usingGlobalDBSetting = usingGlobalDBSetting;
        this.secureUserAccount = secureUserAccount;
        this.user = user;
        this.password = password;
        this.query = query;
        this.driverClassName = driverClassName;
    }

    public Boolean isUsingGlobalDBSetting() {
        return usingGlobalDBSetting;
    }

    public void setUsingGlobalDBSetting(Boolean usingGlobalDBSetting) {
        this.usingGlobalDBSetting = usingGlobalDBSetting;
    }

    public Boolean isSecureUserAccount() {
        return secureUserAccount;
    }

    public void setSecureUserAccount(Boolean secureUserAccount) {
        this.secureUserAccount = secureUserAccount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public DatabaseCheckpointSourceInfo clone() {
        return (DatabaseCheckpointSourceInfo) super.clone();
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
