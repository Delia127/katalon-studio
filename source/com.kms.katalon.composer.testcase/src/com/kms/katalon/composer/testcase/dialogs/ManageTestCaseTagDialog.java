package com.kms.katalon.composer.testcase.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.preferences.TestCaseSettingStore;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("unchecked")
public class ManageTestCaseTagDialog extends Dialog {

    public static final int CM_APPEND_TAGS = 1001;

    private TestCaseSettingStore store;

    private TableViewer tagTableViewer;

    private List<TagTableViewerItem> tagItems;

    private Set<String> currentTestCaseTags;

    private Set<String> appendedTags;

    private Text txtSearch;

    private Button btnAppendTags;

    private Button btnClose;

    public ManageTestCaseTagDialog(Shell parentShell, Set<String> currentTestCaseTags) {
        super(parentShell);
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        store = new TestCaseSettingStore(project.getFolderLocation());
        this.currentTestCaseTags = currentTestCaseTags;
        initializeTagInput();
    }

    private void initializeTagInput() {
        try {
            Set<String> allTagsInProject = store.getTestCaseTags();
            Set<String> inputTags = new HashSet<>();
            inputTags.addAll(allTagsInProject);
            inputTags.addAll(currentTestCaseTags);
            tagItems = inputTags.stream().map(tag -> {
                TagTableViewerItem item = new TagTableViewerItem();
                item.setTagName(tag);
                if (currentTestCaseTags.contains(tag)) {
                    item.setSelected(true);
                    item.setEditable(false);
                } else {
                    item.setSelected(false);
                    item.setEditable(true);
                }
                return item;
            })
            .sorted((item1, item2) -> StringUtils.compareIgnoreCase(item1.getTagName(), item2.getTagName()))
            .collect(Collectors.toList());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            tagItems = new ArrayList<>();
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(1, false));
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 300;
        gdBody.heightHint = 350;
        body.setLayoutData(gdBody);

        txtSearch = new Text(body, SWT.BORDER);
        txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtSearch.setMessage(ComposerTestcaseMessageConstants.ManageTestCaseTagDialog_SEARCH_MSG);

        Composite tableComposite = new Composite(body, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tagTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        tagTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table tagTable = tagTableViewer.getTable();
        tagTable.setHeaderVisible(true);
        tagTable.setLinesVisible(true);
        ColumnViewerUtil.setTableActivation(tagTableViewer);

        TableViewerColumn tableViewerColumnTagName = new TableViewerColumn(tagTableViewer, SWT.LEFT);
        TableColumn tableColumnTagName = tableViewerColumnTagName.getColumn();
        tableColumnTagName.setText(ComposerTestcaseMessageConstants.ManageTestCaseTagDialog_TAG_TABLE_COL_TAG);
        tableViewerColumnTagName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                TagTableViewerItem item = (TagTableViewerItem) element;
                return item.getTagName();
            }
        });

        TableViewerColumn tableViewerColumnIsSelected = new TableViewerColumn(tagTableViewer, SWT.LEFT);
        TableColumn tableColumnIsSelected = tableViewerColumnIsSelected.getColumn();
        tableColumnIsSelected.setText(ComposerTestcaseMessageConstants.ManageTestCaseTagDialog_TAG_TABLE_COL_SELECTED);
        tableViewerColumnIsSelected.setLabelProvider(new TagSelectionColumnLabelProvider());
        tableViewerColumnIsSelected.setEditingSupport(new TagSelectionColumnEditingSupport(tagTableViewer));

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tableColumnTagName, new ColumnWeightData(70, 20));
        tableLayout.setColumnData(tableColumnIsSelected, new ColumnWeightData(30, 10));
        tableComposite.setLayout(tableLayout);

        tagTableViewer.setInput(tagItems);

        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(2, false));
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));

        btnClose = new Button(buttonComposite, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        btnClose.setText(IDialogConstants.CLOSE_LABEL);

        btnAppendTags = new Button(buttonComposite, SWT.NONE);
        btnAppendTags.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        btnAppendTags.setText(ComposerTestcaseMessageConstants.ManageTestCaseTagDialog_BTN_APPEND_TAGS);

        registerControlEventListeners();

        return body;
    }

    private void registerControlEventListeners() {
        txtSearch.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                handleSearchTags();
            }
        });

        btnAppendTags.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleAppendTags();
            };
        });

        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            };
        });
    }

    private void handleSearchTags() {
        String searchText = txtSearch.getText().trim();
        if (!StringUtils.isBlank(searchText)) {
            List<TagTableViewerItem> matchedItems = tagItems.stream()
                    .filter(item -> item.getTagName().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
            tagTableViewer.setInput(matchedItems);
        } else {
            tagTableViewer.setInput(tagItems);
        }
        tagTableViewer.refresh();
    }

    private void handleAppendTags() {
        List<TagTableViewerItem> tagItems = (List<TagTableViewerItem>) tagTableViewer.getInput();
        appendedTags = tagItems.stream()
                .filter(tagItem -> tagItem.isSelected() && !currentTestCaseTags.contains(tagItem.getTagName()))
                .map(tagItem -> tagItem.getTagName()).collect(Collectors.toSet());
        setReturnCode(CM_APPEND_TAGS);
        close();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(ComposerTestcaseMessageConstants.ManageTestCaseTagDialog_DIA_TITLE);
    }

    public Set<String> getAppendedTags() {
        return appendedTags;
    }

    private class TagSelectionColumnLabelProvider extends StyledCellLabelProvider {
        @Override
        protected void paint(Event event, Object element) {
            GC gc = event.gc;
            TagTableViewerItem tagItem = (TagTableViewerItem) element;
            if (tagItem.isSelected()) {
                gc.drawImage(ImageConstants.IMG_16_CHECKBOX_CHECKED, event.getBounds().x + 5, event.getBounds().y);
            } else {
                gc.drawImage(ImageConstants.IMG_16_CHECKBOX_UNCHECKED, event.getBounds().x + 5, event.getBounds().y);
            }
        }
    }

    private class TagSelectionColumnEditingSupport extends EditingSupport {
        private TableViewer viewer;
        private CheckboxCellEditor editor;

        public TagSelectionColumnEditingSupport(TableViewer viewer) {
            super(viewer);
            this.viewer = viewer;
            editor = new CheckboxCellEditor(this.viewer.getTable());
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
            return ((TagTableViewerItem) element).isEditable();
        }

        @Override
        protected Object getValue(Object element) {
            return ((TagTableViewerItem) element).isSelected();
        }

        @Override
        protected void setValue(Object element, Object value) {
            ((TagTableViewerItem) element).setSelected((boolean) value);
            viewer.refresh(element);
        }
    }

    private class TagTableViewerItem {
        private String tagName;

        private boolean isSelected;

        private boolean isEditable;

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String name) {
            this.tagName = name;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public boolean isEditable() {
            return isEditable;
        }

        public void setEditable(boolean isEditable) {
            this.isEditable = isEditable;
        }
    }
}
