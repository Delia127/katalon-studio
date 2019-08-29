package com.kms.katalon.composer.webservice.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.ScrollableComposite;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.tracking.service.Trackings;

public class ApiQuickStartPart {

    private static final Color RIGHT_PART_BACKGROUND_COLOR = ColorUtil.getColor("#F7F7F7");

    private static final Point LEFT_PART_SIZE = new Point(1000, 2150);

    private static final Point QUICKSTART_ITEM_SIZE = new Point(200, 47);

    private ITreeEntity parentTreeEntity;

    private ProjectType projectType;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart part;
    
    @Inject
    private EModelService modelService;

    private Composite mainComposite;

    @PostConstruct
    public void initialize(final Composite parent, MPart part) {
        this.part = part;
        projectType = ProjectController.getInstance().getCurrentProject().getType();
        createDialogArea(parent);
        registerEventListeners();

    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_CREATED, new EventServiceAdapter() {
            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                if (part.isVisible()) {
                    partService.hidePart(part, true);
                }
                part.setVisible(true);
            }
        });
    }

    protected void createDialogArea(Composite parent) {

        FillLayout parentLayout = new FillLayout();
        parentLayout.marginHeight = 0;
        parentLayout.marginWidth = 0;
        parent.setLayout(parentLayout);

        final ScrollableComposite container = new ScrollableComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setBackground(ColorUtil.getCompositeBackgroundColor());
        mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout mainGridLayout = new GridLayout(2, false);
        mainGridLayout.verticalSpacing = 0;
        mainGridLayout.horizontalSpacing = 0;
        mainGridLayout.marginWidth = 0;
        mainGridLayout.marginHeight = 0;
        mainComposite.setLayout(mainGridLayout);
        mainComposite.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
        mainComposite.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(Event event) {
                Point size = mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                if (container.getMinHeight() < size.y) {
                    container.setMinHeight(size.y);
                }
            }
        });

        container.setContent(mainComposite);
        container.setMinSize(getLeftPartSize());
        container.setExpandHorizontal(true);
        container.setExpandVertical(true);

        switch (projectType) {
            case WEBSERVICE: {
                createLeftPart(mainComposite);
                createRightPart(mainComposite);
                break;
            }
            case GENERIC:
            case WEBUI: {
                mainComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
                createLeftPart(mainComposite);
                break;
            }
            case MOBILE: {
                mainComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
                createLeftPart(mainComposite);
                break;
            }
            default: {
                break;
            }
        }

    }

    private Point getLeftPartSize() {
        switch (projectType) {
            case MOBILE: {
                return new Point(1000, 2150);
            }
            case WEBSERVICE: {
                return new Point(1000, 1850);
            }
            default:
                return new Point(1000, 1700);
        }
    }

    public void createLeftPart(Composite parent) {

        switch (projectType) {
            case WEBSERVICE: {
                Composite imageComposite = new Composite(parent, SWT.LEFT);
                GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
                Image backgroundImg = ImageConstants.API_QUICKSTART_BACKGROUND_LEFT;
                gridData.widthHint = backgroundImg.getBounds().width;
                gridData.heightHint = backgroundImg.getBounds().height;
                imageComposite.setLayoutData(gridData);
                imageComposite.setBackgroundImage(backgroundImg);
                break;
            }
            case GENERIC:
            case WEBUI: {
                Composite imageComposite = new Composite(parent, SWT.NONE);
                GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
                Image backgroundWImg = ImageConstants.API_QUICKSTART_BACKGROUND_WEB_LEFT;
                gridData.widthHint = backgroundWImg.getBounds().width;
                gridData.heightHint = backgroundWImg.getBounds().height;
                imageComposite.setLayoutData(gridData);
                imageComposite.setBackgroundImage(backgroundWImg);
                break;
            }
            case MOBILE: {
                Composite imageComposite = new Composite(parent, SWT.NONE);
                GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
                Image backgroundMImg = ImageConstants.API_QUICKSTART_BACKGROUND_MOBILE_LEFT;
                gridData.widthHint = backgroundMImg.getBounds().width;
                gridData.heightHint = backgroundMImg.getBounds().height;
                imageComposite.setLayoutData(gridData);
                imageComposite.setBackgroundImage(backgroundMImg);
                break;
            }
            default: {
                break;
            }
        }

    }

    public void createRightPart(Composite parent) {
        Composite rightComposite = new Composite(parent, SWT.RIGHT);
        GridLayout glRight = new GridLayout(1, false);
        glRight.marginWidth = 0;
        glRight.marginHeight = 0;
        glRight.marginLeft = 0;
        glRight.marginTop = 250;
        rightComposite.setLayout(glRight);
        rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        rightComposite.setBackground(RIGHT_PART_BACKGROUND_COLOR);

        Composite quickStartItemComposite = new Composite(rightComposite, SWT.NONE);
        GridLayout glQuickStartItemComposite = new GridLayout(1, false);

        glQuickStartItemComposite.marginWidth = 0;
        glQuickStartItemComposite.marginHeight = 0;
        glQuickStartItemComposite.horizontalSpacing = 0;
        glQuickStartItemComposite.verticalSpacing = 0;
        quickStartItemComposite.setLayout(glQuickStartItemComposite);
        quickStartItemComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        quickStartItemComposite.setBackground(RIGHT_PART_BACKGROUND_COLOR);

        createNewRestRequestItem(quickStartItemComposite);
        createNewSoapRequestItem(quickStartItemComposite);
        createImportRestRequestItem(quickStartItemComposite);
        createImportSoapRequestItem(quickStartItemComposite);
    }

    private Composite createQuickStartItem(Composite parent, Image image, String toolTip, Listener selectionListener) {
        CLabel lblItem = new CLabel(parent, SWT.NONE);
        GridData gdItem = new GridData(SWT.CENTER, SWT.FILL, true, false);
        gdItem.widthHint = QUICKSTART_ITEM_SIZE.x;
        gdItem.heightHint = QUICKSTART_ITEM_SIZE.y;
        lblItem.setLayoutData(gdItem);
        lblItem.setToolTipText(toolTip);
        lblItem.setBackground(RIGHT_PART_BACKGROUND_COLOR);

        lblItem.setBackground(image);
        lblItem.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblItem.addListener(SWT.MouseDown, selectionListener);
        return lblItem;
    }

    private Composite createNewRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.NEW_DRAFT_REST_REQUEST,
                StringConstants.QUICKSTART_NEW_DRAFT_REST_REQUEST, e -> createNewRestRequest());
        return item;
    }

    private Composite createNewSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.NEW_DRAFT_SOAP_REQUEST,
                StringConstants.QUICKSTART_NEW_DRAFT_SOAP_REQUEST, e -> createNewSoapRequest());
        return item;
    }

    private Composite createImportRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.IMPORT_SWAGGER,
                StringConstants.QUICKSTART_IMPORT_SWAGGER_FROM_FILE_OR_URL, e -> importSwaggerFromFileOrUrl());
        return item;
    }

    private Composite createImportSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.IMPORT_WSDL,
                StringConstants.QUICKSTART_IMPORT_WSDL_FROM_FILE_OR_URL, e -> importWsdlFromUrl());
        return item;
    }

    private void createNewRestRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.RESTFUL);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
        Trackings.trackOpenDraftRequest(entity.getServiceType(), "apiQuickStart");
        partService.hidePart(part);
    }

    private void createNewSoapRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.SOAP);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
        Trackings.trackOpenDraftRequest(entity.getServiceType(), "apiQuickStart");
        partService.hidePart(part);
    }

    private void importSwaggerFromFileOrUrl() {
        FolderEntity parentFolderEntity;
        try {
            parentFolderEntity = (FolderEntity) this.parentTreeEntity.getObject();
            ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

            ImportWebServiceObjectsFromSwaggerDialog dialog = new ImportWebServiceObjectsFromSwaggerDialog(
                    Display.getCurrent().getActiveShell(), parentFolderEntity);

            if (dialog.open() == Dialog.OK) {

                List<WebServiceRequestEntity> requestEntities = dialog.getWebServiceRequestEntities();
                for (WebServiceRequestEntity entity : requestEntities) {
                    toController.saveNewTestObject(entity);
                }

                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                        parentTreeEntity);
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                        parentTreeEntity);
                partService.hidePart(part);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void importWsdlFromUrl() {
        try {
            FolderEntity parentFolderEntity;
            parentFolderEntity = (FolderEntity) this.parentTreeEntity.getObject();

            ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

            ImportWebServiceObjectsFromWSDLDialog dialog = new ImportWebServiceObjectsFromWSDLDialog(
                    Display.getCurrent().getActiveShell());

            String[] requestMethods = new String[] { WebServiceRequestEntity.SOAP, WebServiceRequestEntity.SOAP12 };
            if (dialog.open() == Dialog.OK) {
                for (int i = 0; i < requestMethods.length; i++) {
                    String requestMethod = requestMethods[i];

                    List<WebServiceRequestEntity> soapRequestEntities = dialog
                            .getWebServiceRequestEntities(requestMethod);
                    if (soapRequestEntities != null && soapRequestEntities.size() > 0) {
                        FolderEntity folder = FolderController.getInstance().addNewFolder(parentFolderEntity,
                                requestMethod);
                        FolderTreeEntity newFolderTree = new FolderTreeEntity(folder, parentTreeEntity);
                        for (WebServiceRequestEntity entity : soapRequestEntities) {
                            entity.setElementGuidId(Util.generateGuid());
                            entity.setParentFolder(folder);
                            entity.setProject(folder.getProject());
                            toController.saveNewTestObject(entity);
                        }
                    }
                }
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                        parentTreeEntity);
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                        parentTreeEntity);
                partService.hidePart(part);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Focus
    public void setFocus() {
        mainComposite.forceFocus();
    }
}
