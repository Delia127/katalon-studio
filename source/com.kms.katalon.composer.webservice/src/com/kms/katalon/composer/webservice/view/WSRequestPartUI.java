package com.kms.katalon.composer.webservice.view;

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
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.util.groovy.editor;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.parts.RestServicePart;
import com.kms.katalon.composer.webservice.parts.SoapServicePart;
import com.kms.katalon.composer.webservice.parts.WSRequestChildPart;
import com.kms.katalon.composer.webservice.parts.WebServicePart;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class WSRequestPartUI {
    
    public static final int MAX_LABEL_LENGTH = 40;

    private static final String BUNDLE_URI_WEBSERVICE = "bundleclass://com.kms.katalon.composer.webservice/";

    private static final String WEBSERVICE_REST_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + RestServicePart.class.getName();

    private static final String WEBSERVICE_SOAP_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + SoapServicePart.class.getName();

    private static final String CHILD_PART_OBJECT_URI = BUNDLE_URI_WEBSERVICE + WSRequestChildPart.class.getName();

    private WebServiceRequestEntity requestObject;

    private MCompositePart compositePart;

    private MPart apiControlsPart;

    private MPartStack bottomLeftPartStack;

    private MPart authorizationPart;

    private MPart headersPart;

    private MPart bodyPart;

    private MPart variablePart;
    
    private MPart variableEditorPart;
    
    private MPart configurationPart;

    private MCompositePart verificationPart;

    private MPart scriptEditorPart;

    private MPart snippetPart;

    private MPart responsePart;

    private CTabFolder tabFolder;

    private MPartSashContainer leftPartSashContainer;

    private MPart verificationToolbarPart;

    private WSRequestPartUI(MPartStack stack, WebServiceRequestEntity requestObject) throws IOException, CoreException {

        EPartService partService = PartServiceSingleton.getInstance().getPartService();
        EModelService modelService = ModelServiceSingleton.getInstance().getModelService();

        this.requestObject = requestObject;

        String compositePartId = getCompositePartId(requestObject);
        compositePart = modelService.createModelElement(MCompositePart.class);
        compositePart.setElementId(compositePartId);
        if (requestObject instanceof DraftWebServiceRequestEntity) {
            String label = getShortenLabel(requestObject);
            compositePart.setLabel("(Draft) " + label);
        } else {
            compositePart.setLabel(requestObject.getName());
        }
        compositePart.setCloseable(true);
        if (WebServiceRequestEntity.SOAP.equals(requestObject.getServiceType())) {
            compositePart.setContributionURI(WEBSERVICE_SOAP_OBJECT_PART_URI);
        } else {
            compositePart.setContributionURI(WEBSERVICE_REST_OBJECT_PART_URI);
        }
        compositePart.setIconURI(ImageConstants.URL_16_WS_TEST_OBJECT);
        if (requestObject instanceof DraftWebServiceRequestEntity) {
            compositePart.setTooltip("(Draft) " + ((DraftWebServiceRequestEntity) requestObject).getNameAsUrl());
        } else {
            compositePart.setTooltip(requestObject.getIdForDisplay());
        }
        compositePart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
        stack.getChildren().add(compositePart);

        String mainPartSashContainerId = getMainPartSashContainerId(requestObject);
        MPartSashContainer mainPartSashContainer = modelService.createModelElement(MPartSashContainer.class);
        mainPartSashContainer.setElementId(mainPartSashContainerId);
        mainPartSashContainer.setHorizontal(true);
        compositePart.getChildren().add(mainPartSashContainer);

        String leftPartSashContainerId = getLeftPartSashContainerId(requestObject);
        leftPartSashContainer = modelService.createModelElement(MPartSashContainer.class);
        leftPartSashContainer.setElementId(leftPartSashContainerId);
        leftPartSashContainer.setContainerData("6500");
        leftPartSashContainer.setHorizontal(false);
        mainPartSashContainer.getChildren().add(leftPartSashContainer);

        String apiControlsPartId = getApiControlsPartId(requestObject);
        apiControlsPart = modelService.createModelElement(MPart.class);
        apiControlsPart.setElementId(apiControlsPartId);
        apiControlsPart.setContributionURI(CHILD_PART_OBJECT_URI);
        apiControlsPart.getTags().add(IPresentationEngine.NO_MOVE);
        leftPartSashContainer.getChildren().add(apiControlsPart);

        String bottomLeftPartStackId = getBottomLeftPartStackId(requestObject);
        bottomLeftPartStack = modelService.createModelElement(MPartStack.class);
        bottomLeftPartStack.setElementId(bottomLeftPartStackId);
        bottomLeftPartStack.getTags().add(IPresentationEngine.NO_MOVE);
        leftPartSashContainer.getChildren().add(bottomLeftPartStack);

        String authorizationPartId = getAuthorizationPartId(requestObject);
        authorizationPart = modelService.createModelElement(MPart.class);
        authorizationPart.setElementId(authorizationPartId);
        authorizationPart.setContributionURI(CHILD_PART_OBJECT_URI);
        authorizationPart.setLabel(ComposerWebserviceMessageConstants.TAB_AUTHORIZATION);
        authorizationPart.setCloseable(false);
        bottomLeftPartStack.getChildren().add(authorizationPart);

        String headersPartId = getHeadersPartId(requestObject);
        headersPart = modelService.createModelElement(MPart.class);
        headersPart.setElementId(headersPartId);
        headersPart.setContributionURI(CHILD_PART_OBJECT_URI);
        headersPart.setLabel(StringConstants.PA_LBL_HTTP_HEADER);
        headersPart.setCloseable(false);
        headersPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(headersPart);

        String bodyPartId = getBodyPartId(requestObject);
        bodyPart = modelService.createModelElement(MPart.class);
        bodyPart.setElementId(bodyPartId);
        bodyPart.setContributionURI(CHILD_PART_OBJECT_URI);
        bodyPart.setLabel(StringConstants.PA_LBL_HTTP_BODY);
        bodyPart.setCloseable(false);
        bodyPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(bodyPart);

        String verificationPartId = getVerificationPartId(requestObject);
        verificationPart = modelService.createModelElement(MCompositePart.class);
        verificationPart.setElementId(verificationPartId);
        verificationPart.setContributionURI(CHILD_PART_OBJECT_URI);
        verificationPart.setLabel(StringConstants.PA_LBL_VERIFICATION);
        verificationPart.setCloseable(false);
        verificationPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(verificationPart);

        String verificationPartSashContainerId = getVerificationPartSashContainerId(requestObject);
        MPartSashContainer verificationPartSashContainer = modelService.createModelElement(MPartSashContainer.class);
        verificationPartSashContainer.setElementId(verificationPartSashContainerId);
        verificationPartSashContainer.setHorizontal(true);
        verificationPartSashContainer.getTags().add(IPresentationEngine.NO_MOVE);
        verificationPart.getChildren().add(verificationPartSashContainer);

        String scriptEditorPartId = getScriptEditorPartId(requestObject);
        IFile tempScriptFile = createTempScriptFile(requestObject);
        tempScriptFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        scriptEditorPart = editor.createEditorPart(tempScriptFile, partService);
        scriptEditorPart.setElementId(scriptEditorPartId);
        verificationPartSashContainer.getChildren().add(scriptEditorPart);

        String snippetPartId = getSnippetPartId(requestObject);
        snippetPart = modelService.createModelElement(MPart.class);
        snippetPart.setElementId(snippetPartId);
        snippetPart.setContributionURI(CHILD_PART_OBJECT_URI);
        verificationPartSashContainer.getChildren().add(snippetPart);

        String verificationToolbarPartId = getVerificationToolbarPartId(requestObject);
        verificationToolbarPart = modelService.createModelElement(MPart.class);
        verificationToolbarPart.setElementId(verificationToolbarPartId);
        verificationToolbarPart.setContributionURI(CHILD_PART_OBJECT_URI);
        // verificationPartSashContainer.getChildren().add(verificationToolbarPart);

        String variablePartId = getVariablePartId(requestObject);
        variablePart = modelService.createModelElement(MPart.class);
        variablePart.setElementId(variablePartId);
        variablePart.setContributionURI(CHILD_PART_OBJECT_URI);
        variablePart.setLabel(StringConstants.PA_LBL_VARIABLE);
        variablePart.setCloseable(false);
        variablePart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(variablePart);
        
        String variableEditorPartID = getVariableEditorPartID(requestObject);
        variableEditorPart = modelService.createModelElement(MPart.class);
        variableEditorPart.setElementId(variableEditorPartID);
        variableEditorPart.setContributionURI(CHILD_PART_OBJECT_URI);
        variableEditorPart.setLabel(StringConstants.PA_LBL_VARIABLE_EDITOR);
        variableEditorPart.setCloseable(false);
        variableEditorPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(variableEditorPart);
        
        String configurationPartId = getConfigurationPartId(requestObject);
        configurationPart = modelService.createModelElement(MPart.class);
        configurationPart.setElementId(configurationPartId);
        configurationPart.setContributionURI(CHILD_PART_OBJECT_URI);
        configurationPart.setLabel(StringConstants.PA_LBL_CONFIGURATION);
        configurationPart.setCloseable(false);
        configurationPart.getTags().add(IPresentationEngine.NO_MOVE);
        bottomLeftPartStack.getChildren().add(configurationPart);
        
        String responsePartId = getResponsePartId(requestObject);
        responsePart = modelService.createModelElement(MPart.class);
        responsePart.setElementId(responsePartId);
        responsePart.setContributionURI(CHILD_PART_OBJECT_URI);
        responsePart.setContainerData("3500");
        mainPartSashContainer.getChildren().add(responsePart);

        partService.activate(compositePart);
        partService.activate(apiControlsPart);
        partService.activate(responsePart);
        partService.activate(authorizationPart);
        partService.activate(headersPart);
        partService.activate(bodyPart);
        partService.activate(scriptEditorPart);
        partService.activate(snippetPart);
        partService.activate(variablePart);
        partService.activate(variableEditorPart);
        partService.activate(configurationPart);

        tabFolder = (CTabFolder) bottomLeftPartStack.getWidget();

        calculateWeightsForVerificationChildParts();

        initComponents();
    }

    public static String getShortenLabel(WebServiceRequestEntity requestObject) {
        String label = ((DraftWebServiceRequestEntity) requestObject).getNameAsUrl();
        label = label.length() <= MAX_LABEL_LENGTH ? label : label.substring(0, MAX_LABEL_LENGTH) + "...";
        return label;
    }

    private IFile createTempScriptFile(WebServiceRequestEntity requestObject) throws IOException, CoreException {
        String wsTempFolderPath = ProjectController.getInstance().getWebServiceTempDir();
        File wsTempFolder = new File(wsTempFolderPath);
        if (!wsTempFolder.exists()) {
            wsTempFolder.mkdirs();
        }
        File tempFile = File.createTempFile("kat-", GroovyConstants.GROOVY_FILE_EXTENSION, wsTempFolder);
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

    private void initComponents() {
        WebServicePart webServicePart = (WebServicePart) compositePart.getObject();

        webServicePart.setOriginalWsObject(requestObject);

        webServicePart.initComponents(this);

        calculateLeftPartsWeight();

        webServicePart.getComposite().addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                Composite wsComposite = webServicePart.getComposite();
                if (wsComposite == null || wsComposite.isDisposed()) {
                    return;
                }
                calculateLeftPartsWeight();
            }

            @Override
            public void controlMoved(ControlEvent e) {
                // TODO Auto-generated method stub

            }
        });

        Composite verificationPartComposite = getVerificationPartComposite();
        verificationPartComposite.addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                calculateWeightsForVerificationChildParts();
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });

        // ((WebServiceVariablePart) variablePart.getObject()).initComponents();
    }

    private void calculateLeftPartsWeight() {
        WSRequestChildPart apiControlsPartObject = (WSRequestChildPart) apiControlsPart.getObject();
        if (apiControlsPartObject == null) {
            return;
        }
        Point apiControlsCompositeSize = apiControlsPartObject.getComposite().getChildren()[0].computeSize(SWT.DEFAULT,
                SWT.DEFAULT);

        int totalHeight = ((WebServicePart) compositePart.getObject()).getComposite().getSize().y;

        long apiControlsPartWeight = Math.round(((double) apiControlsCompositeSize.y / totalHeight) * 1000);
        apiControlsPart.setContainerData(String.valueOf(apiControlsPartWeight));
        bottomLeftPartStack.setContainerData(String.valueOf(1000 - apiControlsPartWeight));
    }

    private void calculateWeightsForVerificationChildParts() {
        Composite verificationPartComposite = getVerificationPartComposite();

        int verificationPartWidth = verificationPartComposite.getSize().x;
        long snippetPartWeight = Math.round(((double) 250 / verificationPartWidth) * 1000);
        long editorPartWeight = 1000 - snippetPartWeight;

        snippetPart.setContainerData(String.valueOf(snippetPartWeight));
        scriptEditorPart.setContainerData(String.valueOf(editorPartWeight));
    }

    public static WSRequestPartUI create(WebServiceRequestEntity requestObject, MPartStack stack)
            throws IOException, CoreException {
        return new WSRequestPartUI(stack, requestObject);
    }

    private String getCompositePartId(WebServiceRequestEntity requestObject) {
        if (requestObject instanceof DraftWebServiceRequestEntity) {
            return EntityPartUtil.getDraftRequestPartId(((DraftWebServiceRequestEntity) requestObject).getDraftUid());
        }
        return EntityPartUtil.getTestObjectPartId(requestObject.getId());
    }

    private String getMainPartSashContainerId(WebServiceRequestEntity requestObject) {
        return getCompositePartId(requestObject) + ".partsash";
    }

    private String getLeftPartSashContainerId(WebServiceRequestEntity requestObject) {
        return getMainPartSashContainerId(requestObject) + ".left";
    }

    private String getApiControlsPartId(WebServiceRequestEntity requestObject) {
        return getLeftPartSashContainerId(requestObject) + ".top";
    }

    private String getBottomLeftPartStackId(WebServiceRequestEntity requestObject) {
        return getLeftPartSashContainerId(requestObject) + ".bottom";
    }

    private String getAuthorizationPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".authorization";
    }

    private String getHeadersPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".headers";
    }

    private String getBodyPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".body";
    }

    private String getVariablePartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".variable";
    }

    private String getVariableEditorPartID(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".variableEditor";
    }
    
    private String getConfigurationPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".configuration";
    }

    private String getVerificationPartId(WebServiceRequestEntity requestObject) {
        return getBottomLeftPartStackId(requestObject) + ".verification";
    }

    private String getVerificationPartSashContainerId(WebServiceRequestEntity requestObject) {
        return getVerificationPartId(requestObject) + ".partsash";
    }

    private String getScriptEditorPartId(WebServiceRequestEntity requestObject) {
        return getVerificationPartSashContainerId(requestObject) + ".editor";
    }

    private String getSnippetPartId(WebServiceRequestEntity requestObject) {
        return getVerificationPartSashContainerId(requestObject) + ".snippet";
    }

    private String getVerificationToolbarPartId(WebServiceRequestEntity requestObject) {
        return getVerificationPartSashContainerId(requestObject) + ".toolbar";
    }

    private String getResponsePartId(WebServiceRequestEntity requestObject) {
        return getMainPartSashContainerId(requestObject) + ".response";
    }

    public MCompositePart getCompositePart() {
        return compositePart;
    }

    public MPart getApiControlsPart() {
        return apiControlsPart;
    }

    public MPart getAuthorizationPart() {
        return authorizationPart;
    }

    public MPart getHeadersPart() {
        return headersPart;
    }

    public MPart getBodyPart() {
        return bodyPart;
    }

    public MPart getVariablePart() {
        return variablePart;
    }
    
    public MPart getVariableEditorPart(){
        return variableEditorPart;
    }

    public MCompositePart getVerificationPart() {
        return verificationPart;
    }

    public MPart getScriptEditorPart() {
        return scriptEditorPart;
    }

    public MPart getSnippetPart() {
        return snippetPart;
    }

    public MPart getResponsePart() {
        return responsePart;
    }

    public Composite getApiControlsPartComposite() {
        return getPartComposite(apiControlsPart);
    }

    public Composite getAuthorizationPartComposite() {
        return getPartComposite(authorizationPart);
    }

    public Composite getHeadersPartComposite() {
        return getPartComposite(headersPart);
    }

    public Composite getBodyPartComposite() {
        return getPartComposite(bodyPart);
    }

    public Composite getVariablePartComposite() {
        return getPartComposite(variablePart);
    }
    
    public Composite getVariableEditorPartComposite() {
        return getPartComposite(variableEditorPart);
    }
    
    public Composite getConfigurationPartComposite() {
        return getPartComposite(configurationPart);
    }

    public Composite getVerificationPartComposite() {
        return getPartComposite(verificationPart);
    }

    public Composite getSnippetPartComposite() {
        return getPartComposite(snippetPart);
    }

    public Composite getVerificationToolbarPartComposite() {
        return getPartComposite(verificationToolbarPart);
    }

    public Composite getResponsePartComposite() {
        return getPartComposite(responsePart);
    }

    private Composite getPartComposite(MPart part) {
        Object partElement = part.getObject();
        if (partElement instanceof WSRequestChildPart) {
            return ((WSRequestChildPart) partElement).getComposite();
        } else {
            return null;
        }
    }

    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    public CTabItem getAuthorizationTab() {
        return tabFolder.getItem(0);
    }

    public CTabItem getHeadersTab() {
        return tabFolder.getItem(1);
    }

    public CTabItem getBodyTab() {
        return tabFolder.getItem(2);
    }

    public CTabItem getVerificationTab() {
        return tabFolder.getItem(3);
    }

    public CTabItem getVariableTab() {
        return tabFolder.getItem(4);
    }
    
    public CTabItem getConfigurationTab() {
        return tabFolder.getItem(5);
    }
    
    public CTabItem getVariableEditorTab(){
        return tabFolder.getItem(5);
    }
    
    public void setSelectedTab(CTabItem tabItem) {
        tabFolder.setSelection(tabItem);
    }

}
