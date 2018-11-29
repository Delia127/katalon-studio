package com.kms.katalon.composer.testcase.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestCaseTagSelectionDialog extends Dialog {
    
    private Set<String> tags;
    
    private Set<String> selectedTags;

    public TestCaseTagSelectionDialog(Shell parentShell, Set<String> initialTags) {
        super(parentShell);
        loadProjectTags();
        if (initialTags != null) {
            selectedTags = new HashSet<>(initialTags);
            selectedTags.retainAll(tags);
        }
    }

    private void loadProjectTags() {
        tags = new HashSet<>();
        for (int i = 1; i <= 5; i++) {
            tags.add("tag " + i);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(1, false));
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 200;
        gdBody.heightHint = 400;
        body.setLayoutData(gdBody);
        
        ScrolledComposite scrolledComposite = new ScrolledComposite(body, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        
        Composite tagList = new Composite(scrolledComposite, SWT.NONE);
        tagList.setLayout(new GridLayout(1, false));
        tagList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tagList.setBackground(tagList.getParent().getBackground());
        for (String tag: tags) {
            Button cbTag = new Button(tagList, SWT.CHECK);
            cbTag.setText(tag);
            cbTag.setBackground(cbTag.getParent().getBackground());
            cbTag.addSelectionListener(new SelectionAdapter() {
                 @Override
                 public void widgetSelected(SelectionEvent e) {
                    boolean isTagSelected = cbTag.getSelection();
                    if (isTagSelected) {
                        selectedTags.add(cbTag.getText());
                    } else {
                        selectedTags.remove(cbTag.getText());
                    }
                }
            });
            if (selectedTags.contains(tag)) {
                cbTag.setSelection(true);
            }
        }
        tagList.setSize(tagList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrolledComposite.setContent(tagList);
//        scrolledComposite.setExpandHorizontal(true);
//        scrolledComposite.setExpandVertical(true);
        return body;
    }
    
    public Set<String> getSelectedTags() {
        return selectedTags;
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Select tags");
    }
}
