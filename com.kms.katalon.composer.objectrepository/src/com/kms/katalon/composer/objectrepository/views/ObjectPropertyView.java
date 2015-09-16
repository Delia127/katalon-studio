package com.kms.katalon.composer.objectrepository.views;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.control.ImageButton;
import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.objectrepository.constants.ImageConstants;
import com.kms.katalon.composer.objectrepository.constants.StringConstants;
import com.kms.katalon.composer.objectrepository.events.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.providers.IsSelectedColumnLabelProvider;
import com.kms.katalon.composer.objectrepository.providers.ObjectPropetiesTableViewer;
import com.kms.katalon.composer.objectrepository.support.PropertyConditionEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertyNameEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertySelectedEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertyValueEditingSupport;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.PathUtils;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropertyView {
    private static final String[] FILTER_NAMES = { "Image Files (*.gif,*.png,*.jpg)" };

    private static final String[] FILTER_EXTS = { "*.gif; *.png; *.jpg" };

    private Composite mainPage;

    private SashForm sash;

    private Composite layoutComposite, compositeInfo, compositeInfoDetails, compositeTable;

    private ToolItem toolItemAdd, toolItemDelete;

    private ObjectPropetiesTableViewer treeViewer;

    private ImageButton btnExpandGeneralInformation;

    private TableColumn trclmnColumnSelected;

    private FormLayout formLayout;

    private Text txtName;
    private Text txtId;
    private Text txtImage;
    private Button btnBrowseImage;

    private Button radRelative;

    private IEventBroker eventBroker;

    private MDirtyable dirtyable;

    private WebElementEntity originalTestObject;
    private WebElementEntity cloneTestObject;

    private Composite mainComposite;
    private Text txtDescriptions;
    private boolean isInfoCompositeExpanded = true;

    private Listener layoutGeneralCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutGeneralComposite();
        }
    };

    private Label lblGeneralInformation;

    public ObjectPropertyView(IEventBroker eventBroker, MDirtyable dt) {
        this.eventBroker = eventBroker;
        this.dirtyable = dt;

        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_DIRTY, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TableViewer) {
                    if (object.equals(treeViewer)) {
                        dirtyable.setDirty(true);
                    }
                }
            }
        });

        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TableViewer) {
                    if (object.equals(treeViewer)) {
                        boolean isSelectedAll = treeViewer.getIsSelectedAll();
                        Image isSelectedColumnImageHeader;
                        if (isSelectedAll) {
                            isSelectedColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_CHECKED;
                        } else {
                            isSelectedColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
                        }
                        trclmnColumnSelected.setImage(isSelectedColumnImageHeader);
                    }
                }
            }
        });
    }

    protected void layoutGeneralComposite() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                isInfoCompositeExpanded = !isInfoCompositeExpanded;

                compositeInfoDetails.setVisible(isInfoCompositeExpanded);
                if (!isInfoCompositeExpanded) {
                    ((GridData) compositeInfoDetails.getLayoutData()).exclude = true;
                    compositeInfo.setSize(compositeInfo.getSize().x,
                            compositeInfo.getSize().y - compositeTable.getSize().y);
                } else {
                    ((GridData) compositeInfoDetails.getLayoutData()).exclude = false;
                }
                compositeInfo.layout(true, true);
                compositeInfo.getParent().layout();
                redrawBtnExpandInfo();
            }

        });
    }

    void createLayout() {
        formLayout = new FormLayout();
        layoutComposite.setLayout(formLayout);
    }

    private void createTestObjectInfoComposite() {
        compositeInfo = new Composite(mainComposite, SWT.NONE);
        GridLayout gl_compositeInfo = new GridLayout(1, false);
        gl_compositeInfo.verticalSpacing = 0;
        gl_compositeInfo.marginWidth = 0;
        gl_compositeInfo.marginHeight = 0;
        compositeInfo.setLayout(gl_compositeInfo);
        compositeInfo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

        Composite compositeInfoHeader = new Composite(compositeInfo, SWT.NONE);
        compositeInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeInfoHeader = new GridLayout(2, false);
        gl_compositeInfoHeader.marginWidth = 0;
        gl_compositeInfoHeader.marginHeight = 0;
        compositeInfoHeader.setLayout(gl_compositeInfoHeader);
        compositeInfoHeader.setCursor(compositeInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandGeneralInformation = new ImageButton(compositeInfoHeader, SWT.NONE);
        redrawBtnExpandInfo();

        lblGeneralInformation = new Label(compositeInfoHeader, SWT.NONE);
        lblGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblGeneralInformation.setText(StringConstants.VIEW_LBL_INFO);
        lblGeneralInformation.setFont(JFaceResources.getFontRegistry().getBold(""));

        compositeInfoDetails = new Composite(compositeInfo, SWT.NONE);
        GridLayout gl_compositeInfoDetails = new GridLayout(2, true);
        gl_compositeInfoDetails.marginRight = 40;
        gl_compositeInfoDetails.marginLeft = 40;
        gl_compositeInfoDetails.marginBottom = 5;
        gl_compositeInfoDetails.horizontalSpacing = 30;
        gl_compositeInfoDetails.marginHeight = 0;
        gl_compositeInfoDetails.marginWidth = 0;
        compositeInfoDetails.setLayout(gl_compositeInfoDetails);
        compositeInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeInfoDetails.setBounds(0, 0, 64, 64);

        Composite compositeInfoNameAndId = new Composite(compositeInfoDetails, SWT.NONE);
        compositeInfoNameAndId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeInfoNameAndId = new GridLayout(2, false);
        glCompositeInfoNameAndId.verticalSpacing = 10;
        compositeInfoNameAndId.setLayout(glCompositeInfoNameAndId);

        Label lblId = new Label(compositeInfoNameAndId, SWT.NONE);
        lblId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblId.setText(StringConstants.VIEW_LBL_ID);

        txtId = new Text(compositeInfoNameAndId, SWT.BORDER);
        GridData gd_txtId = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_txtId.heightHint = 20;
        txtId.setLayoutData(gd_txtId);
        txtId.setEditable(false);

        Label lblName = new Label(compositeInfoNameAndId, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblName.setText(StringConstants.VIEW_LBL_NAME);

        txtName = new Text(compositeInfoNameAndId, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtName.heightHint = 20;
        txtName.setLayoutData(gdTxtName);

        Label lblImage = new Label(compositeInfoNameAndId, SWT.NONE);
        lblImage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblImage.setText(StringConstants.VIEW_LBL_IMAGE);

        txtImage = new Text(compositeInfoNameAndId, SWT.BORDER);
        gdTxtName.heightHint = 20;
        txtImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtImage.setEditable(false);

        Composite imageUtilComp = new Composite(compositeInfoNameAndId, SWT.NONE);
        imageUtilComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        GridLayout gl_imageUtilComp = new GridLayout(2, false);
        gl_imageUtilComp.marginHeight = 0;
        gl_imageUtilComp.marginWidth = 0;
        imageUtilComp.setLayout(gl_imageUtilComp);

        radRelative = new Button(imageUtilComp, SWT.CHECK);
        radRelative.setText(StringConstants.VIEW_CHKBOX_LBL_USE_RELATIVE_PATH);

        btnBrowseImage = new Button(imageUtilComp, SWT.NONE);
        btnBrowseImage.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        btnBrowseImage.setText(StringConstants.VIEW_BTN_BROWSE);
        btnBrowseImage.setToolTipText(StringConstants.VIEW_BTN_TIP_BROWSE);

        Composite compositeInfoDescriptions = new Composite(compositeInfoDetails, SWT.NONE);
        compositeInfoDescriptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeInfoDescriptions.setLayout(new GridLayout(2, false));

        Label lblDescription = new Label(compositeInfoDescriptions, SWT.NONE);
        GridData gd_lblDescription = new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1);
        gd_lblDescription.verticalIndent = 5;
        lblDescription.setLayoutData(gd_lblDescription);
        lblDescription.setText(StringConstants.VIEW_LBL_DESC);

        txtDescriptions = new Text(compositeInfoDescriptions, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdTextDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdTextDescription.heightHint = 45;
        txtDescriptions.setLayoutData(gdTextDescription);

        Label lblSupporter = new Label(compositeInfoDescriptions, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
    }

    private void createTableToolbar() {
        Composite compositeTableButtons = new Composite(compositeTable, SWT.NONE);
        compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeTableButtons.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tableToolbar = new ToolBar(compositeTableButtons, SWT.FLAT | SWT.RIGHT);

        toolItemAdd = new ToolItem(tableToolbar, SWT.NONE);
        toolItemAdd.setText(StringConstants.VIEW_LBL_ADD);
        toolItemAdd.setToolTipText(StringConstants.VIEW_LBL_ADD);
        toolItemAdd.setImage(ImageConstants.IMG_24_ADD);

        toolItemDelete = new ToolItem(tableToolbar, SWT.NONE);
        toolItemDelete.setText(StringConstants.VIEW_LBL_DELETE);
        toolItemDelete.setToolTipText(StringConstants.VIEW_LBL_DELETE);
        toolItemDelete.setImage(ImageConstants.IMG_24_REMOVE);
    }

    private void createTableDetails() {
        Composite compositeTableDetails = new Composite(compositeTable, SWT.NONE);
        GridLayout glCompositeTableDetails = new GridLayout(1, false);
        glCompositeTableDetails.marginWidth = 0;
        glCompositeTableDetails.marginHeight = 0;
        compositeTableDetails.setLayout(glCompositeTableDetails);
        compositeTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer = new ObjectPropetiesTableViewer(compositeTableDetails, SWT.BORDER | SWT.FULL_SELECTION, eventBroker);

        Table table = treeViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gridDataTable = new GridData(GridData.FILL_BOTH);
        gridDataTable.horizontalSpan = 3;
        gridDataTable.heightHint = 150;
        table.setLayoutData(gridDataTable);

        TableViewerColumn treeViewerColumnName = new TableViewerColumn(treeViewer, SWT.NONE);
        TableColumn trclmnColumnName = treeViewerColumnName.getColumn();
        trclmnColumnName.setText(StringConstants.VIEW_COL_NAME);
        trclmnColumnName.setWidth(100);
        treeViewerColumnName.setEditingSupport(new PropertyNameEditingSupport(treeViewer, eventBroker));
        treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });

        TableViewerColumn treeViewerColumnCondition = new TableViewerColumn(treeViewer, SWT.NONE);
        TableColumn trclmnColumnCondition = treeViewerColumnCondition.getColumn();
        trclmnColumnCondition.setText(StringConstants.VIEW_COL_MATCH_COND);
        trclmnColumnCondition.setWidth(150);
        treeViewerColumnCondition.setEditingSupport(new PropertyConditionEditingSupport(treeViewer, eventBroker));
        treeViewerColumnCondition.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getMatchCondition();
            }
        });

        TableViewerColumn treeViewerColumnValue = new TableViewerColumn(treeViewer, SWT.NONE);
        TableColumn trclmnColumnValue = treeViewerColumnValue.getColumn();
        trclmnColumnValue.setText(StringConstants.VIEW_COL_VALUE);
        trclmnColumnValue.setWidth(350);
        treeViewerColumnValue.setEditingSupport(new PropertyValueEditingSupport(treeViewer, eventBroker));
        treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });

        TableViewerColumn treeViewerColumnSelected = new TableViewerColumn(treeViewer, SWT.NONE);
        treeViewerColumnSelected.setEditingSupport(new PropertySelectedEditingSupport(treeViewer, eventBroker));
        treeViewerColumnSelected.setLabelProvider(new IsSelectedColumnLabelProvider());

        trclmnColumnSelected = treeViewerColumnSelected.getColumn();
        trclmnColumnSelected.setText(StringConstants.VIEW_COL_CHKBOX);
        trclmnColumnSelected.setWidth(120);

        treeViewer.setContentProvider(ArrayContentProvider.getInstance());
    }

    private void createTestObjectTableComposite() {
        compositeTable = new Composite(mainComposite, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTable = new GridLayout(1, false);
        glCompositeTable.marginHeight = 0;
        glCompositeTable.marginWidth = 0;
        compositeTable.setLayout(glCompositeTable);
        compositeTable.setBackground(ColorUtil.getCompositeBackgroundColor());

        createTableToolbar();

        createTableDetails();
    }

    private void hookControlSelectListerners() {
        btnExpandGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
        lblGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (cloneTestObject != null) {
                    cloneTestObject.setName(txtName.getText());
                    dirtyable.setDirty(true);
                }

            }
        });

        txtDescriptions.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (cloneTestObject != null) {
                    cloneTestObject.setDescription(txtDescriptions.getText());
                    dirtyable.setDirty(true);
                }
            }
        });

        txtImage.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (cloneTestObject != null) {
                    cloneTestObject.setImagePath(txtImage.getText());
                    dirtyable.setDirty(true);
                }
            }
        });

        radRelative.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (txtImage.getText() != null && !txtImage.getText().trim().equals("")) {
                        cloneTestObject.setUseRalativeImagePath(radRelative.getSelection());
                        String projectFolder = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                        String thePath = txtImage.getText();
                        if (radRelative.getSelection()) {
                            String relPath = PathUtils.absoluteToRelativePath(thePath, projectFolder);
                            txtImage.setText(relPath);
                        } else {
                            txtImage.setText(PathUtils.relativeToAbsolutePath(thePath, projectFolder));
                        }
                    }
                    dirtyable.setDirty(true);
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });

        toolItemDelete.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            public void widgetSelected(SelectionEvent e) {
                if (treeViewer.getTable().getSelection().length > 0) {
                    treeViewer.deleteRows(((IStructuredSelection) treeViewer.getSelection()).toList());
                    dirtyable.setDirty(true);
                }
            }
        });

        toolItemAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // set dialog's position is under btnAdd
                Shell shell = new Shell(Display.getCurrent());
                Point pt = treeViewer.getControl().toDisplay(1, 1);
                shell.setSize(0, 0);
                AddPropertyDialog dialog = new AddPropertyDialog(shell);
                shell.setLocation(pt.x + dialog.getInitialSize().x / 2 - 65, pt.y + dialog.getInitialSize().y / 2 + 20);

                int code = dialog.open();
                if (code == Window.OK) {
                    String propName = dialog.getName();
                    String propVal = dialog.getValue();
                    String condition = dialog.getCondition();

                    WebElementPropertyEntity prop = new WebElementPropertyEntity();
                    prop.setName(propName);
                    prop.setValue(propVal);
                    prop.setMatchCondition(condition);
                    prop.setIsSelected(true);

                    treeViewer.addRow(prop);
                    dirtyable.setDirty(true);
                }
            }
        });

        trclmnColumnSelected.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.setSelectedAll();
                dirtyable.setDirty(true);
            }
        });

        btnBrowseImage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String projectFolder = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                FileDialog dialog = new FileDialog(btnBrowseImage.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(projectFolder);

                String absolutePath = dialog.open();
                if (absolutePath == null)
                    return;
                if (radRelative.getSelection()) {
                    String relPath = PathUtils.absoluteToRelativePath(absolutePath, projectFolder);
                    txtImage.setText(relPath);
                } else {
                    txtImage.setText(absolutePath);
                }
                dirtyable.setDirty(true);
            }
        });
    }

    private void createControlGroup() {

        mainComposite = new Composite(sash, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        mainComposite.setLayout(glMainComposite);
        mainComposite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        createTestObjectInfoComposite();

        createTestObjectTableComposite();

        hookControlSelectListerners();
    }

    private void redrawBtnExpandInfo() {
        btnExpandGeneralInformation.getParent().setRedraw(false);
        if (isInfoCompositeExpanded) {
            btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandGeneralInformation.getParent().setRedraw(true);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createMainPage(Composite theParent) {
        // Create a two column page with a SashForm
        mainPage = new Composite(theParent, SWT.NULL);
        mainPage.setLayout(new FillLayout());
        sash = new SashForm(mainPage, SWT.HORIZONTAL);

        // Create the "layout" and "control" columns
        // createLayoutGroup();
        createControlGroup();
        txtName.setFocus();
        return mainPage;
    }

    public void changeOriginalTestObject(WebElementEntity object) {
        originalTestObject = object;
        cloneTestObject = originalTestObject.clone();
        copyObjectProperties(originalTestObject, cloneTestObject);
        loadTestObject();
        dirtyable.setDirty(!verifyObjectProperties());
    }

    private void loadTestObject() {
        try {
            String dispID = ObjectRepositoryController.getInstance().getIdForDisplay(cloneTestObject);
            txtId.setText(dispID);

            txtName.setText(cloneTestObject.getName());

            if (cloneTestObject.getDescription() != null) {
                txtDescriptions.setText(cloneTestObject.getDescription());
            }

            if (cloneTestObject.getImagePath() != null) {
                txtImage.setText(cloneTestObject.getImagePath());
            }

            radRelative.setSelection(cloneTestObject.getUseRalativeImagePath());

            treeViewer.setInput(new ArrayList<WebElementPropertyEntity>(cloneTestObject.getWebElementProperties()));
            treeViewer.refresh();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY);
        }
    }

    private boolean verifyObjectProperties() {
        for (int i = 0; i < treeViewer.getInput().size() - 1; i++) {
            if (!(treeViewer.getInput().get(i) instanceof WebElementPropertyEntity)) {
                continue;
            }
            for (int j = i + 1; j < treeViewer.getInput().size(); j++) {
                if (!(treeViewer.getInput().get(j) instanceof WebElementPropertyEntity)) {
                    continue;
                }
                if (treeViewer.getInput().get(i).equals(treeViewer.getInput().get(j))) {
                    WebElementPropertyEntity webElementProperty = treeViewer.getInput().get(i);
                    MessageDialog.openError(
                            Display.getCurrent().getActiveShell(),
                            StringConstants.ERROR_TITLE,
                            MessageFormat.format(StringConstants.VIEW_ERROR_REASON_OBJ_PROP_EXISTED,
                                    webElementProperty.getName()));
                    return false;
                }
            }
        }
        return true;
    }

    public void save() {
        if (treeViewer.isCellEditorActive()) {
            // if table has a active cell, commit the current editing
            treeViewer.getTable().forceFocus();
        }
        if (!verifyObjectProperties()) {
            return;
        }
        // copy properties
        cloneTestObject.getWebElementProperties().clear();
        cloneTestObject.getWebElementProperties().addAll(treeViewer.getInput());

        // back-up
        WebElementEntity temp = new WebElementEntity();
        copyObjectProperties(originalTestObject, temp);
        try {
            String pk = originalTestObject.getId();
            String oldIdForDisplay = ObjectRepositoryController.getInstance().getIdForDisplay(originalTestObject);

            cloneTestObject.getWebElementProperties().clear();
            for (Object o : treeViewer.getInput()) {
                WebElementPropertyEntity property = (WebElementPropertyEntity) o;
                cloneTestObject.getWebElementProperties().add(property);
            }

            copyObjectProperties(cloneTestObject, originalTestObject);

            ObjectRepositoryController.getInstance().saveWebElement(originalTestObject);
            changeOriginalTestObject(originalTestObject);

            if (!StringUtils.equalsIgnoreCase(temp.getName(), originalTestObject.getName())) {
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldIdForDisplay,
                        ObjectRepositoryController.getInstance().getIdForDisplay(originalTestObject) });
            }

            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { pk, originalTestObject });
            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
            dirtyable.setDirty(false);
        } catch (DuplicatedFileNameException dupplicatedEx) {
            copyObjectProperties(temp, originalTestObject);
            MultiStatusErrorDialog.showErrorDialog(dupplicatedEx,
                    StringConstants.VIEW_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ,
                    MessageFormat.format(StringConstants.VIEW_ERROR_REASON_OBJ_EXISTED, dupplicatedEx.getMessage()));
        } catch (Exception ex) {
            copyObjectProperties(temp, originalTestObject);
            MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.VIEW_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ,
                    ex.toString());
        }
    }

    private void copyObjectProperties(WebElementEntity src, WebElementEntity des) {
        des.setName(src.getName());
        des.setParentFolder(src.getParentFolder());
        des.setProject(src.getProject());

        des.setDescription(src.getDescription());

        des.getWebElementProperties().clear();
        des.getWebElementProperties().addAll(src.getWebElementProperties());

        des.setImagePath(src.getImagePath());
        des.setUseRalativeImagePath(src.getUseRalativeImagePath());
    }
}