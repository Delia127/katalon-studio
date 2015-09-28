package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testdata.InternalDataFilePropertyEntity;

public interface IDataFileDataProvider {
	public DataFileEntity addNewDataFile(FolderEntity parentFolder) throws Exception;
	public String getAvailableDataFileName(FolderEntity parentFolder, String name) throws Exception;
	public DataFileEntity getDataFile(String dataFileValue) throws Exception;
	public List<DataFileEntity> getDataFileByFolder(FolderEntity parentFolder) throws Exception;
	public DataFileEntity updateInternalDataFileProperty(InternalDataFilePropertyEntity internalData) throws Exception;
	public DataFileEntity updateDataFileProperty(DataFilePropertyInputEntity dataFilePropertyInput) throws Exception;
	public void deleteDataFile(DataFileEntity dataFile) throws Exception;
	public DataFileEntity saveDataFile(DataFileEntity newDataFile) throws Exception;
	public DataFileEntity copyDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception;
	public DataFileEntity moveDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception;
	public String getIdForDisplay(DataFileEntity entity) throws Exception;
	public boolean validateDataFileName(FolderEntity parentFolder, String name) throws Exception;
	public DataFileEntity getDataFileByDisplayId(String dataFileId) throws Exception;
}
