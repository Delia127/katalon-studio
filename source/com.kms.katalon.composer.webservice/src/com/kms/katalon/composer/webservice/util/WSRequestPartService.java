package com.kms.katalon.composer.webservice.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.parts.RestServicePart;
import com.kms.katalon.composer.webservice.parts.SoapServicePart;
import com.kms.katalon.composer.webservice.parts.WSRequestChildPart;
import com.kms.katalon.composer.webservice.parts.WebServicePart;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class WSRequestPartService {

    private static final String BUNDLE_URI_WEBSERVICE = "bundleclass://com.kms.katalon.composer.webservice/";

    private static final String WEBSERVICE_REST_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + RestServicePart.class.getName();

    private static final String WEBSERVICE_SOAP_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + SoapServicePart.class.getName();

    private static final String CHILD_PART_OBJECT_URI = BUNDLE_URI_WEBSERVICE + WSRequestChildPart.class.getName();

    private static MApplication application;

    private static EPartService partService;

    private static EModelService modelService;

    static {
        application = ApplicationSingleton.getInstance().getApplication();
        partService = PartServiceSingleton.getInstance().getPartService();
        modelService = ModelServiceSingleton.getInstance().getModelService();
    }

    public static void openPart(WebServiceRequestEntity requestObject) throws IOException, CoreException {

        MCompositePart compositePart = null;
        if (requestObject != null) {
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            if (stack != null) {

                String compositePartId = getCompositePartId(requestObject);
                compositePart = (MCompositePart) modelService.find(compositePartId, stack);
                if (compositePart == null) {
                    compositePart = createNewCompositePart(requestObject, stack);
                }

                stack.setSelectedElement(compositePart);
            }
        }
    }

    private static MCompositePart createNewCompositePart(WebServiceRequestEntity requestObject, MPartStack stack)
            throws IOException, CoreException {

        String compositePartId = getCompositePartId(requestObject);
        MCompositePart compositePart = modelService.createModelElement(MCompositePart.class);
        compositePart.setElementId(compositePartId);
        compositePart.setLabel(requestObject.getName());
        compositePart.setCloseable(true);
        if (WebServiceRequestEntity.SOAP.equals(requestObject.getServiceType())) {
            compositePart.setContributionURI(WEBSERVICE_SOAP_OBJECT_PART_URI);
        } else {
            compositePart.setContributionURI(WEBSERVICE_REST_OBJECT_PART_URI);
        }
        compositePart.setIconURI(ImageConstants.URL_16_WS_TEST_OBJECT);
        compositePart.setTooltip(requestObject.getIdForDisplay());
        compositePart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
        stack.getChildren().add(compositePart);

        String subPartSashContainerId = getSubPartSashContainerId(requestObject);
        MPartSashContainer subPartSashContainer = modelService.createModelElement(MPartSashContainer.class);
        subPartSashContainer.setElementId(subPartSashContainerId);
        subPartSashContainer.setHorizontal(true);
        compositePart.getChildren().add(subPartSashContainer);

        String leftPartSashContainerId = getLeftPartSashContainerId(requestObject);
        MPartSashContainer leftPartSashContainer = modelService.createModelElement(MPartSashContainer.class);
        leftPartSashContainer.setElementId(leftPartSashContainerId);
        leftPartSashContainer.setContainerData("6500");
        leftPartSashContainer.setHorizontal(false);
        subPartSashContainer.getChildren().add(leftPartSashContainer);

        String topLeftPartId = getTopLeftPartId(requestObject);
        MPart topLeftPart = modelService.createModelElement(MPart.class);
        topLeftPart.setElementId(topLeftPartId);
        topLeftPart.setContributionURI(CHILD_PART_OBJECT_URI);
        topLeftPart.getTags().add(IPresentationEngine.NO_MOVE);
        leftPartSashContainer.getChildren().add(topLeftPart);

        String bottomLeftPartStackId = getBottomLeftPartStackId(requestObject);
        MPartStack bottomLeftPartStack = modelService.createModelElement(MPartStack.class);
        bottomLeftPartStack.setElementId(bottomLeftPartStackId);
        bottomLeftPartStack.getTags().add(IPresentationEngine.NO_MOVE);
        leftPartSashContainer.getChildren().add(bottomLeftPartStack);

        String authorizationPartId = getAuthorizationPartId(requestObject);
        MPart authorizationPart = modelService.createModelElement(MPart.class);
        authorizationPart.setElementId(authorizationPartId);
        authorizationPart.setContributionURI(CHILD_PART_OBJECT_URI);
        authorizationPart.setLabel(ComposerWebserviceMessageConstants.TAB_AUTHORIZATION);
        authorizationPart.setCloseable(false);
        bottomLeftPartStack.getChildren().add(authorizationPart);

        String headersPartId = getHeadersPartId(requestObject);
        MPart headersPart = modelService.createModelElement(MPart.class);
        headersPart.setElementId(headersPartId);
        headersPart.setContributionURI(CHILD_PART_OBJECT_URI);
        headersPart.setLabel(StringConstants.PA_LBL_HTTP_HEADER);
        headersPart.setCloseable(false);
        headersPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(headersPart);

        String bodyPartId = getBodyPartId(requestObject);
        MPart bodyPart = modelService.createModelElement(MPart.class);
        bodyPart.setElementId(bodyPartId);
        bodyPart.setContributionURI(CHILD_PART_OBJECT_URI);
        bodyPart.setLabel(StringConstants.PA_LBL_HTTP_BODY);
        bodyPart.setCloseable(false);
        bodyPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(bodyPart);

        String verificationPartId = getVerificationPartId(requestObject);
        File tempFile = File.createTempFile("kat-", GroovyConstants.GROOVY_FILE_EXTENSION);
        IFile tempScriptFile = createTempScriptFile(requestObject, tempFile);
        tempScriptFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        MPart verificationPart = GroovyEditorUtil.createEditorPart(tempScriptFile, partService);
        verificationPart.setElementId(verificationPartId);
        verificationPart.setCloseable(false);
        verificationPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(verificationPart);

        String responsePartId = getResponsePartId(requestObject);
        MPart responsePart = modelService.createModelElement(MPart.class);
        responsePart.setElementId(responsePartId);
        responsePart.setContributionURI(CHILD_PART_OBJECT_URI);
        responsePart.setContainerData("3500");
        subPartSashContainer.getChildren().add(responsePart);

        partService.activate(compositePart);
        partService.activate(topLeftPart);
        partService.activate(responsePart);
        partService.activate(authorizationPart);
        partService.activate(headersPart);
        partService.activate(bodyPart);
        partService.activate(verificationPart);

        WebServicePart webServicePart1 = (WebServicePart) compositePart.getObject();

        webServicePart1.setOriginalWsObject(requestObject);

        webServicePart1.setTempScriptFile(tempFile);
        webServicePart1.initComponents();

        // Get the verification tab and set it label
        // since setLabel() doesn't work for editor part
        CTabFolder tabFolder = (CTabFolder) bottomLeftPartStack.getWidget();
        CTabItem verificationTab = getVerificationTab(tabFolder);
        verificationTab.setText(StringConstants.PA_LBL_VERIFICATION);
        verificationTab.setImage(null);

        return compositePart;
    }

    private static IFile createTempScriptFile(WebServiceRequestEntity requestObject, File tempFile)
            throws IOException, CoreException {
        ProjectEntity projectEntity = requestObject.getProject();
        IPath location = new Path(tempFile.getAbsolutePath());
        IFile tempIFile = GroovyUtil.getGroovyProject(projectEntity).getFile(location.lastSegment());
        tempIFile.createLink(location, IResource.NONE, null);

        String script = requestObject.getVerificationScript();
        if (!StringUtils.isBlank(script)) {
            tempIFile.setContents(new ByteArrayInputStream(script.getBytes()), true, false, null);
        }

        return tempIFile;
    }

    private static String getCompositePartId(WebServiceRequestEntity requestObject) {
        return EntityPartUtil.getTestObjectPartId(requestObject.getId());
    }

    private static String getSubPartSashContainerId(WebServiceRequestEntity requestObject) {
        return getCompositePartId(requestObject) + ".partsash";
    }

    private static String getLeftPartSashContainerId(WebServiceRequestEntity requestObject) {
        return getSubPartSashContainerId(requestObject) + ".left";
    }

    private static String getTopLeftPartId(WebServiceRequestEntity requestObject) {
        return getLeftPartSashContainerId(requestObject) + ".top";
    }

    private static String getBottomLeftPartStackId(WebServiceRequestEntity requestObject) {
        return getLeftPartSashContainerId(requestObject) + ".bottom";
    }

    private static String getAuthorizationPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".authorization";
    }

    private static String getHeadersPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".headers";
    }

    private static String getBodyPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".body";
    }

    private static String getVerificationPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".verification";
    }

    private static String getResponsePartId(WebServiceRequestEntity requestObject) {
        return getSubPartSashContainerId(requestObject) + ".response";
    }

    public static MPart getTopLeftPart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getTopLeftPartId(requestObject), compositePart);
    }

    public static MPart getAuthorizationPart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getAuthorizationPartId(requestObject), compositePart);
    }

    public static MPart getHeadersPart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getHeadersPartId(requestObject), compositePart);
    }

    public static MPart getBodyPart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getBodyPartId(requestObject), compositePart);
    }

    public static MPart getVerificationPart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getVerificationPartId(requestObject), compositePart);
    }

    public static MPart getResponsePart(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        return (MPart) modelService.find(getResponsePartId(requestObject), compositePart);
    }

    public static Composite getPartComposite(MPart part) {
        Object partElement = part.getObject();
        if (partElement instanceof WSRequestChildPart) {
            return ((WSRequestChildPart) partElement).getComposite();
        } else {
            return null;
        }
    }

    public static CTabFolder getTabFolder(MCompositePart compositePart) {
        WebServiceRequestEntity requestObject = getRequestObject(compositePart);
        MPartStack bottomLeftPartStack = (MPartStack) modelService.find(getBottomLeftPartStackId(requestObject),
                compositePart);
        return (CTabFolder) bottomLeftPartStack.getWidget();
    }

    public static CTabItem getAuthorizationTab(CTabFolder tabFolder) {
        return tabFolder.getItem(0);
    }

    public static CTabItem getHeadersTab(CTabFolder tabFolder) {
        return tabFolder.getItem(1);
    }

    public static CTabItem getBodyTab(CTabFolder tabFolder) {
        return tabFolder.getItem(2);
    }

    public static CTabItem getVerificationTab(CTabFolder tabFolder) {
        return tabFolder.getItem(3);
    }

    private static WebServiceRequestEntity getRequestObject(MCompositePart compositePart) {
        WebServicePart wsPart = (WebServicePart) compositePart.getObject();
        return wsPart.getWSRequestObject();
    }
}
