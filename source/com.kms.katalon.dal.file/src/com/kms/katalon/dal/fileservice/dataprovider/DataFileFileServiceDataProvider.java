package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.kms.katalon.dal.IDataFileDataProvider;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.manager.DataFileFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testdata.InternalDataFilePropertyEntity;

public class DataFileFileServiceDataProvider implements IDataFileDataProvider {

    @Override
    public DataFileEntity saveNewTestData(DataFileEntity newTestData) throws Exception {
        return DataFileFileServiceManager.saveNewTestData(newTestData);
    }

    @Override
    public String getAvailableDataFileName(FolderEntity parentFolder, String name) throws Exception {
        return DataFileFileServiceManager.getAvailableDataFileName(parentFolder, name);
    }

    @Override
    public DataFileEntity getDataFile(String dataFilePk) throws Exception {
        return DataFileFileServiceManager.getDataFile(dataFilePk);
    }

    @Override
    public List<DataFileEntity> getDataFileByFolder(FolderEntity parentFolder) throws Exception {
        return FolderFileServiceManager.getChildDataFilesOfFolder(parentFolder);
    }

    @Override
    public DataFileEntity updateInternalDataFileProperty(InternalDataFilePropertyEntity internalData) throws Exception {
        return DataFileFileServiceManager.updateInternalDataFileProperty(internalData, DataProviderState.getInstance()
                .getCurrentProject());
    }

    @Override
    public DataFileEntity updateDataFileProperty(DataFilePropertyInputEntity dataFilePropertyInput) throws Exception {
        return DataFileFileServiceManager.updateDataFileProperty(dataFilePropertyInput, DataProviderState.getInstance()
                .getCurrentProject());
    }

    @Override
    public void deleteDataFile(DataFileEntity dataFile) throws Exception {
        DataFileFileServiceManager.deleteDataFile(dataFile);
    }

    @Override
    public DataFileEntity updateTestData(DataFileEntity newDataFile) throws Exception {
        return DataFileFileServiceManager.updateTestData(newDataFile);
    }

    @Override
    public DataFileEntity copyDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception {
        return DataFileFileServiceManager.copyDataFile(dataFile, destinationFolder);
    }

    @Override
    public DataFileEntity moveDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception {
        return DataFileFileServiceManager.moveDataFile(dataFile, destinationFolder);
    }

    @Override
    public String getIdForDisplay(DataFileEntity entity) throws Exception {
        return entity.getRelativePathForUI();
    }

    @Override
    public boolean validateDataFileName(FolderEntity parentFolder, String name) throws Exception {
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException(StringConstants.DP_EXC_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (!EntityService.getInstance()
                .getAvailableName(parentFolder.getLocation(), name, true)
                .equalsIgnoreCase(name)) {
            throw new InvalidNameException(StringConstants.DP_EXC_NAME_ALREADY_EXISTED);
        }
        EntityService.getInstance().validateName(name);
        return true;
    }

    @Override
    public DataFileEntity getDataFileByDisplayId(String dataFileId) throws Exception {
        ProjectEntity project = DataProviderState.getInstance().getCurrentProject();
        String projectLocation = project.getFolderLocation();
        String dataFileValue = projectLocation + File.separator + dataFileId
                + DataFileEntity.getTestDataFileExtension();
        return getDataFile(dataFileValue);
    }

    @Override
    public Map<String, List<TestSuiteTestCaseLink>> getTestDataReferences(DataFileEntity dataFileEntity)
            throws Exception {
        return DataFileFileServiceManager.getTestDataReferences(dataFileEntity);
    }
}
