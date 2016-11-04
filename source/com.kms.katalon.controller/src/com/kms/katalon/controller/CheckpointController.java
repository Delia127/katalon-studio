package com.kms.katalon.controller;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.controller.exception.EntityNotFoundException;
import com.kms.katalon.core.testdata.CSVData;
import com.kms.katalon.core.testdata.DBData;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.NullAttributeException;
import com.kms.katalon.entity.checkpoint.CheckpointCell;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class CheckpointController extends EntityController {

    private static CheckpointController instance;

    public static CheckpointController getInstance() {
        if (instance == null) {
            instance = new CheckpointController();
        }
        return instance;
    }

    public String getAvailableName(FolderEntity parentFolder, String name) throws Exception {
        return EntityNameController.getInstance().getAvailableName(name, parentFolder, false);
    }

    public CheckpointEntity getById(String checkpointId) throws DALException {
        return getDataProviderSetting().getCheckpointDataProvider().getById(checkpointId);
    }

    public CheckpointEntity getByDisplayedId(String checkpointDisplayedId) throws DALException, EntityNotFoundException {
        return getById(getCheckpointIdByDisplayedId(checkpointDisplayedId));
    }

    public CheckpointEntity initialNewCheckpoint(FolderEntity parentFolder, String name) {
        CheckpointEntity checkpoint = new CheckpointEntity();
        checkpoint.setName(name);
        checkpoint.setProject(ProjectController.getInstance().getCurrentProject());
        checkpoint.setParentFolder(parentFolder);
        // Test Data will be checkpoint source by default
        checkpoint.setSourceInfo(new CheckpointSourceInfo(StringConstants.EMPTY));
        return checkpoint;
    }

    public CheckpointEntity create(CheckpointEntity checkpointEntity) throws DALException {
        return getDataProviderSetting().getCheckpointDataProvider().create(checkpointEntity);
    }

    public CheckpointEntity update(CheckpointEntity checkpointEntity) throws DALException {
        return getDataProviderSetting().getCheckpointDataProvider().update(checkpointEntity);
    }

    ProjectEntity project = ProjectController.getInstance().getCurrentProject();

    public String getCheckpointIdByDisplayedId(String checkpointDisplayedId) throws EntityNotFoundException {
        if (project == null) {
            throw new EntityNotFoundException(StringConstants.EXC_MSG_PROJECT_NOT_FOUND);
        }
        return project.getFolderLocation() + File.separator
                + checkpointDisplayedId.replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator)
                + CheckpointEntity.getCheckpointFileExtension();
    }

    public CheckpointEntity copy(CheckpointEntity checkpointEntity, FolderEntity destinationFolder) throws DALException {
        return getDataProviderSetting().getCheckpointDataProvider().copy(checkpointEntity, destinationFolder);
    }

    public CheckpointEntity move(CheckpointEntity checkpointEntity, FolderEntity destinationFolder) throws DALException {
        return getDataProviderSetting().getCheckpointDataProvider().move(checkpointEntity, destinationFolder);
    }

    public void delete(CheckpointEntity checkpointEntity) throws DALException {
        getDataProviderSetting().getCheckpointDataProvider().delete(checkpointEntity);
    }

    /**
     * @param checkpointEntity Checkpoint entity
     * @return An updated {@link CheckpointEntity} with data snapshot
     * @throws Exception
     */
    public CheckpointEntity takeSnapshot(CheckpointEntity checkpointEntity) throws Exception {
        if (checkpointEntity == null) {
            throw new NullArgumentException(StringConstants.CTRL_EXC_CHECKPOINT_IS_NULL);
        }

        CheckpointSourceInfo sourceInfo = checkpointEntity.getSourceInfo();
        if (sourceInfo.getSourceType() != DataFileDriverType.DBData && StringUtils.isBlank(sourceInfo.getSourceUrl())) {
            throw new NullAttributeException(StringConstants.CTRL_EXC_SOURCE_URL_IS_NULL);
        }
        List<String> columnNames = new ArrayList<>();
        List<List<Object>> data = getCheckpointSourceData(sourceInfo, columnNames);

        if (data == null) {
            throw new IllegalArgumentException(MessageFormat.format(StringConstants.CTRL_EXC_CANNOT_TAKE_SNAPSHOT_DATA,
                    checkpointEntity.getIdForDisplay(), new Object[] { sourceInfo.getSourceType() }));
        }

        checkpointEntity.setColumnNames(columnNames);
        checkpointEntity.setCheckpointData(checkpointDataWrapper(data));
        checkpointEntity.setTakenDate(new Date());
        return checkpointEntity;
    }

    private List<List<Object>> getCheckpointSourceData(CheckpointSourceInfo sourceInfo, List<String> columnNames)
            throws Exception {
        ExcelCheckpointSourceController excelSrcController = ExcelCheckpointSourceController.getInstance();
        CsvCheckpointSourceController csvSrcController = CsvCheckpointSourceController.getInstance();
        DatabaseCheckpointSourceController dbSrcController = DatabaseCheckpointSourceController.getInstance();

        // Test Data source
        if (sourceInfo.isFromTestData()) {
            String testDataDisplayedId = sourceInfo.getSourceUrl();
            TestData testdata = TestDataFactory.findTestDataForExternalBundleCaller(testDataDisplayedId,
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            columnNames.addAll(Arrays.asList(testdata.getColumnNames()));
            return testdata.getAllData();
        }

        // Self-defined source. Supported Excel, CSV and Database only.
        DataFileDriverType sourceType = sourceInfo.getSourceType();
        if (sourceType == null) {
            return null;
        }

        switch (sourceType) {
            case ExcelFile:
                ExcelData excelData = excelSrcController.getSourceData((ExcelCheckpointSourceInfo) sourceInfo);
                columnNames.addAll(Arrays.asList(excelData.getColumnNames()));
                return excelData.getAllData();
            case CSV:
                CSVData csvData = csvSrcController.getSourceData((CsvCheckpointSourceInfo) sourceInfo);
                columnNames.addAll(Arrays.asList(csvData.getColumnNames()));
                return csvData.getAllData();
            case DBData:
                DBData dbData = dbSrcController.getSourceData((DatabaseCheckpointSourceInfo) sourceInfo);
                columnNames.addAll(Arrays.asList(dbData.getColumnNames()));
                return dbData.getAllData();
            default:
                return null;
        }
    }

    private List<List<CheckpointCell>> checkpointDataWrapper(List<List<Object>> data) {
        List<List<CheckpointCell>> checkpointData = new ArrayList<List<CheckpointCell>>();
        for (List<Object> row : data) {
            List<CheckpointCell> r = new ArrayList<>();
            for (Object o : row) {
                r.add(new CheckpointCell(o));
            }
            checkpointData.add(r);
        }
        return checkpointData;
    }

}
