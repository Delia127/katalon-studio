package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.credential.IQTestToken;
import com.kms.katalon.integration.qtest.credential.impl.QTestCredentialImpl;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class ProjectChoosingWizardPage extends AbstractWizardPage implements QTestWizardPage {
    // Control
    private Group grpQtestProjects;

    // Field
    private List<QTestProject> qTestProjects;

    private String fServerUrl;

    private IQTestToken fToken;

    private QTestProject selectedQTestProject;

    private Composite connectingComposite;

    private InputStream inputStream;

    private GifCLabel connectingLabel;

    private Composite composite;

    private ScrolledComposite scrolledComposite;

    public ProjectChoosingWizardPage() {
        fServerUrl = "";
    }

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_PROJECT_TITLE;
    }

    @Override
    public boolean canFlipToNextPage() {
        return selectedQTestProject != null;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        composite = new Composite(scrolledComposite, SWT.NONE);

        composite.setLayout(new GridLayout(1, false));

        Label lblHeader = new Label(composite, SWT.NONE);
        GridData gdLblHeader = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdLblHeader.horizontalIndent = 5;
        lblHeader.setLayoutData(gdLblHeader);
        lblHeader.setText(StringConstants.WZ_P_PROJECT_INFO);

        connectingComposite = new Composite(composite, SWT.NONE);
        GridLayout glConnectingComposite = new GridLayout(2, false);
        glConnectingComposite.marginWidth = 0;
        connectingComposite.setLayout(glConnectingComposite);
        connectingComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        connectingLabel = new GifCLabel(connectingComposite, SWT.NONE);
        connectingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Label lblConnectingStatus = new Label(connectingComposite, SWT.NONE);
        lblConnectingStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblConnectingStatus.setText(StringConstants.CM_CONNECTING);

        grpQtestProjects = new Group(composite, SWT.NONE);
        grpQtestProjects.setText(StringConstants.WZ_P_PROJECT_LIST);
        grpQtestProjects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        grpQtestProjects.setLayout(new GridLayout(1, false));

        scrolledComposite.setContent(composite);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.addListener(SWT.Resize, event -> {
            scrolledComposite.setMinSize(grpQtestProjects.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        });
      
        
        
    
    }


    
    private void updateProjectRadioButtons() {
        if (qTestProjects == null) {
            return;
        }

        for (QTestProject qTestProject : qTestProjects) {
            Button radioButton = new Button(grpQtestProjects, SWT.RADIO);
            radioButton.setText(qTestProject.getName());
            radioButton.setData(qTestProject);

            if (qTestProject.equals(selectedQTestProject)) {
                radioButton.setSelection(true);
            }

            radioButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (e.getSource() != null && (e.getSource() instanceof Button)) {
                        updateSelectedProject((Button) e.getSource());
                    }
                }
            });
        }

        grpQtestProjects.getParent().layout(true);
    }

    private void updateSelectedProject(Button button) {
        selectedQTestProject = (QTestProject) button.getData();
        firePageChanged();
    }

    private void setConnectingCompositeVisible(boolean isConnectingCompositeVisible) {
        if (isConnectingCompositeVisible) {
            try {
                inputStream = ImageConstants.URL_16_LOADING.openStream();
                connectingLabel.setGifImage(inputStream);
                connectingComposite.layout(true, true);
            } catch (IOException ex) {} finally {
                if (inputStream != null) {
                    closeQuietlyWithLog(inputStream);
                    inputStream = null;
                }
            }
        } else {
            if (inputStream != null) {
                closeQuietlyWithLog(inputStream);
                inputStream = null;
            }
        }
        connectingComposite.setVisible(isConnectingCompositeVisible);
        ((GridData) connectingComposite.getLayoutData()).exclude = !isConnectingCompositeVisible;
        connectingComposite.getParent().layout(true, true);
    }

    @Override
    public void registerControlModifyListeners() {
        composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                closeQuietlyWithLog(inputStream);
            }
        });
    }

    @Override
    public Map<String, Object> storeControlStates() {
        Map<String, Object> sharedData = new HashMap<String, Object>();
        sharedData.put("qTestProjects", qTestProjects);
        sharedData.put("qTestProject", selectedQTestProject);
        return sharedData;
    }

    @Override
    public void setInput(final Map<String, Object> sharedData) {
        final IQTestToken token = (IQTestToken) sharedData.get(QTestSettingStore.TOKEN_PROPERTY);
        final String serverUrl = (String) sharedData.get(QTestSettingStore.SERVER_URL_PROPERTY);
        setConnectingCompositeVisible(true);

        Job job = new Job("") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    // If the token or server url is different than the current one, update new qTest Project from
                    // qTest.
                    if (!token.equals(fToken) || !serverUrl.equals(fServerUrl)) {
                        fServerUrl = serverUrl;
                        fToken = token;

                        try {
                            qTestProjects = QTestIntegrationProjectManager
                                    .getAllProject(new QTestCredentialImpl().setToken(fToken).setServerUrl(fServerUrl));
                        } catch (QTestException e) {
                            UISynchronizeService.syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialog.openWarning(null, StringConstants.WARN,
                                            StringConstants.WZ_P_PROJECT_MSG_GET_PROJECTS_FAILED);
                                }
                            });
                        }
                    }
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                updateProjectRadioButtons();
                            } catch (IllegalStateException | IllegalArgumentException | SWTException e) {
                                // Display is disposed
                            }
                        }
                    });
                    return Status.OK_STATUS;
                } finally {
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setConnectingCompositeVisible(false);
                            } catch (IllegalStateException | IllegalArgumentException | SWTException e) {
                                // Display is disposed
                            }
                        }
                    });
                    monitor.done();
                }
            }
        };
        job.setUser(false);
        job.schedule();
    }

    @Override
    public String getStepIndexAsString() {
        return "2";
    }

    @Override
    public boolean isChild() {
        return false;
    }
}
