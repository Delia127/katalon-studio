package com.kms.katalon.composer.preferences.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MultiLineStringFieldEditor extends StringFieldEditor {

    /**
     * Validation strategy constant (value <code>0</code>) indicating that the
     * editor should perform validation after every key stroke.
     *
     * @see #setValidateStrategy
     */
    public static final int VALIDATE_ON_KEY_STROKE = 0;

    /**
     * Validation strategy constant (value <code>1</code>) indicating that the
     * editor should perform validation only when the text widget loses focus.
     *
     * @see #setValidateStrategy
     */
    public static final int VALIDATE_ON_FOCUS_LOST = 1;

    /**
     * Text limit constant (value <code>-1</code>) indicating unlimited text
     * limit and width.
     */
    public static int UNLIMITED = -1;

    /**
     * Old text value.
     * 
     * @since 3.4 this field is protected.
     */
    protected String oldValue;

    /**
     * The text field, or <code>null</code> if none.
     */
    Text textField;

    /**
     * Text limit of text field in characters; initially unlimited.
     */
    private int textLimit = UNLIMITED;

    /**
     * The error message, or <code>null</code> if none.
     */
    private String errorMessage;

    /**
     * Indicates whether the empty string is legal; <code>true</code> by
     * default.
     */
    private boolean emptyStringAllowed = true;

    /**
     * The validation strategy; <code>VALIDATE_ON_KEY_STROKE</code> by default.
     */
    private int validateStrategy = VALIDATE_ON_KEY_STROKE;

    /**
     * Width of text field in characters; initially unlimited.
     */
    private int widthInChars = UNLIMITED;

    /**
     * Height of text field in characters; initially unlimited.
     */
    private int heightInChars = UNLIMITED;

    /**
     * Cached valid state.
     */
    private boolean isValid;

    public MultiLineStringFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, UNLIMITED, UNLIMITED, parent);
    }

    public MultiLineStringFieldEditor(String name, String labelText, int width, int height, int strategy,
            Composite parent) {
        init(name, labelText);
        widthInChars = width;
        heightInChars = height;
        setValidateStrategy(strategy);
        isValid = false;
        errorMessage = JFaceResources.getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
        createControl(parent);
    }

    public MultiLineStringFieldEditor(String name, String labelText, int width, int height, Composite parent) {
        this(name, labelText, width, height, VALIDATE_ON_KEY_STROKE, parent);
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    protected boolean checkState() {
        boolean result = false;
        if (emptyStringAllowed) {
            result = true;
        }

        if (textField == null) {
            result = false;
        }

        String txt = textField.getText();

        result = (txt.trim().length() > 0) || emptyStringAllowed;

        // call hook for subclasses
        result = result && doCheckState();

        if (result) {
            clearErrorMessage();
        } else {
            showErrorMessage(errorMessage);
        }

        return result;
    }

    @Override
    protected void doLoad() {
        if (textField != null) {
            String value = getPreferenceStore().getString(getPreferenceName());
            textField.setText(value);
            oldValue = value;
        }
    }

    @Override
    protected void doLoadDefault() {
        if (textField != null) {
            String value = getPreferenceStore().getDefaultString(getPreferenceName());
            textField.setText(value);
        }
        valueChanged();
    }

    @Override
    protected void doStore() {
        getPreferenceStore().setValue(getPreferenceName(), textField.getText());
    }

    @Override
    public String getStringValue() {
        if (textField != null) {
            return textField.getText();
        }

        return getPreferenceStore().getString(getPreferenceName());
    }

    @Override
    protected Text getTextControl() {
        return textField;
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        getLabelControl(parent);

        textField = getTextControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        if (widthInChars != UNLIMITED) {
            GC gc = new GC(textField);
            try {
                Point extent = gc.textExtent("X");//$NON-NLS-1$
                gd.widthHint = widthInChars * extent.x;
            } finally {
                gc.dispose();
            }
        } else {
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
        }
        gd.grabExcessVerticalSpace = true;
        if (heightInChars != UNLIMITED) {
            gd.heightHint = heightInChars;
        } else {
            gd.verticalAlignment = GridData.FILL;
        }
        textField.setLayoutData(gd);
    }

    @Override
    public Text getTextControl(Composite parent) {
        if (textField == null) {
            textField = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
            textField.setFont(parent.getFont());
            switch (validateStrategy) {
            case VALIDATE_ON_KEY_STROKE:
                textField.addKeyListener(new KeyAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse
                     * .swt.events.KeyEvent)
                     */
                    @Override
                    public void keyReleased(KeyEvent e) {
                        valueChanged();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    // Ensure that the value is checked on focus loss in case we
                    // missed a keyRelease or user hasn't released key.
                    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
                    @Override
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                    }
                });

                break;
            case VALIDATE_ON_FOCUS_LOST:
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        refreshValidState();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                        clearErrorMessage();
                    }
                });
                break;
            default:
                Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
            }
            textField.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    textField = null;
                }
            });
            if (textLimit > 0) {// Only set limits above 0 - see SWT spec
                textField.setTextLimit(textLimit);
            }
        } else {
            checkParent(textField, parent);
        }
        return textField;
    }

    @Override
    public boolean isEmptyStringAllowed() {
        return emptyStringAllowed;
    }

    @Override
    public void setEmptyStringAllowed(boolean b) {
        emptyStringAllowed = b;
    }

    @Override
    public void setErrorMessage(String message) {
        errorMessage = message;
    }

    @Override
    public void setFocus() {
        if (textField != null) {
            textField.setFocus();
        }
    }

    @Override
    public void setStringValue(String value) {
        if (textField != null) {
            if (value == null) {
                value = "";//$NON-NLS-1$
            }
            oldValue = textField.getText();
            if (!oldValue.equals(value)) {
                textField.setText(value);
                valueChanged();
            }
        }
    }

    @Override
    public void setTextLimit(int limit) {
        textLimit = limit;
        if (textField != null) {
            textField.setTextLimit(limit);
        }
    }

    @Override
    public void setValidateStrategy(int value) {
        Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
        validateStrategy = value;
    }

    @Override
    public void showErrorMessage() {
        showErrorMessage(errorMessage);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    protected void refreshValidState() {
        isValid = checkState();
    }

    @Override
    protected void valueChanged() {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();

        if (isValid != oldState) {
            fireStateChanged(IS_VALID, oldState, isValid);
        }

        String newValue = textField.getText();
        if (!newValue.equals(oldValue)) {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }

}
