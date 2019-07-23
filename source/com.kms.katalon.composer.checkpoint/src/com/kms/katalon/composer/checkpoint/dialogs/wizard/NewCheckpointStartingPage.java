package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class NewCheckpointStartingPage extends AbstractCheckpointWizardPage {

    private static final String NAME_ALREADY_EXISTS = com.kms.katalon.composer.components.impl.constants.StringConstants.DIA_NAME_EXISTED;

    public static String[] sourceTypeNames = { StringConstants.TEST_DATA, DataFileDriverType.ExcelFile.toString(),
            DataFileDriverType.CSV.toString(), DataFileDriverType.DBData.toString() };

    private String name;

    private String typeName = StringConstants.TEST_DATA;

    private String checkpointDescription = StringConstants.EMPTY;

    private FolderEntity parentFolder;

    private Text txtName;

    private Text txtDescription;

    private Combo comboSourceType;

    public NewCheckpointStartingPage(String name, FolderEntity parentFolder) {
        super(NewCheckpointStartingPage.class.getSimpleName(), StringConstants.CHECKPOINT,
                StringConstants.DIA_MSG_CREATE_CHECKPOINT);
        this.name = name;
        this.parentFolder = parentFolder;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(2, false));

        Label labelName = new Label(container, SWT.NONE);
        labelName.setText(StringConstants.NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtName.setText(getName());
        txtName.selectAll();

        Label labelDataSourceType = new Label(container, SWT.NONE);
        labelDataSourceType.setText(StringConstants.DIA_LBL_DATA_TYPE);

        comboSourceType = new Combo(container, SWT.READ_ONLY);
        comboSourceType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comboSourceType.setItems(sourceTypeNames);
        // default selection is Test Data
        comboSourceType.select(0);

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(StringConstants.DESCRIPTION);

        txtDescription = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridData descLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        descLayoutData.heightHint = 80;
        txtDescription.setLayoutData(descLayoutData);

        setControlListeners();
        setControl(container);
        setPageComplete(isComplete());
    }

    private void setControlListeners() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setName(((Text) e.getSource()).getText());
                setPageComplete(isComplete());
            }
        });

        comboSourceType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTypeName(((Combo) e.getSource()).getText());
            }
        });

        txtDescription.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setCheckpointDescription(((Text) e.getSource()).getText());
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            // trim and replace multiple space by single one
            name = name.trim().replaceAll("\\s+", " ");
        }
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCheckpointDescription() {
        return checkpointDescription;
    }

    public void setCheckpointDescription(String checkpointDescription) {
        this.checkpointDescription = checkpointDescription;
    }

    /**
     * Validate checkpoint name
     * 
     * @param name Checkpoint name
     * @throws InvalidNameException
     * @throws Exception
     */
    public void validateName(String name) throws Exception {
        if (!StringUtils.equalsIgnoreCase(EntityNameController.getInstance()
                .getAvailableName(name, parentFolder, false), name)) {
            throw new InvalidNameException(NAME_ALREADY_EXISTS);
        }
    }

    @Override
    protected boolean isComplete() {
        try {
            String checkpointName = getName();
            EntityNameController.getInstance().validateName(checkpointName);
            validateName(checkpointName);
            setErrorMessage(null);
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return false;
        }
    }

    @Override
    public Point getPageSize() {
        return getShell().computeSize(600, 350);
    }

}
