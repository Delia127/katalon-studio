package com.kms.katalon.platform.internal.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.TestObjectEntity;
import com.katalon.platform.api.model.testobject.BasicRestRequestEntity;
import com.katalon.platform.api.model.testobject.BasicSoapRequestEntity;
import com.katalon.platform.api.model.testobject.BasicWebElementEntity;
import com.katalon.platform.api.model.testobject.RestRequestEntity;
import com.katalon.platform.api.model.testobject.SoapRequestEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;
import com.kms.katalon.entity.repository.WebElementXpathEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.platform.internal.entity.testobject.RestRequestEntityImpl;
import com.kms.katalon.platform.internal.entity.testobject.SoapRequestEntityImpl;
import com.kms.katalon.platform.internal.entity.testobject.WebElementEntityImpl;

public class ObjectRepositoryControllerImpl implements com.katalon.platform.api.controller.ObjectRepositoryController {

    private ObjectRepositoryController objectRepositoryController = ObjectRepositoryController.getInstance();

    private FolderController folderController = FolderController.getInstance();

    private ProjectController projectController = ProjectController.getInstance();

    @Override
    public com.katalon.platform.api.model.TestObjectEntity getTestObject(
            com.katalon.platform.api.model.ProjectEntity project, String testObjectId) throws ResourceException {
        try {
            WebElementEntity sourceWebElement = objectRepositoryController.getWebElementByDisplayPk(testObjectId);
            if (sourceWebElement == null) {
                return null;
            }

            if (sourceWebElement instanceof WebServiceRequestEntity) {
                WebServiceRequestEntity sourceRequestElement = (WebServiceRequestEntity) sourceWebElement;

                if (sourceRequestElement.getServiceType() == WebServiceRequestEntity.SOAP) {
                    return new SoapRequestEntityImpl(sourceRequestElement);
                } else {
                    return new RestRequestEntityImpl(sourceRequestElement);
                }
            }
            return new WebElementEntityImpl(sourceWebElement);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    @Override
    public List<com.katalon.platform.api.model.TestObjectEntity> getChildTestObjects(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folder = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (folder == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }

            return folderController.getChildren(folder)
                    .stream()
                    .filter(entity -> entity instanceof WebElementEntity)
                    .map(entity -> {
                        try {
                            return getTestObject(project, entity.getIdForDisplay());
                        } catch (ResourceException e) {
                            return null;
                        }
                    })
                    .filter(entity -> entity != null)
                    .collect(Collectors.toList());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.testobject.RestRequestEntity newRestRequest(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name,
            BasicRestRequestEntity requestTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folder = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (folder == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }
            WebServiceRequestEntity sourceWebService = objectRepositoryController.newWSTestObject(folder, name);
            updateRestRequest(requestTemplate, sourceWebService);
            return (RestRequestEntity) getTestObject(project, sourceWebService.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    private void updateRestRequest(BasicRestRequestEntity requestTemplate, WebServiceRequestEntity sourceWebService)
            throws ControllerException {
        if (requestTemplate != null) {
            sourceWebService.setServiceType(WebServiceRequestEntity.REST);
            sourceWebService.setRestUrl(requestTemplate.getRequestUrl());
            sourceWebService.setRestRequestMethod(requestTemplate.getRequestMethod());
            List<VariableEntity> variableEntities = new ArrayList<>();
            if (requestTemplate.getVariables() != null) {
                variableEntities.addAll(requestTemplate.getVariables()
                        .stream()
                        .map(v -> new VariableEntity(v.getName(), v.getDefaultValue()))
                        .collect(Collectors.toList()));
            }

            List<WebElementPropertyEntity> httpHeaderProperties = new ArrayList<>();
            if (requestTemplate.getHttpHeaders() != null) {
                httpHeaderProperties.addAll(requestTemplate.getHttpHeaders()
                        .stream()
                        .map(prop -> new WebElementPropertyEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebService.setHttpHeaderProperties(httpHeaderProperties);

            List<WebElementPropertyEntity> paramProperties = new ArrayList<>();
            if (requestTemplate.getHttpHeaders() != null) {
                paramProperties.addAll(requestTemplate.getRequestParameters()
                        .stream()
                        .map(prop -> new WebElementPropertyEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebService.setRestParameters(paramProperties);
            sourceWebService.setVerificationScript(requestTemplate.getVerificationScript());

            sourceWebService.setHttpBodyContent(requestTemplate.getHttpBodyContent());
            sourceWebService.setHttpBodyType(requestTemplate.getHttpBodyType());
        }

        objectRepositoryController.updateTestObject(sourceWebService);
    }

    @Override
    public com.katalon.platform.api.model.testobject.SoapRequestEntity newSoapRequest(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name,
            BasicSoapRequestEntity requestTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folder = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (folder == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }
            WebServiceRequestEntity sourceWebService = objectRepositoryController.newWSTestObject(folder, name);
            updateSoapRequest(requestTemplate, sourceWebService);
            return (SoapRequestEntity) getTestObject(project, sourceWebService.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.testobject.WebElementEntity newWebElement(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name,
            BasicWebElementEntity webElementTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folder = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (folder == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }
            WebElementEntity sourceWebElement = objectRepositoryController.newTestObject(folder, name);

            updateWebElement(webElementTemplate, sourceWebElement);
            return (com.katalon.platform.api.model.testobject.WebElementEntity) getTestObject(project,
                    sourceWebElement.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    private void updateWebElement(BasicWebElementEntity webElementTemplate, WebElementEntity sourceWebElement) {
        if (webElementTemplate != null) {
            sourceWebElement.setImagePath(webElementTemplate.getImagePath());
            sourceWebElement.setUseRalativeImagePath(webElementTemplate.isRelativeImagePath());

            sourceWebElement
                    .setSelectorMethod(WebElementSelectorMethod.valueOf(webElementTemplate.getSelectorMethod().name()));

            Map<WebElementSelectorMethod, String> selectorCollection = new HashMap<>();
            if (webElementTemplate != null) {
                webElementTemplate.getSelectorCollection().entrySet().stream().forEach(entry -> selectorCollection
                        .put(WebElementSelectorMethod.valueOf(entry.getKey().name()), entry.getValue()));
            }

            List<WebElementPropertyEntity> webElementProperties = new ArrayList<>();

            if (webElementTemplate.hasParentElement()) {
                WebElementPropertyEntity parentRefElement = new WebElementPropertyEntity(WebElementEntity.ref_element,
                        webElementTemplate.getParentElementId());
                webElementProperties.add(parentRefElement);
            }

            if (webElementTemplate.getWebElementProperties() != null) {
                webElementProperties.addAll(webElementTemplate.getWebElementProperties()
                        .stream()
                        .map(prop -> new WebElementPropertyEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebElement.setWebElementProperties(webElementProperties);

            List<WebElementXpathEntity> xpathElementProperties = new ArrayList<>();
            if (webElementTemplate.getWebElementProperties() != null) {
                xpathElementProperties.addAll(webElementTemplate.getXpathElementProperties()
                        .stream()
                        .map(prop -> new WebElementXpathEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebElement.setWebElementXpaths(xpathElementProperties);
        }
    }

    @Override
    public com.katalon.platform.api.model.testobject.RestRequestEntity updateRestRequest(
            com.katalon.platform.api.model.ProjectEntity project, String requestId,
            BasicRestRequestEntity requestTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            WebElementEntity sourceObject = objectRepositoryController.getWebElementByDisplayPk(requestId);
            if (!(sourceObject instanceof WebServiceRequestEntity)) {
                throw new ResourceException(
                        MessageFormat.format("Request object {0} is not valid Rest Request", requestId));
            }
            WebServiceRequestEntity sourceWebService = (WebServiceRequestEntity) sourceObject;
            updateRestRequest(requestTemplate, sourceWebService);
            return (RestRequestEntity) getTestObject(project, sourceWebService.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.testobject.SoapRequestEntity updateSoapRequest(
            com.katalon.platform.api.model.ProjectEntity project, String requestId,
            BasicSoapRequestEntity requestTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            WebElementEntity sourceObject = objectRepositoryController.getWebElementByDisplayPk(requestId);
            if (!(sourceObject instanceof WebServiceRequestEntity)) {
                throw new ResourceException(MessageFormat.format("Request object {0} is not SOAP Request", requestId));
            }
            WebServiceRequestEntity sourceWebService = (WebServiceRequestEntity) sourceObject;
            updateSoapRequest(requestTemplate, sourceWebService);
            return (SoapRequestEntity) getTestObject(project, sourceWebService.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    private void updateSoapRequest(BasicSoapRequestEntity requestTemplate, WebServiceRequestEntity sourceWebService)
            throws ControllerException {
        if (requestTemplate != null) {
            sourceWebService.setServiceType(WebServiceRequestEntity.SOAP);
            sourceWebService.setWsdlAddress(requestTemplate.getRequestUrl());
            sourceWebService.setRestRequestMethod(requestTemplate.getRequestMethod());
            List<VariableEntity> variableEntities = new ArrayList<>();
            if (requestTemplate.getVariables() != null) {
                variableEntities.addAll(requestTemplate.getVariables()
                        .stream()
                        .map(v -> new VariableEntity(v.getName(), v.getDefaultValue()))
                        .collect(Collectors.toList()));
            }

            List<WebElementPropertyEntity> httpHeaderProperties = new ArrayList<>();
            if (requestTemplate.getHttpHeaders() != null) {
                httpHeaderProperties.addAll(requestTemplate.getHttpHeaders()
                        .stream()
                        .map(prop -> new WebElementPropertyEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebService.setHttpHeaderProperties(httpHeaderProperties);

            List<WebElementPropertyEntity> paramProperties = new ArrayList<>();
            if (requestTemplate.getHttpHeaders() != null) {
                paramProperties.addAll(requestTemplate.getRequestParameters()
                        .stream()
                        .map(prop -> new WebElementPropertyEntity(prop.getName(), prop.getValue()))
                        .collect(Collectors.toList()));
            }
            sourceWebService.setSoapParameters(paramProperties);
            sourceWebService.setVerificationScript(requestTemplate.getVerificationScript());

            sourceWebService.setSoapBody(requestTemplate.getSoapBodyContent());
            sourceWebService.setSoapServiceFunction(requestTemplate.getSoapServiceFunction());
        }

        objectRepositoryController.updateTestObject(sourceWebService);
    }

    @Override
    public com.katalon.platform.api.model.testobject.WebElementEntity updateWebElement(
            com.katalon.platform.api.model.ProjectEntity project, String webElementId,
            BasicWebElementEntity webElementTemplate) throws ResourceException {
        try {
            ProjectEntity projectEntity = projectController.getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            WebElementEntity sourceWebElement = objectRepositoryController.getWebElementByDisplayPk(webElementId);
            updateWebElement(webElementTemplate, sourceWebElement);
            return (com.katalon.platform.api.model.testobject.WebElementEntity) getTestObject(project,
                    sourceWebElement.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    @Override
    public TestObjectEntity renameTestObject(com.katalon.platform.api.model.ProjectEntity projectEntity,
            String testObjectId, String newName) throws ResourceException {
        try {
            WebElementEntity sourceWebElement = objectRepositoryController.getWebElementByDisplayPk(testObjectId);

            if (sourceWebElement == null) {
                return null;
            }

            WebElementEntity renamedSourceElement = objectRepositoryController.updateTestObject(sourceWebElement);

            return getTestObject(projectEntity, renamedSourceElement.getIdForDisplay());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }
}
