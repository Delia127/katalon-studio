package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class NewCheckpointTestDataPage extends AbstractCheckpointWizardPage {

    private String pluginId = FrameworkUtil.getBundle(getClass()).getSymbolicName();

    private Text txtTestDataId;

    private Button btnBrowse;

    public NewCheckpointTestDataPage() {
        super(NewCheckpointTestDataPage.class.getSimpleName(), StringConstants.TEST_DATA,
                StringConstants.WIZ_TEST_DATA_SOURCE_CONFIGURATION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(3, false));

        Label lblSourceUrl = new Label(container, SWT.NONE);
        lblSourceUrl.setText(StringConstants.TEST_DATA);

        txtTestDataId = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtTestDataId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnBrowse = new Button(container, SWT.PUSH | SWT.FLAT);
        btnBrowse.setText(StringConstants.BROWSE);
        btnBrowse.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, false, false));

        setControlListeners();
        setControl(container);
        setPageComplete(isComplete());
    }

    private void setControlListeners() {
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
                    TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(getShell(),
                            new EntityLabelProvider(), entityProvider, new EntityViewerFilter(entityProvider));
                    dialog.setAllowMultiple(false);
                    dialog.setTitle(StringConstants.PART_TITLE_TEST_DATA_BROWSER);
                    dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
                    String testDataId = txtTestDataId.getText();
                    if (StringUtils.isNotBlank(testDataId)) {
                        DataFileEntity testDataEntity = TestDataController.getInstance().getTestDataByDisplayId(
                                testDataId);
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

                    DataFileEntity testData = ((TestDataTreeEntity) selectedItems[0]).getObject();
                    txtTestDataId.setText(testData.getIdForDisplay());
                    setPageComplete(isComplete());
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                            StringConstants.PART_MSG_UNABLE_TO_SELECT_TEST_DATA);
                }
            }
        });
    }

    public CheckpointSourceInfo getSourceInfo() {
        String testDataId = StringConstants.EMPTY;
        if (this.equals(getContainer().getCurrentPage())) {
            testDataId = txtTestDataId.getText();
        }
        return new CheckpointSourceInfo(testDataId);
    }

    @Override
    protected boolean isComplete() {
        return StringUtils.isNotBlank(txtTestDataId.getText());
    }

    @Override
    public Point getPageSize() {
        return getShell().computeSize(600, 300);
    }

}
