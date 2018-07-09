package com.kms.katalon.composer.components.impl.editors;

import java.nio.file.FileSystems;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;

public class SingleFileSelectionDialogCellEditor extends CellEditor {

    private Text txtFilePath;

    private Button btnChooseFile;

    private Composite editor;

    private boolean fileDialogOpened = false;

    public SingleFileSelectionDialogCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }

    private class FileSelectionCellLayout extends Layout {
        @Override
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point size = btnChooseFile.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (txtFilePath != null) {
                txtFilePath.setBounds(0, 0, bounds.width - size.x, bounds.height);
            }
            btnChooseFile.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
        }

        @Override
        public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }
            Point contentsSize = txtFilePath.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            Point buttonSize = btnChooseFile.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            // Just return the button width to ensure the button is not
            // clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y, buttonSize.y));
            return result;
        }
    }

    @Override
    protected Control createControl(Composite parent) {
        editor = new Composite(parent, getStyle());
        editor.setLayout(new FileSelectionCellLayout());
        editor.addListener(SWT.Deactivate, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (!fileDialogOpened) {
                    SingleFileSelectionDialogCellEditor.this.focusLost();
                }
            }
        });

        txtFilePath = new Text(editor, getStyle());
        txtFilePath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);

                // as a result of processing the above call, clients may
                // have
                // disposed this cell editor
                if ((getControl() == null) || getControl().isDisposed()) {
                    return;
                }
            }
        });

        btnChooseFile = new Button(editor, SWT.DOWN);
        btnChooseFile.setText(StringConstants.BTN_CHOOSE_A_FILE);
        btnChooseFile.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {

                fileDialogOpened = true;
                Object newValue = openDialogBox(editor);
                fileDialogOpened = false;
                if (newValue != null) {
                    boolean newValidState = isCorrect(newValue);
                    if (newValidState) {
                        markDirty();
                        doSetValue(newValue);
                    } else {
                        // try to insert the current value into the error
                        // message.
                        setErrorMessage(
                                MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString() }));
                    }
                    fireApplyEditorValue();
                }
            }
        });

        setValueValid(true);

        return editor;
    }

    protected Object openDialogBox(Control cellEditorWindow) {
        Object oldValue = doGetValue();

        FileDialog fileDialog = new FileDialog(cellEditorWindow.getShell(), SWT.SINGLE);

        String firstFile = fileDialog.open();
        if (firstFile != null) {
            String filterPath = fileDialog.getFilterPath();
            String fileName = fileDialog.getFileName();
            String filePath = String.format("%s%s%s", filterPath, FileSystems.getDefault().getSeparator(),
                    fileName);
            return filePath;
        } else {
            return oldValue;
        }
    }

    @Override
    protected Object doGetValue() {
        return txtFilePath.getText();
    }

    @Override
    protected void doSetFocus() {
        if (txtFilePath != null) {
            txtFilePath.selectAll();
            txtFilePath.setFocus();
        }
    }

    @Override
    protected void doSetValue(Object value) {
        String text = StringUtils.EMPTY;
        if (value != null) {
            text = value.toString();
        }
        txtFilePath.setText(text);
    }
}
