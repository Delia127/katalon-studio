package com.kms.katalon.composer.checkpoint.parts;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
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

    private FormText txtTestDataLink;

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

        txtTestDataLink = new FormText(compSourceInfoDetails, SWT.BORDER);
        txtTestDataLink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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
                executeOperation(new ChangeTestDataOperation());
            }
        });
        
        txtTestDataLink.addHyperlinkListener(new HyperlinkAdapter() { 
            @Override 
            public void linkActivated(HyperlinkEvent e) { 
                try {
                    DataFileEntity testData = TestDataController.getInstance().getTestDataByDisplayId(e.getLabel());
                    eventBroker.post(EventConstants.TEST_DATA_OPEN, testData);
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            } 
        });
    }

    @Override
    protected void loadCheckpointSourceInfo(CheckpointSourceInfo sourceInfo) {
        setTestDataLinkText(sourceInfo.getSourceUrl());
    }

    private class ChangeTestDataOperation extends ChangeCheckpointSourceInfoOperation {
        public ChangeTestDataOperation() {
            super(ChangeTestDataOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            try {
                CheckpointEntity checkpoint = getCheckpoint();
                CheckpointSourceInfo currentCheckpointSourceInfo = checkpoint.getSourceInfo();
                oldCheckpointSourceInfo = currentCheckpointSourceInfo.clone();
                DataFileEntity testData = openDialogForSelectingTestData(currentCheckpointSourceInfo);
                if (testData == null) {
                    return Status.CANCEL_STATUS;
                }
                currentCheckpointSourceInfo.setSourceUrl(testData.getIdForDisplay());
                newCheckpointSourceInfo = currentCheckpointSourceInfo.clone();
                loadCheckpointSourceInfo(currentCheckpointSourceInfo);
                setDirty(true);
                return Status.OK_STATUS;
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
                MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                        StringConstants.PART_MSG_UNABLE_TO_SELECT_TEST_DATA);
                return Status.CANCEL_STATUS;
            }
        }

        private DataFileEntity openDialogForSelectingTestData(CheckpointSourceInfo currentCheckpointSourceInfo)
                throws Exception {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return null;
            }

            FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(currentProject);
            EntityProvider entityProvider = new EntityProvider();
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(tableViewer.getTable().getShell(),
                    new EntityLabelProvider(), entityProvider, new EntityViewerFilter(entityProvider));
            dialog.setAllowMultiple(false);
            dialog.setTitle(StringConstants.PART_TITLE_TEST_DATA_BROWSER);
            dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));

            String testDataId = currentCheckpointSourceInfo.getSourceUrl();
            if (StringUtils.isNotBlank(testDataId)) {
                DataFileEntity testDataEntity = TestDataController.getInstance().getTestDataByDisplayId(testDataId);
                TestDataTreeEntity testDataTreeEntity = TreeEntityUtil.getTestDataTreeEntity(testDataEntity,
                        currentProject);
                dialog.setInitialSelection(testDataTreeEntity);
            }
            dialog.setValidator(new ISelectionStatusValidator() {

                @Override
                public IStatus validate(Object[] selection) {
                    if (selection == null || selection.length == 0 || !(selection[0] instanceof TestDataTreeEntity)) {
                        return new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
                                StringConstants.PART_MSG_PLEASE_SELECT_A_TEST_DATA, null);
                    }
                    return new Status(IStatus.OK, pluginId, IStatus.OK, StringConstants.EMPTY, null);
                }
            });

            if (dialog.open() != Dialog.OK) {
                return null;
            }

            Object[] selectedItems = dialog.getResult();
            if (selectedItems == null || selectedItems.length == 0
                    || !(selectedItems[0] instanceof TestDataTreeEntity)) {
                return null;
            }

            DataFileEntity testData = (DataFileEntity) ((TestDataTreeEntity) selectedItems[0]).getObject();
            return testData;
        }
    }

    private void setTestDataLinkText(String testDataLink) {
        if (testDataLink == null) {
            return;
        }
        txtTestDataLink.setText("<form><p><a>" + testDataLink + "</a></p></form>", true, false);
    }
    
    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.CHECKPOINT_EXISTING_DATA;
    }
}
