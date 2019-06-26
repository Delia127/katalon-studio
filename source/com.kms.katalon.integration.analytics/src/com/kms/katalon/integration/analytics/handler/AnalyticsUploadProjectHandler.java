package com.kms.katalon.integration.analytics.handler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.util.ZipHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestSuiteCollection;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;

public class AnalyticsUploadProjectHandler {

    public static AnalyticsTestProject uploadProject(final String serverUrl, final String email, final String password,
            final String nameFileZip, final AnalyticsProject sellectProject, final String folderCurrentProject,
            ProgressMonitorDialog monitorDialog) {
        AnalyticsTestProject testProject = new AnalyticsTestProject();
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_TITLE_UPLOAD_CODE, 6);
                        monitor.subTask(IntegrationAnalyticsMessages.STORE_CODE_COMPRESSING_PROJECT);

                        String tempDir = ProjectController.getInstance().getTempDir();
                        File zipTeamFile = new File(tempDir, nameFileZip + ".zip");
                        try {
                            ZipHelper.Compress(folderCurrentProject, zipTeamFile.toString());

                            monitor.subTask(IntegrationAnalyticsMessages.STORE_CODE_REQUEST_SERVER);
                            monitor.worked(2);

                            AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                            AnalyticsUploadInfo uploadInfo = AnalyticsApiProvider.getUploadInfo(serverUrl,
                                    token.getAccess_token(), sellectProject.getId());
                            AnalyticsApiProvider.uploadFile(uploadInfo.getUploadUrl(), zipTeamFile);

                            monitor.subTask(IntegrationAnalyticsMessages.STORE_CODE_GET_TEAM_PROJECT);
                            monitor.worked(1);

                            long timestamp = System.currentTimeMillis();
                            Long teamId = sellectProject.getTeamId();
                            Long projectId = sellectProject.getId();

                            monitor.subTask(IntegrationAnalyticsMessages.STORE_CODE_UPLOAD);
                            monitor.worked(1);
                            AnalyticsTestProject newTestProject = AnalyticsApiProvider.uploadTestProject(serverUrl,
                                    projectId, teamId, timestamp, nameFileZip, zipTeamFile.toString(),
                                    zipTeamFile.getName().toString(), uploadInfo.getPath(), token.getAccess_token());

                            //here
                            AnalyticsTestSuiteCollection tsc = newTestProject.getTestSuiteCollections().get(0);
                            AnalyticsApiProvider.createTestPlan(serverUrl, projectId, teamId, 
                                    tsc.getName(), 
                                    newTestProject.getId(), "CIRCLE_CI", "TSC", tsc.getId(), token.getAccess_token());
                            
                            testProject.setId(newTestProject.getId());

                            monitor.subTask(IntegrationAnalyticsMessages.STORE_CODE_OPEN_BROWSER);
                            monitor.worked(1);

                            URIBuilder builder = new URIBuilder(serverUrl);
                            builder.setScheme("https");
                            builder.setPath("/team/" + teamId.toString() + "/project/" + projectId.toString()
                                    + "/test-project");
                            Program.launch(builder.toString());
                        } catch (Exception exception) {
                            MultiStatusErrorDialog.showErrorDialog(exception,
                                    IntegrationAnalyticsMessages.STORE_CODE_ERROR_COMPRESS, exception.getMessage());
                        } finally {
                            zipTeamFile.deleteOnExit();
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException exception) {
            MultiStatusErrorDialog.showErrorDialog(exception, ComposerAnalyticsStringConstants.ERROR,
                    exception.getMessage());
        }
        return testProject;
    }
}
