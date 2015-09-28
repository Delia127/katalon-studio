package com.kms.katalon.entity.testdata;

import java.util.List;

public class InternalDataFilePropertyEntity {

	private String pk;
	private String name;
	private String description;
	private String datFileDriverName;
	private String dataSourceURL;
	private String sheetName;
	private String tableName;
	private List<Object> headerColumn;
	private List<List<Object>> data;

	public String getPk() {
		return pk;
	}

	public void setPk(String idInput) {
		pk = idInput;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameInput) {
		name = nameInput;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descriptionInput) {
		description = descriptionInput;
	}

	public String getDataFileDriverName() {
		return datFileDriverName;

	}

	public void setDataFileDriverName(String dataFileDriverNameInput) {
		datFileDriverName = dataFileDriverNameInput;

	}

	public String getDataSourceURL() {
		return dataSourceURL;
	}

	public void setDataSourceURL(String dataSourceURLInput) {
		dataSourceURL = dataSourceURLInput;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetNameInput) {
		sheetName = sheetNameInput;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableNameInput) {
		tableName = tableNameInput;
	}

	public List<Object> getHeaderColumn() {
		return headerColumn;
	}

	public void setHeaderColumn(List<Object> headerCol) {
		headerColumn = headerCol;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> dataInput) {
		data = dataInput;
	}

}
