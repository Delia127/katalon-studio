package com.kms.katalon.composer.testsuite.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.DefaultRerunSetting.RetryStrategyValue;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.tracking.service.Trackings;

public class TestSuiteRetryUiPart {
    
    private String RETRY_DOCS_URL = "https://docs.katalon.com/katalon-studio/docs/test-suite.html#modify-execution-information";

    private static final int MINIMUM_COMPOSITE_SIZE = 300;

    private Text txtRetryAfterExecuteAll, txtRetryImmediately;

    private Button radioBtnRetryAllExecutions, radioBtnRetryFailedExecutionsOnly, radioBtnRetryImmediately;

    private Button radioBtnRetryAfterExecuteAll;
    
    private Label linkToRetryDocs1, linkToRetryDocs2;

    private TestSuiteRetryUiAdapter adapter;

    public TestSuiteRetryUiPart(TestSuiteRetryUiAdapter adapter) {
        this.adapter = adapter;
    }

    private TestSuiteEntity getTestSuite() {
        return adapter.getTestSuite();
    }

    public void registerRetryControlListeners(VerifyListener verifyNumberListener) {
        if (getTestSuite() == null) {
            return;
        }
        txtRetryAfterExecuteAll.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    int rerun = Integer.parseInt(text);
                    // limit to 100 times only
                    if (rerun > 100) {
                        rerun = 100;
                        ((Text) e.getSource()).setText(String.valueOf(rerun));
                    }
                    getTestSuite().setNumberOfRerun(rerun);
                    radioBtnRetryAllExecutions.setEnabled(!(rerun == 0));
                    radioBtnRetryFailedExecutionsOnly.setEnabled(!(rerun == 0));
                } catch (NumberFormatException ex) {}
            }
        });

        txtRetryAfterExecuteAll.addVerifyListener(verifyNumberListener);

        txtRetryImmediately.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    int rerun = Integer.parseInt(text);
                    // limit to 100 times only
                    if (rerun > 100) {
                        rerun = 100;
                        ((Text) e.getSource()).setText(String.valueOf(rerun));
                    }
                    getTestSuite().setNumberOfRerun(rerun);
                } catch (NumberFormatException ex) {}
            }
        });

        txtRetryImmediately.addVerifyListener(verifyNumberListener);

        radioBtnRetryImmediately.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (LicenseUtil.isFreeLicense()) {
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.RERUN_IMMEDIATELY);
                    radioBtnRetryImmediately.setSelection(false);
                }
                boolean value = radioBtnRetryImmediately.getSelection();
                if (value) {
                    radioBtnRetryAfterExecuteAll.setSelection(false);
                    radioBtnRetryAllExecutions.setSelection(false);
                    radioBtnRetryFailedExecutionsOnly.setSelection(false);
                    txtRetryImmediately.setEnabled(true);
                    getTestSuite().setRerunFailedTestCasesOnly(false);
                    getTestSuite().setRerunFailedTestCasesTestDataOnly(false);
                    enableRetryAfterExecuteAll(false);
                }
                getTestSuite().setRerunImmediately(value);
                setDirty(true);
            }
        });

        radioBtnRetryAfterExecuteAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean value = radioBtnRetryAfterExecuteAll.getSelection();
                if (value) {
                    radioBtnRetryImmediately.setSelection(false);
                    txtRetryImmediately.setEnabled(false);
                    getTestSuite().setRerunImmediately(false);
                    enableRetryAfterExecuteAll(true);
                }
                setDirty(true);
            }
        });

        radioBtnRetryAllExecutions.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean val = radioBtnRetryAllExecutions.getSelection();
                if (val) {
                    getTestSuite().setRerunFailedTestCasesOnly(false);
                    getTestSuite().setRerunFailedTestCasesTestDataOnly(false);
                }
                setDirty(true);
            }
        });

        radioBtnRetryFailedExecutionsOnly.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean val = radioBtnRetryFailedExecutionsOnly.getSelection();
                getTestSuite().setRerunFailedTestCasesOnly(val);
                getTestSuite().setRerunFailedTestCasesTestDataOnly(val);
                setDirty(true);
            }
        });
        
        MouseListener openDocsMouseHandler = new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch(RETRY_DOCS_URL);
                Trackings.trackOpenHelp(RETRY_DOCS_URL);
            }

            @Override
            public void mouseDown(MouseEvent e) {
                // Do nothing
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // Do nothing
            }
        };
        
        linkToRetryDocs1.addMouseListener(openDocsMouseHandler);
        linkToRetryDocs2.addMouseListener(openDocsMouseHandler);
    }

    public void enableRetryAfterExecuteAll(boolean val) {
        txtRetryAfterExecuteAll.setEnabled(val);
        radioBtnRetryAllExecutions.setEnabled(val);
        radioBtnRetryFailedExecutionsOnly.setEnabled(val);
    }

    public void createRetryComposite(Composite compositePageLoadTimeout) {
        Composite compositeLastRunAndReRun = new Composite(compositePageLoadTimeout, SWT.NONE);
        GridData gdCompositeTestDataAndLastRun = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositeTestDataAndLastRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeLastRunAndReRun.setLayoutData(gdCompositeTestDataAndLastRun);
        GridLayout glCompositeTestDataAndLastRun = new GridLayout(4, false);
        compositeLastRunAndReRun.setLayout(glCompositeTestDataAndLastRun);

        Composite grpRetryExecution = new Composite(compositeLastRunAndReRun, SWT.NONE);
        grpRetryExecution.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        GridLayout gl_grpRetryExecution = new GridLayout(4, false);
        gl_grpRetryExecution.marginWidth = 5;
        gl_grpRetryExecution.marginHeight = 5;
        grpRetryExecution.setLayout(gl_grpRetryExecution);

        radioBtnRetryImmediately = new Button(grpRetryExecution, SWT.RADIO);
        GridData gdLblStopImmediately = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblStopImmediately.widthHint = 150;
        radioBtnRetryImmediately.setLayoutData(gdLblStopImmediately);
        radioBtnRetryImmediately.setText(StringConstants.PA_LBL_RETRY_IMMEDIATELY);
        
        linkToRetryDocs1 = new Label(grpRetryExecution, SWT.NONE);
        linkToRetryDocs1.setImage(ImageManager.getImage(IImageKeys.HELP_16));
        linkToRetryDocs1.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
        
        txtRetryImmediately = new Text(grpRetryExecution, SWT.BORDER);
        GridData gdTxtStopImmediately = new GridData(SWT.RIGHT, SWT.FILL, true, false, 2, 1);
        gdTxtStopImmediately.widthHint = 20;
        txtRetryImmediately.setLayoutData(gdTxtStopImmediately);
        txtRetryImmediately.setTextLimit(3);
        
        Composite grpRetryExecutions = new Composite(compositeLastRunAndReRun, SWT.NONE);
        grpRetryExecutions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        GridLayout gl_grpRetryExecutions = new GridLayout(4, false);
        gl_grpRetryExecutions.marginWidth = 5;
        gl_grpRetryExecutions.marginHeight = 5;
        grpRetryExecutions.setLayout(gl_grpRetryExecutions);

        radioBtnRetryAfterExecuteAll = new Button(grpRetryExecutions, SWT.RADIO);
        GridData gdLblReRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblReRun.widthHint = 150;
        radioBtnRetryAfterExecuteAll.setLayoutData(gdLblReRun);
        radioBtnRetryAfterExecuteAll.setText(StringConstants.PA_LBL_RETRY_AFTER_EXECUTE_ALL);
        radioBtnRetryAfterExecuteAll.setToolTipText(StringConstants.PA_LBL_TOOLTIP_RETRY);
        
        linkToRetryDocs2 = new Label(grpRetryExecutions, SWT.NONE);
        linkToRetryDocs2.setImage(ImageManager.getImage(IImageKeys.HELP_16));
        linkToRetryDocs2.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

        txtRetryAfterExecuteAll = new Text(grpRetryExecutions, SWT.BORDER);
        GridData gdTxtRerun = new GridData(SWT.RIGHT, SWT.FILL, false, false, 2, 1);
        gdTxtRerun.widthHint = 20;
        txtRetryAfterExecuteAll.setLayoutData(gdTxtRerun);
        txtRetryAfterExecuteAll.setTextLimit(3);

        Composite grpRetryExecutionsChildComposite = new Composite(grpRetryExecutions, SWT.NONE);
        grpRetryExecutionsChildComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        GridLayout gl_grpRetryExecutionsChildComposite = new GridLayout(4, false);
        gl_grpRetryExecutionsChildComposite.marginWidth = 5;
        gl_grpRetryExecutionsChildComposite.marginHeight = 5;
        gl_grpRetryExecutionsChildComposite.marginLeft = 45;
        grpRetryExecutionsChildComposite.setLayout(gl_grpRetryExecutionsChildComposite);

        radioBtnRetryAllExecutions = new Button(grpRetryExecutionsChildComposite, SWT.RADIO);
        GridData gdRerunTestCase = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        radioBtnRetryAllExecutions.setLayoutData(gdRerunTestCase);
        radioBtnRetryAllExecutions.setText(StringConstants.PA_LBL_RETRY_ALL_EXECUTIONS);

        radioBtnRetryFailedExecutionsOnly = new Button(grpRetryExecutionsChildComposite, SWT.RADIO);
        GridData gdRerunTestCaseTestData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        radioBtnRetryFailedExecutionsOnly.setLayoutData(gdRerunTestCaseTestData);
        radioBtnRetryFailedExecutionsOnly.setText(StringConstants.PA_LBL_RETRY_FAILED_EXECUTIONS);
    }

    public void syncRetryControlStatesWithTestSuiteInfo(TestSuiteEntity testSuite) {
        boolean shouldRetryFailedExecutions = (testSuite.isRerunFailedTestCasesOnly()
                || testSuite.isRerunFailedTestCasesAndTestDataOnly());
        boolean shouldRetryImmediately = testSuite.isRerunImmediately();
        boolean shouldRetryAllExecutions = !(testSuite.isRerunFailedTestCasesOnly()
                || testSuite.isRerunFailedTestCasesAndTestDataOnly());
        boolean shouldRetryAfterExecuteAll = (shouldRetryAllExecutions || shouldRetryFailedExecutions)
                && !shouldRetryImmediately;

        // Shouldn't retry immediately for free users
        shouldRetryImmediately = (LicenseUtil.isNotFreeLicense() && shouldRetryImmediately);

        radioBtnRetryAfterExecuteAll.setSelection(shouldRetryAfterExecuteAll);
        radioBtnRetryImmediately.setSelection(shouldRetryImmediately);
        radioBtnRetryAllExecutions.setSelection(shouldRetryAllExecutions);
        radioBtnRetryFailedExecutionsOnly.setSelection(shouldRetryFailedExecutions);

        txtRetryImmediately.setEnabled(shouldRetryImmediately);
        enableRetryAfterExecuteAll(shouldRetryAfterExecuteAll);

        if (shouldRetryAfterExecuteAll) {
            txtRetryAfterExecuteAll.setText(String.valueOf(testSuite.getNumberOfRerun()));
        }
        if (shouldRetryImmediately) {
            txtRetryImmediately.setText(String.valueOf(testSuite.getNumberOfRerun()));
        }
    }

    public void synRetryControlStatesByDescription(RetryControlStateDescription description) {
        boolean shouldRetryImmediately = (LicenseUtil.isNotFreeLicense()
                && description.getRetryStrategyValue().equals(RetryStrategyValue.immediately));
        boolean shouldRetryAfterExecuteAll = !shouldRetryImmediately;
        boolean shouldRetryAllExecutions = description.getRetryStrategyValue().equals(RetryStrategyValue.allExecutions);
        boolean shouldRetryFailedExecutions = description.getRetryStrategyValue()
                .equals(RetryStrategyValue.failedExecutions);
        radioBtnRetryAfterExecuteAll.setSelection(shouldRetryAfterExecuteAll);
        radioBtnRetryImmediately.setSelection(shouldRetryImmediately);
        radioBtnRetryAllExecutions.setSelection(shouldRetryAllExecutions);
        radioBtnRetryFailedExecutionsOnly.setSelection(shouldRetryFailedExecutions);

        txtRetryImmediately.setEnabled(shouldRetryImmediately);
        enableRetryAfterExecuteAll(shouldRetryAfterExecuteAll);

        if (shouldRetryAfterExecuteAll) {
            txtRetryAfterExecuteAll.setText(String.valueOf(description.getRetryNumber()));
        }
        if (shouldRetryImmediately) {
            txtRetryImmediately.setText(String.valueOf(description.getRetryNumber()));
        }
    }
    
    public int getRetryNumber() {
        if (radioBtnRetryAfterExecuteAll.getSelection()) {
            return Integer.valueOf(txtRetryAfterExecuteAll.getText());
        }
        return Integer.valueOf(txtRetryImmediately.getText());
    }
    
    public RetryStrategyValue getRetryStrategy() {
        if (radioBtnRetryAllExecutions.getSelection()) {
            return RetryStrategyValue.allExecutions;
        } else if (radioBtnRetryFailedExecutionsOnly.getSelection()) {
            return RetryStrategyValue.failedExecutions;
        } else if (radioBtnRetryImmediately.getSelection()) {
            return RetryStrategyValue.immediately;
        }
        return RetryStrategyValue.failedExecutions;
    }

    private void setDirty(boolean b) {
        adapter.setDirty(b);
    }

    public static class RetryControlStateDescription {
        private int retryNumber;

        private RetryStrategyValue retryStrategyValue;

        public RetryControlStateDescription(int retryNum, RetryStrategyValue retryStrategyValue) {
            this.retryNumber = retryNum;
            this.retryStrategyValue = retryStrategyValue;
        }

        public int getRetryNumber() {
            return retryNumber;
        }

        public RetryStrategyValue getRetryStrategyValue() {
            return retryStrategyValue;
        }
    }

}
