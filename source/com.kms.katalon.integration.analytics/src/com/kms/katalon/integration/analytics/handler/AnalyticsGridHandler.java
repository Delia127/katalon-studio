package com.kms.katalon.integration.analytics.handler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.util.ZipHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsRunConfiguration;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestSuiteCollection;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;

public class AnalyticsGridHandler {

    public static AnalyticsTestProject uploadProject(final String serverUrl, final String email, final String password,
            final String nameFileZip, final AnalyticsProject sellectProject, final String folderCurrentProject,
            ProgressMonitorDialog monitorDialog) {
        AnalyticsTestProject testProject = new AnalyticsTestProject();
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_TITLE_UPLOAD_CODE, 5);
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

                            testProject.setId(newTestProject.getId());
                            testProject.setName(newTestProject.getName());
                            testProject.setDescription(newTestProject.getDescription());
                            testProject.setTestSuiteCollections(newTestProject.getTestSuiteCollections());

                        } catch (Exception exception) {
                            throw new InvocationTargetException(exception);
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

    public static void createTestPlan(final String serverUrl, final String email, final String password,
            final String testPlanName, final AnalyticsProject project, final AnalyticsTestProject testProject,
            final AnalyticsTestSuiteCollection tsc, ProgressMonitorDialog monitorDialog) {
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                    try {
                        monitor.beginTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_CREATE_TEST_PLAN, 1);

                        AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);

                        Long teamId = project.getTeamId();
                        Long projectId = project.getId();
                        AnalyticsRunConfiguration analyticsRunConfiguration = AnalyticsApiProvider.createTestPlan(
                                serverUrl, projectId, teamId, testPlanName, testProject.getId(),
                                AnalyticsStringConstants.ANALYTICS_CLOUD_TYPE_CIRCLE_CI,
                                AnalyticsStringConstants.ANALYTICS_CONFIG_TYPE_TEST_SUITE_COLLECTION, tsc.getId(),
                                token.getAccess_token());
                        monitor.worked(1);
                        monitor.setTaskName("");
                        URIBuilder builder = new URIBuilder(serverUrl);
                        builder.setScheme(AnalyticsStringConstants.ANALYTICS_SCHEME_HTTPS);
                        builder.setPath(String.format(AnalyticsStringConstants.ANALYTICS_URL_TEST_PLAN, teamId,
                                projectId, analyticsRunConfiguration.getId()));
                        Program.launch(builder.toString());

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

    }
}
