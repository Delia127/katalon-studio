package com.kms.katalon.composer.testcase.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;

public class TestCaseSettingPage extends PreferencePage {

    private Composite container;

    private Combo cbbFailureHanlings;

    private String projectDir;

    public TestCaseSettingPage() {
        projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container = new Composite(parent, SWT.NONE);
        container.setLayout(layout);

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        label.setText(StringConstants.DEFAULT_FAILURE_HANDLING);

        cbbFailureHanlings = new Combo(container, SWT.READ_ONLY);
        cbbFailureHanlings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        initValue();
        return container;
    }

    private void initValue() {
        cbbFailureHanlings.setItems(FailureHandling.valueStrings());
        FailureHandling defaultFailureHandling = TestCaseSettingStore.getDefaultFailureHandling(projectDir);
        cbbFailureHanlings.select(defaultFailureHandling.ordinal());
    }

    @Override
    public boolean performOk() {
        if (container == null)
            return false;
        TestCaseSettingStore.saveDefaultFailureHandling(projectDir,
                FailureHandling.valueStrings()[cbbFailureHanlings.getSelectionIndex()]);
        return super.performOk();
    }
}
