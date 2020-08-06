package com.kms.katalon.composer.integration.analytics.testops.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.analytics.testops.constants.TestOpsStringConstants;
import com.kms.katalon.composer.integration.analytics.testops.utils.ExecutionViewer;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsExecution;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.util.CryptoUtil;

@SuppressWarnings("restriction")
public class ExecutionHistoryPart {

	private Composite parent;

	private MPart part;

	private ExecutionViewer viewer;

	private Composite executionPart;

	private Composite loadingPart;

	private StackLayout viewerLayout;

	private Composite errorPart;

	private Composite viewerPart;
	
	private Composite emptyPart;

	@PostConstruct
	public void createPartControl(final Composite parent, MCompositePart mpart) {
		this.parent = parent;
		this.part = mpart;

		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);
		createHeaderPart();

		viewerPart = new Composite(parent, SWT.NONE);
		viewerLayout = new StackLayout();
		viewerPart.setLayout(viewerLayout);
		viewerPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createErrorPart();
		createLoadingPart();
		createEmptyPart();

		refresh();
	}

	private void refresh() {
		viewerLayout.topControl = loadingPart;
		viewerPart.layout();
		Thread getExecutionsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<AnalyticsExecution> executions = getExecution();
                if (executions == null) {
                    return;
                }

                if (executions.isEmpty()) {
			        UISynchronizeService.asyncExec(new Runnable() {

	                    @Override
	                    public void run() {
	                        viewerLayout.topControl = emptyPart;
	                        viewerPart.layout();
	                    }
	                });
			        
			        return;
			    }
			    
				UISynchronizeService.asyncExec(new Runnable() {

					@Override
					public void run() {
						if (viewer == null) {
							createExecutionPart(executions);
						} else {
							viewer.getTable().removeAll();
							viewer.populateData(executions);
						}
						viewerLayout.topControl = executionPart;
						viewerPart.layout();
					}
				});
			}
		});
		getExecutionsThread.start();
	}

	private List<AnalyticsExecution> getExecution() {
		try {
			AnalyticsSettingStore settingStore = new AnalyticsSettingStore(
					ProjectController.getInstance().getCurrentProject().getFolderLocation());
			AnalyticsProject project = settingStore.getProject();
			String serverUrl = settingStore.getServerEndpoint();
			String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
			String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

			if (!StringUtils.isBlank(email) && !StringUtils.isBlank(encryptedPassword)) {
			    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
				AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                return AnalyticsApiProvider.getExecutions(project.getId(), serverUrl, token.getAccess_token());
			}
		} catch (Exception e) {
		    LoggerSingleton.logError(e);
            UISynchronizeService.asyncExec(new Runnable() {

                @Override
                public void run() {
                    viewerLayout.topControl = errorPart;
                    viewerPart.layout();
                }
            });
		}

		return null;
    }

    private void createEmptyPart() {
        emptyPart = new Composite(viewerPart, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        emptyPart.setLayout(rowLayout);

        Link link = new Link(emptyPart, SWT.NONE);
        setFontStyle(link, 14, SWT.NONE);
        link.setLayoutData(new RowData());
        link.setText(TestOpsStringConstants.LNK_EXECUTION_EMPTY);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(TestOpsStringConstants.LNK_ENABLE_INTEGRATION_GUIDE);
            }
        });

    }
	
	private void createErrorPart() {
		errorPart = new Composite(viewerPart, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		errorPart.setLayout(rowLayout);

		Label lblError = new Label(errorPart, SWT.NONE);
		lblError.setLayoutData(new RowData());
		setFontStyle(lblError, 16, SWT.NONE);
		lblError.setText(TestOpsStringConstants.MSG_ANALYTICS_CONNECTION_ERROR);
		lblError.setForeground(new Color(errorPart.getDisplay(), new RGB(228, 84, 108)));
	}

	private void createLoadingPart() {
		loadingPart = new Composite(viewerPart, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		loadingPart.setLayout(rowLayout);

		Label lblLoading = new Label(loadingPart, SWT.NONE);
		lblLoading.setLayoutData(new RowData());
		setFontStyle(lblLoading, 16, SWT.NONE);
		lblLoading.setText(TestOpsStringConstants.LBL_LOADING);
		lblLoading.setForeground(new Color(loadingPart.getDisplay(), new RGB(22, 204, 142)));

	}

	private void createExecutionPart(List<AnalyticsExecution> executions) {
		executionPart = new Composite(viewerPart, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		executionPart.setLayout(gridLayout);
		viewer = new ExecutionViewer(executionPart, SWT.BORDER);
		viewer.populateData(executions);
	}

	private void createHeaderPart() {
		Composite headerComposite = new Composite(parent, SWT.NONE);
		GridLayout headerLayout = new GridLayout(2, true);
		headerComposite.setLayout(headerLayout);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label lblExecution = new Label(headerComposite, SWT.NONE);
		lblExecution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblExecution.setText(TestOpsStringConstants.LBL_EXECUTIONS);
		setFontStyle(lblExecution, 14, SWT.BOLD);

		Composite viewAllComposite = new Composite(headerComposite, SWT.NONE);
		GridLayout viewAllLayout = new GridLayout(5, false);
		viewAllLayout.marginWidth = 0;
		viewAllLayout.marginHeight = 0;
		viewAllLayout.verticalSpacing = 0;
		viewAllLayout.horizontalSpacing = 0;
		viewAllComposite.setLayout(viewAllLayout);
		viewAllComposite.setLayoutData(new GridData(GridData.END, SWT.CENTER, true, false));

		ToolBar toolBar = new ToolBar(viewAllComposite, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setToolTipText(StringConstants.REFRESH);
		item.setImage(ImageConstants.IMG_16_REFRESH);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblDelimeter = new Label(viewAllComposite, SWT.NONE);
		lblDelimeter.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		lblDelimeter.setText(" ");

		Label imgTestOps = new Label(viewAllComposite, SWT.NONE);
		imgTestOps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		imgTestOps.setImage(ImageConstants.IMG_16_KATALON_TESTOPS);

		Link lnkViewAll = new Link(viewAllComposite, SWT.NONE);
		setFontStyle(lnkViewAll, 14, SWT.NONE);
		String testOpsUrl = getViewAllExecutionsURL();
		lnkViewAll
				.setText(" <a href=\"" + testOpsUrl + "\">" + TestOpsStringConstants.LNK_VIEW_ALL_EXECUTIONS + "</a>");
		lnkViewAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(testOpsUrl);
			}
		});

		Label lblArrow = new Label(viewAllComposite, SWT.NONE);
		lblArrow.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblArrow.setText(">>");
		setFontStyle(lblArrow, 14, SWT.NONE);
	}

	private void setFontStyle(Label label, int fontSize, int style) {
		FontData[] fontData = label.getFont().getFontData();
		for (int i = 0; i < fontData.length; ++i) {
			fontData[i].setHeight(fontSize);
			fontData[i].setStyle(style);
		}
		final Font newFont = new Font(label.getDisplay(), fontData);
		label.setFont(newFont);

		label.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				newFont.dispose();
			}
		});
	}

	private void setFontStyle(Link link, int fontSize, int style) {
		FontData[] fontData = link.getFont().getFontData();
		for (int i = 0; i < fontData.length; ++i) {
			fontData[i].setHeight(fontSize);
			fontData[i].setStyle(style);
		}
		final Font newFont = new Font(link.getDisplay(), fontData);
		link.setFont(newFont);

		link.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				newFont.dispose();
			}
		});
	}

	private String getViewAllExecutionsURL() {
		AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
				ProjectController.getInstance().getCurrentProject().getFolderLocation());
		AnalyticsProject project = analyticsSettingStore.getProject();
		AnalyticsTeam team = analyticsSettingStore.getTeam();
		String serverUrl = analyticsSettingStore.getServerEndpoint();
		return String.format("%s/team/%d/project/%d/executions", serverUrl, team.getId(), project.getId());
	}

}
