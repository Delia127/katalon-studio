package com.kms.katalon.dal.fileservice;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings({ "rawtypes" })
public final class EntityService {

    private static EntityService singleton;

    private EntityCache cache;

    private Marshaller marshaller;

    private Unmarshaller unmarshaller;

    private JAXBContext jaxbContext;

    private static final String BIDING_FILES_LOCATION = "res/mapping";

    public static EntityService getInstance() throws Exception {
        if (singleton == null) {
            singleton = new EntityService();
        }
        return singleton;
    }

    private EntityService() throws Exception {
        this.cache = new EntityCache();
        initializeJAXB();
    }

    private void initializeJAXB() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        List<Object> bindingSources = new ArrayList<Object>();
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        JarFile jar = null;
        try {
            File jarFile = new File(path);
            if (jarFile.isFile()) {
                jar = new JarFile(jarFile);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();
                    if (name.startsWith(BIDING_FILES_LOCATION) && name.endsWith(".xml")) {
                        bindingSources.add(jar.getInputStream(jarEntry));
                    }
                }

            } else { // Run with IDE
                File mapping = new File(path, BIDING_FILES_LOCATION);
                for (File xmlFile : mapping.listFiles(EntityFileServiceManager.fileFilter)) {
                    bindingSources.add(xmlFile);
                }
            }
            System.setProperty("javax.xml.bind.context.factory", JAXBContextFactory.class.getName());
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, bindingSources);
            jaxbContext = JAXBContext.newInstance(getEntityClasses(), properties);
            marshaller = jaxbContext.createMarshaller();
            unmarshaller = jaxbContext.createUnmarshaller();
        } finally {
            if (jar != null) {
                jar.close();
            }
        }
    }

    private Class[] getEntityClasses() {
        return new Class[] { com.kms.katalon.entity.Entity.class, com.kms.katalon.entity.folder.FolderEntity.class,
                com.kms.katalon.entity.project.ProjectEntity.class,
                com.kms.katalon.entity.repository.WebElementEntity.class,
                com.kms.katalon.entity.repository.WebElementPropertyEntity.class,
                com.kms.katalon.entity.repository.WebElementSelectorMethod.class,
                com.kms.katalon.entity.repository.WindowsElementEntity.class,
                com.kms.katalon.entity.testcase.TestCaseEntity.class,
                com.kms.katalon.entity.testdata.DataFileEntity.class,
                com.kms.katalon.entity.testdata.InternalDataColumnEntity.class,
                com.kms.katalon.entity.testsuite.TestSuiteEntity.class,
                com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity.class,
                com.kms.katalon.entity.variable.VariableEntity.class,
                com.kms.katalon.entity.link.TestSuiteTestCaseLink.class, 
                com.kms.katalon.entity.link.VariableLink.class,
                com.kms.katalon.entity.repository.WebServiceRequestEntity.class,
                com.kms.katalon.entity.global.ExecutionProfileEntity.class,
                com.kms.katalon.entity.global.GlobalVariableEntity.class,
                com.kms.katalon.entity.integration.IntegratedEntity.class,
                com.kms.katalon.entity.file.IntegratedFileEntity.class,
                com.kms.katalon.entity.report.ReportEntity.class,
                com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.class,
                com.kms.katalon.dal.fileservice.adapter.TestSuiteReferenceXmlAdapter.class,
                com.kms.katalon.entity.report.ReportCollectionEntity.class,
                com.kms.katalon.entity.checkpoint.CheckpointEntity.class,
                com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo.class,
                com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo.class,
                com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo.class,
                com.kms.katalon.entity.checkpoint.CheckpointSourceInfo.class,
                com.kms.katalon.dal.fileservice.adapter.CheckpointDataXmlAdapter.class,
                com.kms.katalon.entity.project.SourceContent.class,
                com.kms.katalon.entity.variable.VariableEntityWrapper.class,
                com.kms.katalon.entity.project.SourceFolderConfiguration.class
                };
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public EntityCache getEntityCache() {
        return cache;
    }

    public FileEntity getEntityByPath(String path) throws Exception {
        // Find in cache first
        // If not found or out of date, load it using JAXB
        if (path.isEmpty()) {
            return null;
        }
        FileEntity entity = findEntityInCache(path);
        if (entity == null || isFileModified(path, entity)) {
            entity = loadEntityFromFile(path);
            if (entity != null) {
                getEntityCache().put(path, entity);
            }
        }
        return entity;
    }

    public FileEntity getEntityByPathWithoutCache(String path) throws Exception {
        if (path.isEmpty()) {
            return null;
        }
        return loadEntityFromFile(path);
    }

    public FileEntity findEntityInCache(String path) {
        return cache.get(path.trim());
    }

    public synchronized FileEntity loadEntityFromFile(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        FileEntity entity = (FileEntity) unmarshaller.unmarshal(file);
        setEntityTimeAttributes(path, entity);
        return entity;
    }

    // Tested on Windows only. TODO: test on other platforms.
    private void setEntityTimeAttributes(String path, FileEntity entity) throws IOException {
        Path filePath = Paths.get(path);
        BasicFileAttributeView basicView = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        BasicFileAttributes basicAttr = basicView.readAttributes();
        FileTime creationTime = basicAttr.creationTime();
        if (creationTime != null) {
            entity.setDateCreated(new Date(creationTime.toMillis()));
        }
        FileTime modifyTime = basicAttr.lastModifiedTime();
        if (modifyTime != null) {
            entity.setDateModified(new Date(modifyTime.toMillis()));
        }
    }

    public boolean saveEntity(FileEntity entity) throws Exception {
        saveEntityWithoutCache(entity);

        // Put entity into cache
        getEntityCache().put(entity.getLocation(), entity);

        setEntityTimeAttributes(entity.getLocation(), entity);
        return true;
    }

    public boolean saveEntity(FileEntity entity, String location) throws Exception {
        if (entity instanceof ProjectEntity) {
            saveProjectFolder((ProjectEntity) entity);
        }

        if (location.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(location.length(), FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }

        if (entity instanceof FolderEntity) {
            File folder = new File(location);
            if (!folder.exists()) {
                folder.mkdir();
            }
        } else {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Write to File
            marshaller.marshal(entity, new File(location));
        }
        return true;
    }

    public boolean saveIntergratedFolderMetadataEntity(IntegratedFileEntity entity) throws Exception {
        String metaDataFileLocation = entity.getLocation() + File.separator + FolderEntity.getMetaDataFileExtension();

        if (metaDataFileLocation.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(metaDataFileLocation.length(), FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }

        File folder = new File(entity.getLocation());
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folderMetaFile = new File(metaDataFileLocation);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // Write to File
        marshaller.marshal(entity, folderMetaFile);

        cache.put(entity.getLocation(), entity);
        return true;
    }

    public boolean saveFolderMetadataEntity(FileEntity entity) throws Exception {
        String metaDataFileLocation = entity.getLocation() + File.separator + FolderEntity.getMetaDataFileExtension();

        if (metaDataFileLocation.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(metaDataFileLocation.length(), FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }

        File folder = new File(entity.getLocation());
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folderMetaFile = new File(metaDataFileLocation);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // Write to File
        marshaller.marshal(entity, folderMetaFile);

        cache.put(entity.getLocation(), entity);
        return true;
    }

    public boolean saveEntityWithoutCache(FileEntity entity) throws Exception {
        return saveEntity(entity, entity.getLocation());
    }

    private boolean saveProjectFolder(ProjectEntity projectEntity) throws Exception {
        if (projectEntity.getFolderLocation().length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(projectEntity.getFolderLocation().length(),
                    FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }

        File folder = new File(projectEntity.getFolderLocation());
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return false;
    }

    public boolean deleteEntity(FileEntity entity) throws Exception {
        String path = entity.getLocation();
        return deleteEntity(path);
    }

    public boolean deleteEntity(String path) {
        File file = new File(path);
        if (FileUtils.deleteQuietly(file)) {
            // Update cache
            getEntityCache().remove(path);
        }
        // TODO: Also update references, and delete related files, Ex: test
        // suite, webelement has extension folder
        return true;
    }

    public String getAvailableName(String folderPk, String name, boolean isFile) {
        String newName = name;
        File parentFolder = new File(folderPk);
        List<String> fileNames = new ArrayList<String>();
        if (parentFolder.exists() && parentFolder.isDirectory()) {
            for (File file : parentFolder.listFiles(EntityFileServiceManager.fileFilter)) {
                if (file.isFile()) {
                    fileNames.add(FilenameUtils.removeExtension(file.getName()).toLowerCase());
                }
                if (!isFile && file.isDirectory()) {
                    fileNames.add(file.getName().toLowerCase());
                }
            }
        }
        if (fileNames.contains(newName.toLowerCase())) {
            for (int i = 1; fileNames.contains(newName.toLowerCase()); i++) {
                newName = name + " (" + i + ")";
            }
        }
        return newName;
    }

    public void validateName(String name) throws InvalidNameException {
        // null, blank and empty check
        if (StringUtils.isBlank(name)) {
            throw new InvalidNameException(StringConstants.FS_INVALID_FILE_NAME_BY_BLANK);
        }

        // invalid characters check
        Pattern pattern = Pattern.compile("^[\\w]+[\\w,\\s-().]*$", Pattern.UNICODE_CHARACTER_CLASS); // ver3
        // Pattern pattern = Pattern.compile("[\\w,\\s-().]+$"); // ver2
        // Pattern pattern = Pattern.compile("^[^/\\\\:*?\"'<>|]+$"); // ver1
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidNameException(StringConstants.FS_INVALID_FILE_NAME_BY_SPECIAL_CHAR);
        }

        // invalid dot check
        if (name.contains("..") || StringUtils.endsWith(name.trim(), ".")) {
            throw new InvalidNameException(StringConstants.FS_INVALID_FILE_NAME_BY_DOTS);
        }
    }

    private boolean isFileModified(String path, FileEntity entity) throws IOException {
        Path filePath = Paths.get(path);
        BasicFileAttributeView basicView = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        BasicFileAttributes basicAttr = basicView.readAttributes();
        FileTime modifyTime = basicAttr.lastModifiedTime();
        Date dateModifyTime = new Date(modifyTime.toMillis());
        if (modifyTime != null && entity.getDateModified() != null
                && dateModifyTime.compareTo(entity.getDateModified()) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public Unmarshaller newUnmarshaller() throws DALException {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new DALException(e);
        }
    }

    public Unmarshaller changeUnmarshaller(Unmarshaller unmashaller) {
        return this.unmarshaller = unmashaller;
    }

	public String toXmlString(Object entity) {
        try {
    		StringWriter sw = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.marshal(entity, sw);
	        return sw.toString();
	        
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return StringUtils.EMPTY;
	}


    public <T> T toEntity(String xmlString, Class<T> clazz) throws JAXBException {
	    T obj = clazz.cast(JAXBIntrospector.getValue(unmarshaller.unmarshal(new StringReader(xmlString))));
	    return obj;
	}
	
	

}
