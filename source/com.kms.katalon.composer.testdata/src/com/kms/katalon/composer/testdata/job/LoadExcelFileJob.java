package com.kms.katalon.composer.testdata.job;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.core.testdata.reader.AppPOI;

public class LoadExcelFileJob extends Job {

    private String fSourceUrl;
    private String[] fSheetNames;

    public LoadExcelFileJob(String sourceUrl) {
        super(StringConstants.JOB_LOAD_EXCL_TITLE);
        fSourceUrl = sourceUrl;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            monitor.beginTask(StringConstants.JOB_LOAD_EXCL_TASK_NAME, 1);
            AppPOI appoi = new AppPOI(fSourceUrl);
            fSheetNames = appoi.getSheetNames();
            return Status.OK_STATUS;
        } catch (IOException e) {
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }

    public String[] getSheetNames() {
        return fSheetNames;
    }
}
