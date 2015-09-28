package com.kms.katalon.entity.testdata;

public class DataFilePropertyInputEntity {
	private String pk;

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	private String name;
	private String description;
	private String dataFileDriver;
	private String dataSourceURL;
	private String sheetName;
	private boolean isInternalPath;
	private boolean hasHeaders;
	private String csvSeperator;

	public DataFilePropertyInputEntity() {
		pk = "";
		name = "";
		description = "";
		dataFileDriver = "";
		dataSourceURL = "";
		sheetName = "";
		isInternalPath = false;
	}

	public DataFilePropertyInputEntity(DataFileEntity dataFile) {
		pk = dataFile.getLocation();
		name = dataFile.getName();
		description = dataFile.getDescription();
		dataFileDriver = dataFile.getDriver().toString();
		dataSourceURL = dataFile.getDataSourceUrl();
		sheetName = dataFile.getSheetName();
		isInternalPath = dataFile.getIsInternalPath();
		hasHeaders = dataFile.isContainsHeaders();
		setCsvSeperator(dataFile.getCsvSeperator());
	}

	public boolean getIsInternalPath() {
		return isInternalPath;
	}

	public void setIsInternalPath(boolean isInternal) {
		this.isInternalPath = isInternal;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getDataSourceURL() {
		return dataSourceURL;
	}

	public void setdataSourceURL(String datasource) {
		this.dataSourceURL = datasource;
	}

	public String getDataFileDriver() {
		return dataFileDriver;
	}

	public void setDataFileDriver(String driver) {
		this.dataFileDriver = driver;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desInput) {
		this.description = desInput;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameInput) {
		this.name = nameInput;
	}
	
	public void setEnableHeader(boolean hasHeaders) {
	    this.hasHeaders = hasHeaders;
	}
	
	public boolean isEnableHeaders() {
	    return hasHeaders;
	}

    public String getCsvSeperator() {
        return csvSeperator;
    }

    public void setCsvSeperator(String csvSeperator) {
        this.csvSeperator = csvSeperator;
    }

}
