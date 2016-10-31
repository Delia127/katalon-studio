package com.kms.katalon.composer.checkpoint.parts;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class CheckpointTestDataPart extends CheckpointAbstractPart {

    private Button btnBrowse;

    private String pluginId = FrameworkUtil.getBundle(getClass()).getSymbolicName();

    @Override
    protected Composite createSourceInfoPartDetails(Composite parent) {
        compSourceInfoDetails = new Composite(parent, SWT.NONE);
        GridLayout glCompositeSrcInfoDetails = new GridLayout(4, false);
        glCompositeSrcInfoDetails.marginWidth = 0;
        glCompositeSrcInfoDetails.marginHeight = 0;
        compSourceInfoDetails.setLayout(glCompositeSrcInfoDetails);
        compSourceInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSourceUrl = new Label(compSourceInfoDetails, SWT.NONE);
        lblSourceUrl.setText(StringConstants.TEST_DATA);

        txtSourceUrl = new Text(compSourceInfoDetails, SWT.BORDER | SWT.READ_ONLY);
        txtSourceUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnBrowse = new Button(compSourceInfoDetails, SWT.PUSH | SWT.FLAT);
        btnBrowse.setText(StringConstants.BROWSE);
        btnBrowse.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, false, true));

        return parent;
    }

    @Override
    protected void addSourceInfoConstrolListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                    if (currentProject == null) {
                        return;
                    }

                    FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(currentProject);
                    EntityProvider entityProvider = new EntityProvider();
                    TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(tableViewer.getTable().getShell(),
                            new EntityLabelProvider(), entityProvider, new EntityViewerFilter(entityProvider));
                    dialog.setAllowMultiple(false);
                    dialog.setTitle(StringConstants.PART_TITLE_TEST_DATA_BROWSER);
                    dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
                    CheckpointEntity currentCheckpoint = getCheckpoint();
                    CheckpointSourceInfo currentCheckpointSourceInfo = currentCheckpoint.getSourceInfo();
                    String testDataId = currentCheckpointSourceInfo.getSourceUrl();
                    if (StringUtils.isNotBlank(testDataId)) {
                        DataFileEntity testDataEntity = TestDataController.getInstance()
                                .getTestDataByDisplayId(testDataId);
                        TestDataTreeEntity testDataTreeEntity = TreeEntityUtil.getTestDataTreeEntity(testDataEntity,
                                currentProject);
                        dialog.setInitialSelection(testDataTreeEntity);
                    }
                    dialog.setValidator(new ISelectionStatusValidator() {

                        @Override
                        public IStatus validate(Object[] selection) {
                            if (selection == null || selection.length == 0
                                    || !(selection[0] instanceof TestDataTreeEntity)) {
                                return new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
                                        StringConstants.PART_MSG_PLEASE_SELECT_A_TEST_DATA, null);
                            }
                            return new Status(IStatus.OK, pluginId, IStatus.OK, StringConstants.EMPTY, null);
                        }
                    });

                    if (dialog.open() != Dialog.OK) {
                        return;
                    }

                    Object[] selectedItems = dialog.getResult();
                    if (selectedItems == null || selectedItems.length == 0
                            || !(selectedItems[0] instanceof TestDataTreeEntity)) {
                        return;
                    }

                    DataFileEntity testData = (DataFileEntity) ((TestDataTreeEntity) selectedItems[0]).getObject();
                    currentCheckpointSourceInfo.setSourceUrl(testData.getIdForDisplay());
                    loadCheckpointSourceInfo(currentCheckpointSourceInfo);
                    save();
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                            StringConstants.PART_MSG_UNABLE_TO_SELECT_TEST_DATA);
                }
            }
        });
    }

    @Override
    protected void loadCheckpointSourceInfo(CheckpointSourceInfo sourceInfo) {
        txtSourceUrl.setText(sourceInfo.getSourceUrl());
    }

}
