package com.kms.katalon.dal.fileservice.dataprovider.setting;

import com.kms.katalon.dal.IDataFileDataProvider;
import com.kms.katalon.dal.IEntityNameProvider;
import com.kms.katalon.dal.IExportDataProvider;
import com.kms.katalon.dal.IFolderDataProvider;
import com.kms.katalon.dal.IGlobalVariableDataProvider;
import com.kms.katalon.dal.IImportDataProvider;
import com.kms.katalon.dal.IProjectDataProvider;
import com.kms.katalon.dal.IReportDataProvider;
import com.kms.katalon.dal.ITestCaseDataProvider;
import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.IWebElementDataProvider;
import com.kms.katalon.dal.TestSuiteCollectionDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.DataFileFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.EntityNameFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ExportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.FolderFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.GlobalVariableFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ImportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ProjectFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.ReportFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestCaseFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestRunFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.TestSuiteFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.WebElementFileServiceDataProvider;
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
        return new TestRunFileServiceDataProvider();
    }

}
