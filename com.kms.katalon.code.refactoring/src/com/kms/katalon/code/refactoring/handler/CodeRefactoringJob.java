package com.kms.katalon.code.refactoring.handler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.code.refactoring.filter.OldSettingFileNameFilter;
import com.kms.katalon.code.refactoring.setting.CodeRefactoringSettingStore;
import com.kms.katalon.core.setting.PropertySettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class CodeRefactoringJob extends Job {

	private ProjectEntity projectEntity;

	public CodeRefactoringJob(String name, ProjectEntity projectEntity) {
		super(name);
		this.projectEntity = projectEntity;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String projectDir = projectEntity.getFolderLocation();
			monitor.beginTask("Script migrating", 2);
			GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts("com.kms.qautomate.core",
					"com.kms.katalon.core", projectEntity);
			monitor.worked(1);

			File settingFolder = new File(projectDir, PropertySettingStore.ROOT_FOLDER_NAME);
			for (String fileName : settingFolder.list(new OldSettingFileNameFilter())) {
				FileUtils.moveFile(new File(settingFolder, fileName),
						new File(settingFolder, fileName.replace("qautomate", "katalon")));
			}
			monitor.worked(1);

			CodeRefactoringSettingStore.saveMigrated(projectDir);
			return Status.OK_STATUS;
		} catch (CoreException | IOException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
	}

}
