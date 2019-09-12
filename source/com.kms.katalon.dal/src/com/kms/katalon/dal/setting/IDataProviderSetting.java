package com.kms.katalon.dal.setting;

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
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.PKType;
import com.kms.katalon.entity.project.ProjectEntity;

public interface IDataProviderSetting {

    public void reset(String configValue) throws Exception;

    public ITestCaseDataProvider getTestCaseDataProvider();

    public IFolderDataProvider getFolderDataProvider();

    public ITestSuiteDataProvider getTestSuiteDataProvider();

    public TestSuiteCollectionDataProvider getTestSuiteCollectionDataProvider();

    public IWebElementDataProvider getWebElementDataProvider();

    public IProjectDataProvider getProjectDataProvider();

    public IImportDataProvider getImportDataProvider();

    public IExportDataProvider getExportDataProvider();

    public IDataFileDataProvider getDataFileDataProvider();

    public PKType getEntityPKType();

    public void setCurrentProject(ProjectEntity project);

    public IReportDataProvider getReportDataProvider();

    public String getEntityPk(Entity entity);

    public IGlobalVariableDataProvider getGlobalVariableDataProvider();

    public IEntityNameProvider getEntityNameProvider();

    public ICheckpointProvider getCheckpointDataProvider();

    public IEntityDataProvider getEntityDataProvider();

    public ITestListenerDataProvider getTestListenerDataProvider();
    
    public ISystemFileDataProvider getSystemFileDataProvider();
    
    public IUserFileDataProvider getUserFileDataProvider();

    public IWindowsElementDataProvider getWindowsElementDataProvider();
}
