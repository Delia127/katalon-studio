package com.kms.katalon.composer.testlistener.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.TitleAreaDialog;
import com.kms.katalon.composer.testlistener.constant.ComposerTestListenerMessageConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.core.annotation.AfterTestCase;
import com.kms.katalon.core.annotation.AfterTestSuite;
import com.kms.katalon.core.annotation.BeforeTestCase;
import com.kms.katalon.core.annotation.BeforeTestSuite;
import com.kms.katalon.entity.file.TestListenerEntity;

@SuppressWarnings("restriction")
public class NewTestListenerDialog extends TitleAreaDialog {
    private static final String NEW_TEST_LISTENER_NAME = "NewTestListener";

    private Text txtName;

    private NewTestListenerResult result;

    private Map<String, Button> generateMethodButtons = new HashMap<>();

    private List<TestListenerEntity> currentListeners;

    public NewTestListenerDialog(Shell parentShell, List<TestListenerEntity> currentListeners) {
        super(parentShell);
        this.currentListeners = currentListeners;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite nameComposite = new Composite(container, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        nameComposite.setLayout(layout);

        Label lblName = new Label(nameComposite, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblName.setText(GlobalMessageConstants.NAME);

        txtName = new Text(nameComposite, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtName.minimumWidth = 200;
        txtName.setLayoutData(gdTxtName);

        Composite sampleMethodComposite = new Composite(container, SWT.NONE);
        sampleMethodComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sampleMethodComposite.setLayout(new GridLayout(1, false));

        Button btnGenerateBeforeTC = new Button(sampleMethodComposite, SWT.CHECK);
        btnGenerateBeforeTC.setText(ComposerTestListenerMessageConstants.DIA_LBL_GENERATE_SAMPLE_BEFORE_TEST_CASE);
        generateMethodButtons.put(BeforeTestCase.class.getName(), btnGenerateBeforeTC);

        Button btnGenerateAfterTC = new Button(sampleMethodComposite, SWT.CHECK);
        btnGenerateAfterTC.setText(ComposerTestListenerMessageConstants.DIA_LBL_GENERATE_SAMPLE_AFTER_TEST_CASE);
        generateMethodButtons.put(AfterTestCase.class.getName(), btnGenerateAfterTC);

        Button btnGenerateBeforeTS = new Button(sampleMethodComposite, SWT.CHECK);
        btnGenerateBeforeTS.setText(ComposerTestListenerMessageConstants.DIA_LBL_GENERATE_SAMPLE_BEFORE_TEST_SUITE);
        generateMethodButtons.put(BeforeTestSuite.class.getName(), btnGenerateBeforeTS);

        Button btnGenerateAfterTS = new Button(sampleMethodComposite, SWT.CHECK);
        btnGenerateAfterTS.setText(ComposerTestListenerMessageConstants.DIA_LBL_GENERATE_SAMPLE_AFTER_TEST_SUITE);
        generateMethodButtons.put(AfterTestSuite.class.getName(), btnGenerateAfterTS);

        setInput();
        registerControlModifyListeners();

        return container;
    }

    private void setInput() {
        txtName.setText(getSuggestion(NEW_TEST_LISTENER_NAME));
        txtName.selectAll();
        txtName.forceFocus();
        setMessage(ComposerTestListenerMessageConstants.DIA_MSG_CREATE_NEW_TEST_LISTENER, IMessageProvider.INFORMATION);
    }

    private boolean isNameDupplicated(String newName) {
        return this.currentListeners.parallelStream().filter(l -> l.getName().equals(newName)).findAny().isPresent();
    }

    private String getSuggestion(String suggestion) {
        String newName = suggestion;
        int index = 0;

        while (isNameDupplicated(newName)) {
            index += 1;
            newName = String.format("%s_%d", suggestion, index);
        }
        return newName;
    }

    private void checkNewName(String newName) {
        if (isNameDupplicated(newName)) {
            setMessage(StringConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
            getButton(NewTestListenerDialog.OK).setEnabled(false);
            return;
        }

        IStatus status = JavaConventionsUtil.validateJavaTypeName(txtName.getText(), null);
        switch (status.getSeverity()) {
            case IStatus.OK: {
                setMessage(ComposerTestListenerMessageConstants.DIA_MSG_CREATE_NEW_TEST_LISTENER,
                        IMessageProvider.INFORMATION);
                break;
            }
            case IStatus.WARNING: {
                setMessage(status.getMessage(), IMessageProvider.WARNING);
                break;
            }
            case IStatus.ERROR: {
                setMessage(status.getMessage(), IMessageProvider.ERROR);
                break;
            }
        }
        getButton(NewTestListenerDialog.OK).setEnabled(status.getSeverity() != IStatus.ERROR);
    }

    protected void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                checkNewName(txtName.getText());
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ComposerTestListenerMessageConstants.ITEM_LBL_NEW_TEST_LISTENER);
    }

    @Override
    protected int getShellStyle() {
        return SWT.SHELL_TRIM;
    }

    @Override
    protected void okPressed() {
        String newName = txtName.getText();
        Map<String, Boolean> sampleMethodAllowed = generateMethodButtons.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getSelection()));
        result = new NewTestListenerResult(newName, sampleMethodAllowed);
        super.okPressed();
    }

    public NewTestListenerResult getResult() {
        return result;
    }

    public class NewTestListenerResult {
        private final String newName;

        private final Map<String, Boolean> sampleMethodAllowed;

        private NewTestListenerResult(String newName, final Map<String, Boolean> sampleMethodAllowed) {
            this.newName = newName;
            this.sampleMethodAllowed = sampleMethodAllowed;
        }

        public String getNewName() {
            return newName;
        }

        public Map<String, Boolean> getSampleMethodAllowed() {
            return sampleMethodAllowed;
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, super.getInitialSize().y);
    }
}
