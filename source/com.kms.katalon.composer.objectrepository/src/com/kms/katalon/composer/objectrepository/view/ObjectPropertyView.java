package com.kms.katalon.composer.objectrepository.view;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.objectrepository.constant.ImageConstants;
import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.provider.IsSelectedColumnLabelProvider;
import com.kms.katalon.composer.objectrepository.provider.ObjectPropetiesTableViewer;
import com.kms.katalon.composer.objectrepository.provider.ParentObjectViewerFilter;
import com.kms.katalon.composer.objectrepository.support.PropertyConditionEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertyNameEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertySelectedEditingSupport;
import com.kms.katalon.composer.objectrepository.support.PropertyValueEditingSupport;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.PathUtils;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropertyView implements EventHandler {
    private static final String[] FILTER_NAMES = { "Image Files (*.gif,*.png,*.jpg)" };

    private static final String[] FILTER_EXTS = { "*.gif; *.png; *.jpg" };

    private Composite mainPage, mainComposite, layoutComposite;

    private Composite compositeInfo, compositeInfoDetails, compositeObjectDetails;

    private ToolItem toolItemAdd, toolItemDelete;

    private ObjectPropetiesTableViewer tableViewer;

    private ImageButton btnExpandGeneralInformation, btnExpandIframeSetting;

    private TableColumn trclmnColumnSelected;

    private Text txtName, txtId, txtImage, txtDescriptions, txtParentObject;

    private Button btnBrowseImage, chckUseRelative;

    private IEventBroker eventBroker;

    private MDirtyable dirtyable;

    private WebElementEntity originalTestObject, cloneTestObject;

    private boolean isInfoCompositeExpanded = true;
    private boolean isParentObjectCompositeExpanded = true;

    private Label lblGeneralInformation, lblParentObjectHeader;
    private Composite compositeTable;

    private Button btnBrowseParentObj, chckUseParentObject;

    private Composite compositeParentObjectArea, compositeParentObjectHeader, compositeParentObjectDetails,
            compositeParentObject;

    private Listener layoutGeneralCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutGeneralComposite();
        }
    };

    private Listener layoutParentObjectCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutParentObjectComposite();
        }
    };
    private Composite composite;

    public ObjectPropertyView(IEventBroker eventBroker, MDirtyable dt) {
        this.eventBroker = eventBroker;
        this.dirtyable = dt;

        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
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
                            compositeInfo.getSize().y - compositeObjectDetails.getSize().y);
                } else {
                    ((GridData) compositeInfoDetails.getLayoutData()).exclude = false;
                }
                compositeInfo.layout(true, true);
                compositeInfo.getParent().layout();
                redrawBtnExpandInfo();
            }

        });
    }

    protected void layoutParentObjectComposite() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                isParentObjectCompositeExpanded = !isParentObjectCompositeExpanded;

                compositeParentObjectDetails.setVisible(isParentObjectCompositeExpanded);
                if (!isParentObjectCompositeExpanded) {
                    ((GridData) compositeParentObjectDetails.getLayoutData()).exclude = true;
                    compositeParentObject.setSize(compositeParentObject.getSize().x, compositeParentObject.getSize().y
                            - compositeParentObjectDetails.getSize().y);
                } else {
                    ((GridData) compositeParentObjectDetails.getLayoutData()).exclude = false;
                }
                compositeParentObject.layout(true, true);
                compositeParentObject.getParent().layout();
                redrawBtnExpandParentObject();
            }
        });
    }

    void createLayout() {
        FormLayout formLayout = new FormLayout();
        layoutComposite.setLayout(formLayout);
    }

    private void createTestObjectInfoComposite() {
        compositeInfo = new Composite(mainComposite, SWT.NONE);
        GridLayout glCompositeInfo = new GridLayout(1, false);
        glCompositeInfo.verticalSpacing = 0;
        glCompositeInfo.marginWidth = 0;
        glCompositeInfo.marginHeight = 0;
        compositeInfo.setLayout(glCompositeInfo);
        compositeInfo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

        Composite compositeInfoHeader = new Composite(compositeInfo, SWT.NONE);
        compositeInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeInfoHeader = new GridLayout(2, false);
        glCompositeInfoHeader.marginWidth = 0;
        glCompositeInfoHeader.marginHeight = 0;
        compositeInfoHeader.setLayout(glCompositeInfoHeader);
        compositeInfoHeader.setCursor(compositeInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandGeneralInformation = new ImageButton(compositeInfoHeader, SWT.NONE);
        redrawBtnExpandInfo();

        lblGeneralInformation = new Label(compositeInfoHeader, SWT.NONE);
        lblGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblGeneralInformation.setText(StringConstants.VIEW_LBL_INFO);
        ControlUtils.setFontToBeBold(lblGeneralInformation);

        compositeInfoDetails = new Composite(compositeInfo, SWT.NONE);
        GridLayout glCompositeInfoDetails = new GridLayout(2, true);
        glCompositeInfoDetails.marginRight = 40;
        glCompositeInfoDetails.marginLeft = 40;
        glCompositeInfoDetails.horizontalSpacing = 30;
        glCompositeInfoDetails.marginHeight = 0;
        glCompositeInfoDetails.marginWidth = 0;
        compositeInfoDetails.setLayout(glCompositeInfoDetails);
        compositeInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeInfoNameAndId = new Composite(compositeInfoDetails, SWT.NONE);
        compositeInfoNameAndId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeInfoNameAndId = new GridLayout(2, false);
        glCompositeInfoNameAndId.verticalSpacing = ControlUtils.DF_VERTICAL_SPACING;
        glCompositeInfoNameAndId.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        compositeInfoNameAndId.setLayout(glCompositeInfoNameAndId);

        Label lblId = new Label(compositeInfoNameAndId, SWT.NONE);
        lblId.setText(StringConstants.VIEW_LBL_ID);

        txtId = new Text(compositeInfoNameAndId, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxt = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxt.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtId.setLayoutData(gdTxt);

        Label lblName = new Label(compositeInfoNameAndId, SWT.NONE);
        lblName.setText(StringConstants.VIEW_LBL_NAME);

        txtName = new Text(compositeInfoNameAndId, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxtName.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtName.setLayoutData(gdTxtName);

        Label lblImage = new Label(compositeInfoNameAndId, SWT.NONE);
        lblImage.setText(StringConstants.VIEW_LBL_IMAGE);

        txtImage = new Text(compositeInfoNameAndId, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtImage = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtImage.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtImage.setLayoutData(gdTxtImage);
        txtImage.setEditable(false);

        Composite imageUtilComp = new Composite(compositeInfoNameAndId, SWT.NONE);
        imageUtilComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        GridLayout glImageUtilComp = new GridLayout(2, false);
        glImageUtilComp.marginHeight = 0;
        glImageUtilComp.marginWidth = 0;
        imageUtilComp.setLayout(glImageUtilComp);

        chckUseRelative = new Button(imageUtilComp, SWT.CHECK);
        chckUseRelative.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        chckUseRelative.setText(StringConstants.VIEW_CHKBOX_LBL_USE_RELATIVE_PATH);

        btnBrowseImage = new Button(imageUtilComp, SWT.FLAT);
        btnBrowseImage.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        btnBrowseImage.setText(StringConstants.VIEW_BTN_BROWSE);
        btnBrowseImage.setToolTipText(StringConstants.VIEW_BTN_TIP_BROWSE);

        Composite compositeInfoDescriptions = new Composite(compositeInfoDetails, SWT.NONE);
        compositeInfoDescriptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeInfoDescriptions = new GridLayout(2, false);
        glCompositeInfoDescriptions.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        compositeInfoDescriptions.setLayout(glCompositeInfoDescriptions);

        Label lblDescription = new Label(compositeInfoDescriptions, SWT.NONE);
        GridData gd_lblDescription = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        gd_lblDescription.verticalIndent = 5;
        lblDescription.setLayoutData(gd_lblDescription);
        lblDescription.setText(StringConstants.VIEW_LBL_DESC);

        txtDescriptions = new Text(compositeInfoDescriptions, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdTextDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
        gdTextDescription.heightHint = 45;
        txtDescriptions.setLayoutData(gdTextDescription);
        new Label(compositeInfoDescriptions, SWT.NONE);
        new Label(compositeInfoDescriptions, SWT.NONE);
    }

    private void createTableToolbar() {
        Composite compositeTableHeader = new Composite(compositeTable, SWT.NONE);
        compositeTableHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        FillLayout fl_compositeTableHeader = new FillLayout(SWT.HORIZONTAL);
        fl_compositeTableHeader.marginWidth = 5;
        compositeTableHeader.setLayout(fl_compositeTableHeader);

        Label lblObjectProperties = new Label(compositeTableHeader, SWT.NONE);
        lblObjectProperties.setText("Object's Properties");
        ControlUtils.setFontToBeBold(lblObjectProperties);
        Composite compositeTableToolBar = new Composite(compositeTable, SWT.NONE);
        compositeTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeTableToolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tableToolbar = new ToolBar(compositeTableToolBar, SWT.FLAT | SWT.RIGHT);

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
        compositeTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTableDetails = new GridLayout(1, false);
        glCompositeTableDetails.marginWidth = 0;
        glCompositeTableDetails.marginHeight = 0;
        compositeTableDetails.setLayout(glCompositeTableDetails);

        tableViewer = new ObjectPropetiesTableViewer(compositeTableDetails, SWT.BORDER | SWT.FULL_SELECTION,
                eventBroker);

        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gridDataTable = new GridData(GridData.FILL_BOTH);
        gridDataTable.horizontalSpan = 3;
        gridDataTable.heightHint = 150;
        table.setLayoutData(gridDataTable);

        TableViewerColumn treeViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn trclmnColumnName = treeViewerColumnName.getColumn();
        trclmnColumnName.setText(StringConstants.VIEW_COL_NAME);
        trclmnColumnName.setWidth(100);
        treeViewerColumnName.setEditingSupport(new PropertyNameEditingSupport(tableViewer, eventBroker));
        treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });

        TableViewerColumn treeViewerColumnCondition = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn trclmnColumnCondition = treeViewerColumnCondition.getColumn();
        trclmnColumnCondition.setText(StringConstants.VIEW_COL_MATCH_COND);
        trclmnColumnCondition.setWidth(150);
        treeViewerColumnCondition.setEditingSupport(new PropertyConditionEditingSupport(tableViewer, eventBroker));
        treeViewerColumnCondition.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getMatchCondition();
            }
        });

        TableViewerColumn treeViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn trclmnColumnValue = treeViewerColumnValue.getColumn();
        trclmnColumnValue.setText(StringConstants.VIEW_COL_VALUE);
        trclmnColumnValue.setWidth(350);
        treeViewerColumnValue.setEditingSupport(new PropertyValueEditingSupport(tableViewer, eventBroker));
        treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });

        TableViewerColumn treeViewerColumnSelected = new TableViewerColumn(tableViewer, SWT.NONE);
        treeViewerColumnSelected.setEditingSupport(new PropertySelectedEditingSupport(tableViewer, eventBroker));
        treeViewerColumnSelected.setLabelProvider(new IsSelectedColumnLabelProvider());

        trclmnColumnSelected = treeViewerColumnSelected.getColumn();
        trclmnColumnSelected.setText(StringConstants.VIEW_COL_CHKBOX);
        trclmnColumnSelected.setWidth(150);

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    }

    private void createTestObjectDetailsComposite() {
        compositeObjectDetails = new Composite(mainComposite, SWT.NONE);
        compositeObjectDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        GridLayout glCompositeObjectDetails = new GridLayout(1, false);
        glCompositeObjectDetails.verticalSpacing = 15;
        glCompositeObjectDetails.marginHeight = 0;
        compositeObjectDetails.setLayout(glCompositeObjectDetails);
        compositeObjectDetails.setBackground(ColorUtil.getCompositeBackgroundColor());

        createParentObjectComposite();

        compositeTable = new Composite(compositeObjectDetails, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTable = new GridLayout(1, false);
        glCompositeTable.marginWidth = 0;
        glCompositeTable.marginHeight = 0;
        compositeTable.setLayout(glCompositeTable);

        createTableToolbar();

        createTableDetails();
    }

    private void createParentObjectComposite() {
        compositeParentObject = new Composite(compositeObjectDetails, SWT.NONE);
        GridLayout glCompositeParentObject = new GridLayout(1, true);
        glCompositeParentObject.horizontalSpacing = 40;
        glCompositeParentObject.marginWidth = 0;
        glCompositeParentObject.marginHeight = 0;
        compositeParentObject.setLayout(glCompositeParentObject);
        compositeParentObject.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        compositeParentObjectHeader = new Composite(compositeParentObject, SWT.NONE);
        GridData gd_compositeParentObjectHeader = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd_compositeParentObjectHeader.horizontalIndent = -5;
        compositeParentObjectHeader.setLayoutData(gd_compositeParentObjectHeader);
        GridLayout glCompositeParentObjectHeader = new GridLayout(2, false);
        glCompositeParentObjectHeader.marginHeight = 0;
        glCompositeParentObjectHeader.marginWidth = 0;
        compositeParentObjectHeader.setLayout(glCompositeParentObjectHeader);
        compositeParentObjectHeader
                .setCursor(compositeParentObjectHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandIframeSetting = new ImageButton(compositeParentObjectHeader, SWT.PUSH);
        redrawBtnExpandParentObject();

        lblParentObjectHeader = new Label(compositeParentObjectHeader, SWT.NONE);
        lblParentObjectHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblParentObjectHeader.setText("Settings");
        ControlUtils.setFontToBeBold(lblParentObjectHeader);

        compositeParentObjectDetails = new Composite(compositeParentObject, SWT.NONE);
        compositeParentObjectDetails.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeParentObjectDetails = new GridLayout(2, true);
        glCompositeParentObjectDetails.marginWidth = 35;
        glCompositeParentObjectDetails.marginHeight = 0;
        glCompositeParentObjectDetails.verticalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        glCompositeParentObjectDetails.horizontalSpacing = 40;
        compositeParentObjectDetails.setLayout(glCompositeParentObjectDetails);

        chckUseParentObject = new Button(compositeParentObjectDetails, SWT.CHECK);
        chckUseParentObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        chckUseParentObject.setText(StringConstants.VIEW_LBL_USE_IFRAME);

        compositeParentObjectArea = new Composite(compositeParentObjectDetails, SWT.NONE);
        compositeParentObjectArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeParentObjectArea = new GridLayout(2, false);
        glCompositeParentObjectArea.horizontalSpacing = 30;
        glCompositeParentObjectArea.marginHeight = 0;
        glCompositeParentObjectArea.marginWidth = 0;
        compositeParentObjectArea.setLayout(glCompositeParentObjectArea);

        Label lblParentObjectID = new Label(compositeParentObjectArea, SWT.NONE);
        GridData gdLblParentObjectID = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblParentObjectID.horizontalIndent = 5;
        lblParentObjectID.setLayoutData(gdLblParentObjectID);
        lblParentObjectID.setText(StringConstants.DIA_FIELD_TEST_OBJECT_ID);

        composite = new Composite(compositeParentObjectArea, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        composite.setLayout(glComposite);

        txtParentObject = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtParentObject = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxtParentObject.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtParentObject.setLayoutData(gdTxtParentObject);

        btnBrowseParentObj = new Button(composite, SWT.FLAT);
        btnBrowseParentObj.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnBrowseParentObj.setText(StringConstants.BROWSE);
        new Label(compositeParentObjectDetails, SWT.NONE);
    }

    private void hookControlSelectListerners() {
        btnExpandGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
        lblGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);

        btnExpandIframeSetting.addListener(SWT.MouseDown, layoutParentObjectCompositeListener);
        lblParentObjectHeader.addListener(SWT.MouseDown, layoutParentObjectCompositeListener);

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

        chckUseRelative.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (txtImage.getText() != null && !txtImage.getText().trim().equals("")) {
                        cloneTestObject.setUseRalativeImagePath(chckUseRelative.getSelection());
                        String projectFolder = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                        String thePath = txtImage.getText();
                        if (chckUseRelative.getSelection()) {
                            String relPath = PathUtils.absoluteToRelativePath(thePath, projectFolder);
                            txtImage.setText(relPath);
                        } else {
                            txtImage.setText(PathUtils.relativeToAbsolutePath(thePath, projectFolder));
                        }
                        
                        File file = new File(thePath);
                        if (!file.exists() || !file.isFile()) {
                            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                                    StringConstants.VIEW_WARN_FILE_NOT_FOUND);
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
                if (tableViewer.getTable().getSelection().length > 0) {
                    tableViewer.deleteRows(((IStructuredSelection) tableViewer.getSelection()).toList());
                    dirtyable.setDirty(true);
                }
            }
        });

        toolItemAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // set dialog's position is under btnAdd
                Shell shell = new Shell(Display.getCurrent());
                Point pt = tableViewer.getControl().toDisplay(1, 1);
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

                    tableViewer.addRow(prop);
                    dirtyable.setDirty(true);
                }
            }
        });

        trclmnColumnSelected.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setSelectedAll();
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
                if (absolutePath == null) return;
                if (chckUseRelative.getSelection()) {
                    String relPath = PathUtils.absoluteToRelativePath(absolutePath, projectFolder);
                    txtImage.setText(relPath);
                } else {
                    txtImage.setText(absolutePath);
                }
                dirtyable.setDirty(true);
            }
        });

        btnBrowseParentObj.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSelectParentObject();
            }
        });

        chckUseParentObject.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableParentObjectComposite(chckUseParentObject.getSelection());
                dirtyable.setDirty(true);
            }
        });
    }

    private void enableParentObjectComposite(boolean enable) {
        ControlUtils.recursiveSetEnabled(compositeParentObjectArea, enable);
    }

    private void performSelectParentObject() {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return;
            }

            EntityProvider entityProvider = new EntityProvider();
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(null, new EntityLabelProvider(),
                    new EntityProvider(), new ParentObjectViewerFilter(entityProvider, originalTestObject));

            FolderEntity objectRepoRootFolder = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
            dialog.setAllowMultiple(false);
            dialog.setTitle(StringConstants.VIEW_TEST_OBJECT_BROWSE);
            dialog.setInput(TreeEntityUtil.getChildren(null, objectRepoRootFolder));

            String currentParentObjectId = txtParentObject.getText();
            if (!StringUtils.isBlank(currentParentObjectId)) {
                WebElementEntity currentParentWebElement = ObjectRepositoryController.getInstance()
                        .getWebElementByDisplayPk(currentParentObjectId);
                if (currentParentWebElement != null) {
                    dialog.setInitialSelection(new WebElementTreeEntity(currentParentWebElement, TreeEntityUtil
                            .createSelectedTreeEntityHierachy(currentParentWebElement.getParentFolder(),
                                    objectRepoRootFolder)));
                }
            }

            if (dialog.open() == Dialog.OK) {
                Object selectedObject = dialog.getResult()[0];
                ITreeEntity treeEntity = (ITreeEntity) selectedObject;
                if (treeEntity.getObject() instanceof WebElementEntity) {
                    WebElementEntity parentObject = (WebElementEntity) treeEntity.getObject();
                    String parentObjectId = ObjectRepositoryController.getInstance().getIdForDisplay(parentObject);
                    txtParentObject.setText(parentObjectId);
                    dirtyable.setDirty(true);
                }
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }

    }

    private void createControlGroup(Composite parent) {

        mainComposite = new Composite(parent, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.verticalSpacing = 10;
        mainComposite.setLayout(glMainComposite);
        mainComposite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        createTestObjectInfoComposite();

        createTestObjectDetailsComposite();

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

    private void redrawBtnExpandParentObject() {
        btnExpandIframeSetting.getParent().setRedraw(false);
        if (isParentObjectCompositeExpanded) {
            btnExpandIframeSetting.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandIframeSetting.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandIframeSetting.getParent().setRedraw(true);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createMainPage(Composite theParent) {
        // Create a two column page with a SashForm
        mainPage = new Composite(theParent, SWT.NULL);
        mainPage.setLayout(new FillLayout());
        SashForm sash = new SashForm(mainPage, SWT.HORIZONTAL);

        // Create the "layout" and "control" columns
        // createLayoutGroup();
        createControlGroup(sash);
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

            chckUseRelative.setSelection(cloneTestObject.getUseRalativeImagePath());

            List<WebElementPropertyEntity> webElementProperties = new ArrayList<WebElementPropertyEntity>();

            WebElementPropertyEntity parentObjectProperty = null;
            for (WebElementPropertyEntity webElementProperty : cloneTestObject.getWebElementProperties()) {
                if (WebElementEntity.ref_element.equals(webElementProperty.getName())) {
                    parentObjectProperty = webElementProperty;
                } else {
                    webElementProperties.add(webElementProperty);
                }
            }

            if (parentObjectProperty != null) {
                chckUseParentObject.setSelection(parentObjectProperty.getIsSelected());
                txtParentObject.setText(parentObjectProperty.getValue());
            } else {
                chckUseParentObject.setSelection(false);
                txtParentObject.setText("");
            }
            enableParentObjectComposite(chckUseParentObject.getSelection());

            tableViewer.setInput(webElementProperties);
            tableViewer.refresh();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY);
        }
    }

    private boolean verifyObjectProperties() {
        for (int i = 0; i < tableViewer.getInput().size() - 1; i++) {
            if (!(tableViewer.getInput().get(i) instanceof WebElementPropertyEntity)) {
                continue;
            }
            for (int j = i + 1; j < tableViewer.getInput().size(); j++) {
                if (!(tableViewer.getInput().get(j) instanceof WebElementPropertyEntity)) {
                    continue;
                }
                if (tableViewer.getInput().get(i).equals(tableViewer.getInput().get(j))) {
                    WebElementPropertyEntity webElementProperty = tableViewer.getInput().get(i);
                    MessageDialog.openError(
                            null,
                            StringConstants.WARN_TITLE,
                            MessageFormat.format(StringConstants.VIEW_ERROR_REASON_OBJ_PROP_EXISTED,
                                    webElementProperty.getName()));
                    return false;
                }
            }
        }
        return true;
    }

    public void save() {
        if (tableViewer.isCellEditorActive()) {
            // if table has a active cell, commit the current editing
            tableViewer.getTable().forceFocus();
        }

        if (!verifyObjectProperties()) {
            return;
        }

        // prepare properties
        cloneTestObject.getWebElementProperties().clear();
        cloneTestObject.getWebElementProperties().addAll(tableViewer.getInput());

        if (chckUseParentObject.getSelection() || !StringUtils.isBlank(txtParentObject.getText())) {
            WebElementPropertyEntity parentObjectProperty = new WebElementPropertyEntity();
            parentObjectProperty.setName(WebElementEntity.ref_element);
            parentObjectProperty.setValue(txtParentObject.getText());
            parentObjectProperty.setIsSelected(chckUseParentObject.getSelection());
            cloneTestObject.getWebElementProperties().add(parentObjectProperty);
        }

        // back-up
        WebElementEntity temp = new WebElementEntity();
        copyObjectProperties(originalTestObject, temp);
        try {
            String pk = originalTestObject.getId();
            String oldIdForDisplay = ObjectRepositoryController.getInstance().getIdForDisplay(originalTestObject);
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
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
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

    @PreDestroy
    public void preDestroy() {
        eventBroker.unsubscribe(this);
    }

    @Override
    public void handleEvent(Event event) {
        String topic = event.getTopic();
        Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        switch (topic) {
            case ObjectEventConstants.OBJECT_UPDATE_DIRTY: {
                if (object != null && object instanceof TableViewer) {
                    if (object.equals(tableViewer)) {
                        dirtyable.setDirty(true);
                    }
                }
            }
            case ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER: {
                if (object != null && object instanceof TableViewer) {
                    if (object.equals(tableViewer)) {
                        boolean isSelectedAll = tableViewer.getIsSelectedAll();
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
            case (EventConstants.TEST_OBJECT_UPDATED): {
                // Check if the referred object is updated.
                if (object != null && object instanceof Object[]) {
                    Object[] objects = (Object[]) object;
                    String testObjectId = (String) objects[0];
                    String projectFolderId = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                    String oldTestObjectRelativeId = testObjectId.replace(projectFolderId + File.separator, "")
                            .replace(WebElementEntity.getWebElementFileExtension(), "")
                            .replace(File.separator, StringConstants.ENTITY_ID_SEPERATOR);
                    if (oldTestObjectRelativeId.equals(txtParentObject.getText())) {
                        loadTestObject();
                        dirtyable.setDirty(false);
                    }
                }
            }
            default:
                break;
        }

    }
}