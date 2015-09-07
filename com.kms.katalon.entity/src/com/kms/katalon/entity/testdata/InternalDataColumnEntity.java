package com.kms.katalon.entity.testdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InternalDataColumnEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int entitySize = 3;
	public static final int columnNameIndex = 1;
	public static final int sizeIndex = 2;
	public static final int dataTypeIndex = 3;

	private String name;
	
	private int columnIndex;

	private String dataType;

	private int size;

	private DataFileEntity dataFile;

	private DataFileEntity dataFileLocation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public DataFileEntity getDataFileLocation() {
		return dataFileLocation;
	}

	public void setDataFileLocation(DataFileEntity dataFileLocation) {
		this.dataFileLocation = dataFileLocation;
	}

	public DataFileEntity getDataFile() {
		return this.dataFile;
	}

	public void setDataFile(DataFileEntity dataFile) {
		this.dataFile = dataFile;
	}

	public InternalDataColumnEntity clone() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(this);

			byte[] bytes = bos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

			return (InternalDataColumnEntity) ois.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			return null;
		}
	}
}
