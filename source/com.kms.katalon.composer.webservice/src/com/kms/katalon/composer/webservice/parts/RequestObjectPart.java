package com.kms.katalon.composer.webservice.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.support.PropertyNameEditingSupport;
import com.kms.katalon.composer.webservice.support.PropertyValueEditingSupport;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.composer.webservice.view.ParameterTable;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public abstract class RequestObjectPart implements EventHandler, IComposerPart {

    @Inject
    protected MApplication application;

    @Inject
    protected EModelService modelService;

    @Inject
    private IEventBroker eventBroker;

    protected MPart mPart;

    protected WebServiceRequestEntity originalWsObject;

    protected ScrolledComposite sComposite;

    protected Composite mainComposite;

    protected ModifyListener modifyListener;

    protected Text txtID;

    protected Text txtName;

    protected Text txtDescription;

    protected Text txtHttpBody;

    protected ParameterTable tblHttpHeader;

    protected List<WebElementPropertyEntity> listHttpHeaderProps = new ArrayList<WebElementPropertyEntity>();

    protected List<WebElementPropertyEntity> tempPropList = new ArrayList<WebElementPropertyEntity>();

    @Inject
    protected MDirtyable dirtyable;

    protected GridData labelGridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);

    public void createComposite(Composite parent, MPart part) {
        this.mPart = part;
        this.originalWsObject = (WebServiceRequestEntity) part.getObject();

        parent.setLayout(new FillLayout());

        sComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sComposite.setExpandHorizontal(true);
        sComposite.setExpandVertical(true);
        sComposite.setBackground(ColorUtil.getCompositeBackgroundColor());
        sComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);
        sComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                sComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });

        mainComposite = new Composite(sComposite, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        mainComposite.setLayout(glMainComposite);
        sComposite.setContent(mainComposite);

        createModifyListener();

        // Label width
        labelGridData.widthHint = 100;

        // Init UI
        createEntityInfoComposite(mainComposite);
        createHttpComposite(mainComposite);
        createServiceInfoComposite(mainComposite);

        showEntityFieldsToUi();

        dirtyable.setDirty(false);

        registerListeners();
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.TEST_OBJECT_UPDATED)) {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            if (object != null && object instanceof Object[]) {
                String elementId = EntityPartUtil.getTestObjectPartId((String) ((Object[]) object)[0]);
                if (elementId.equalsIgnoreCase(mPart.getElementId())) {
                    WebServiceRequestEntity webElement = (WebServiceRequestEntity) ((Object[]) object)[1];
                    mPart.setLabel(webElement.getName());
                    mPart.setElementId(EntityPartUtil.getTestObjectPartId(webElement.getId()));
                    showEntityFieldsToUi();
                }
            }
        } else if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ITreeEntity) {
                    if (object instanceof WebElementTreeEntity) {
                        WebElementTreeEntity testObjectTreeEntity = (WebElementTreeEntity) object;
                        WebServiceRequestEntity wsObject = (WebServiceRequestEntity) (testObjectTreeEntity).getObject();
                        if (wsObject != null && wsObject.getId().equals(originalWsObject.getId())) {
                            if (ObjectRepositoryController.getInstance().getWebElement(wsObject.getId()) != null) {
                                if (!dirtyable.isDirty()) {
                                    originalWsObject = wsObject;
                                    showEntityFieldsToUi();
                                }
                            } else {
                                dispose();
                            }
                        } else {
                            if (ObjectRepositoryController.getInstance().getWebElement(originalWsObject.getId()) == null) {
                                dispose();
                            }
                        }
                    } else if (object instanceof FolderTreeEntity) {
                        FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                        if (folder != null
                                && FolderController.getInstance().isFolderAncestorOfEntity(folder, originalWsObject)) {
                            if (ObjectRepositoryController.getInstance().getWebElement(originalWsObject.getId()) == null) {
                                dispose();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void dispose() {
        eventBroker.unsubscribe(this);
        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        mStackPart.getChildren().remove(mPart);
    }

    private void createEntityInfoComposite(Composite mainComposite) {
        ExpandableComposite entityComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_INFO, 1,
                true);
        Composite generalInfoComposite = entityComposite.createControl();
        GridLayout gl_compositeInfoDetails = new GridLayout(2, true);
        gl_compositeInfoDetails.marginRight = 40;
        gl_compositeInfoDetails.marginLeft = 40;
        gl_compositeInfoDetails.marginBottom = 5;
        gl_compositeInfoDetails.horizontalSpacing = 30;
        gl_compositeInfoDetails.marginHeight = 0;
        gl_compositeInfoDetails.marginWidth = 0;
        generalInfoComposite.setLayout(gl_compositeInfoDetails);
        generalInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        generalInfoComposite.setBounds(0, 0, 64, 64);

        Composite idNameComposite = new Composite(generalInfoComposite, SWT.NONE);
        idNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeInfoNameAndId = new GridLayout(2, false);
        glCompositeInfoNameAndId.verticalSpacing = 10;
        idNameComposite.setLayout(glCompositeInfoNameAndId);

        GridData idNameGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        idNameGridData.heightHint = 20;

        Label lblID = new Label(idNameComposite, SWT.LEFT);
        lblID.setText(StringConstants.PA_LBL_ID);
        lblID.setLayoutData(labelGridData);

        txtID = new Text(idNameComposite, SWT.BORDER);
        txtID.setLayoutData(idNameGridData);
        txtID.setEditable(false);
        txtID.addModifyListener(modifyListener);

        Label lblName = new Label(idNameComposite, SWT.LEFT);
        lblName.setText(StringConstants.PA_LBL_NAME);
        lblName.setLayoutData(labelGridData);

        txtName = new Text(idNameComposite, SWT.BORDER);
        txtName.setLayoutData(idNameGridData);
        txtName.addModifyListener(modifyListener);

        Composite descComposite = new Composite(generalInfoComposite, SWT.NONE);
        descComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        descComposite.setLayout(new GridLayout(2, false));

        Label lblDesc = new Label(descComposite, SWT.LEFT);
        lblDesc.setText(StringConstants.PA_LBL_DESC);
        lblDesc.setLayoutData(labelGridData);

        txtDescription = new Text(descComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdTextDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdTextDescription.heightHint = 45;
        txtDescription.setLayoutData(gdTextDescription);
        txtDescription.addModifyListener(modifyListener);
    }

    private void createHttpComposite(Composite mainComposite) {
        ExpandableComposite soapComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_HTTP, 1,
                true);
        Composite compositeDetails = soapComposite.createControl();

        Composite httpContainerComposite = new Composite(compositeDetails, SWT.NONE);
        httpContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        httpContainerComposite.setLayout(new GridLayout(2, false));

        // HTTP Header
        Label lblHttpHeader = new Label(httpContainerComposite, SWT.LEFT | SWT.WRAP);
        lblHttpHeader.setText(StringConstants.PA_LBL_HTTP_HEADER);
        lblHttpHeader.setLayoutData(labelGridData);

        tblHttpHeader = createParamsTable(httpContainerComposite, true);
        tblHttpHeader.setInput(listHttpHeaderProps);

        // HTTP Body
        Label lblSoapBody = new Label(httpContainerComposite, SWT.LEFT | SWT.WRAP);
        lblSoapBody.setText(StringConstants.PA_LBL_HTTP_BODY);
        lblSoapBody.setLayoutData(labelGridData);

        txtHttpBody = new Text(httpContainerComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 45;
        txtHttpBody.setLayoutData(gdData);
        txtHttpBody.addModifyListener(modifyListener);
    }

    protected abstract void createServiceInfoComposite(Composite mainComposite);

    protected ParameterTable createParamsTable(Composite containerComposite, boolean isHttpHeader) {

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite compositeTableDetails = new Composite(containerComposite, SWT.NONE);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 100;
        compositeTableDetails.setLayoutData(gdData);
        compositeTableDetails.setLayout(tableColumnLayout);

        final ParameterTable tblProperties = new ParameterTable(compositeTableDetails, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.NO_SCROLL | SWT.V_SCROLL, dirtyable);
        tblProperties.createTableEditor();

        tblProperties.getTable().setHeaderVisible(true);
        tblProperties.getTable().setLinesVisible(true);
        GridData gridDataTable = new GridData(GridData.FILL_BOTH);
        gridDataTable.horizontalSpan = 3;
        gridDataTable.heightHint = 150;
        tblProperties.getTable().setLayoutData(gridDataTable);

        // Double click to add new property
        tblProperties.getTable().addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                WebElementPropertyEntity newProp = new WebElementPropertyEntity(StringConstants.EMPTY,
                        StringConstants.EMPTY);
                // Add new row
                tblProperties.addRow(newProp);

                // Focus on the new row
                tblProperties.editElement(newProp, 0);
            }
        });

        TableViewerColumn treeViewerColumnName = new TableViewerColumn(tblProperties, SWT.NONE);
        treeViewerColumnName.getColumn().setText(ParameterTable.columnNames[0]);
        treeViewerColumnName.getColumn().setWidth(400);
        treeViewerColumnName.setEditingSupport(new PropertyNameEditingSupport(tblProperties, dirtyable, isHttpHeader));
        treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });
        tableColumnLayout.setColumnData(treeViewerColumnName.getColumn(), new ColumnWeightData(30));

        TableViewerColumn treeViewerColumnValue = new TableViewerColumn(tblProperties, SWT.NONE);
        treeViewerColumnValue.getColumn().setText(ParameterTable.columnNames[1]);
        treeViewerColumnValue.getColumn().setWidth(500);
        treeViewerColumnValue.setEditingSupport(new PropertyValueEditingSupport(tblProperties, dirtyable));
        treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });
        tableColumnLayout.setColumnData(treeViewerColumnValue.getColumn(), new ColumnWeightData(60));

        tblProperties.setContentProvider(ArrayContentProvider.getInstance());

        // Set tooltip for table
        DefaultToolTip toolTip = new DefaultToolTip(tblProperties.getControl(), ToolTip.RECREATE, false);
        toolTip.setText(StringConstants.PA_TOOLTIP_DOUBLE_CLICK_FOR_QUICK_INSERT);
        toolTip.setPopupDelay(0);
        toolTip.setShift(new Point(15, 0));

        return tblProperties;
    }

    private void createModifyListener() {
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dirtyable.setDirty(true);
            }
        };
    }

    /**
     * Update entity fields before saved, Child class should override this method and do some more updates for it own
     * properties
     */
    protected void updateEntityBeforeSaved() {
        // Update object properties
        originalWsObject.setName(txtName.getText());
        originalWsObject.setDescription(txtDescription.getText());

        originalWsObject.setHttpBody(txtHttpBody.getText());

        tblHttpHeader.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(tblHttpHeader.getInput());
    }

    protected void showEntityFieldsToUi() {
        String dispID = "";
        try {
            dispID = originalWsObject.getIdForDisplay();
        } catch (Exception ex) {
            MessageDialog
                    .openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, ex.getMessage());
        }
        txtID.setText(dispID);
        txtName.setText(originalWsObject.getName());
        txtDescription.setText(originalWsObject.getDescription());

        txtHttpBody.setText(originalWsObject.getHttpBody());

        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getHttpHeaderProperties());
        listHttpHeaderProps.clear();
        listHttpHeaderProps.addAll(tempPropList);
        tblHttpHeader.refresh();
    }

    protected void save() {
        try {
            String oldPk = originalWsObject.getId();
            String oldName = originalWsObject.getName();
            String oldIdForDisplay = originalWsObject.getIdForDisplay();

            updateEntityBeforeSaved();
            ObjectRepositoryController.getInstance().saveWebElement(originalWsObject);

            // Notify if name has changed
            if (!oldName.equalsIgnoreCase(txtName.getText())) {
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldIdForDisplay,
                        originalWsObject.getIdForDisplay() });
            }
            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { oldPk, originalWsObject });
            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
            dirtyable.setDirty(false);
        } catch (Exception e1) {
            MessageDialog
                    .openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e1.getMessage());
        }
    }

    public WebServiceRequestEntity getWSRequestObject() {
        return originalWsObject;
    }

    @Override
    public String getEntityId() {
        return getWSRequestObject().getIdForDisplay();
    }
}
