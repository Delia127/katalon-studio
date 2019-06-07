package com.kms.katalon.composer.execution.settings;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.exceptions.FileBeingUsedException;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.classpath.ProjectBuildPath;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ExternalLibratiesSettingPage extends PreferencePageWithHelp {
    private static final int DELETING_EXTERNAL_JAR_TIMEOUT = 30000;

    private static final int TICK = 1;

    private static final String JAR_FILE_EXTENSION = "*.jar";

    private TableViewer tableViewer;

    private ProjectBuildPath projectBuildPath;

    private ToolItem tltmRemoveJars;

    private ToolItem tltmAddJars;

    private Collection<File> externalJars;

    private boolean modified;
    
    public ExternalLibratiesSettingPage() {
        noDefaultButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

        tltmAddJars = new ToolItem(toolBar, SWT.NONE);
        tltmAddJars.setText(StringConstants.ADD);
        tltmAddJars.setImage(ImageConstants.IMG_24_ADD);

        tltmRemoveJars = new ToolItem(toolBar, SWT.NONE);
        tltmRemoveJars.setText(StringConstants.REMOVE);
        tltmRemoveJars.setImage(ImageConstants.IMG_24_REMOVE);

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout tableLayout = new TableColumnLayout();
        composite.setLayout(tableLayout);

        tableViewer = new CTableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

        TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
        tableViewerColumn.setLabelProvider(new FileLabelCellProvider());
        tableLayout.setColumnData(tblclmnNewColumn, new ColumnWeightData(98, 0));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        updateInput();
        registerControlModifyListeners();

        return container;
    }

    private void registerControlModifyListeners() {
        tltmAddJars.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.MULTI);
                fileDialog.setFilterExtensions(new String[] { JAR_FILE_EXTENSION });
                if (StringUtils.isEmpty(fileDialog.open())) {
                    return;
                }

                String[] selectedFileNames = fileDialog.getFileNames();
                if (ArrayUtils.isEmpty(selectedFileNames)) {
                    return;
                }

                List<File> newFiles = new ArrayList<>();
                for (String fileName : selectedFileNames) {
                    File newExternalFile = new File(getExternalDir(), fileName);
                    newFiles.add(newExternalFile);
                    try {
                        FileUtils.copyFile(new File(fileDialog.getFilterPath(), fileName), newExternalFile, true);
                        if (!externalJars.contains(newExternalFile)) {
                            externalJars.add(newExternalFile);
                        }
                    } catch (IOException veryImportantException) {
                        LoggerSingleton.logError(veryImportantException);
                    }
                }

                tableViewer.refresh();
                tableViewer.setSelection(new StructuredSelection(newFiles));
                modified = true;
            }
        });

        tltmRemoveJars.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedExternalJars();
            }
        });

        tableViewer.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    removeSelectedExternalJars();
                }
            }
        });
    }

    private void removeSelectedExternalJars() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();

        if (selection.isEmpty()) {
            return;
        }

        for (Object objectFile : selection.toArray()) {
            final File file = (File) objectFile;
            externalJars.remove(file);
        }

        tableViewer.refresh();
        modified = true;
    }

    private ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    private void updateInput() {
        ProjectEntity currentProject = getCurrentProject();
        projectBuildPath = new ProjectBuildPath(currentProject);
        externalJars = getJars();
        tableViewer.setInput(externalJars);
        modified = false;
    }

    private Collection<File> getJars() {
        final File externalDir = getExternalDir();
        Collection<File> listJarFiles = new ArrayList<>();
        if (externalDir.exists()) {
            listJarFiles = FileUtils.listFiles(externalDir, new WildcardFileFilter(JAR_FILE_EXTENSION),
                    new AbstractFileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file == externalDir;
                        }
                    });
        }
        return listJarFiles;
    }

    private File getExternalDir() {
        return projectBuildPath.getExternalLibrariesDir();
    }

    private class FileLabelCellProvider extends TypeCheckedStyleCellLabelProvider<File> {

        public FileLabelCellProvider() {
            super(0);
        }

        @Override
        protected Class<File> getElementType() {
            return File.class;
        }

        @Override
        protected Image getImage(File element) {
            return ImageConstants.IMG_16_EXTERNAL_LIBRARY;
        }

        @Override
        protected String getText(File file) {
            return FilenameUtils.getName(file.getName());
        }
    }

    @Override
    public boolean performOk() {
        if (!modified || tableViewer == null || tableViewer.getTable().isDisposed()) {
            return true;
        }

        try {
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                private void removeUnusedFiles(IProgressMonitor monitor) throws FileBeingUsedException {
                    Collection<File> needRemovedJars = getJars();
                    needRemovedJars.removeAll(externalJars);
                    if (needRemovedJars.isEmpty()) {
                        return;
                    }
                    monitor.beginTask(ComposerExecutionMessageConstants.MSG_DELETING_LIBRARY_FILES, needRemovedJars.size());
                    for (File file : needRemovedJars) {
                        monitor.subTask(MessageFormat.format(ComposerExecutionMessageConstants.MSG_DELETING_FILE_X,
                                file.getName()));
                        safelyDeleleFile(file, DELETING_EXTERNAL_JAR_TIMEOUT);
                        monitor.worked(TICK);
                    }
                }

                private void safelyDeleleFile(File file, long timeoutInMillis) throws FileBeingUsedException {
                    long startTime = System.currentTimeMillis();
                    while (file.exists() && (System.currentTimeMillis() - startTime) <= timeoutInMillis) {
                        FileUtils.deleteQuietly(file);
                    }
                    if (file.exists()) {
                        throw new FileBeingUsedException(file, getCurrentProject());
                    }
                }

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(StringConstants.PAGE_EXTERNAL_LIB_JOB_TASK_REBUILD_PROJECT, 5);
                        ProjectController projectController = ProjectController.getInstance();
                        ProjectEntity currentProject = getCurrentProject();

                        projectController.cleanProjectUISettings(currentProject);
                        monitor.worked(TICK);

                        saveAllProject();
                        monitor.worked(TICK);

                        GroovyUtil.getGroovyProject(currentProject).close(new SubProgressMonitor(monitor, TICK));

                        try {
                            removeUnusedFiles(monitor);
                            monitor.worked(TICK);
                        } finally {
                            projectController.openProjectForUI(currentProject.getId(),
                                    new SubProgressMonitor(monitor, TICK));
                        }
                    } catch (final Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }

            });
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (!(targetException instanceof FileBeingUsedException)) {
                LoggerSingleton.logError(targetException);
            } else {
                updateInput();
            }
            MultiStatusErrorDialog.showErrorDialog(targetException,
                    StringConstants.PAGE_EXTERNAL_LIB_MSG_UNABLE_UPDATE_PROJECT, targetException.getMessage());
            return false;
        } catch (InterruptedException ignored) {
            // Ignore it
        }
        return super.performOk();
    }

    private void saveAllProject() {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                if (getService(EPartService.class).getDirtyParts().isEmpty()) {
                    return;
                }

                try {
                    getService(IHandlerService.class).executeCommand(IdConstants.SAVE_ALL_COMMAND_ID, null);
                } catch (CommandException e) {
                    LoggerSingleton.logError(e);
                }
            }

            @SuppressWarnings("unchecked")
            private <T> T getService(Class<? extends T> clazz) {
                return (T) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(clazz);
            }
        });
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXTERNAL_LIBRARIES;
    }
}
