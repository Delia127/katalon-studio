package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class NewCheckpointWizard extends Wizard {

    private NewCheckpointStartingPage startingPage;

    private NewCheckpointTestDataPage testDataPage;

    private NewCheckpointExcelPage excelPage;

    private NewCheckpointCsvPage csvPage;

    private NewCheckpointDatabasePage databasePage;

    private FolderEntity parentFolder;

    private String name;

    private CheckpointSourceInfo sourceInfo;

    public NewCheckpointWizard(String name, FolderEntity parentFolder) {
        setWindowTitle(StringConstants.DIA_WINDOW_TITLE_NEW);
        this.parentFolder = parentFolder;
        this.name = name;
    }

    @Override
    public void addPages() {
        startingPage = new NewCheckpointStartingPage(name, parentFolder);
        testDataPage = new NewCheckpointTestDataPage();
        excelPage = new NewCheckpointExcelPage();
        csvPage = new NewCheckpointCsvPage();
        databasePage = new NewCheckpointDatabasePage();

        addPage(startingPage);
        addPage(testDataPage);
        addPage(excelPage);
        addPage(csvPage);
        addPage(databasePage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (super.getNextPage(page) == null || !startingPage.equals(page)) {
            return null;
        }

        String typeName = startingPage.getTypeName();
        if (DataFileDriverType.ExcelFile.toString().equals(typeName)) {
            return excelPage;
        }

        if (DataFileDriverType.CSV.toString().equals(typeName)) {
            return csvPage;
        }

        if (DataFileDriverType.DBData.toString().equals(typeName)) {
            return databasePage;
        }

        return testDataPage;
    }

    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        if (super.getPreviousPage(page) == null) {
            return null;
        }
        return startingPage;
    }

    @Override
    public boolean canFinish() {
        return getContainer().getCurrentPage().isPageComplete();
    }

    @Override
    public boolean performFinish() {
        name = startingPage.getName();
        String typeName = startingPage.getTypeName();
        if (DataFileDriverType.ExcelFile.toString().equals(typeName)) {
            sourceInfo = excelPage.getSourceInfo();
        } else if (DataFileDriverType.CSV.toString().equals(typeName)) {
            sourceInfo = csvPage.getSourceInfo();
        } else if (DataFileDriverType.DBData.toString().equals(typeName)) {
            sourceInfo = databasePage.getSourceInfo();
        } else {
            sourceInfo = testDataPage.getSourceInfo();
        }
        return true;
    }

    public CheckpointEntity getCheckpoint() {
        CheckpointEntity checkpoint = new CheckpointEntity();
        checkpoint.setName(name);
        checkpoint.setDescription(startingPage.getCheckpointDescription());
        checkpoint.setParentFolder(parentFolder);
        checkpoint.setProject(parentFolder.getProject());
        checkpoint.setSourceInfo(sourceInfo);
        return checkpoint;
    }

}
