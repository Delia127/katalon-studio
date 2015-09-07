package com.kms.katalon.entity.testdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.util.Util;

public class DataFileEntity extends FileEntity {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_DATA_SOURCE_URL = "SourceURL";
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
			data = new ArrayList<>();
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
		ExcelFile("Excel File"), CSV("CSV File"), InternalData("Internal Data");

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
	
    @Override
    public boolean equals(Object that) {
        boolean equals = super.equals(that);
        if (equals) {
            DataFileEntity anotherDataFile = (DataFileEntity) that;
            if (!getDataFileGUID().equals(anotherDataFile.getDataFileGUID())) {
                return false;
            }
            
            if (!getDateCreated().equals(anotherDataFile.getDateCreated())) {
                return false;
            }
            
            if (!getDateModified().equals(anotherDataFile.getDateModified())) {
                return false;
            }
            
            if (!getDescription().equals(anotherDataFile.getDescription())) {
                return false;
            }
            
            if (isContainsHeaders() != anotherDataFile.isContainsHeaders()) { 
                return false;
            }
            
            if (!getDataSourceUrl().equals(anotherDataFile.getDataSourceUrl())) {
                return false;
            }
            
            if (!getSheetName().equals(anotherDataFile.getSheetName())) {
                return false;
            }
            
            if (!getCsvSeperator().equals(anotherDataFile.getCsvSeperator())) {
                return false;
            }
            
            if (getIsInternalPath() != anotherDataFile.getIsInternalPath()) {
                return false;
            }
            
            if (getIsInternalPath()) {
                if (!getData().isEmpty()) {
                    if (!getData().equals(anotherDataFile.getData())) {
                        return false;
                    }
                } else {
                    if (!anotherDataFile.getData().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return equals;
    }
}
