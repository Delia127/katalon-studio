package com.kms.katalon.composer.project.dialog;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.core.util.internal.PathUtil;

public class ProjectChoosingDialog extends AbstractDialog {

    private CTableViewer tableViewer;

    private List<File> projectFiles;

    private File rootFolder;

    private File selectedProjectFile;

    public ProjectChoosingDialog(Shell parentShell, File rootFolder, List<File> projectFiles) {
        super(parentShell);

        this.projectFiles = projectFiles;
        this.rootFolder = rootFolder;
    }

    @Override
    protected void registerControlModifyListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedProjectFile = (File) tableViewer.getStructuredSelection().getFirstElement();
            }
        });
    }

    @Override
    protected void setInput() {
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setInput(projectFiles);

        tableViewer.setSelection(new StructuredSelection(projectFiles.get(0)));
        selectedProjectFile = projectFiles.get(0);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        Label lblNotification = new Label(container, SWT.WRAP);
        lblNotification.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        lblNotification.setText(
                "There are some nested Katalon Studio projects under your selection. Please choose one project to open.");

        tableViewer = new CTableViewer(container, SWT.NONE);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        TableViewerColumn tbvcName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnId = tbvcName.getColumn();
        tblclmnId.setWidth(150);
        tbvcName.setLabelProvider(new ProjectFileLableProvider(0));
        tblclmnId.setText("Name");

        TableViewerColumn tbvcPath = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnPath = tbvcPath.getColumn();
        tblclmnPath.setWidth(200);
        tbvcPath.setLabelProvider(new ProjectFileLableProvider(1));
        tblclmnPath.setText("Path");

        return container;
    }

    private class ProjectFileLableProvider extends TypeCheckStyleCellTableLabelProvider<File> {

        public static final int CLMN_NAME_IDX = 0;

        public static final int CLMN_PATH_IDX = 1;

        public ProjectFileLableProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected Class<File> getElementType() {
            return File.class;
        }

        @Override
        protected Image getImage(File element) {
            switch (columnIndex) {
                case CLMN_NAME_IDX:
                    return ImageConstants.IMG_PROJECT_16;
                default:
                    return null;
            }
        }

        @Override
        protected String getText(File element) {
            switch (columnIndex) {
                case CLMN_NAME_IDX:
                    return FilenameUtils.getBaseName(element.getName());
                case CLMN_PATH_IDX:
                    return PathUtil.absoluteToRelativePath(element.getAbsolutePath(), rootFolder.getAbsolutePath());
                default:
                    return StringUtils.EMPTY;
            }
        }
    }

    @Override
    public String getDialogTitle() {
        return "Project Choosing Dialog";
    }

    public File getSelectedProjectFile() {
        return selectedProjectFile;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 400);
    }
}
