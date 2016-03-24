package com.kms.katalon.composer.testdata.job;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.reader.ExcelFactory;

public class LoadExcelFileJob extends Job {

    private String sourceUrl;
    private boolean hasHeaders;
    private ExcelData excelData;

    public LoadExcelFileJob(String sourceUrl, boolean hasHeaders) {
        super(StringConstants.JOB_LOAD_EXCL_TITLE);
        this.sourceUrl = sourceUrl;
        this.hasHeaders = hasHeaders;
        excelData = null;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            monitor.beginTask(StringConstants.JOB_LOAD_EXCL_TASK_NAME, IProgressMonitor.UNKNOWN);
            excelData = ExcelFactory.getExcelData(sourceUrl, hasHeaders);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            return Status.OK_STATUS;
        } catch (IOException e) {
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    public ExcelData getExcelData() {
        return excelData;
    }
}
