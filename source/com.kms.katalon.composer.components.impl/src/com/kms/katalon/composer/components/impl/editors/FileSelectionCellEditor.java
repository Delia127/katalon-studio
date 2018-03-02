package com.kms.katalon.composer.components.impl.editors;

import java.nio.file.FileSystems;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class FileSelectionCellEditor extends DialogCellEditor {

    public FileSelectionCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }
    
    @Override
    protected Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText("Choose files");
        return result;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        Object oldValue = doGetValue();
        
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);

        String firstFile = fileDialog.open();
        if (firstFile != null) {
            String filterPath = fileDialog.getFilterPath();
            String[] selectedFiles = fileDialog.getFileNames();
            String[] filePaths = new String[selectedFiles.length];
            for (int i = 0; i < selectedFiles.length; i++) {
                String file = selectedFiles[i];
                String filePath = String.format("%s%s%s", 
                                            filterPath, 
                                            FileSystems.getDefault().getSeparator(), 
                                            file);
                filePaths[i]= filePath;
            }
            return filePaths;
        } else {
            return oldValue;
        }
    }
}
