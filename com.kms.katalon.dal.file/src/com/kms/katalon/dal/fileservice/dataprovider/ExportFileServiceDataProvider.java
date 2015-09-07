package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.dal.IExportDataProvider;
import com.kms.katalon.dal.fileservice.manager.ExportFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.ProjectFileServiceManager;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ExportFileServiceDataProvider implements IExportDataProvider {
	private static Map<String, ExportFileServiceManager> exportMapping;

	public ExportFileServiceDataProvider() {
		if (exportMapping == null) {
			exportMapping = new HashMap<String, ExportFileServiceManager>();
		}
	}

	@Override
	public boolean exportProject(String projectValue, String exportGUID,
			String exportFolder) throws Exception {

		ProjectEntity project = ProjectFileServiceManager
				.getProject(projectValue);
		if (project != null) {
			try {
				ExportFileServiceManager exportManager = new ExportFileServiceManager();
				exportMapping.put(exportGUID, exportManager);
				return exportManager.exportProject(project, exportFolder
						+ File.separator + project.getName() + "_"
						+ getCurrentDateTime());
			} finally {
				exportMapping.remove(exportGUID);
			}
		}
		return false;
	}

	@Override
	public boolean exportProject(String projectValue, String exportGUID,
			List<TestCaseEntity> testCases, String exportFolder)
			throws Exception {
		ProjectEntity project = ProjectFileServiceManager
				.getProject(projectValue);
		if (project != null) {
			try {
				ExportFileServiceManager exportManager = new ExportFileServiceManager();
				exportMapping.put(exportGUID, exportManager);
				return exportManager.exportSeletectTestCases(testCases, project, exportFolder
						+ File.separator + project.getName() + "_"
						+ getCurrentDateTime());
			} finally {
				exportMapping.remove(exportGUID);
			}
		}
		return false;
	}

	@Override
	public Integer getExportProgress(String exportGUID) throws Exception {
		ExportFileServiceManager exportManager = exportMapping.get(exportGUID);
		if (exportManager != null) {
			return exportManager.getProgress();
		}
		return 0;
	}

	@Override
	public void cancelExport(String exportGUID) throws Exception {
		ExportFileServiceManager exportManager = exportMapping.get(exportGUID);
		if (exportManager != null) {
			exportManager.cancelExport();
		}
	}

	private static String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

}
