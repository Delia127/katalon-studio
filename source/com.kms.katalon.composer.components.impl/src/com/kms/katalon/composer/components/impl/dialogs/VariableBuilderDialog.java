package com.kms.katalon.composer.components.impl.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.model.VariableDialogModel;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyStringUtil;

public abstract class VariableBuilderDialog extends AbstractDialog {
    private static final String BOOLEAN_REGEX = "^(true|false)$";
    private static final String NUMBER_REGEX = "^(-)?((\\d+)|(\\d*\\.\\d+)|(\\d+\\.\\d*))$";
    private static final String STRING_REGEX = "^\'.*\'$";

    public enum DialogType {
        NEW, EDIT
    }

    public enum VariableType {
        STRING("String"), NUMBER("Number"), BOOLEAN("Boolean");
        private final String text;

        private VariableType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static List<String> getValueStrings() {
            List<String> valueStrings = new ArrayList<>();
            for (VariableType type : values()) {
                valueStrings.add(type.toString());
            }
            return valueStrings;
        }

        public static VariableType parse(String text) {
            for (VariableType type : values()) {
                if (type.text.equals(text)) {
                    return type;
                }
            }
            return STRING;
        }
    }

    public enum BooleanValue {
        TRUE("true"), FALSE("false");
        private final String text;

        private BooleanValue(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static List<String> getValueStrings() {
            List<String> valueStrings = new ArrayList<>();
            for (BooleanValue type : values()) {
                valueStrings.add(type.toString());
            }
            return valueStrings;
        }

        public static BooleanValue parse(String text) {
            for (BooleanValue type : values()) {
                if (type.text.equals(text)) {
                    return type;
                }
            }
            return TRUE;
        }
    }

    protected String dialogTitle;
    protected String dialogMessage;
    protected DialogType type;
    protected Text textVariableName;
    protected Control textDefaultValue;
    private Combo cbbValueType;

    private VariableDialogModel fVariable;
    private Composite container;

    public VariableBuilderDialog(Shell parentShell, DialogType type, VariableDialogModel variable) {
        super(parentShell);
        this.type = type;
        fVariable = variable;
        switch (type) {
            case EDIT:
                dialogTitle = StringConstants.DIA_TITLE_EDIT_VAR;
                dialogMessage = StringConstants.DIA_INFO_MSG_EDIT_NEW_VAR;
                break;
            case NEW:
                dialogTitle = StringConstants.DIA_TITLE_NEW_VAR;
                dialogMessage = StringConstants.DIA_INFO_MSG_CREATE_NEW_VAR;
                break;
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        glContainer.verticalSpacing = ControlUtils.DF_VERTICAL_SPACING;
        container.setLayout(glContainer);

        Label lblVariableName = new Label(container, SWT.NONE);
        lblVariableName.setText(StringConstants.DIA_LBL_NAME);

        textVariableName = new Text(container, SWT.BORDER);
        GridData gdTextVariableName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTextVariableName.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        textVariableName.setLayoutData(gdTextVariableName);

        ControlDecoration controlDecoration = new ControlDecoration(textVariableName, SWT.LEFT | SWT.TOP);
        controlDecoration.setDescriptionText(StringConstants.DIA_CTRL_VAR_INFO);
        controlDecoration.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());

        Label lblValueType = new Label(container, SWT.NONE);
        lblValueType.setText(StringConstants.DIA_LBL_VALUE_TYPE);

        cbbValueType = new Combo(container, SWT.READ_ONLY);
        GridData gdCbbValueType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdCbbValueType.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        cbbValueType.setLayoutData(gdCbbValueType);
        cbbValueType
                .setItems(VariableType.getValueStrings().toArray(new String[VariableType.getValueStrings().size()]));
        cbbValueType.select(0);

        Label lblDefaultValue = new Label(container, SWT.NONE);
        GridData gdLblDefaultValue = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblDefaultValue.verticalIndent = 5;
        lblDefaultValue.setLayoutData(gdLblDefaultValue);
        lblDefaultValue.setText(StringConstants.DIA_LBL_DEFAULT_VALUE);
        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 250);
    }

    @Override
    protected void registerControlModifyListeners() {
        textVariableName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                fVariable.setName(textVariableName.getText());
                validate();
            }
        });

        cbbValueType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                VariableType variableType = VariableType.parse(cbbValueType.getText());
                switch (variableType) {
                    case BOOLEAN: {
                        fVariable.setValue(BooleanValue.TRUE.toString());
                        break;
                    }
                    case NUMBER: {
                        fVariable.setValue(Integer.toString(0));
                        break;
                    }
                    case STRING: {
                        fVariable.setValue("''");
                        break;
                    }
                    default:
                        return;

                }
                layoutTxtMessage();
            }
        });
    }

    protected void validate() {
        boolean enable = true;
        String newVariableName = textVariableName.getText();
        if (!GroovyConstants.VARIABLE_NAME_REGEX.matcher(newVariableName).find()) {
            enable &= false;
        } else {
            enable &= true;
        }

        String value = fVariable.getValue();
        VariableType variableType = VariableType.parse(cbbValueType.getText());
        switch (variableType) {
            case BOOLEAN: {
                enable &= Pattern.matches(BOOLEAN_REGEX, value);
                break;
            }
            case NUMBER: {
                enable &= Pattern.matches(NUMBER_REGEX, value);
                break;
            }
            case STRING: {
                enable &= Pattern.matches(STRING_REGEX, value);
                break;
            }

        }

        getButton(OK).setEnabled(enable);
    }

    protected String getDialogTitle() {
        return dialogTitle != null ? dialogTitle : "";
    }

    @Override
    protected final void setInput() {
        layoutTxtMessage();
        validate();
    }

    private void layoutTxtMessage() {
        if (textDefaultValue != null && !textDefaultValue.isDisposed()) {
            textDefaultValue.dispose();
        }
        if (fVariable == null) {
            fVariable = new VariableDialogModel("", "''");
        }
        textVariableName.setText(fVariable.getName());

        String rawDefaultValue = fVariable.getValue();

        if (Pattern.matches(STRING_REGEX, rawDefaultValue)) {
            cbbValueType.select(VariableType.STRING.ordinal());
            textDefaultValue = new Text(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            GridData gdTextVariableDefaultValue = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            gdTextVariableDefaultValue.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
            textDefaultValue.setLayoutData(gdTextVariableDefaultValue);
            ((Text) textDefaultValue).setText(GroovyStringUtil.unescapeGroovy(rawDefaultValue.substring(1,
                    rawDefaultValue.length() - 1)));

            ((Text) textDefaultValue).addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    fVariable.setValue("'" + GroovyStringUtil.escapeGroovy(((Text) textDefaultValue).getText()) + "'");
                }
            });

            ((Text) textDefaultValue).addListener(SWT.Modify, ControlUtils.getAutoHideScrollbarListener);
            ((Text) textDefaultValue).addListener(SWT.Resize, ControlUtils.getAutoHideScrollbarListener);

        } else if (Pattern.matches(NUMBER_REGEX, rawDefaultValue)) {
            cbbValueType.select(VariableType.NUMBER.ordinal());
            textDefaultValue = new Text(container, SWT.BORDER);
            GridData gdTextVariableName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gdTextVariableName.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
            textDefaultValue.setLayoutData(gdTextVariableName);
            ((Text) textDefaultValue).setText(rawDefaultValue);

            ((Text) textDefaultValue).addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    fVariable.setValue(((Text) textDefaultValue).getText().trim());
                    validate();
                }

            });
        } else if (Pattern.matches(BOOLEAN_REGEX, rawDefaultValue)) {
            cbbValueType.select(VariableType.BOOLEAN.ordinal());
            textDefaultValue = new Combo(container, SWT.READ_ONLY);
            textDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((Combo) textDefaultValue).setItems(BooleanValue.getValueStrings().toArray(
                    new String[BooleanValue.getValueStrings().size()]));
            ((Combo) textDefaultValue).select(BooleanValue.parse(rawDefaultValue).ordinal());

            ((Combo) textDefaultValue).addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    fVariable.setValue(((Combo) textDefaultValue).getText());
                }
            });
        }
        container.layout(true, true);
    }

    protected VariableDialogModel getVariable() {
        return fVariable;
    }
}
