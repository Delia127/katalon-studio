package com.kms.katalon.composer.objectrepository.view;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CMenu;
import com.kms.katalon.composer.components.impl.control.HotkeyActiveListener;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.objectrepository.constant.ComposerObjectRepositoryMessageConstants;
import com.kms.katalon.composer.objectrepository.constant.ImageConstants;
import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.part.TestObjectPart;
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
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropertyView implements EventHandler {

    private static final String HK_ADD = "M1+N";

    private static final String HK_DEL = "Delete";

    private static final String[] FILTER_NAMES = { "Image Files (*.gif,*.png,*.jpg)" };

    private static final String[] FILTER_EXTS = { "*.gif; *.png; *.jpg" };

    private ToolItem toolItemAdd, toolItemDelete, toolItemClear;

    private ObjectPropetiesTableViewer tableViewer;

    private ImageButton btnExpandIframeSetting;

    private TableColumn trclmnColumnSelected;

    private Text txtImage;

    private FormText txtParentObject, txtParentShadowRoot;

    private Button btnBrowseImage, chkUseRelative;

    private IEventBroker eventBroker;

    private MDirtyable dirtyable;

    private WebElementEntity originalTestObject, cloneTestObject;

    private boolean isSettingsExpanded = true;

    private Label lblSettings;

    private Composite compositeTable;

    private Button btnBrowseParentObj, btnBrowseParentShadowRoot, rdoUseParentObject, rdoShadowRootParent, rdoNoParent;

    private Composite compositeParentObject, compositeNoParent, compositeShadowRootParent, compositeSettingsDetails,
            compositeSettings;

    private ObjectViewToolItemListener toolItemListener;

    private String parentObjectId = null;

    private boolean isParentObjectShadowRoot = false;

    private TestObjectPart testObjectPart;

    private Listener layoutParentObjectCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutParentObjectComposite();
        }
    };

    public ObjectPropertyView(IEventBroker eventBroker, MDirtyable dt, TestObjectPart testObjectPart) {
        this.eventBroker = eventBroker;
        this.dirtyable = dt;
        this.toolItemListener = new ObjectViewToolItemListener();
        this.testObjectPart = testObjectPart;
        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
        eventBroker.subscribe(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
    }

    protected void layoutParentObjectComposite() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                isSettingsExpanded = !isSettingsExpanded;

                compositeSettingsDetails.setVisible(isSettingsExpanded);
                if (!isSettingsExpanded) {
                    ((GridData) compositeSettingsDetails.getLayoutData()).exclude = true;
                    compositeSettings.setSize(compositeSettings.getSize().x,
                            compositeSettings.getSize().y - compositeSettingsDetails.getSize().y);
                } else {
                    ((GridData) compositeSettingsDetails.getLayoutData()).exclude = false;
                }
                compositeSettings.layout(true, true);
                compositeSettings.getParent().layout();
                redrawBtnExpandParentObject();
            }
        });
    }

    private void createTableToolbar() {
        Composite compositeTableHeader = new Composite(compositeTable, SWT.NONE);
        compositeTableHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        FillLayout fl_compositeTableHeader = new FillLayout(SWT.HORIZONTAL);
        fl_compositeTableHeader.marginWidth = 5;
        compositeTableHeader.setLayout(fl_compositeTableHeader);

        Label lblObjectProperties = new Label(compositeTableHeader, SWT.NONE);
        lblObjectProperties.setText(StringConstants.VIEW_LBL_OBJ_PROPERTIES);
        ControlUtils.setFontToBeBold(lblObjectProperties);
        Composite compositeTableToolBar = new Composite(compositeTable, SWT.NONE);
        compositeTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeTableToolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tableToolbar = new ToolBar(compositeTableToolBar, SWT.FLAT | SWT.RIGHT);

        toolItemAdd = new ToolItem(tableToolbar, SWT.NONE);
        toolItemAdd.setText(StringConstants.VIEW_LBL_ADD);
        toolItemAdd.setToolTipText(StringConstants.VIEW_LBL_ADD);
        toolItemAdd.setImage(ImageConstants.IMG_16_ADD);

        toolItemDelete = new ToolItem(tableToolbar, SWT.NONE);
        toolItemDelete.setText(StringConstants.VIEW_LBL_DELETE);
        toolItemDelete.setToolTipText(StringConstants.VIEW_LBL_DELETE);
        toolItemDelete.setImage(ImageConstants.IMG_16_REMOVE);

        toolItemClear = new ToolItem(tableToolbar, SWT.NONE);
        toolItemClear.setText(StringConstants.VIEW_LBL_CLEAR);
        toolItemClear.setToolTipText(StringConstants.VIEW_LBL_CLEAR);
        toolItemClear.setImage(ImageConstants.IMG_16_CLEAR);
    }

    private void createTableDetails() {
        Composite compositeTableDetails = new Composite(compositeTable, SWT.NONE);
        compositeTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTableDetails = new GridLayout(1, false);
        glCompositeTableDetails.marginWidth = 0;
        glCompositeTableDetails.marginHeight = 0;
        compositeTableDetails.setLayout(glCompositeTableDetails);

        tableViewer = new ObjectPropetiesTableViewer(compositeTableDetails, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
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
        treeViewerColumnName
                .setEditingSupport(new PropertyNameEditingSupport(tableViewer, eventBroker, testObjectPart));
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
        treeViewerColumnCondition
                .setEditingSupport(new PropertyConditionEditingSupport(tableViewer, eventBroker, testObjectPart));
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
        treeViewerColumnValue
                .setEditingSupport(new PropertyValueEditingSupport(tableViewer, eventBroker, testObjectPart));
        treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });

        TableViewerColumn treeViewerColumnSelected = new TableViewerColumn(tableViewer, SWT.NONE);
        treeViewerColumnSelected
                .setEditingSupport(new PropertySelectedEditingSupport(tableViewer, eventBroker, testObjectPart));
        treeViewerColumnSelected.setLabelProvider(new IsSelectedColumnLabelProvider());

        trclmnColumnSelected = treeViewerColumnSelected.getColumn();
        trclmnColumnSelected.setText(StringConstants.VIEW_COL_CHKBOX);
        trclmnColumnSelected.setWidth(150);

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    }

    /**
     * Create menu like this
     * 
     * <pre>
     * -----------------------------
     * Add          Ctrl/Command + N
     * Delete       Delete
     * -----------------------------
     * Clear
     * -----------------------------
     * </pre>
     */
    private void createTableMenu() {
        Table table = tableViewer.getTable();
        CMenu menu = new CMenu(table, toolItemListener);
        table.setMenu(menu);

        menu.createMenuItem(StringConstants.VIEW_LBL_ADD, HK_ADD);
        menu.createMenuItem(StringConstants.VIEW_LBL_DELETE, HK_DEL, new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return isAbleToDelete();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        menu.createMenuItem(StringConstants.VIEW_LBL_CLEAR, null, new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return isAbleToClear();
            }
        });
    }

    private void createTestObjectDetailsComposite(Composite parent) {
        Composite compositeObjectDetails = new Composite(parent, SWT.NONE);
        compositeObjectDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        GridLayout glCompositeObjectDetails = new GridLayout(1, false);
        glCompositeObjectDetails.verticalSpacing = 15;
        glCompositeObjectDetails.marginHeight = 0;
        compositeObjectDetails.setLayout(glCompositeObjectDetails);
        compositeObjectDetails.setBackground(ColorUtil.getCompositeBackgroundColor());

        createSettingsComposite(compositeObjectDetails);

        compositeTable = new Composite(compositeObjectDetails, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTable = new GridLayout(1, false);
        glCompositeTable.marginWidth = 0;
        glCompositeTable.marginHeight = 0;
        compositeTable.setLayout(glCompositeTable);

        createTableToolbar();

        createTableDetails();

        createTableMenu();
    }

    private void createSettingsComposite(Composite parent) {
        compositeSettings = new Composite(parent, SWT.NONE);
        GridLayout glCompositeParentObject = new GridLayout(1, true);
        glCompositeParentObject.horizontalSpacing = 40;
        glCompositeParentObject.marginWidth = 0;
        glCompositeParentObject.marginHeight = 0;
        compositeSettings.setLayout(glCompositeParentObject);
        compositeSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Composite compositeSettingsHeader = new Composite(compositeSettings, SWT.NONE);
        GridData gd_compositeParentObjectHeader = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        compositeSettingsHeader.setLayoutData(gd_compositeParentObjectHeader);
        GridLayout glCompositeParentObjectHeader = new GridLayout(2, false);
        glCompositeParentObjectHeader.marginHeight = 0;
        glCompositeParentObjectHeader.marginWidth = 0;
        compositeSettingsHeader.setLayout(glCompositeParentObjectHeader);
        compositeSettingsHeader.setCursor(compositeSettingsHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandIframeSetting = new ImageButton(compositeSettingsHeader, SWT.PUSH);
        btnExpandIframeSetting.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        redrawBtnExpandParentObject();

        lblSettings = new Label(compositeSettingsHeader, SWT.NONE);
        lblSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblSettings.setText(StringConstants.VIEW_LBL_SETTINGS);
        ControlUtils.setFontToBeBold(lblSettings);

        compositeSettingsDetails = new Composite(compositeSettings, SWT.NONE);
        compositeSettingsDetails.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeParentObjectDetails = new GridLayout(2, true);
        glCompositeParentObjectDetails.marginWidth = 35;
        glCompositeParentObjectDetails.marginHeight = 0;
        glCompositeParentObjectDetails.verticalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        glCompositeParentObjectDetails.horizontalSpacing = 40;
        compositeSettingsDetails.setLayout(glCompositeParentObjectDetails);

        // Left column
        Composite compositeSettingsLeft = new Composite(compositeSettingsDetails, SWT.NONE);
        compositeSettingsLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeSettingsLeft.setLayout(new GridLayout(1, false));
        
        Group haveParentGroup = new Group(compositeSettingsLeft, SWT.NONE);
        haveParentGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        haveParentGroup.setLayout(new GridLayout(1, false));
        haveParentGroup.setText(ComposerObjectRepositoryMessageConstants.GRP_HAVE_PARENT_OBJECT);
        
        createCompositeNoParentObject(haveParentGroup);

        createCompositeParentObject(haveParentGroup);

        createCompositeShadowRootParent(haveParentGroup);

        // Right column
        Composite compositeSettingsRight = new Composite(compositeSettingsDetails, SWT.NONE);
        compositeSettingsRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeSettingsRight.setLayout(new GridLayout(3, false));

        new Label(compositeSettingsRight, SWT.NONE);
        chkUseRelative = new Button(compositeSettingsRight, SWT.CHECK);
        chkUseRelative.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        chkUseRelative.setText(StringConstants.VIEW_CHKBOX_LBL_USE_RELATIVE_PATH);

        Label lblImage = new Label(compositeSettingsRight, SWT.NONE);
        lblImage.setText(StringConstants.VIEW_LBL_IMAGE);

        txtImage = new Text(compositeSettingsRight, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtImage = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxtImage.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtImage.setLayoutData(gdTxtImage);
        txtImage.setEditable(false);

        btnBrowseImage = new Button(compositeSettingsRight, SWT.FLAT);
        btnBrowseImage.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
        btnBrowseImage.setText(StringConstants.VIEW_BTN_BROWSE);
        btnBrowseImage.setToolTipText(StringConstants.VIEW_BTN_TIP_BROWSE);
    }

    private void createCompositeShadowRootParent(Composite compositeSettingsLeft) {
        Composite compositeUseShadowRoot = new Composite(compositeSettingsLeft, SWT.NONE);
        compositeUseShadowRoot.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeUseShadowRoot = new GridLayout(2, false);
        glCompositeUseShadowRoot.marginWidth = 0;
        glCompositeUseShadowRoot.marginHeight = 0;
        compositeUseShadowRoot.setLayout(glCompositeUseShadowRoot);

        rdoShadowRootParent = new Button(compositeUseShadowRoot, SWT.RADIO);
        rdoShadowRootParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        rdoShadowRootParent.setText(ComposerObjectRepositoryMessageConstants.RDO_SHADOW_ROOT_PARENT);

        compositeShadowRootParent = new Composite(compositeUseShadowRoot, SWT.NONE);
        compositeShadowRootParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeShadowRootParent = new GridLayout(2, false);
        glCompositeShadowRootParent.marginWidth = 0;
        glCompositeShadowRootParent.marginHeight = 0;
        compositeShadowRootParent.setLayout(glCompositeShadowRootParent);

        txtParentShadowRoot = new FormText(compositeShadowRootParent, SWT.BORDER);
        GridData gdTxtParentObject = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdTxtParentObject.heightHint = 100;
        txtParentShadowRoot.setLayoutData(gdTxtParentObject);

        btnBrowseParentShadowRoot = new Button(compositeShadowRootParent, SWT.FLAT);
        btnBrowseParentShadowRoot.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, false, false, 1, 1));
        btnBrowseParentShadowRoot.setText(StringConstants.BROWSE);
    }

    private void createCompositeNoParentObject(Composite parentComposite) {
        compositeNoParent = new Composite(parentComposite, SWT.NONE);
        compositeNoParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glSettingsLeft = new GridLayout(1, false);
        glSettingsLeft.marginWidth = 0;
        glSettingsLeft.marginHeight = 0;
        compositeNoParent.setLayout(glSettingsLeft);

        rdoNoParent = new Button(compositeNoParent, SWT.RADIO);
        rdoNoParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        rdoNoParent.setText(ComposerObjectRepositoryMessageConstants.RDO_NO_PARENT);
    }

    private void createCompositeParentObject(Composite parentComposite) {
        Composite compositeUseParentIframe = new Composite(parentComposite, SWT.NONE);
        compositeUseParentIframe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeUseParentIframe = new GridLayout(2, false);
        glCompositeUseParentIframe.marginWidth = 0;
        glCompositeUseParentIframe.marginHeight = 0;
        compositeUseParentIframe.setLayout(glCompositeUseParentIframe);

        rdoUseParentObject = new Button(compositeUseParentIframe, SWT.RADIO);
        rdoUseParentObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        rdoUseParentObject.setText(StringConstants.VIEW_LBL_USE_IFRAME);

        compositeParentObject = new Composite(compositeUseParentIframe, SWT.NONE);
        compositeParentObject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeParentObject = new GridLayout(2, false);
        glCompositeParentObject.marginWidth = 0;
        glCompositeParentObject.marginHeight = 0;
        compositeParentObject.setLayout(glCompositeParentObject);

        txtParentObject = new FormText(compositeParentObject, SWT.BORDER);
        GridData gdTxtParentObject = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdTxtParentObject.heightHint = 100;
        txtParentObject.setLayoutData(gdTxtParentObject);

        btnBrowseParentObj = new Button(compositeParentObject, SWT.FLAT);
        btnBrowseParentObj.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, false, false, 1, 1));
        btnBrowseParentObj.setText(StringConstants.BROWSE);
    }

    private void hookControlSelectListerners() {
        btnExpandIframeSetting.addListener(SWT.MouseDown, layoutParentObjectCompositeListener);
        lblSettings.addListener(SWT.MouseDown, layoutParentObjectCompositeListener);

        txtImage.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (cloneTestObject != null) {
                    cloneTestObject.setImagePath(txtImage.getText());
                    setDirty(true);
                }
            }
        });

        chkUseRelative.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testObjectPart.executeOperation(new CheckImageRelativePathOperation());
            }
        });

        toolItemClear.addSelectionListener(toolItemListener);

        toolItemDelete.addSelectionListener(toolItemListener);

        toolItemAdd.addSelectionListener(toolItemListener);

        trclmnColumnSelected.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setSelectedAll();
                setDirty(true);
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
                testObjectPart.executeOperation(new ChangeImagePathOperation(absolutePath));
            }
        });

        btnBrowseParentObj.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSelectParentObject();
            }
        });

        btnBrowseParentShadowRoot.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSelectParentObject();
            }
        });

        rdoUseParentObject.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testObjectPart.executeOperation(new CheckUseParentObjectOperation());
            }
        });

        rdoNoParent.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testObjectPart.executeOperation(new CheckNoParentObjectOperation());
            }
        });

        rdoShadowRootParent.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testObjectPart.executeOperation(new CheckUseParentShadowRootObjectOperation());
            }
        });

        final HyperlinkAdapter hyperLinkListener = new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                openParentTestObject(e.getLabel());
            }
        };
        txtParentObject.addHyperlinkListener(hyperLinkListener);
        txtParentShadowRoot.addHyperlinkListener(hyperLinkListener);
    }

    private void setDirty(final boolean isDirty) {
        dirtyable.setDirty(isDirty);
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

            String currentParentObjectId = getParentObjectId();
            if (!StringUtils.isBlank(currentParentObjectId)) {
                WebElementEntity currentParentWebElement = ObjectRepositoryController.getInstance()
                        .getWebElementByDisplayPk(currentParentObjectId);
                if (currentParentWebElement != null) {
                    dialog.setInitialSelection(new WebElementTreeEntity(currentParentWebElement,
                            TreeEntityUtil.createSelectedTreeEntityHierachy(currentParentWebElement.getParentFolder(),
                                    objectRepoRootFolder)));
                }
            }

            if (dialog.open() == Dialog.OK) {
                Object selectedObject = dialog.getResult()[0];
                ITreeEntity treeEntity = (ITreeEntity) selectedObject;
                if (treeEntity.getObject() instanceof WebElementEntity) {
                    WebElementEntity parentObject = (WebElementEntity) treeEntity.getObject();
                    String parentObjectId = parentObject.getIdForDisplay();
                    testObjectPart.executeOperation(new ChangeParentTestObjectOperation(parentObjectId));
                    setDirty(true);
                }
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }

    }

    private void createControlGroup(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.verticalSpacing = 10;
        mainComposite.setLayout(glMainComposite);

        createTestObjectDetailsComposite(mainComposite);

        hookControlSelectListerners();
    }

    private void redrawBtnExpandParentObject() {
        btnExpandIframeSetting.getParent().setRedraw(false);
        if (isSettingsExpanded) {
            btnExpandIframeSetting.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandIframeSetting.setImage(ImageConstants.IMG_16_ARROW);
        }
        btnExpandIframeSetting.getParent().setRedraw(true);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createMainPage(Composite theParent) {
        // Create a two column page with a SashForm
        Composite mainPage = new Composite(theParent, SWT.NULL);
        mainPage.setLayout(new FillLayout());
        SashForm sash = new SashForm(mainPage, SWT.HORIZONTAL);

        // Create the "layout" and "control" columns
        // createLayoutGroup();
        createControlGroup(sash);
        return mainPage;
    }

    public void changeOriginalTestObject(WebElementEntity object) {
        originalTestObject = object;
        cloneTestObject = originalTestObject.clone();
        loadTestObject();
        dirtyable.setDirty(!verifyObjectProperties());
    }

    private void loadTestObject() {
        try {
            if (cloneTestObject.getImagePath() != null) {
                txtImage.setText(cloneTestObject.getImagePath());
            }

            chkUseRelative.setSelection(cloneTestObject.getUseRalativeImagePath());

            List<WebElementPropertyEntity> webElementProperties = new ArrayList<WebElementPropertyEntity>();

            WebElementPropertyEntity parentObjectProperty = null;
            WebElementPropertyEntity isParentShadowRootProperty = null;
            for (WebElementPropertyEntity webElementProperty : cloneTestObject.getWebElementProperties()) {
                if (WebElementEntity.ref_element.equals(webElementProperty.getName())) {
                    parentObjectProperty = webElementProperty;
                } else if (WebElementEntity.REF_ELEMENT_IS_SHADOW_ROOT.equals(webElementProperty.getName())) {
                    isParentShadowRootProperty = webElementProperty;
                } else {
                    webElementProperties.add(webElementProperty);
                }
            }

            setParentObjectId(parentObjectProperty != null && parentObjectProperty.getIsSelected()
                    ? parentObjectProperty.getValue() : null);
            setParentObjectShadowRoot(isParentShadowRootProperty != null && isParentShadowRootProperty.getIsSelected());

            refreshParentObjectComposites();

            tableViewer.setInput(webElementProperties);
            tableViewer.refresh();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY);
        }
    }

    private void refreshParentObjectComposites() {
        refreshParentObjectComposite(rdoUseParentObject, ParentObjectType.PARENT_IFRAME, compositeParentObject);
        refreshParentObjectComposite(rdoShadowRootParent, ParentObjectType.PARENT_SHADOW_ROOT,
                compositeShadowRootParent);
        refreshParentObjectComposite(rdoNoParent, ParentObjectType.NO_PARENT, null);

        ParentObjectType currentParentObjectType = getParentObjectType();
        switch (currentParentObjectType) {
            case PARENT_IFRAME:
                setLinkIdToFormText(getParentObjectId(), txtParentObject);
                clearFormText(txtParentShadowRoot);
                break;
            case PARENT_SHADOW_ROOT:
                clearFormText(txtParentObject);
                setLinkIdToFormText(getParentObjectId(), txtParentShadowRoot);
                break;
            case NO_PARENT:
                clearFormText(txtParentObject);
                clearFormText(txtParentShadowRoot);
            default:
                break;
        }
    }

    // private void enableParentObjectComposite(boolean enable) {
    // if (enable) {
    // setParentObjectId(parentObjectId);
    // setLinkIdToFormText(parentObjectId, txtParentObject);
    // } else {
    // txtParentObject.setText(parentObjectId, false, false);
    // }
    // ControlUtils.recursiveSetEnabled(compositeParentObject, enable);
    // }

    private void refreshParentObjectComposite(Button rdoButton, ParentObjectType parentObjectType,
            Composite composite) {
        ParentObjectType currentParentObjectType = getParentObjectType();
        rdoButton.setSelection(parentObjectType == currentParentObjectType);
        if (composite == null) {
            return;
        }
        ControlUtils.recursiveSetEnabled(composite, parentObjectType == currentParentObjectType);
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
                    MessageDialog.openError(null, StringConstants.WARN_TITLE, MessageFormat
                            .format(StringConstants.VIEW_ERROR_REASON_OBJ_PROP_EXISTED, webElementProperty.getName()));
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
        final List<WebElementPropertyEntity> webElementProperties = cloneTestObject.getWebElementProperties();
        webElementProperties.clear();
        webElementProperties.addAll(tableViewer.getInput());

        ParentObjectType parentObjectType = getParentObjectType();
        switch (parentObjectType) {
            case PARENT_IFRAME:
                if (!StringUtils.isBlank(getParentObjectId())) {
                    addElementProperty(webElementProperties, WebElementEntity.ref_element, getParentObjectId());
                    removeElementProperty(webElementProperties, WebElementEntity.REF_ELEMENT_IS_SHADOW_ROOT);
                }
                break;
            case PARENT_SHADOW_ROOT:
                if (!StringUtils.isBlank(getParentObjectId())) {
                    addElementProperty(webElementProperties, WebElementEntity.ref_element, getParentObjectId());
                    addElementProperty(webElementProperties, WebElementEntity.REF_ELEMENT_IS_SHADOW_ROOT, "true");
                }
                break;
            case NO_PARENT:
                removeElementProperty(webElementProperties, WebElementEntity.ref_element);
                removeElementProperty(webElementProperties, WebElementEntity.REF_ELEMENT_IS_SHADOW_ROOT);
                break;
            default:
                break;
        }

        // back-up
        WebElementEntity temp = new WebElementEntity();
        copyObjectProperties(originalTestObject, temp);
        try {
            String pk = originalTestObject.getId();
            String oldIdForDisplay = originalTestObject.getIdForDisplay();
            copyObjectProperties(cloneTestObject, originalTestObject);

            ObjectRepositoryController.getInstance().updateTestObject(originalTestObject);
            changeOriginalTestObject(originalTestObject);

            if (!StringUtils.equalsIgnoreCase(temp.getName(), originalTestObject.getName())) {
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM,
                        new Object[] { oldIdForDisplay, originalTestObject.getIdForDisplay() });
            }

            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { pk, originalTestObject });
            WebElementTreeEntity testObjectTreeEntity = TreeEntityUtil.getWebElementTreeEntity(originalTestObject,
                    ProjectController.getInstance().getCurrentProject());
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testObjectTreeEntity);
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

    private void addElementProperty(final List<WebElementPropertyEntity> webElementProperties, String propertyName,
            String propertyValue) {
        Optional<WebElementPropertyEntity> elementProperty = webElementProperties.parallelStream()
                .filter(webElementProperty -> webElementProperty.getName().equals(propertyName))
                .findAny();
        if (elementProperty.isPresent()) {
            elementProperty.get().setIsSelected(true);
            return;
        }
        webElementProperties.add(new WebElementPropertyEntity(propertyName, propertyValue, true));
    }

    private void removeElementProperty(final List<WebElementPropertyEntity> webElementProperties, String propertyName) {
        webElementProperties.parallelStream()
                .filter(webElementProperty -> webElementProperty.getName().equals(propertyName))
                .findAny()
                .ifPresent(elementProperty -> webElementProperties.remove(elementProperty));
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
                if (object != null && object instanceof TableViewer && !trclmnColumnSelected.isDisposed()) {
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
                            .replace(File.separator, StringConstants.ENTITY_ID_SEPARATOR);
                    if (oldTestObjectRelativeId.equals(getParentObjectId())) {
                        loadTestObject();
                        dirtyable.setDirty(false);
                    }
                }
            }
            default:
                break;
        }
    }

    private class ObjectViewToolItemListener extends SelectionAdapter implements HotkeyActiveListener {

        @Override
        public void executeAction(String actionId) {
            if (actionId == null) {
                return;
            }
            switch (ActionId.parse(actionId)) {
                case ADD: {
                    testObjectPart.executeOperation(new AddPropertyOperation());
                    break;
                }
                case DELETE: {
                    testObjectPart.executeOperation(
                            new RemovePropertyOperation(((IStructuredSelection) tableViewer.getSelection()).toList()));
                    break;
                }
                case CLEAR: {
                    testObjectPart.executeOperation(new ClearPropertyOperation());
                    break;
                }
            }
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Widget widget = e.widget;
            if (!(widget instanceof ToolItem)) {
                return;
            }
            executeAction(((ToolItem) widget).getText());
        }
    }

    private WebElementPropertyEntity openAddPropertyDialog() {
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

            return prop;
        }
        return null;
    }

    private boolean addProperty(WebElementPropertyEntity prop) {
        if (prop == null) {
            return false;
        }
        tableViewer.addRow(prop);
        dirtyable.setDirty(true);
        return true;
    }

    private boolean addPropertiesWithPosition(List<ObjectPropertyTableRow> props) {
        if (props == null || props.isEmpty()) {
            return false;
        }
        tableViewer.addRowsWithPosition(props);
        dirtyable.setDirty(true);
        return true;
    }

    private boolean addProperties(List<WebElementPropertyEntity> props) {
        if (props == null || props.isEmpty()) {
            return false;
        }
        tableViewer.addRows(props);
        dirtyable.setDirty(true);
        return true;
    }

    private boolean removeProperty(WebElementPropertyEntity prop) {
        if (prop == null) {
            return false;
        }
        tableViewer.deleteRow(prop);
        dirtyable.setDirty(true);
        return true;
    }

    private boolean removeProperties(List<WebElementPropertyEntity> props) {
        if (props == null || props.isEmpty()) {
            return false;
        }
        tableViewer.deleteRows(props);
        dirtyable.setDirty(true);
        return true;
    }

    public boolean isAbleToDelete() {
        return tableViewer.getTable().getSelection().length > 0;
    }

    public boolean isAbleToClear() {
        return tableViewer.getInput() != null && tableViewer.getInput().size() > 0;
    }

    public enum ActionId {
        ADD(StringConstants.VIEW_LBL_ADD),
        DELETE(StringConstants.VIEW_LBL_DELETE),
        CLEAR(StringConstants.VIEW_LBL_CLEAR);

        private final String id;

        private ActionId(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static ActionId parse(String id) {
            if (id == null) {
                return null;
            }
            for (ActionId actionId : values()) {
                if (actionId.getId().equals(id)) {
                    return actionId;
                }
            }
            return null;
        }
    }

    private void openParentTestObject(String currentParentObjectId) {
        WebElementEntity currentParentWebElement = getTestObjectPk(currentParentObjectId);
        if (currentParentWebElement == null) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                    ComposerObjectRepositoryMessageConstants.HAND_ERROR_MSG_TEST_OBJECT_NOT_EXIST);
            return;
        }
        eventBroker.send(EventConstants.TEST_OBJECT_OPEN, currentParentWebElement);
    }

    private WebElementEntity getTestObjectPk(String currentParentObjectId) {
        try {
            if (StringUtils.isNotBlank(currentParentObjectId)) {
                return ObjectRepositoryController.getInstance().getWebElementByDisplayPk(currentParentObjectId);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        return null;
    }

    private void setParentObjectId(String parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    private void setLinkIdToFormText(String parentObjectId, final FormText txtInput) {
        if (parentObjectId == null) {
            return;
        }
        txtInput.setText("<form><p><a>" + parentObjectId + "</a></p></form>", true, false);
    }

    private void clearFormText(final FormText txtInput) {
        if (txtInput == null || txtInput.isDisposed()) {
            return;
        }
        txtInput.setText("", false, false);
    }

    private String getParentObjectId() {
        return parentObjectId;
    }

    private boolean isParentObjectShadowRoot() {
        return isParentObjectShadowRoot;
    }

    private void setParentObjectShadowRoot(boolean isParentObjectShadowRoot) {
        this.isParentObjectShadowRoot = isParentObjectShadowRoot;
    }

    private ParentObjectType getParentObjectType() {
        if (getParentObjectId() != null && isParentObjectShadowRoot()) {
            return ParentObjectType.PARENT_SHADOW_ROOT;
        }
        if (getParentObjectId() != null) {
            return ParentObjectType.PARENT_IFRAME;
        }
        return ParentObjectType.NO_PARENT;
    }

    private class AddPropertyOperation extends AbstractOperation {

        WebElementPropertyEntity property;

        public AddPropertyOperation() {
            super(AddPropertyOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            property = openAddPropertyDialog();
            if (addProperty(property)) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (addProperty(property)) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (removeProperty(property)) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }
    }

    private class RemovePropertyOperation extends AbstractOperation {

        private List<ObjectPropertyTableRow> objectPropertyRows;

        public RemovePropertyOperation(List<WebElementPropertyEntity> properties) {
            super(RemovePropertyOperation.class.getName());
            objectPropertyRows = new ArrayList<>();
            for (WebElementPropertyEntity entity : properties) {
                objectPropertyRows.add(new ObjectPropertyTableRow(entity, tableViewer.getInput().indexOf(entity)));
            }
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (removeProperties(getPropertyListFromRows(objectPropertyRows))) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (removeProperties(getPropertyListFromRows(objectPropertyRows))) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (addPropertiesWithPosition(objectPropertyRows)) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }
    }

    private class ClearPropertyOperation extends AbstractOperation {

        List<WebElementPropertyEntity> properties = new ArrayList<>();

        public ClearPropertyOperation() {
            super(ClearPropertyOperation.class.getName());
            for (int i = 0; i < tableViewer.getInput().size(); ++i) {
                properties.add(tableViewer.getInput().get(i));
            }
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (properties.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            tableViewer.clear();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            tableViewer.clear();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (addProperties(properties)) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }
    }

    private class CheckNoParentObjectOperation extends AbstractOperation {
        private String parentObjectId = null;

        private boolean isParentShadowRoot = false;

        public CheckNoParentObjectOperation() {
            super(CheckNoParentObjectOperation.class.getName());
            parentObjectId = getParentObjectId();
            isParentShadowRoot = isParentObjectShadowRoot();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(null);
            setParentObjectShadowRoot(false);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(null);
            setParentObjectShadowRoot(false);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(parentObjectId);
            setParentObjectShadowRoot(isParentShadowRoot);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class CheckUseParentObjectOperation extends AbstractOperation {
        private boolean isParentShadowRoot = false;

        private String parentObjectId = null;

        public CheckUseParentObjectOperation() {
            super(CheckUseParentObjectOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            ParentObjectType parentObjectType = getParentObjectType();
            if (parentObjectType == ParentObjectType.PARENT_IFRAME) {
                return Status.CANCEL_STATUS;
            }
            isParentShadowRoot = isParentObjectShadowRoot();
            parentObjectId = getParentObjectId();
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (parentObjectId == null) {
                setParentObjectId("");
            }
            setParentObjectShadowRoot(false);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(parentObjectId);
            setParentObjectShadowRoot(isParentShadowRoot);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class CheckUseParentShadowRootObjectOperation extends AbstractOperation {
        private boolean isParentShadowRoot = false;
        
        private String parentObjectId = null;

        public CheckUseParentShadowRootObjectOperation() {
            super(CheckUseParentShadowRootObjectOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            ParentObjectType parentObjectType = getParentObjectType();
            if (parentObjectType == ParentObjectType.PARENT_SHADOW_ROOT) {
                return Status.CANCEL_STATUS;
            }
            isParentShadowRoot = isParentObjectShadowRoot();
            parentObjectId = getParentObjectId();
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (parentObjectId == null) {
                setParentObjectId("");
            }
            setParentObjectShadowRoot(true);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectShadowRoot(isParentShadowRoot);
            setParentObjectId(parentObjectId);
            refreshParentObjectComposites();
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class CheckImageRelativePathOperation extends AbstractOperation {

        boolean value;

        public CheckImageRelativePathOperation() {
            super(CheckImageRelativePathOperation.class.getName());
            value = chkUseRelative.getSelection();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doCheckImageRelativePath(value);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doCheckImageRelativePath(value);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doCheckImageRelativePath(!value);
        }

        public IStatus doCheckImageRelativePath(boolean value) {
            try {
                if (txtImage.getText() != null && !txtImage.getText().trim().equals("")) {
                    cloneTestObject.setUseRalativeImagePath(value);
                    String projectFolder = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                    String thePath = txtImage.getText();
                    if (value) {
                        String relPath = PathUtil.absoluteToRelativePath(thePath, projectFolder);
                        txtImage.setText(relPath);
                    } else {
                        txtImage.setText(PathUtil.relativeToAbsolutePath(thePath, projectFolder));
                    }
                    File file = new File(
                            value ? (projectFolder + File.separator + txtImage.getText()) : txtImage.getText());
                    if (!file.exists() || !file.isFile()) {
                        MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                                StringConstants.VIEW_WARN_FILE_NOT_FOUND);
                    }
                }
                chkUseRelative.setSelection(value);
                dirtyable.setDirty(true);
                return Status.OK_STATUS;
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
            return Status.CANCEL_STATUS;
        }
    }

    private class ChangeParentTestObjectOperation extends AbstractOperation {

        String oldParentId, newParentId;

        public ChangeParentTestObjectOperation(String value) {
            super(ChangeParentTestObjectOperation.class.getName());
            oldParentId = getParentObjectId();
            newParentId = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(newParentId);
            refreshParentObjectComposites();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(newParentId);
            refreshParentObjectComposites();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setParentObjectId(oldParentId);
            refreshParentObjectComposites();
            return Status.OK_STATUS;
        }
    }

    private class ChangeImagePathOperation extends AbstractOperation {

        String oldImagePath, newImagePath;

        public ChangeImagePathOperation(String value) {
            super(ChangeImagePathOperation.class.getName());
            oldImagePath = txtImage.getText();
            newImagePath = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetImagePath(newImagePath);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetImagePath(newImagePath);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetImagePath(oldImagePath);
        }

        public IStatus doSetImagePath(String path) {
            if (path == null)
                return Status.CANCEL_STATUS;
            if (chkUseRelative.getSelection()) {
                String relPath = PathUtil.absoluteToRelativePath(path,
                        ProjectController.getInstance().getCurrentProject().getFolderLocation());
                txtImage.setText(relPath);
            } else {
                txtImage.setText(path);
            }
            dirtyable.setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private List<WebElementPropertyEntity> getPropertyListFromRows(List<ObjectPropertyTableRow> objectPropertyRows) {
        List<WebElementPropertyEntity> propertyEntities = new ArrayList<>();
        for (ObjectPropertyTableRow row : objectPropertyRows) {
            propertyEntities.add(row.getWebElementPropertyEntity());
        }
        return propertyEntities;
    }

    private enum ParentObjectType {
        NO_PARENT, PARENT_IFRAME, PARENT_SHADOW_ROOT;
    }
}
