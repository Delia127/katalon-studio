package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.entity.repository.SaveWebElementInfoEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;

@Creatable
public class ObjectRepositoryController extends EntityController {
    private static EntityController _instance;

    private ObjectRepositoryController() {
        super();
    }

    public static ObjectRepositoryController getInstance() {
        if (_instance == null) {
            _instance = new ObjectRepositoryController();
        }
        return (ObjectRepositoryController) _instance;
    }

    /**
     * Create and save new Test Object
     * 
     * @param parentFolder
     * @param testObjectName Test Object name. Default name (New Element) will be used if this null or empty
     * @return {@link WebElementEntity}
     * @throws Exception
     */
    public WebElementEntity newTestObject(FolderEntity parentFolder, String testObjectName) throws ControllerException {
        try {
            return saveNewTestObject(newTestObjectWithoutSave(parentFolder, testObjectName));
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    /**
     * Create and save new Mobile Object
     * 
     * @param parentFolder
     * @param testObjectName Test Object name. Default name (New Element) will be used if this null or empty
     * @return {@link WebElementEntity}
     * @throws Exception
     */
    public MobileElementEntity newMobileElement(MobileElementEntity mobileElement) throws ControllerException {
        try {
            return (MobileElementEntity) getDataProviderSetting().getWebElementDataProvider().saveNewTestObject(mobileElement);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    /**
     * Create and save new Web Service Test Object
     * 
     * @param parentFolder
     * @param wsTestObjectName Web Service Test Object name. Default name (New Request) will be used if this null or
     * empty
     * @return {@link WebServiceRequestEntity}
     * @throws Exception
     */
    public WebServiceRequestEntity newWSTestObject(FolderEntity parentFolder, String wsTestObjectName) throws ControllerException {
    	try {
            return (WebServiceRequestEntity) saveNewTestObject(newWSTestObjectWithoutSave(parentFolder, wsTestObjectName));
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    /**
     * Create new Test Object without save
     * 
     * @param parentFolder
     * @param testObjectName Test Object name. Default name (New Element) will be used if this null or empty
     * @return {@link WebElementEntity}
     * @throws Exception
     */
    public WebElementEntity newTestObjectWithoutSave(FolderEntity parentFolder, String testObjectName) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        if (StringUtils.isBlank(testObjectName)) {
            testObjectName = StringConstants.CTRL_NEW_TEST_OBJECT;
        }

        WebElementEntity newWebElement = new WebElementEntity();
        newWebElement.setElementGuidId(Util.generateGuid());
        newWebElement.setName(getAvailableWebElementName(parentFolder, testObjectName));
        newWebElement.setParentFolder(parentFolder);
        newWebElement.setProject(parentFolder.getProject());

        return newWebElement;
    }
    
    /**
     * Create new Test Object without save
     * 
     * @param parentFolder
     * @param testObjectName Test Object name. Default name (New Element) will be used if this null or empty
     * @return {@link WebElementEntity}
     * @throws Exception
     */
    public DraftWebServiceRequestEntity newDraftWebServiceEntity(ProjectEntity project) {
        DraftWebServiceRequestEntity newWebElement = new DraftWebServiceRequestEntity();
        newWebElement.setProject(project);

        return newWebElement;
    }

    /**
     * Create new Web Service Test Object without save
     * 
     * @param parentFolder
     * @param wsTestObjectName Web Service Test Object name. Default name (New Request) will be used if this null or
     * empty
     * @return {@link WebServiceRequestEntity}
     * @throws Exception
     */
    public WebServiceRequestEntity newWSTestObjectWithoutSave(FolderEntity parentFolder, String wsTestObjectName)
            throws Exception {
        if (parentFolder == null) {
            return null;
        }       

        if (StringUtils.isBlank(wsTestObjectName)) {
            wsTestObjectName = StringConstants.CTRL_NEW_WS_REQUEST;
        }
        
        WebServiceRequestEntity newWS = new WebServiceRequestEntity();
        newWS.setElementGuidId(Util.generateGuid());
        newWS.setName(getAvailableWebElementName(parentFolder, wsTestObjectName));
        newWS.setParentFolder(parentFolder);
        newWS.setProject(parentFolder.getProject());

        return newWS;
    }
    

    /**
     * Save a NEW Test Object or Web Service entity.<br>
     * Please use {@link #saveWebElement(WebElementEntity)} if you want to save an existing one.
     * 
     * @param newTestObject new Test Object or Web Service Request
     * @return {@link WebElementEntity}
     * @throws Exception
     */
    public WebElementEntity saveNewTestObject(WebElementEntity newTestObject) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().saveNewTestObject(newTestObject);
    }

    public WebElementEntity getWebElement(String elementPk) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().getWebElement(elementPk);
    }

    public WebElementEntity getWebElementByDisplayPk(String elementDisplayPk) throws ControllerException {
        try {
            return getDataProviderSetting().getWebElementDataProvider().getWebElement(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator
                            + elementDisplayPk + WebElementEntity.getWebElementFileExtension());
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public void importWebElement(List<SaveWebElementInfoEntity> entities) throws Exception {
        getDataProviderSetting().getWebElementDataProvider().importWebElement(entities);
    }

    public FolderEntity importWebElementFolder(FolderEntity folder, FolderEntity parentFolder) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().importWebElementFolder(folder, parentFolder);
    }

    public WebElementEntity importWebElement(WebElementEntity webElement, FolderEntity parentFolder) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().importWebElement(webElement, parentFolder);
    }

    public WebElementEntity copyWebElement(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().copyWebElement(webElement, targetFolder);
    }

    public WebElementEntity moveWebElement(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().moveWebElement(webElement, targetFolder);
    }

    public void deleteWebElement(WebElementEntity webElement) throws Exception {
        getDataProviderSetting().getWebElementDataProvider().deleteWebElement(webElement);
    }

    public WebElementEntity updateTestObject(WebElementEntity webElement) throws ControllerException {
        try {
            return getDataProviderSetting().getWebElementDataProvider().updateTestObject(webElement);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public List<String> getSibblingWebElementNames(WebElementEntity webElement) throws Exception {
        List<WebElementEntity> sibblingWebElements = getDataProviderSetting().getWebElementDataProvider()
                .getChildWebElementsOfFolder(webElement.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (WebElementEntity sibblingWebElement : sibblingWebElements) {
            if (!getDataProviderSetting().getEntityPk(sibblingWebElement)
                    .equals(getDataProviderSetting().getEntityPk(webElement))) {
                sibblingName.add(sibblingWebElement.getName());
            }
        }
        return sibblingName;
    }

    public String getAvailableWebElementName(FolderEntity parentFolder, String name) throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().getAvailableWebElementName(parentFolder, name);
    }

    public List<WebElementEntity> getTestObjectReferences(WebElementEntity webElement, ProjectEntity projectEntity)
            throws Exception {
        return getDataProviderSetting().getWebElementDataProvider().getWebElementPropertyByRefElement(
                webElement.getIdForDisplay(), projectEntity, true);
    }

    public WebElementPropertyEntity getRefElementProperty(WebElementEntity webElement) {
        return getDataProviderSetting().getWebElementDataProvider().getRefElementProperty(webElement);
    }

    public void reloadTestObject(WebElementEntity testObject, Entity entity) throws Exception {
        entity = testObject = getWebElement(entity.getId());
    }

    public List<WebElementEntity> getAllDescendantWebElements(FolderEntity folder) throws Exception {
        if (folder.getFolderType() != FolderType.WEBELEMENT) {
            return Collections.emptyList();
        }

        List<WebElementEntity> childWebElements = new ArrayList<>();
        for (FileEntity child : FolderController.getInstance().getChildren(folder)) {
            if (child instanceof WebElementEntity) {
                childWebElements.add((WebElementEntity) child);
                continue;
            }
            
            if (child instanceof FolderEntity) {
                childWebElements.addAll(getAllDescendantWebElements((FolderEntity) child));
            }
        }

        return childWebElements;
    }

    public void updateDraftTestObject(WebServiceRequestEntity originalWsObject) {
        // TODO Auto-generated method stub
        
    }
}
