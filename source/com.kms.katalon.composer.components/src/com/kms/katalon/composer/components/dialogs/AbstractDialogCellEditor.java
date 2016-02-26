package com.kms.katalon.composer.components.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.kms.katalon.composer.components.constants.StringConstants;

public abstract class AbstractDialogCellEditor extends DialogCellEditor {
    protected String defaultContent;
    protected Composite editor;
    protected boolean isEditorClosed;

    public AbstractDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, SWT.NONE);
        this.isEditorClosed = false;
        this.defaultContent = defaultContent;
    }

    @Override
    protected void updateContents(Object value) {
        if (defaultContent != null) {
            super.updateContents(defaultContent.replace("&", "&&"));
        } else {
            super.updateContents(value);
        }
    }

    protected String getValidatorMessage(String className) {
        return MessageFormat.format(StringConstants.EDI_MSG_VALIDATOR_REQUIRE_MESSAGE, className);
    }

    @Override
    protected Button createButton(Composite parent) {
        Button button = super.createButton(parent);
        button.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(Event e) {
                getControl().notifyListeners(SWT.Traverse, e);
            }
        });
        return button;
    }

    @Override
    protected boolean dependsOnExternalFocusListener() {
        return false;
    }

    @Override
    public void activate(ColumnViewerEditorActivationEvent activationEvent) {
        super.activate(activationEvent);
        doShowDialog();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        isEditorClosed = true;
    }

    protected void waitTofireApplyEditorValue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isEditorClosed) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // interrupted, do nothing
                    }
                }
                fireApplyEditorValue();
            }
        }).start();
    }

    protected void waitTofireCancelEditor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isEditorClosed) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // interrupted, do nothing
                    }
                }
                fireCancelEditor();
            }
        }).start();
    }

    protected void doShowDialog() {
        Object newValue = openDialogBox(editor);
        if (newValue == null) {
            fireCancelEditor();
            return;
        }
        if (!isCorrect(newValue)) {
            setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString() }));
            return;
        }
        markDirty();
        doSetValue(newValue);
        fireApplyEditorValue();
    }

    @Override
    protected final Control createControl(Composite parent) {
        return null;
    }

}
