package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.SaveWebElementInfoEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

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

    public WebElementEntity addNewWebElement(FolderEntity parentFolder, String elementName) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().addNewWebElement(parentFolder, elementName);
    }

    public WebElementEntity getWebElement(String elementPk) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().getWebElement(elementPk);
    }

    public WebElementEntity getWebElementByDisplayPk(String elementDisplayPk) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().getWebElement(
                ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator
                        + elementDisplayPk + WebElementEntity.getWebElementFileExtension());
    }

    public void importWebElement(List<SaveWebElementInfoEntity> entities) throws Exception {
        dataProviderSetting.getWebElementDataProvider().importWebElement(entities);
    }

    public FolderEntity importWebElementFolder(FolderEntity folder, FolderEntity parentFolder) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().importWebElementFolder(folder, parentFolder);
    }

    public WebElementEntity importWebElement(WebElementEntity webElement, FolderEntity parentFolder) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().importWebElement(webElement, parentFolder);
    }

    public void saveWebElement(WebElementEntity webElement) throws Exception {
        dataProviderSetting.getWebElementDataProvider().updateWebElement(webElement);
    }

    /**
     * Get entity ID for display This function is deprecated. Please use {@link WebElementEntity#getIdForDisplay()}
     * instead.
     * 
     * @param entity
     * @return Test Object ID for display
     * @throws Exception
     */
    @Deprecated
    public String getIdForDisplay(WebElementEntity entity) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().getIdForDisplay(entity)
                .replace(File.separator, GlobalStringConstants.ENTITY_ID_SEPERATOR);
    }

    public WebElementEntity copyWebElement(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().copyWebElement(webElement, targetFolder);
    }

    public WebElementEntity moveWebElement(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().moveWebElement(webElement, targetFolder);
    }

    public void deleteWebElement(WebElementEntity webElement) throws Exception {
        dataProviderSetting.getWebElementDataProvider().deleteWebElement(webElement);
    }

    public void updateWebElement(WebElementEntity webElement) throws Exception {
        dataProviderSetting.getWebElementDataProvider().updateWebElement(webElement);
    }

    public List<String> getSibblingWebElementNames(WebElementEntity webElement) throws Exception {
        List<WebElementEntity> sibblingWebElements = dataProviderSetting.getWebElementDataProvider()
                .getChildWebElementsOfFolder(webElement.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (WebElementEntity sibblingWebElement : sibblingWebElements) {
            if (!dataProviderSetting.getEntityPk(sibblingWebElement)
                    .equals(dataProviderSetting.getEntityPk(webElement))) {
                sibblingName.add(sibblingWebElement.getName());
            }
        }
        return sibblingName;
    }

    public String getAvailableWebElementName(FolderEntity parentFolder, String name) throws Exception {
        return dataProviderSetting.getWebElementDataProvider().getAvailableWebElementName(parentFolder, name);
    }

    public WebServiceRequestEntity addNewRequest(FolderEntity parentFolder, WebServiceRequestEntity request)
            throws Exception {
        return dataProviderSetting.getWebElementDataProvider().addNewRequest(parentFolder, request);
    }

    public List<WebElementEntity> getTestObjectReferences(WebElementEntity webElement, ProjectEntity projectEntity)
            throws Exception {
        return dataProviderSetting.getWebElementDataProvider().getWebElementPropertyByRefElement(
                webElement.getIdForDisplay(), projectEntity, true);
    }

    public static WebElementPropertyEntity getRefElementProperty(WebElementEntity webElement) {
        return dataProviderSetting.getWebElementDataProvider().getRefElementProperty(webElement);
    }
}
