package com.kms.katalon.composer.testdata.views;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testdata.constants.StringConstants;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

public class NewTestDataColumnDialog extends Dialog {

    private String[] currentNames;

    private String name;

    private Text txtName;

    private ControlDecoration controlDecoration;

    // For creating new
    /**
     * @wbp.parser.constructor
     */
    public NewTestDataColumnDialog(Shell parentShell, String[] currentNames) {
        super(parentShell);
        this.setCurrentNames(currentNames);
    }

    // For editing
    public NewTestDataColumnDialog(Shell parentShell, String name, String[] currentNames) {
        super(parentShell);
        setName(name);
        setCurrentNames(currentNames);
        this.setCurrentNames(currentNames);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) area.getLayout();
        gridLayout.marginWidth = 0;

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.horizontalSpacing = 15;
        container.setLayout(gl_container);

        Label theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.VIEW_COL_COL_NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtName.setSize(200, SWT.DEFAULT);

        controlDecoration = new ControlDecoration(txtName, SWT.LEFT | SWT.TOP);
        Image imgInfo = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage();
        controlDecoration.setImage(imgInfo);
        controlDecoration.setShowHover(true);

        // Build the separator line
        Label separator = new Label(area, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.VIEW_SHELL_DATA_COL_DEFINITION);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 150);
    }

    @Override
    protected void okPressed() {
        if (txtName.getText().trim().isEmpty()) {
            MessageDialog.openInformation(getParentShell(), StringConstants.VIEW_INFO_TITLE_INVALID_DATA,
                    StringConstants.VIEW_INFO_MSG_ENTER_COL_NAME);
            return;
        }
        name = txtName.getText();
        super.okPressed();
    }

    @Override
    public void create() {
        super.create();

        loadInput();
        addSelectionListener();
    }

    private void addSelectionListener() {
        if (currentNames != null) {
            txtName.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    validate();
                }
            });
        }
    }

    private void showNotification(String message) {
        controlDecoration.setDescriptionText(message);
        controlDecoration.show();
    }

    private void validate() {
        String text = txtName.getText();
        if (text.isEmpty()) {
            showNotification(StringConstants.VIEW_WARN_MSG_NAME_IS_EMPTY);
            getButton(OK).setEnabled(false);
            return;
        }

        if (ArrayUtils.contains(currentNames, text)) {
            showNotification(StringConstants.VIEW_WARN_MSG_DUPLICATED_NAME);
            getButton(OK).setEnabled(false);
        } else {
            controlDecoration.hide();
            getButton(OK).setEnabled(true);
        }
    }

    private void loadInput() {
        if (name != null) {
            txtName.setText(name);
            txtName.selectAll();
        }
        validate();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void setCurrentNames(String[] currentNames) {
        this.currentNames = currentNames;
        Arrays.sort(currentNames);
    }
}
