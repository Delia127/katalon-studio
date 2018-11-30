package com.kms.katalon.composer.testcase.dialogs;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.TestCaseSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;

public class ManageTestCaseTagDialog extends Dialog {
    
    private Set<String> tags;
    
    private TableViewer tagTable;

    private ToolItem tiAddTag;

    private ToolItem tiRemoveTag;

    private ToolItem tiEditTag;
    
    private TestCaseSettingStore store = getStore();

    public ManageTestCaseTagDialog(Shell parentShell) {
        super(parentShell);
        loadTags();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 400;
        gdBody.heightHint = 500;
        body.setLayoutData(gdBody);
        body.setLayout(new GridLayout(1, false));
      
        createToolBar(body);
        createTagTable(body);
        
        return super.createDialogArea(parent);
    }
    
    private void createToolBar(Composite parent) {
        Composite toolbarComposite = new Composite(parent, SWT.NONE);
        toolbarComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
       
        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        
        tiAddTag = new ToolItem(toolBar, SWT.NONE);
        tiAddTag.setText("Add");
        tiAddTag.setImage(ImageConstants.IMG_16_ADD);
        tiAddTag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addTag();
            }
        });
        
        tiRemoveTag = new ToolItem(toolBar, SWT.NONE);
        tiRemoveTag.setText("Remove");
        tiRemoveTag.setImage(ImageConstants.IMG_16_REMOVE);
        tiRemoveTag.setEnabled(false);
        tiRemoveTag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedTag();
            }
        });

        tiEditTag = new ToolItem(toolBar, SWT.NONE);
        tiEditTag.setText("Edit");
        tiEditTag.setEnabled(false);
        tiEditTag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editSelectedTag();
            }
        });
    }
    
    private void createTagTable(Composite parent) {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableComposite.setLayout(new FillLayout());
        
        tagTable = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Table table = tagTable.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableViewerColumn tvcTag = new TableViewerColumn(tagTable, SWT.NONE);
        TableColumn tcTag = tvcTag.getColumn();
        tcTag.setWidth(500);
        tcTag.setText("Tag");
        tvcTag.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null) {
                    return (String) element;
                } else {
                    return "";
                }
            }
        });
        
        tagTable.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean isAnyTagSelected = isAnyTagSelected();
                tiEditTag.setEnabled(isAnyTagSelected);
                tiRemoveTag.setEnabled(isAnyTagSelected);
            }
            
        });
        tagTable.setContentProvider(new ArrayContentProvider());
        tagTable.setInput(tags);
        tagTable.refresh();
    }
    
    private boolean isAnyTagSelected() {
        StructuredSelection selection = (StructuredSelection) tagTable.getSelection();
        return selection != null && selection.getFirstElement() != null;
    }

    private void editSelectedTag() {
        String selectedTag = (String) tagTable.getStructuredSelection().toArray()[0];
        AddNewTestCaseTagDialog dialog = new AddNewTestCaseTagDialog(Display.getCurrent().getActiveShell(), tags);
        if (dialog.open() == Dialog.OK) {
            String newTagName = dialog.getTagName();
            Set<String> newTags = new LinkedHashSet<>();
            tags.stream().forEach(tag -> {
               if (tag.equals(selectedTag)) {
                   newTags.add(newTagName);
               } else {
                   newTags.add(tag);
               }
            });
            tags = newTags;
            tagTable.setInput(tags);
            tagTable.refresh();
        }
    }

    private void removeSelectedTag() {
        String selectedTag = (String) tagTable.getStructuredSelection().toArray()[0];
        tags.remove(selectedTag);
        tagTable.refresh();
    }

    private void addTag() {
        AddNewTestCaseTagDialog dialog = new AddNewTestCaseTagDialog(Display.getCurrent().getActiveShell(), tags);
        if (dialog.open() == Dialog.OK) {
            String newTagName = dialog.getTagName();
            tags.add(newTagName);
            tagTable.refresh();
        }
    }
    
    private void loadTags() {
        try {
            tags = store.getTestCaseTags();
        } catch (IOException e) {
            tags = new LinkedHashSet<>();
            LoggerSingleton.logError(e);
        }
    }
    
    @Override
    protected void okPressed() {
        try {
            store.setTestCaseTags(tags);
        } catch (GeneralSecurityException | IOException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, "Failed to save test case tags", e.getClass().getSimpleName());
        }
        super.okPressed();
    }
    
    private TestCaseSettingStore getStore() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return new TestCaseSettingStore(project.getFolderLocation());
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
}
