package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;

public class EntityFileServiceManager {

    private static final String[] EXCLUDED_FOLDER = new String[] { ".svn", ".meta", ".DS_Store", ".git" };

    public static final FileFilter fileFilter = new FileFilter() {
        List<String> list = Arrays.asList(EXCLUDED_FOLDER);

        @Override
        public boolean accept(File file) {
            if (file != null) {
                return !list.contains(file.getName());
            }
            return false;
        }
    };

    public static FileEntity get(File localFile) throws Exception {
        if (localFile != null && localFile.exists()) {
            FileEntity entity = null;
            if (localFile.isDirectory()) {
                if (EntityService.getInstance().findEntityInCache(localFile.getAbsolutePath()) != null) {
                    entity = EntityService.getInstance().findEntityInCache(localFile.getAbsolutePath());
                    setParentFolder(entity, localFile);
                } else {
                    entity = loadFolder(localFile);
                    setParentFolder(entity, localFile);
                    EntityService.getInstance().getEntityCache().put(entity.getLocation(), entity);
                }
            } else if (localFile.isFile()) {
                entity = EntityService.getInstance().getEntityByPath(localFile.getAbsolutePath());
                setParentFolder(entity, localFile);
                init(entity);
            }
            return entity;
        }
        return null;
    }
    
    public static FolderEntity getFolder(File localFile) throws Exception {
        if (localFile == null || !localFile.exists()) {
            return null;
        }
        FolderEntity entity = null;
        if (EntityService.getInstance().findEntityInCache(localFile.getAbsolutePath()) != null) {
            entity = (FolderEntity) EntityService.getInstance().findEntityInCache(localFile.getAbsolutePath());
        } else {
            entity = new FolderEntity();
            entity.setName(localFile.getName());
        }
        setParentFolder(entity, localFile);
        EntityService.getInstance().getEntityCache().put(entity.getLocation(), entity);
        return entity;
    }

    private static FileEntity loadFolder(File folderFile) throws Exception {
        if (folderFile == null || !folderFile.exists() || !folderFile.isDirectory()) return null;

        File folderMetaDataFile = new File(folderFile, FolderEntity.getMetaDataFileExtension());
        FileEntity folderEntity = null;
        if (folderMetaDataFile.exists()) {
            folderEntity = EntityService.getInstance().getEntityByPath(
                    folderMetaDataFile.getAbsolutePath());
            if (folderEntity instanceof FolderEntity) {
                return folderEntity;
            }
        } 
            
        folderEntity = new FolderEntity();
        folderEntity.setName(folderFile.getName());
        
        return folderEntity;
    }

    private static void setParentFolder(FileEntity entity, File localFile) throws Exception {
        if (entity != null && entity.getParentFolder() == null && localFile != null && localFile.exists()) {
            String parentFolderLocation = localFile.getParent();
            FolderEntity parentFolder = (FolderEntity) EntityService.getInstance().findEntityInCache(
                    parentFolderLocation);
            if (parentFolder != null) {
                entity.setParentFolder(parentFolder);
                entity.setProject(parentFolder.getProject());
                if (entity instanceof FolderEntity) {
                    ((FolderEntity) entity).setFolderType(parentFolder.getFolderType());
                }
            } else {
                ProjectEntity currentProject = DataProviderState.getInstance().getCurrentProject();
                if (currentProject == null) return;
                String projectFolderLocation = currentProject.getFolderLocation();

                if (!parentFolderLocation.toLowerCase().equalsIgnoreCase(projectFolderLocation.toLowerCase()) &&
                    !parentFolderLocation.toLowerCase().startsWith(projectFolderLocation.toLowerCase() + File.separator)) return;

                if (projectFolderLocation.equalsIgnoreCase(parentFolderLocation) && entity instanceof FolderEntity) {
                    String fileName = localFile.getName();
                    FolderEntity folderEntity = (FolderEntity) entity;
                    if (FileServiceConstant.TEST_CASE_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.TESTCASE);
                    } else if (FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.TESTSUITE);
                    } else if (FileServiceConstant.OBJECT_REPOSITORY_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.WEBELEMENT);
                    } else if (FileServiceConstant.DATA_FILE_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.DATAFILE);
                    } else if (FileServiceConstant.CHECKPOINT_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.CHECKPOINT);
                    } else if (FileServiceConstant.KEYWORD_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.KEYWORD);
                    } else if (FileServiceConstant.REPORT_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.REPORT);
                    }  else if (FileServiceConstant.INCLUDE_SCRIPT_ROOT_FOLDER_NAME.equals(fileName)) {
                        folderEntity.setFolderType(FolderType.INCLUDE);
                    }
                    folderEntity.setProject(currentProject);
                } else {
                    parentFolder = (FolderEntity) loadFolder(localFile.getParentFile());
                    parentFolder.setProject(currentProject);

                    setParentFolder(parentFolder, localFile.getParentFile());
                    if (entity instanceof FolderEntity) {
                        ((FolderEntity) entity).setFolderType(parentFolder.getFolderType());
                    }

                    entity.setParentFolder(parentFolder);
                    entity.setProject(parentFolder.getProject());
                    EntityService.getInstance().getEntityCache().put(parentFolder.getLocation(), parentFolder);
                }

            }
        }
    }

    /**
     * After being read from JAXB, some entity need to be standardized its data.
     * 
     * @param entity
     * @throws Exception
     */
    private static void init(FileEntity entity) throws Exception {
        if (entity instanceof TestSuiteEntity) {
            TestSuiteFileServiceManager.initTestSuite((TestSuiteEntity) entity);
        } else if (entity instanceof DataFileEntity) {
            DataFileFileServiceManager.initTestData((DataFileEntity) entity);
        } else if (entity instanceof TestCaseEntity) {
            TestCaseFileServiceManager.initTestCase((TestCaseEntity) entity);
        }
    }

    public static <T extends FileEntity> List<T> getChildren(FolderEntity parentFolder, Class<T> clazz)
            throws Exception {
        if (parentFolder != null) {
            List<T> childrenEntities = new ArrayList<T>();
            File localFolder = new File(parentFolder.getLocation());
            if (localFolder.exists() && localFolder.isDirectory()) {
                for (File localFile : localFolder.listFiles(fileFilter)) {
                    try {
                        FileEntity fileEntity = EntityFileServiceManager.get(localFile);
                        if (fileEntity != null && clazz.isInstance(fileEntity)) {
                            childrenEntities.add(clazz.cast(fileEntity));
                        }
                    } catch (Exception e) {
                        // ignore this file
                    }
                }
            }
            return childrenEntities;
        }
        return Collections.emptyList();
    }
    
    
    
    
    public static List<File> getFileChildren(FolderEntity parentFolder) throws Exception {
        if (parentFolder != null) {
            List<File> childrenEntities = new ArrayList<File>();
            File localFolder = new File(parentFolder.getLocation());
            if (localFolder.exists() && localFolder.isDirectory()) {
                for (File localFile : localFolder.listFiles(fileFilter)) {
                    try {
                        childrenEntities.add(localFile);
                    } catch (Exception e) {
                        // ignore this file
                    }
                }
            }
            return childrenEntities;
        }
        return Collections.emptyList();
    }

    public static <T extends FileEntity> List<T> getDescendants(FolderEntity parentFolder, Class<T> clazz)
            throws Exception {
        if (parentFolder == null) {
            throw new IllegalArgumentException("Folder cannot be null");
        }
        
        List<T> childrenEntities = new ArrayList<T>();
        File localFolder = new File(parentFolder.getLocation());
        if (localFolder.exists() && localFolder.isDirectory()) {
            for (File localFile : localFolder.listFiles(fileFilter)) {
                FileEntity fileEntity = null;
                try {
                    fileEntity = EntityFileServiceManager.get(localFile);
                } catch (UnmarshalException ex) {
                    //Ignore trashed files
                    continue;
                }
                
                if (fileEntity != null && clazz.isInstance(fileEntity)) {
                    childrenEntities.add(clazz.cast(fileEntity));
                }
                if (fileEntity instanceof FolderEntity) {
                    childrenEntities.addAll(getDescendants((FolderEntity) fileEntity, clazz));
                }
            }
        }
        return childrenEntities;
    }

    public static void delete(FileEntity entity) throws Exception {
        if (entity != null) {
            EntityService.getInstance().deleteEntity(entity);
        }
    }

    public static void deleteFolder(FolderEntity folder) throws Exception {
        if (folder != null) {
            List<FileEntity> childEntities = getChildren(folder, FileEntity.class);
            for (FileEntity childEntity : childEntities) {
                if (childEntity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) childEntity);
                } else {
                    delete(childEntity);
                }
            }
            EntityService.getInstance().deleteEntity(folder);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends FileEntity> T copy(T entity, FolderEntity destinationFolder) throws Exception {
        if (entity != null && destinationFolder != null) {
            T clonedEntity = (T) entity.clone();
            File fEntity = new File(destinationFolder.getLocation() + File.separator + entity.getName() + entity.getFileExtension());
            if (fEntity.exists()) {
                // if entity existed, put a prefix "- Copy" into its name
                String name = entity.getName() + Util.STRING_COPY_OF_NAME;
                name = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(), name, true);
                clonedEntity.setName(name);
            }
            clonedEntity.setParentFolder(destinationFolder);
            clonedEntity.setProject(destinationFolder.getProject());
            if (clonedEntity instanceof IntegratedFileEntity) {
                IntegratedFileEntity integratedEntity = (IntegratedFileEntity) clonedEntity;
                integratedEntity.getIntegratedEntities().clear();
            }
            EntityService.getInstance().saveEntity(clonedEntity);
            return clonedEntity;
        }
        return null;
    }

    public static FolderEntity copyFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        if (folder != null && destinationFolder != null) {
            FolderEntity clonedFolder = folder.clone();
            File fFolder = new File(destinationFolder.getLocation() + File.separator + folder.getName());
            if (fFolder.exists()) {
                // if folder existed, put a prefix "- Copy" into its name
                String name = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(),
                        folder.getName() + Util.STRING_COPY_OF_NAME, false);
                clonedFolder.setName(name);
            }
            clonedFolder.setParentFolder(destinationFolder);
            clonedFolder.setProject(destinationFolder.getProject());
            clonedFolder.getIntegratedEntities().clear();
            EntityService.getInstance().saveEntity(clonedFolder);

            for (FileEntity entity : FolderFileServiceManager.getChildren(folder)) {
                if (entity instanceof FolderEntity) {
                    copyFolder((FolderEntity) entity, clonedFolder);
                } else {
                    copy(entity, clonedFolder);
                }
            }
            return clonedFolder;
        }
        return null;
    }
    
    public static FolderEntity copyKeywordFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        if (folder != null && destinationFolder != null) {
            FolderEntity clonedFolder = folder.clone();
            File fFolder = new File(destinationFolder.getLocation() + File.separator + folder.getName());
            if (fFolder.exists()) {
                // if folder existed, put a prefix "- Copy" into its name
                String name = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(),
                        folder.getName() + Util.STRING_DUPLICATE_OF_PACKAGE_NAME, false);
                clonedFolder.setName(name);
            }
            clonedFolder.setParentFolder(destinationFolder);
            clonedFolder.setProject(destinationFolder.getProject());
            clonedFolder.getIntegratedEntities().clear();
            EntityService.getInstance().saveEntity(clonedFolder);

            for (File entity : FolderFileServiceManager.getFileChildren(folder)) {
                if (entity.isDirectory()) {
                    FolderEntity tmpFolder = FolderFileServiceManager.getFolder(entity.getAbsolutePath());
                    if (tmpFolder.getLocation() == null) {
                        tmpFolder.setName(entity.getAbsolutePath());
                    }
                    copyKeywordFolder(tmpFolder, clonedFolder);
                } else {
                    Files.copy(Paths.get(entity.getPath()), Paths.get(clonedFolder.getLocation()+ File.separator + entity.getName()));
                }
            }
            return clonedFolder;
        }
        return null;
    }
    
    public static <T extends FileEntity> T move(T entity, FolderEntity destinationFolder) throws Exception {
        if (entity != null && destinationFolder != null) {
            String oldName = entity.getName();
            String newName = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(), oldName,
                    true);
            if (!oldName.equals(newName)) {
                EntityService.getInstance().deleteEntity(entity);
                rename(entity, newName);
            }
            File sourceFile = new File(entity.getLocation());
            File destinationFile = new File(destinationFolder.getLocation());
            if (sourceFile.exists() && destinationFile.exists() && destinationFile.isDirectory()) {
                EntityService.getInstance().getEntityCache().remove(entity, false);
                FileUtils.moveFileToDirectory(sourceFile, destinationFile, false);
            }
            entity.setParentFolder(destinationFolder);
            entity.setProject(destinationFolder.getProject());
            entity.setName(newName);

            if (entity instanceof IntegratedFileEntity) {
                IntegratedFileEntity integratedEntity = (IntegratedFileEntity) entity;
                integratedEntity.getIntegratedEntities().clear();
            }

            EntityService.getInstance().saveEntity(entity);
            return entity;
        }
        return null;
    }

    public static FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        if (folder != null && destinationFolder != null) {
            // List<FileEntity> childEntities = getChildren(folder,
            // FileEntity.class);
            String newName = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(),
                    folder.getName(), false);
            FolderEntity newFolder = new FolderEntity();
            newFolder.setParentFolder(destinationFolder);
            newFolder.setProject(destinationFolder.getProject());
            newFolder.setName(newName);
            newFolder.setFolderType(destinationFolder.getFolderType());

            newFolder.getIntegratedEntities().clear();

            EntityService.getInstance().saveEntity(newFolder);

            // File sourceFile = new File(folder.getLocation());
            // File destinationFile = new File(destinationFolder.getLocation());
            // if (sourceFile.exists() && destinationFile.exists() &&
            // destinationFile.isDirectory()) {
            // EntityService.getInstance().getEntityCache().remove(folder,
            // false);
            // FileUtils.moveDirectoryToDirectory(sourceFile, destinationFile,
            // false);
            // }
            // folder.setParentFolder(destinationFolder);
            // folder.setProject(destinationFolder.getProject());
            // folder.setName(newName);
            // EntityService.getInstance().saveEntity(folder);

            for (FileEntity childEntity : getChildren(folder, FileEntity.class)) {
                if (childEntity instanceof TestCaseEntity) {
                    TestCaseFileServiceManager.moveTestCase((TestCaseEntity) childEntity, newFolder);
                    continue;
                }

                if (childEntity instanceof TestSuiteEntity) {
                    TestSuiteFileServiceManager.moveTestSuite((TestSuiteEntity) childEntity, newFolder);
                    continue;
                }

                if (childEntity instanceof DataFileEntity) {
                    DataFileFileServiceManager.moveDataFile((DataFileEntity) childEntity, newFolder);
                }

                if (childEntity instanceof WebElementEntity) {
                    WebElementFileServiceManager.moveWebElement((WebElementEntity) childEntity, newFolder);
                    continue;
                }

                if (childEntity instanceof TestSuiteCollectionEntity) {
                    new FileServiceDataProviderSetting().getTestSuiteCollectionDataProvider().move(childEntity.getId(), newFolder);
                    continue;
                }

                if (childEntity instanceof CheckpointEntity) {
                    CheckpointFileServiceManager.move((CheckpointEntity) childEntity, newFolder);
                    continue;
                }

                if (childEntity instanceof FolderEntity) {
                    moveFolder((FolderEntity) childEntity, newFolder);
                }
            }
            EntityService.getInstance().getEntityCache().remove(folder, false);
            File folderFile = new File(folder.getLocation());
            if (folderFile.exists() && folderFile.isDirectory()) {
                FileUtils.deleteDirectory(new File(folder.getLocation()));
            }
            return newFolder;
        }
        return null;
    }

    public static void rename(FileEntity entity, String newName) throws Exception {
        File file = new File(entity.getLocation());
        if (file.exists()) {
            EntityService.getInstance().getEntityCache().remove(entity, false);
            file.renameTo(new File(newName));
            entity.setName(newName);
            EntityService.getInstance().saveEntity(entity);
        }
    }

    public static boolean update(FileEntity entity) throws Exception {
        if (entity == null) {
            return false;
        }

        File file = new File(entity.getLocation());
        if (!file.exists()) {
            return false;
        }

        return EntityService.getInstance().saveEntity(entity);
    }

	public static String toXmlString(Object entity) throws Exception {
		return EntityService.getInstance().toXmlString(entity);
	}

	public static <T> T toEntity(String xmlString, Class<T> clazz) throws Exception{
		return EntityService.getInstance().toEntity(xmlString, clazz);
	}

}
