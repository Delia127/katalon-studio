package com.kms.katalon.composer.objectrepository.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.SaveWebElementInfoEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class Util {

	public static final String SPY_EXEC_FILE_NAME = "ObjectSpyToolStandaloneApplication.exe";

	public static String getPhysicalLocation(String relativePath) {
		try {
			String currentPath = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			// Production mode
			if (currentPath.endsWith("jar")) {
				File file = new File(Util.class.getProtectionDomain().getCodeSource().getLocation().getPath());
				file = new File(file.getParentFile(), "..//" + relativePath);
				return file.getAbsolutePath();
			} else {
				File file = new File(currentPath + relativePath);
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isProcessRunning(String exeName) {
		try {
			if (exeName.length() > 25) {
				exeName = exeName.substring(0, 25);
			}
			Process process = Runtime.getRuntime().exec("tasklist", null, null);
			// process.waitFor();
			String output = readInputStream(process.getInputStream(), true);
			for (String line : output.split("\n")) {
				if (line != null && (line.trim().contains(exeName))) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String readInputStream(InputStream is, boolean multiline) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			if (multiline)
				sb.append("\n");
		}
		reader.close();
		is.close();
		return sb.toString();
	}

	public static String generateGuid() {
		return UUID.randomUUID().toString();
	}

	public static void importElements(ProjectEntity project, FolderEntity webElementRootFolder, String elementFileDir,
			String propertyFileDir, ObjectRepositoryController controller) throws Exception {
		
		Map<String, Entity> entitiesMap = new HashMap<String, Entity>();

		List<Object> qatWebElements = readFromCSVFile(elementFileDir, QatWebElement.class);
		List<Object> qatProperties = readFromCSVFile(propertyFileDir, QatWebElementProperty.class);
		List<Entity> entities = new ArrayList<Entity>();
		
		for(Object obj : qatWebElements){	
			QatWebElement qatWebElement = (QatWebElement)obj;
			String name = qatWebElement.getName();
			Entity entity = null;
			if(qatWebElement.isPage()){
				entity = new FolderEntity();
				if(name == null || name.trim().equals("")){
					name = "folder";
				}
			}
			else{
				entity = new WebElementEntity();
				((WebElementEntity)entity).setElementGuidId(qatWebElement.getId());
				if(name == null || name.trim().equals("")){
					name = "element";
				}
			}			
			
			entity.setId("");
			entity.setName(name);
			entity.setProject(project);
			
			entitiesMap.put(qatWebElement.getId(), entity);
			entities.add(entity);

			String defaultRootElementGuidId = WebElementEntity.defaultElementGUID;
			if (qatWebElement.getParentId().equals(defaultRootElementGuidId)) {
				entity.setParentFolder(webElementRootFolder);
				//The page should be default parent for other child
			} else {
				Entity parentWebElement = entitiesMap.get(qatWebElement.getParentId());
				if (parentWebElement == null) {
					entity.setParentFolder(webElementRootFolder);
				}
				else{
					if(parentWebElement instanceof FolderEntity){
						entity.setParentFolder((FolderEntity)parentWebElement);	
					}
					else if(parentWebElement instanceof WebElementEntity){
						entity.setParentFolder(((WebElementEntity)parentWebElement).getParentFolder());	
					}
				}
			}
			if(entity instanceof WebElementEntity){
				addWebElementProperties((WebElementEntity)entity, qatProperties);
			}
		}
		
		if (entities.size() > 0) {
			List<SaveWebElementInfoEntity> webElementInfoEntities = new ArrayList<SaveWebElementInfoEntity>();
			for(Entity entity : entities) {
				SaveWebElementInfoEntity webInfo = new SaveWebElementInfoEntity();
				if(entity instanceof WebElementEntity){
					setRefElementGuiD((WebElementEntity)entity, entitiesMap, controller);	
					webInfo.setWebElement((WebElementEntity)entity);
				}
				else if(entity instanceof FolderEntity){
					webInfo.setFolder((FolderEntity)entity);
				}
				webElementInfoEntities.add(webInfo);
			}
			controller.importWebElement(webElementInfoEntities);
		}
		
	}

	private static List<Object> readFromCSVFile(String csvFileName, Class<?> beanCleass) throws Exception {
		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(csvFileName), CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			// final CellProcessor[] processors = getProcessors();

			List<Object> returnValue = new ArrayList<Object>();
			Object obj;
			while ((obj = beanReader.read(beanCleass, header)) != null) {
				returnValue.add(obj);
			}
			return returnValue;

		} finally {
			if (beanReader != null) {
				beanReader.close();
			}
		}
	}

	private static void addWebElementProperties(WebElementEntity webElement, List<Object> qatProperties)
			throws Exception {

		Set<String> excluded = new HashSet<String>();

		for (Object obj : qatProperties) {
			QatWebElementProperty prop = (QatWebElementProperty) obj;
			if (prop.getElementId().equalsIgnoreCase(webElement.getElementGuidId())) {
				WebElementPropertyEntity propEntity = new WebElementPropertyEntity();
				propEntity.setName(prop.getName());
				propEntity.setType(prop.getType());
				String propValue = prop.getValue();
				if (propValue != null && propValue.length() > 1000) {
					propValue = propValue.substring(0, 1000);
				}
				propEntity.setValue(propValue);
				propEntity.setMatchCondition("is exactly".equals(prop.getMatchCondition()) ? "equals" : prop
						.getMatchCondition());
				propEntity.setIsSelected(prop.getIsSelected());
				String strProps = String.format("%s\t%s\t%s\t%s", prop.getName(), prop.getType(), prop.getValue(),
						prop.getMatchCondition());
				if (excluded.add(strProps)) {
					webElement.getWebElementProperties().add(propEntity);
				}
			}
		}

	}

	private static void setRefElementGuiD(WebElementEntity entity, Map<String, Entity> entitiesMap, ObjectRepositoryController controller) {
		List<WebElementPropertyEntity> props = entity.getWebElementProperties();
		for (WebElementPropertyEntity webElementPropertyEntity : props) {
			if (webElementPropertyEntity.getName().equalsIgnoreCase(WebElementEntity.ref_element)) {
				//For now, the referenced Entity is only IFrame
				Entity theFrame = entitiesMap.get(webElementPropertyEntity.getValue());
				if(theFrame != null && theFrame instanceof WebElementEntity){
					try{
						webElementPropertyEntity.setValue(controller.getIdForDisplay((WebElementEntity)theFrame).replace("\\", "/"));
					}catch (Exception e) {
						webElementPropertyEntity.setValue(((WebElementEntity)theFrame).getLocation());
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}
}
