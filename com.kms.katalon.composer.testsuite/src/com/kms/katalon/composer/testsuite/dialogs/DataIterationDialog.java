package com.kms.katalon.composer.testsuite.dialogs;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.entity.link.IterationEntity;
import com.kms.katalon.entity.link.IterationType;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

public class DataIterationDialog extends Dialog {

    private IterationEntity iterationEntity;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button btnRunAllRows;
    private Button btnRunFromRow;
    private Button btnRunSpecificRows;
    private Text textSpecificRow;
    private ControlDecoration controlDecoration;

    public DataIterationDialog(Shell parentShell, IterationEntity iterationEntity) {
        super(parentShell);
        this.iterationEntity = iterationEntity;
    }

    @Override
    public void create() {
        super.create();
        getShell().setText(StringConstants.DIA_SHELL_DATA_ITERATION);
        registerListeners();
        initSelection();
    }

    private void initSelection() {
        switch (iterationEntity.getIterationType()) {
            case ALL:
                btnRunAllRows.setSelection(true);
                spinnerFrom.setEnabled(false);
                spinnerTo.setEnabled(false);
                textSpecificRow.setEnabled(false);
                break;
            case RANGE:
                btnRunFromRow.setSelection(true);
                spinnerFrom.setEnabled(true);
                spinnerTo.setEnabled(true);
                textSpecificRow.setEnabled(false);

                spinnerFrom.setSelection(iterationEntity.getFrom());
                spinnerTo.setSelection(iterationEntity.getTo());
                break;
            case SPECIFIC:
                btnRunSpecificRows.setSelection(true);
                spinnerFrom.setEnabled(false);
                spinnerTo.setEnabled(false);

                textSpecificRow.setEnabled(true);
                textSpecificRow.setText(iterationEntity.getValue());
                break;
        }
    }

    private void registerListeners() {
        btnRunAllRows.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                spinnerFrom.setEnabled(false);
                spinnerTo.setEnabled(false);
                textSpecificRow.setEnabled(false);
            }

        });

        btnRunFromRow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                spinnerFrom.setEnabled(true);
                spinnerTo.setEnabled(true);
                textSpecificRow.setEnabled(false);
            }
        });

        spinnerFrom.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                validateRangeIteration();
            }
        });

        spinnerTo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                validateRangeIteration();
            }
        });

        spinnerFrom.addListener(SWT.Verify, new Listener() {

            @Override
            public void handleEvent(Event event) {
                verifySpinnerEvent(event);
            }
        });

        spinnerTo.addListener(SWT.Verify, new Listener() {

            @Override
            public void handleEvent(Event event) {
                verifySpinnerEvent(event);
            }
        });

        btnRunSpecificRows.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textSpecificRow.setEnabled(true);
                spinnerFrom.setEnabled(false);
                spinnerTo.setEnabled(false);
            }
        });

        textSpecificRow.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                verifySpecificTextEvent(e);
            }
        });

        textSpecificRow.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                validateSpecificInteration();
            }
        });
    }

    private void verifySpinnerEvent(Event event) {
        Spinner spinner = (Spinner) event.widget;
        StringBuilder builder = new StringBuilder(spinner.getText());
        builder.replace(event.start, event.end, event.text);
        String newSpinnerText = builder.toString();
        if (!newSpinnerText.isEmpty() && !isInteger(newSpinnerText)) {
            Display.getCurrent().beep();
            event.doit = false;
        }
    }

    private void verifySpecificTextEvent(VerifyEvent event) {
        Text txt = (Text) event.widget;
        StringBuilder builder = new StringBuilder(txt.getText());
        builder.replace(event.start, event.end, event.text);
        String newSpinnerText = builder.toString().replace(" ", "");
        if (!newSpinnerText.isEmpty() && !Pattern.matches("(\\d+|\\-|,)*", newSpinnerText)) {
            Display.getCurrent().beep();
            event.doit = false;
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 4;

        btnRunAllRows = new Button(container, SWT.RADIO);
        btnRunAllRows.setText(StringConstants.DIA_BTN_RUN_ALL_ROWS);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        btnRunFromRow = new Button(container, SWT.RADIO);
        btnRunFromRow.setText(StringConstants.DIA_BTN_RUN_FROM_ROW);

        spinnerFrom = new Spinner(container, SWT.BORDER);
        spinnerFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        spinnerFrom.setMinimum(1);
        spinnerFrom.setMaximum(Integer.MAX_VALUE);
        spinnerFrom.setIncrement(1);
        spinnerFrom.setTextLimit(Integer.MAX_VALUE);

        controlDecoration = new ControlDecoration(spinnerFrom, SWT.LEFT | SWT.TOP);
        Image imgNotification = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
        controlDecoration.setImage(imgNotification);
        controlDecoration.setDescriptionText(StringConstants.DIA_MSG_START_ROW_BIGGER_THAN_END_ROW);
        controlDecoration.hide();

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText(StringConstants.DIA_LBL_TO_ROW);

        spinnerTo = new Spinner(container, SWT.BORDER);
        spinnerTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        spinnerTo.setMinimum(1);
        spinnerTo.setMaximum(Integer.MAX_VALUE);
        spinnerTo.setIncrement(1);
        spinnerTo.setTextLimit(Integer.MAX_VALUE);

        btnRunSpecificRows = new Button(container, SWT.RADIO);
        btnRunSpecificRows.setText(StringConstants.DIA_BTN_RUN_SPECIFIC_ROWS);

        textSpecificRow = new Text(container, SWT.BORDER);
        textSpecificRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        return container;
    }

    @Override
    protected void okPressed() {
        prepareReturnValue();
        super.okPressed();
    }

    private void prepareReturnValue() {
        if (btnRunAllRows.getSelection()) {
            iterationEntity.setIterationType(IterationType.ALL);
        }

        if (btnRunFromRow.getSelection()) {
            iterationEntity.setIterationType(IterationType.RANGE);
            iterationEntity.setRangeValue(spinnerFrom.getSelection(), spinnerTo.getSelection());
        }

        if (btnRunSpecificRows.getSelection()) {
            iterationEntity.setIterationType(IterationType.SPECIFIC);
            iterationEntity.setSpecificValue(textSpecificRow.getText());
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 200);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    private void validateRangeIteration() {
        if (!isInteger(spinnerFrom.getText()) || !isInteger(spinnerTo.getText())) {
            getButton(Dialog.OK).setEnabled(false);
            return;
        }

        if (Integer.valueOf(spinnerFrom.getText()) > Integer.valueOf(spinnerTo.getText())) {
            getButton(Dialog.OK).setEnabled(false);
            controlDecoration.show();
        } else {
            getButton(Dialog.OK).setEnabled(true);
            controlDecoration.hide();
        }
    }

    private void validateSpecificInteration() {
        String textSpecific = textSpecificRow.getText().replace(" ", "");
        String positiveNumber = "[1-9][0-9]*";
        String positiveNumberCorePattern = "(" + positiveNumber + "\\-" + positiveNumber + "|" + positiveNumber + ")";
        if (Pattern.matches(positiveNumberCorePattern + "(," +positiveNumberCorePattern + ")*,?",
                textSpecific)) {
            getButton(Dialog.OK).setEnabled(true);
        } else {
            getButton(Dialog.OK).setEnabled(false);
        }
    }

    public IterationEntity getIterationEntity() {
        return iterationEntity;
    }

    public boolean isInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return value >= 1;
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
