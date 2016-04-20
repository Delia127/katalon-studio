package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.dal.exception.NoEntityException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testdata.InternalDataColumnEntity;
import com.kms.katalon.entity.testdata.InternalDataFilePropertyEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.webservice.WsEntities;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DataFileFileServiceManager {

    /**
     * add a new child Data File to the specific folder and initialize its properties
     * 
     * @param parentFolderPk
     * @param projectPk
     * @return
     * @throws Exception
     */
    public static DataFileEntity addNewDataFile(FolderEntity parentFolder) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        DataFileEntity newDataFile = new DataFileEntity();
        newDataFile.setParentFolder(parentFolder);
        String name = EntityService.getInstance().getAvailableName(parentFolder.getLocation(),
                StringConstants.MNG_NEW_TEST_DATA, true);
        newDataFile.setName(name);
        newDataFile.setParentFolder(parentFolder);
        newDataFile.setDriver(DataFileDriverType.ExcelFile);
        newDataFile.setDataSourceUrl(DataFileEntity.DEFAULT_DATA_SOURCE_URL);
        newDataFile.setProject(parentFolder.getProject());
        newDataFile.setDataFileGUID(Util.generateGuid());

        EntityService.getInstance().saveEntity(newDataFile);
        FolderFileServiceManager.refreshFolder(parentFolder);
        return newDataFile;
    }

    /**
     * Returns a map of list of {@link TestSuiteTestCaseLink} that are references of the given data file.
     * <p>
     * Key of the map is id of test suite, Value is a list {@link TestSuiteTestCaseLink} in that test suite that each
     * one has at least one {@link TestCaseTestDataLink} that contains test data id of the given parameter.
     * 
     * @param dataFileEntity
     * @return a hash map of list of {@link TestSuiteTestCaseLink}
     * @throws Exception thrown if the given test data is null or system cannot get all test suites of the current
     * project.
     */
    public static Map<String, List<TestSuiteTestCaseLink>> getTestDataReferences(DataFileEntity dataFileEntity)
            throws Exception {
        if (dataFileEntity == null) {
            throw new NoEntityException("Test data not found.");
        }

        Map<String, List<TestSuiteTestCaseLink>> testDataReferences = new HashMap<String, List<TestSuiteTestCaseLink>>();

        List<TestSuiteEntity> allTestSuites = FolderFileServiceManager.getDescendantTestSuitesOfFolder(FolderFileServiceManager.getTestSuiteRoot(dataFileEntity.getProject()));

        String dataFileId = dataFileEntity.getRelativePathForUI().replace(File.separator,
                GlobalStringConstants.ENTITY_ID_SEPERATOR);

        for (TestSuiteEntity testSuiteEntity : allTestSuites) {
            List<TestSuiteTestCaseLink> testCaseLinkReferences = new ArrayList<TestSuiteTestCaseLink>();
            for (TestSuiteTestCaseLink testCaseLink : testSuiteEntity.getTestSuiteTestCaseLinks()) {

                for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
                    if (dataFileId.equals(testDataLink.getTestDataId())) {
                        testCaseLinkReferences.add(testCaseLink);
                        break;
                    }
                }
            }

            if (!testCaseLinkReferences.isEmpty()) {
                String testSuiteId = testSuiteEntity.getRelativePathForUI().replace(File.separator,
                        GlobalStringConstants.ENTITY_ID_SEPERATOR);
                testDataReferences.put(testSuiteId, testCaseLinkReferences);
            }
        }
        return testDataReferences;
    }

    /**
     * Deletes the given data file entity
     * 
     * @param dataFile
     * @throws Exception
     */
    public static void deleteDataFile(DataFileEntity dataFile) throws Exception {
        if (dataFile == null) {
            return;
        }

        EntityFileServiceManager.delete(dataFile);
        FolderFileServiceManager.refreshFolder(dataFile.getParentFolder());
    }

    /**
     * Delete recursively folder data file entity and all its children
     * 
     * @param folder
     * @throws Exception
     */
    public static void deteleDataFileFolder(FolderEntity folder) throws Exception {
        if (folder == null) {
            return;
        }

        EntityFileServiceManager.deleteFolder(folder);
        FolderFileServiceManager.refreshFolder(folder.getParentFolder());
    }

    /**
     * get a data file entity by its location (absolute path)
     * 
     * @param dataFilePk : the data file's location
     * @return
     * @throws Exception
     */
    public static DataFileEntity getDataFile(String dataFilePk) throws Exception {
        FileEntity entity = EntityFileServiceManager.get(new File(dataFilePk));
        if (entity instanceof DataFileEntity) {
            return (DataFileEntity) entity;
        }
        return null;
    }

    public static void initTestData(DataFileEntity dataFile) {
        if (dataFile != null) {
            if (dataFile.getDataFileGUID() == null) {
                dataFile.setDataFileGUID(Util.generateGuid());
            }
            if (dataFile.getEncriptData() != null) {
                List<List<Object>> arrays = new ArrayList<List<Object>>();
                for (List<Object> list : dataFile.getEncriptData()) {
                    if (list != null) {
                        List<Object> values = new ArrayList<Object>();
                        for (Object val : list) {
                            try {
                                String decodedValue = URLDecoder.decode((String) val, "UTF-8");
                                if (!decodedValue.equals("null")) {
                                    values.add(decodedValue);
                                } else {
                                    values.add("");
                                }
                            } catch (Exception ex) {
                                values.add(val);
                            }
                        }
                        arrays.add(values);
                    }
                }
                dataFile.setData(arrays);
            }
        }
    }

    /**
     * save a data file entity to it location (absolute path)
     * 
     * @param newDataFile
     * @param parentFolderPk
     * @return
     * @throws Exception
     */
    public static DataFileEntity saveDataFile(DataFileEntity newDataFile) throws Exception {

        validateDataFile(newDataFile);
        // If renamed the name, clean up the old one on cache before saving the
        // new one
        if (EntityService.getInstance().getEntityCache().contains(newDataFile)) {
            String oldTestDataLocation = EntityService.getInstance().getEntityCache().getKey(newDataFile);
            if (oldTestDataLocation != null && !oldTestDataLocation.equals(newDataFile.getLocation())) {
                EntityService.getInstance().getEntityCache().remove(newDataFile, true);

                // update test data's references
                updateReferencesAfterDataFileRenamed(oldTestDataLocation, newDataFile, newDataFile.getProject());
            }
        }

        EntityService.getInstance().saveEntity(newDataFile);

        FolderFileServiceManager.refreshFolder(newDataFile.getParentFolder());

        return newDataFile;
    }

    public static void validateDataFile(DataFileEntity dataFile) throws Exception {
        EntityService.getInstance().validateName(dataFile.getName());
    }

    public static DataFileEntity copyDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception {
        return EntityFileServiceManager.copy(dataFile, destinationFolder);
    }

    /**
     * copy a folder of data files to another folder
     * 
     * @param sourceFolder
     * @param destinationFolder
     * @return
     * @throws Exception
     */
    public static FolderEntity copyDataFileFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        return EntityFileServiceManager.copyFolder(folder, destinationFolder);
    }

    public static DataFileEntity moveDataFile(DataFileEntity dataFile, FolderEntity destinationFolder) throws Exception {
        return EntityFileServiceManager.move(dataFile, destinationFolder);
    }

    public static List<DataFileEntity> getDataFileByDataFileEntity(String dataFileEntityPk) throws Exception {
        DataFileEntity entity = getDataFile(dataFileEntityPk);
        return new ArrayList<DataFileEntity>(Arrays.asList(entity));
    }

    /**
     * get available name for a new name in a folder of data file
     * 
     * @param parentFolderPk
     * @param name
     * @return
     * @throws Exception
     */
    public static String getAvailableDataFileName(FolderEntity parentFolder, String name) throws Exception {
        if (parentFolder != null) {
            return EntityService.getInstance().getAvailableName(parentFolder.getLocation(), name, true);
        }
        return null;
    }

    /**
     * update properties of a data file entity and its referrer
     * 
     * @param dataFilePropertyInput
     * @param project
     * @return
     * @throws Exception
     */
    public static DataFileEntity updateDataFileProperty(DataFilePropertyInputEntity dataFilePropertyInput,
            ProjectEntity project) throws Exception {

        DataFileEntity testDataEntity = getDataFile(dataFilePropertyInput.getPk());

        String oldLocation = testDataEntity.getLocation();

        EntityService.getInstance().validateName(dataFilePropertyInput.getName());

        // name changed
        if (!testDataEntity.getName().equals(dataFilePropertyInput.getName())) {
            if (!testDataEntity.getName().equalsIgnoreCase(dataFilePropertyInput.getName())) {
                String availableName = getAvailableDataFileName(testDataEntity.getParentFolder(),
                        dataFilePropertyInput.getName());
                if (!availableName.equalsIgnoreCase(dataFilePropertyInput.getName())) {
                    throw new DuplicatedFileNameException(MessageFormat.format(
                            StringConstants.MNG_EXC_EXISTED_DATA_FILE_NAME, dataFilePropertyInput.getName()));
                }
            }

            EntityService.getInstance().getEntityCache().remove(testDataEntity, true);
        }

        testDataEntity.setName(dataFilePropertyInput.getName());
        testDataEntity.setDescription(dataFilePropertyInput.getDescription());
        testDataEntity.setDriver(DataFileDriverType.fromValue(dataFilePropertyInput.getDataFileDriver()));

        testDataEntity.setDataSourceUrl(dataFilePropertyInput.getDataSourceURL());
        testDataEntity.setSheetName(dataFilePropertyInput.getSheetName());
        testDataEntity.setIsInternalPath(dataFilePropertyInput.getIsInternalPath());
        testDataEntity.setContainsHeaders(dataFilePropertyInput.isEnableHeaders());
        testDataEntity.setCsvSeperator(dataFilePropertyInput.getCsvSeperator());

        updateReferencesAfterDataFileRenamed(oldLocation, testDataEntity, project);

        EntityService.getInstance().saveEntity(testDataEntity);

        FolderFileServiceManager.refreshFolder(testDataEntity.getParentFolder());
        return testDataEntity;
    }

    /**
     * update all test cases, test suites and job definition that each one refers to the given data file after it
     * renamed
     * 
     * @param oldEntity
     * @param newEntity
     * @param project
     * @throws Exception
     */
    private static void updateReferencesAfterDataFileRenamed(String oldTestDataLocation, DataFileEntity newEntity,
            ProjectEntity project) throws Exception {
        if (!oldTestDataLocation.equals(newEntity.getLocation())) {

            // TODO need to update reference between test suite and the given
            // data file
            // update reference Location in testCases, testSuites and JobDef
            // that refer to dataFile Entity
            FolderEntity testSuiteRoot = FolderFileServiceManager.getTestSuiteRoot(project);
            List<TestSuiteEntity> lstTestSuite = FolderFileServiceManager.getDescendantTestSuitesOfFolder(testSuiteRoot);
            File projectFile = new File(project.getLocation());

            String oldRelativeTdLocation = oldTestDataLocation.substring(projectFile.getParent().length() + 1);
            String oldTestDataId = FilenameUtils.removeExtension(oldRelativeTdLocation).replace(File.separator, "/");

            String newTestDataId = newEntity.getIdForDisplay();
            for (TestSuiteEntity testSuiteEntity : lstTestSuite) {
                boolean save = false;
                for (TestSuiteTestCaseLink testCaseLink : testSuiteEntity.getTestSuiteTestCaseLinks()) {
                    for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
                        String testDataId = testDataLink.getTestDataId();
                        if (testDataId == null || !oldTestDataId.equals(testDataId))
                            continue;

                        testDataLink.setTestDataId(newTestDataId);
                        save = true;
                    }
                }

                if (save) {
                    TestSuiteFileServiceManager.updateTestSuite(testSuiteEntity);
                }
            }

            GroovyRefreshUtil.updateStringScriptReferences(oldTestDataId, newTestDataId, project);
        }
    }

    /**
     * get all internal data values of a data file
     * 
     * @param dataFile
     * @param columnName
     * @return
     * @throws Exception
     */
    public static WsEntities getAllInternalDataValue(DataFileEntity dataFile, String columnName) throws Exception {
        // List<InternalDataColumnEntity> lstInternalDatas = new
        // ArrayList<InternalDataColumnEntity>();
        List<Object> valuesList = new ArrayList<Object>();
        for (InternalDataColumnEntity entity : dataFile.getInternalDataColumns()) {
            if (entity.getName().equals(columnName)) {
                int colIndex = dataFile.getInternalDataColumns().indexOf(entity);
                valuesList = dataFile.getData().get(colIndex);
            }
        }
        WsEntities wsEntity = new WsEntities();
        wsEntity.setInternalDataValues(valuesList);
        return wsEntity;
    }

    /**
     * get number internal data rows by name of a data file
     * 
     * @param dataFile
     * @param columnName
     * @return
     * @throws Exception
     */
    public static Integer getInternalDataRowCount(DataFileEntity dataFile, String columnName) throws Exception {
        List<InternalDataColumnEntity> lstInternalColumns = dataFile.getInternalDataColumns();
        for (int i = 0; i < lstInternalColumns.size(); i++) {
            if (lstInternalColumns.get(i).getName().equals(columnName)) {
                return dataFile.getData().get(i).size();
            }
        }
        return 0;
    }

    /**
     * update a data file entity and its referrer if it is internal data
     * 
     * @param internalData
     * @param project
     * @return
     * @throws Exception
     */
    public static DataFileEntity updateInternalDataFileProperty(InternalDataFilePropertyEntity internalData,
            ProjectEntity project) throws Exception {
        DataFileEntity testDataEntity = getDataFile(internalData.getPk());
        String oldLocation = testDataEntity.getLocation();

        EntityService.getInstance().validateName(internalData.getName());

        EntityService.getInstance().getEntityCache().remove(testDataEntity, true);

        testDataEntity.setName(internalData.getName());
        testDataEntity.setDescription(internalData.getDescription());
        testDataEntity.setDriver(DataFileDriverType.valueOf(internalData.getDataFileDriverName()));

        testDataEntity.setDataSourceUrl(internalData.getDataSourceURL());
        testDataEntity.setSheetName(internalData.getSheetName());

        if (testDataEntity.getDriver() == DataFileDriverType.InternalData) {
            List<InternalDataColumnEntity> lstInternalDataColumnEntities = new ArrayList<InternalDataColumnEntity>();
            for (Object o : internalData.getHeaderColumn()) {
                lstInternalDataColumnEntities.add((InternalDataColumnEntity) o);
            }
            testDataEntity.setInternalDataColumns(lstInternalDataColumnEntities);
            testDataEntity.setData(internalData.getData());
            // Encrypt data before save to avoid white spaces
            if (testDataEntity.getData() != null) {
                List<List<Object>> arrays = new ArrayList<List<Object>>();
                for (List<Object> list : testDataEntity.getData()) {
                    if (list != null) {
                        List<Object> values = new ArrayList<Object>();
                        for (Object val : list) {
                            try {
                                if (!val.equals("")) {
                                    values.add(URLEncoder.encode((String) val, "UTF-8"));
                                } else {
                                    values.add("null");
                                }
                            } catch (Exception ex) {
                                if (val == null) {
                                    values.add("null");
                                } else {
                                    values.add(val);
                                }
                            }
                        }
                        arrays.add(values);
                    }
                }
                testDataEntity.setEncriptData(arrays);
            }

        }

        if (!oldLocation.equals(testDataEntity.getLocation())) {
            updateReferencesAfterDataFileRenamed(oldLocation, testDataEntity, project);
        }

        EntityService.getInstance().saveEntity(testDataEntity);
        FolderFileServiceManager.refreshFolder(testDataEntity.getParentFolder());

        return testDataEntity;
    }

    /**
     * get a data file entity that has name same with specific name in same folder
     * 
     * @param folderLocation
     * @param dataFileName
     * @return
     * @throws Exception
     */
    public static DataFileEntity getDuplicatedDataFile(FolderEntity parentFolder, String dataFileName) throws Exception {
        List<DataFileEntity> dataFiles = FolderFileServiceManager.getChildDataFilesOfFolder(parentFolder);
        for (DataFileEntity dataFile : dataFiles) {
            if (dataFile.getName().equals(dataFileName)) {
                return dataFile;
            }
        }
        return null;
    }

    /**
     * get a data file entity by its GUID and project
     * 
     * @param guid
     * @param project
     * @return
     * @throws Exception
     */
    public static DataFileEntity getByGUID(String guid, ProjectEntity project) throws Exception {
        File projectFolder = new File(project.getFolderLocation());
        if (projectFolder.exists() && projectFolder.isDirectory()) {
            File dataFileFolder = new File(
                    FileServiceConstant.getDataFileRootFolderLocation(projectFolder.getAbsolutePath()));
            if (dataFileFolder.exists() && dataFileFolder.isDirectory()) {
                return getByGUID(dataFileFolder.getAbsolutePath(), guid);
            }
        }
        return null;
    }

    /**
     * get a data file entity in the specific folder by a GUID
     * 
     * @param dataFileFolder
     * @param guid
     * @return
     * @throws Exception
     */
    private static DataFileEntity getByGUID(String dataFileFolder, String guid) throws Exception {
        File folder = new File(dataFileFolder);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
                if (file.isFile()
                        && file.getName()
                                .toLowerCase()
                                .endsWith(DataFileEntity.getTestDataFileExtension().toLowerCase())) {
                    DataFileEntity dataFile = getDataFile(file.getAbsolutePath());
                    if (dataFile.getDataFileGUID().equals(guid)) {
                        return dataFile;
                    }
                } else if (file.isDirectory()) {
                    DataFileEntity result = getByGUID(file.getAbsolutePath(), guid);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * rename a folder of data file and update all references of its children data files after it renamed
     * 
     * @param newFolderName
     * @param folderEntity
     * @param project
     * @throws Exception
     */
    public static void saveAllReferencesToDataFileAfterFolderRenamed(String oldFolderLocation,
            FolderEntity folderEntity, List<TestSuiteEntity> lstTestSuites) throws Exception {

        String oldFolderDisplayId = oldFolderLocation.replace(File.separator, "/") + "/";
        String newFolderDisplayId = folderEntity.getIdForDisplay() + "/";
        Set<TestSuiteEntity> lstTestSuitesWillSave = new HashSet<>();

        // update references in test suite.
        for (TestSuiteEntity testSuiteEntity : lstTestSuites) {
            for (TestSuiteTestCaseLink testCaseLink : testSuiteEntity.getTestSuiteTestCaseLinks()) {
                for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
                    String testDataId = testDataLink.getTestDataId();
                    if (testDataId == null || !(testDataId.contains(oldFolderDisplayId)))
                        continue;

                    testDataLink.setTestDataId(testDataId.replace(oldFolderDisplayId, newFolderDisplayId));
                    lstTestSuitesWillSave.add(testSuiteEntity);
                }
            }
        }

        for (TestSuiteEntity testSuiteEntity : lstTestSuitesWillSave) {
            TestSuiteFileServiceManager.updateTestSuite(testSuiteEntity);
        }

        // update references in test case's script and keywords.
        FolderFileServiceManager.refreshFolderScriptReferences(oldFolderLocation, folderEntity);
    }
}
