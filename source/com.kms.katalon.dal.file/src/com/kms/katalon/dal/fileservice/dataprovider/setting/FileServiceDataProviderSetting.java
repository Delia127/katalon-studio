package com.kms.katalon.dal.fileservice.dataprovider.setting;

import com.kms.katalon.dal.ICheckpointProvider;
import com.kms.katalon.dal.IDataFileDataProvider;
import com.kms.katalon.dal.IEntityDataProvider;
import com.kms.katalon.dal.IEntityNameProvider;
import com.kms.katalon.dal.IExportDataProvider;
import com.kms.katalon.dal.IFolderDataProvider;
import com.kms.katalon.dal.IGlobalVariableDataProvider;
import com.kms.katalon.dal.IImportDataProvider;
import com.kms.katalon.dal.IProjectDataProvider;
import com.kms.katalon.dal.IReportDataProvider;
import com.kms.katalon.dal.ISystemFileDataProvider;
import com.kms.katalon.dal.ITestCaseDataProvider;
import com.kms.katalon.dal.ITestListenerDataProvider;
import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.IUserFileDataProvider;
import com.kms.katalon.dal.IWebElementDataProvider;
import com.kms.katalon.dal.IWindowsElementDataProvider;
import com.kms.katalon.dal.TestSuiteCollectionDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.CheckpointFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.DataFileFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.EntityFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.EntityNameFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ExportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.FolderFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.GlobalVariableFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ImportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ProjectFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ReportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.SystemFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestCaseFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestListenerFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestSuiteCollectionFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestSuiteFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.UserFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.WebElementFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.WindowsElementFileServiceDataProvider;
import com.kms.katalon.dal.setting.IDataProviderSetting;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.PKType;
import com.kms.katalon.entity.project.ProjectEntity;

public class FileServiceDataProviderSetting implements IDataProviderSetting {

    @Override
    public void reset(String configValue) throws Exception {
        // TODO initialize
    }

    @Override
    public ITestCaseDataProvider getTestCaseDataProvider() {
        return new TestCaseFileServiceDataProvider();
    }

    @Override
    public IFolderDataProvider getFolderDataProvider() {
        return new FolderFileServiceDataProvider();
    }

    @Override
    public ITestSuiteDataProvider getTestSuiteDataProvider() {
        return new TestSuiteFileServiceDataProvider();
    }

    @Override
    public IWebElementDataProvider getWebElementDataProvider() {
        return new WebElementFileServiceDataProvider();
    }

    @Override
    public IProjectDataProvider getProjectDataProvider() {
        return new ProjectFileServiceDataProvider();
    }

    @Override
    public IImportDataProvider getImportDataProvider() {
        return new ImportFileServiceDataProvider();
    }

    @Override
    public IExportDataProvider getExportDataProvider() {
        return new ExportFileServiceDataProvider();
    }

    @Override
    public IDataFileDataProvider getDataFileDataProvider() {
        return new DataFileFileServiceDataProvider();
    }

    @Override
    public PKType getEntityPKType() {
        return PKType.Path;
    }

    @Override
    public void setCurrentProject(ProjectEntity project) {
        DataProviderState.getInstance().setCurrentProject(project);
    }

    @Override
    public IReportDataProvider getReportDataProvider() {
        return new ReportFileServiceDataProvider();
    }

    @Override
    public String getEntityPk(Entity entity) {
        return entity.getId();
    }

    @Override
    public IGlobalVariableDataProvider getGlobalVariableDataProvider() {
        return new GlobalVariableFileServiceDataProvider();
    }

    @Override
    public IEntityNameProvider getEntityNameProvider() {
        return new EntityNameFileServiceDataProvider();
    }

    @Override
    public TestSuiteCollectionDataProvider getTestSuiteCollectionDataProvider() {
        return new TestSuiteCollectionFileServiceDataProvider();
    }

    @Override
    public ICheckpointProvider getCheckpointDataProvider() {
        return new CheckpointFileServiceDataProvider();
    }

    @Override
    public IEntityDataProvider getEntityDataProvider() {
        return new EntityFileServiceDataProvider();
    }

    @Override
    public ITestListenerDataProvider getTestListenerDataProvider() {
        return new TestListenerFileServiceDataProvider();
    }

    @Override
    public ISystemFileDataProvider getSystemFileDataProvider() {
        return new SystemFileServiceDataProvider();
    }
    
    @Override
    public IUserFileDataProvider getUserFileDataProvider() {
        return new UserFileServiceDataProvider();
    }

    @Override
    public IWindowsElementDataProvider getWindowsElementDataProvider() {
        return new WindowsElementFileServiceDataProvider();
    }
}
