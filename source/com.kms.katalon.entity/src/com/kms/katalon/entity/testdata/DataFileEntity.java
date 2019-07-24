package com.kms.katalon.entity.testdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.util.Util;

@XmlRootElement
public class DataFileEntity extends FileEntity {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_DATA_SOURCE_URL = "";

    private static final String ID_PREFIX = "DF";

    private DataFileDriverType driver;

    private String dataSourceUrl;

    private String sheetName;

    private String dataFileGUID;

    private boolean isInternalPath;

    private List<InternalDataColumnEntity> internalDataColumns;

    private List<List<Object>> data;

    private List<List<Object>> encriptData;

    private boolean containsHeaders;

    private String csvSeperator;
    
    private Map<String, String> properties;

    // Database properties
    /**
     * Using global database settings which declared in
     * <code>[project_path]/settings/external/database.properties</code>
     */
    private boolean usingGlobalDBSetting;

    /**
     * Secure User Account indicates whether user & password are excluded in {@link #dataSourceUrl} or not.<br>
     * If user & password are specified in {@link #dataSourceUrl}, they will be stored as plain text (without
     * encryption).
     */
    private boolean secureUserAccount;

    private String user;

    /** Base64 encrypted password */
    private String password;

    private String query;

    public DataFileDriverType getDriver() {
        if (driver == null) {
            driver = DataFileDriverType.ExcelFile;
        }
        return this.driver;
    }

    public void setDriver(DataFileDriverType driver) {
        this.driver = driver;
    }

    public String getDataSourceUrl() {
        if (dataSourceUrl == null) {
            dataSourceUrl = StringUtils.EMPTY;
        }
        return this.dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getSheetName() {
        if (sheetName == null) {
            sheetName = StringUtils.EMPTY;
        }
        return this.sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getDataFileGUID() {
        if (dataFileGUID == null) {
            dataFileGUID = Util.generateGuid();
        }
        return this.dataFileGUID;
    }

    public void setDataFileGUID(String dataFileGUID) {
        this.dataFileGUID = dataFileGUID;
    }

    public DataFileEntity clone() {
        DataFileEntity clonedDataFile = (DataFileEntity) super.clone();
        clonedDataFile.setDataFileGUID(dataFileGUID);
        return clonedDataFile;
    }

    public List<List<Object>> getData() {
        if (data == null) {
            data = new ArrayList<List<Object>>();
        }
        return data;
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    public List<InternalDataColumnEntity> getInternalDataColumns() {
        if (internalDataColumns == null) {
            internalDataColumns = new ArrayList<InternalDataColumnEntity>();
        }
        return internalDataColumns;
    }

    public void setInternalDataColumns(List<InternalDataColumnEntity> internalDataColumns) {
        this.internalDataColumns = internalDataColumns;
    }

    public String getTableDataName() {
        return ID_PREFIX + getId();
    }

    public enum DataFileDriverType {
        ExcelFile("Excel File"), CSV("CSV File"), DBData("Database Data"), InternalData("Internal Data");

        private String text;

        private DataFileDriverType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public String value() {
            return name();
        }

        public static DataFileDriverType fromValue(String v) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].toString().equals(v)) {
                    return values()[i];
                }
            }
            return valueOf(v);
        }

        public static String[] stringValues() {
            String[] stringValues = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                stringValues[i] = values()[i].toString();
            }
            return stringValues;
        }
    }

    public String buildDataFileEntityId() {
        return ID_PREFIX + String.format("%010d", getId());
    }

    public String buildDataFileEntityId(long id) {
        return ID_PREFIX + String.format("%010d", id);
    }

    public boolean getIsInternalPath() {
        return isInternalPath;
    }

    public void setIsInternalPath(boolean isInternalPath) {
        this.isInternalPath = isInternalPath;
    }

    public static String getTestDataFileExtension() {
        return ".dat";
    }

    @Override
    public String getFileExtension() {
        return getTestDataFileExtension();
    }

    public List<List<Object>> getEncriptData() {
        return encriptData;
    }

    public void setEncriptData(List<List<Object>> data) {
        this.encriptData = data;
    }

    public String getCsvSeperator() {
        if (csvSeperator == null) {
            csvSeperator = StringUtils.EMPTY;
        }
        return csvSeperator;
    }

    public void setCsvSeperator(String csvSeperator) {
        this.csvSeperator = csvSeperator;
    }

    public boolean isContainsHeaders() {
        return containsHeaders;
    }

    public void setContainsHeaders(boolean containsHeaders) {
        this.containsHeaders = containsHeaders;
    }

    public boolean isUsingGlobalDBSetting() {
        return usingGlobalDBSetting;
    }

    public void setUsingGlobalDBSetting(boolean usingGlobalDBSetting) {
        this.usingGlobalDBSetting = usingGlobalDBSetting;
    }

    public boolean isSecureUserAccount() {
        return secureUserAccount;
    }

    public void setSecureUserAccount(boolean secureUserAccount) {
        this.secureUserAccount = secureUserAccount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return Base64 encrypted password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        if (query == null) {
            query = StringUtils.EMPTY;
        }
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @XmlElementWrapper(name="properties")
    private Map<String, String> getProperties() {
        return properties;
    }
    
    public String getProperty(String key) {
        if (properties == null || key == null || key.equals(StringUtils.EMPTY)) {
            return StringUtils.EMPTY;
        }
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        if (properties == null) {
        	properties = new HashMap<String, String>();
        }
        properties.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEquals = super.equals(obj);
        if (!(obj instanceof DataFileEntity)) {
            return false;
        }
        DataFileEntity that = (DataFileEntity) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder().append(this.getDataFileGUID(), that.getDataFileGUID())
                .append(this.getDriver(), that.getDriver())
                .append(this.getDateCreated(), that.getDateCreated())
                .append(this.getDateModified(), that.getDateModified())
                .append(this.getDescription(), that.getDescription())
                .append(this.isContainsHeaders(), that.isContainsHeaders())
                .append(this.getDataSourceUrl(), that.getDataSourceUrl())
                .append(this.getSheetName(), that.getSheetName())
                .append(this.getCsvSeperator(), that.getCsvSeperator())
                .append(this.getIsInternalPath(), that.getIsInternalPath());
        if (this.getIsInternalPath() && equalsBuilder.isEquals()) {
            equalsBuilder.append(this.getData(), that.getData());
        }
        equalsBuilder.append(this.isUsingGlobalDBSetting(), that.isUsingGlobalDBSetting())
                .append(this.isSecureUserAccount(), that.isSecureUserAccount())
                .append(this.getUser(), that.getUser())
                .append(this.getPassword(), that.getPassword())
                .append(this.getQuery(), that.getQuery())
                .append(this.getProperties(), that.getProperties());
        return isEquals && equalsBuilder.isEquals();
    }
}
