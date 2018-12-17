package com.kms.katalon.composer.testcase.editors.extensions;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.execution.entity.TestDataExecutedEntity;

public class TestObjectsHyperlink implements IHyperlink {
	 private final IRegion fRegion;
	 private final String fArgument;
	 private final String fName;
	 private static final String WEBELEMENT_FILE_EXTENSION = ".rs";
	 private static final String TESTCASE_FILE_EXTENSION = ".tc";	
	 private static final String DATA_FILE_EXTENSION =".dat";
	
	 
	 public TestObjectsHyperlink(IRegion urlRegion, String functionName, String functionArgument) {
		 fRegion = urlRegion;
		 fName = functionName;
		 fArgument = functionArgument;
	 }

	 @Override
	 public IRegion getHyperlinkRegion() {
		 return fRegion;
	 }

	 @Override
	 public String getTypeLabel() {
		 return null;
	 }

	 @Override
	 public String getHyperlinkText() {
		 return null;
	 }

	 @SuppressWarnings("static-access")
	@Override
	 public void open() {
		 String location = fArgument.replace("'", "");
		 String objectID = StringUtils.EMPTY;
		 try{
			 if(fName.contains("TestObject")){
				 location = location.startsWith("Object Repository") ? location : "Object Repository/" + location;
				 objectID = ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator + location + WEBELEMENT_FILE_EXTENSION;
				 
				 WebElementEntity webElementEntity = ObjectRepositoryController.getInstance().getWebElement(objectID);
				 String event = (webElementEntity instanceof WebServiceRequestEntity)
			                ? EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN : EventConstants.TEST_OBJECT_OPEN;
			        
				 EventBrokerSingleton.getInstance().getEventBroker().post(event, webElementEntity);
			 }else if(fName.contains("TestCase")){
				 location = location.startsWith("Test Cases") ? location : "Test Cases/" + location;
				 objectID = ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator + location + TESTCASE_FILE_EXTENSION;
				 TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCase(objectID);
				 EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TESTCASE_OPEN, testCaseEntity);
			 }else if(fName.contains("TestData")){
				 location = location.startsWith("Data Files") ? location : "Data Files/" + location;
				 objectID = ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator + location + DATA_FILE_EXTENSION;
				 DataFileEntity testDataEntity = TestDataController.getInstance().getTestData(objectID);
				 EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TEST_DATA_OPEN, testDataEntity);
			 }
		 } catch(Exception e){
			 LoggerSingleton.getInstance().logError(e.getMessage());
		 }
	 }
}
