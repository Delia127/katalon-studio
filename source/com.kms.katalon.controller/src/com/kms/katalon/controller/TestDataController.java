package com.kms.katalon.controller;

import java.io.File;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.testdata.DBData;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testdata.InternalDataFilePropertyEntity;
import com.kms.katalon.entity.util.Util;

@Creatable
public class TestDataController extends EntityController {

    private static EntityController _instance;

    private TestDataController() {
        super();
    }

    public static TestDataController getInstance() {
        if (_instance == null) {
            _instance = new TestDataController();
        }
        return (TestDataController) _instance;
    }

    /**
     * Create and save new Test Data
     * 
     * @param parentFolder Parent folder entity
     * @param testDataName Test Data name
     * @return {@link DataFileEntity}
     * @throws Exception
     */
    public DataFileEntity newTestData(FolderEntity parentFolder, String testDataName) throws Exception {
        return saveNewTestData(newTestDataWithoutSave(parentFolder, testDataName));
    }

    /**
     * Create new Test Data without save.
     * 
     * @param parentFolder Parent folder entity
     * @param testDataName Test Data name
     * @return {@link DataFileEntity}
     * @throws Exception
     */
    public DataFileEntity newTestDataWithoutSave(FolderEntity parentFolder, String testDataName) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        if (StringUtils.isBlank(testDataName)) {
            testDataName = StringConstants.CTRL_NEW_TEST_DATA;
        }

        DataFileEntity newTestData = new DataFileEntity();
        newTestData.setDataFileGUID(Util.generateGuid());
        newTestData.setName(getAvailableTestDataName(parentFolder, testDataName));
        newTestData.setParentFolder(parentFolder);
        newTestData.setProject(parentFolder.getProject());
        newTestData.setDriver(DataFileDriverType.ExcelFile);
        newTestData.setDataSourceUrl(DataFileEntity.DEFAULT_DATA_SOURCE_URL);

        return newTestData;
    }

    /**
     * Save a NEW Test Data.<br>
     * Please use {@link #updateTestData(DataFileEntity, FolderEntity)} if you want to save an existing Test Data.
     * 
     * @param newTestData the new Test Data which is created by {@link #newTestDataWithoutSave(FolderEntity, String)}
     * @return {@link DataFileEntity} the saved Test Data
     * @throws Exception
     */
    public DataFileEntity saveNewTestData(DataFileEntity newTestData) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().saveNewTestData(newTestData);
    }

    /**
     * Save an existing Test Data.
     * 
     * @param newDataFile Test Data
     * @param parentFolder parent folder entity
     * @return {@link DataFileEntity} the saved Test Data
     * @throws Exception
     */
    public DataFileEntity updateTestData(DataFileEntity newDataFile, FolderEntity parentFolder) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().updateTestData(newDataFile);
    }

    public List<DataFileEntity> getDataFileFromParentFolder(FolderEntity parentFolder) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().getDataFileByFolder(parentFolder);
    }

    public DataFileEntity copyDataFile(DataFileEntity dataFile, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().copyDataFile(dataFile, targetFolder);
    }

    public DataFileEntity moveDataFile(DataFileEntity dataFile, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().moveDataFile(dataFile, targetFolder);
    }

    public void deleteDataFile(DataFileEntity dataFile) throws Exception {
        getDataProviderSetting().getDataFileDataProvider().deleteDataFile(dataFile);
    }

    public DataFileEntity updateDataFile(DataFilePropertyInputEntity dataFileInputProperties) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().updateDataFileProperty(dataFileInputProperties);
    }

    public DataFileEntity updateDataFile(InternalDataFilePropertyEntity internalDataFileInputProperties)
            throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().updateInternalDataFileProperty(
                internalDataFileInputProperties);

    }

    public DataFileEntity renameDataFile(DataFileEntity dataFile, String newName) throws Exception {
        DataFilePropertyInputEntity dataFileInputProperties = new DataFilePropertyInputEntity(dataFile);
        dataFileInputProperties.setName(newName);
        return getDataProviderSetting().getDataFileDataProvider().updateDataFileProperty(dataFileInputProperties);
    }

    /**
     * Get entity ID for display This function is deprecated. Please use {@link DataFileEntity#getIdForDisplay()}
     * instead.
     * 
     * @param entity
     * @return Test Data ID for display
     * @throws Exception
     */
    @Deprecated
    public String getIdForDisplay(DataFileEntity entity) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider()
                .getIdForDisplay(entity)
                .replace(File.separator, GlobalStringConstants.ENTITY_ID_SEPARATOR);
    }

    public List<String> getSibblingDataFileNames(DataFileEntity dataFile) throws Exception {
        List<DataFileEntity> sibblingDataFiles = getDataFileFromParentFolder(dataFile.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (DataFileEntity sibblingDataFile : sibblingDataFiles) {
            if (!getDataProviderSetting().getEntityPk(sibblingDataFile).equals(
                    getDataProviderSetting().getEntityPk(dataFile))) {
                sibblingName.add(sibblingDataFile.getName());
            }
        }
        return sibblingName;
    }

    public String getAvailableTestDataName(FolderEntity parentFolder, String name) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().getAvailableDataFileName(parentFolder, name);
    }

    /**
     * validate name of new test date before saving to file
     * 
     * @param parentFolder
     * @param name
     * @return
     * @throws Exception
     */
    public boolean validateTestDataName(FolderEntity parentFolder, String name) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().validateDataFileName(parentFolder, name);
    }

    public DataFileEntity getTestDataByDisplayId(String displayId) throws Exception {
        String relativePathWithoutExtensions = displayId.replace(GlobalStringConstants.ENTITY_ID_SEPARATOR,
                File.separator);
        return getDataProviderSetting().getDataFileDataProvider().getDataFileByDisplayId(relativePathWithoutExtensions);
    }

    public DataFileEntity getTestData(String pk) throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().getDataFile(pk);
    }

    public String getTestDataDisplayIdByPk(String pk, String projectLocation) {
        return FilenameUtils.removeExtension(pk)
                .replace(projectLocation + File.separator, "")
                .replace(File.separator, GlobalStringConstants.ENTITY_ID_SEPARATOR);
    }

    public Map<String, List<TestSuiteTestCaseLink>> getTestDataReferences(DataFileEntity dataFileEntity)
            throws Exception {
        return getDataProviderSetting().getDataFileDataProvider().getTestDataReferences(dataFileEntity);
    }

    public void reloadTestData(DataFileEntity testData, Entity entity) throws Exception {
        entity = testData = getTestData(entity.getId());
    }

    public DatabaseConnection getDatabaseConnection(DataFileEntity testData) throws Exception {
        if (testData.getDriver() != DataFileDriverType.DBData) {
            throw new IllegalArgumentException(MessageFormat.format(StringConstants.CTRL_EXC_TEST_DATA_IS_NOT_DB_TYPE,
                    testData.getIdForDisplay()));
        }

        return DatabaseController.getInstance()
                .getDatabaseConnection(testData.isUsingGlobalDBSetting(), testData.isSecureUserAccount(),
                        testData.getUser(), testData.getPassword(), testData.getDataSourceUrl(), testData.getDriverClassName());
    }
    
	public TestData getTestDataInstance(String dataFileId, String projectLocation) throws Exception {
		DataFileEntity dataFile = getTestDataByDisplayId(dataFileId);
		if (dataFile.getDriver() == DataFileDriverType.DBData) {
			ClassLoader oldClassLoader = null;
			try {
				
				oldClassLoader = Thread.currentThread().getContextClassLoader();
				// fetch data and load into table
				URLClassLoader projectClassLoader = ProjectController.getInstance()
						.getProjectClassLoader(ProjectController.getInstance().getCurrentProject());
				Thread.currentThread().setContextClassLoader(projectClassLoader);
				
				DatabaseConnection dbConnection = TestDataController.getInstance().getDatabaseConnection(dataFile);
				
				if (dbConnection == null) {
					throw new Exception("DatabaseConnection is null");
				}
				
				dbConnection.getConnection();
				DBData dbData = new DBData(dbConnection, dataFile.getQuery());
				return dbData;
			} catch (Exception e) {
				return null;
			} finally {
				if (oldClassLoader != null) {
					Thread.currentThread().setContextClassLoader(oldClassLoader);
				}
			}
		} else {
			TestData testData = null;
			testData = TestDataFactory.findTestDataForExternalBundleCaller(dataFileId, projectLocation);
			return testData;
		}
	}
}
