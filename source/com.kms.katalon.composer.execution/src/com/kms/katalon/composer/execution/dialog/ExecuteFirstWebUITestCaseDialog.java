package com.kms.katalon.composer.execution.dialog;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.helper.UserProfileHelper;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.execution.jobs.ExecuteTestCaseJob;
import com.kms.katalon.composer.quickstart.BaseQuickStartDialog;
import com.kms.katalon.composer.quickstart.BrowserSelect;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.webui.configuration.ChromeRunConfiguration;
import com.kms.katalon.execution.webui.configuration.EdgeChromiumRunConfiguration;
import com.kms.katalon.execution.webui.configuration.FirefoxRunConfiguration;
import com.kms.katalon.execution.webui.configuration.IERunConfiguration;
import com.kms.katalon.tracking.service.Trackings;

public class ExecuteFirstWebUITestCaseDialog extends BaseQuickStartDialog {

    private TestCaseEntity firstTestCase;

    private Text txtTestCase;

    private BrowserSelect browserSelect;

    interface BrowserSelectionCallback {
        void call(WebUIDriverType browser);
    }

    public ExecuteFirstWebUITestCaseDialog(Shell parentShell, TestCaseEntity firstTestCase) {
        super(parentShell);
        this.firstTestCase = firstTestCase;
    }

    @Override
    protected void createContent(Composite parent) {
        Composite body = ComponentBuilder.gridContainer(parent).gridMargin(50).gridVerticalSpacing(5).build();

        createTitle(body);
        createRunTestCaseComposite(body);

        Trackings.trackQuickStartRunOpen();
    }

    private void createTitle(Composite parent) {
        ComponentBuilder.label(parent)
                .text("Execute your newly created test case")
                .font(FontUtil.size(FontUtil.BOLD, FontUtil.SIZE_H3))
//                .center()
                .build();
    }

    private void createRunTestCaseComposite(Composite parent) {
        Composite runFirstTestCaseComposite = ComponentBuilder.gridContainer(parent)
                .gridMarginTop(30)
                .gridMarginBottom(20)
                .gridVerticalSpacing(10)
                .build();

        Composite configComposite = ComponentBuilder.gridContainer(runFirstTestCaseComposite, 3)
                .gridHorizontalSpacing(10)
                .build();

        Composite textWrapper = ComponentBuilder.gridContainer(configComposite, 1, SWT.BORDER).build();
        txtTestCase = ComponentBuilder.text(textWrapper)
                .gridMarginTop(5)
                .gridMarginLeft(5)
                .size(350, 22)
                .fontSize(FontUtil.SIZE_H5)
                .build();
        txtTestCase.setEnabled(false);

        browserSelect = new BrowserSelect(configComposite, SWT.NONE);

        ComponentBuilder.button(configComposite)
                .text("Run")
                .fontSize(FontUtil.SIZE_H3)
                .size(100, 30)
                .primaryButton()
                .image(ImageManager.getImage(IImageKeys.RIGHT_ARROW), SWT.RIGHT)
                .onClick((event) -> {
                    executeFirstTestCase();
                    okPressed();
                })
                .build();
    }

    private void executeFirstTestCase() {
        ExecuteTestCaseJob firstExecutionJob = new ExecuteTestCaseJob(StringConstants.HAND_JOB_LAUNCHING_TEST_CASE,
                firstTestCase, LaunchMode.RUN, UISynchronizeService.getInstance().getSync(),
                new AbstractExecutionHandler() {

                    @Override
                    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
                            throws IOException, ExecutionException, InterruptedException {
                        switch (browserSelect.getInput()) {
                            case CHROME_DRIVER:
                                return new ChromeRunConfiguration(projectDir);
                            case FIREFOX_DRIVER:
                                return new FirefoxRunConfiguration(projectDir);
                            case EDGE_CHROMIUM_DRIVER:
                                return new EdgeChromiumRunConfiguration(projectDir);
                            case IE_DRIVER:
                                return new IERunConfiguration(projectDir);
                            default:
                                return new ChromeRunConfiguration(projectDir);
                        }
                    }
                });
        firstExecutionJob.setUser(true);
        firstExecutionJob.schedule();
        firstExecutionJob.addJobChangeListener(new IJobChangeListener() {

            @Override
            public void sleeping(IJobChangeEvent event) {
            }

            @Override
            public void scheduled(IJobChangeEvent event) {
            }

            @Override
            public void running(IJobChangeEvent event) {
            }

            @Override
            public void done(IJobChangeEvent event) {
                List<ILauncher> lauchers = LauncherManager.getInstance().getRunningLaunchers();
                ILauncher firstExecution = lauchers.get(0);
                Thread waitForExecuteThread = new Thread(() -> {
                    while (firstExecution.getStatus() != LauncherStatus.DONE
                            && firstExecution.getStatus() != LauncherStatus.TERMINATED) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    if (firstExecution.getStatus() == LauncherStatus.DONE) {
                        UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
                        currentProfile.setDoneRunFirstTestCase(true);
                        UserProfileHelper.saveProfile(currentProfile);

                        UISynchronizeService.syncExec(() -> {
                            ILauncherResult result = firstExecution.getResult();
                            if (result.getReturnCode() == 0) {
                                ExecuteFirstTestSuccessfullyDialog congratulationDialog = new ExecuteFirstTestSuccessfullyDialog(
                                        Display.getCurrent().getActiveShell());
                                congratulationDialog.open();
                            } else {
                                ExecuteFirstTestUnsuccessfullyDialog troubleshotDialog = new ExecuteFirstTestUnsuccessfullyDialog(
                                        Display.getCurrent().getActiveShell());
                                troubleshotDialog.open();
                            }
                        });
                    }
                });
                waitForExecuteThread.start();
            }

            @Override
            public void awake(IJobChangeEvent event) {
            }

            @Override
            public void aboutToRun(IJobChangeEvent event) {
            }
        });
    }

    @Override
    protected String getTipContent() {
        return "Click the Run button on the menu bar to execute tests.";
    }

    @Override
    protected void createMoreTips(Composite tipsComposite) {
        addTip(ComponentBuilder.label(tipsComposite).size(5, 24).build());
        addTip(ComponentBuilder.image(tipsComposite, IImageKeys.TIP_RUN_BUTTON, 30).size(30, 24).build());
        addTip(ComponentBuilder.image(tipsComposite, IImageKeys.TIP_DEBUG_BUTTON, 35).size(30, 24).build());
    }

    @Override
    protected void setInput() {
        UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
        browserSelect.setInput(currentProfile.getPreferredBrowser());
        txtTestCase.setText(firstTestCase.getIdForDisplay());
    }

    @Override
    protected void okPressed() {
        Trackings.trackQuickStartStartRun(browserSelect.getInput().name());
        super.okPressed();
    }
}
