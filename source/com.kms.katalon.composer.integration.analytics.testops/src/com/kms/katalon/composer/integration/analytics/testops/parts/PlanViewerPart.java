package com.kms.katalon.composer.integration.analytics.testops.parts;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
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

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsRunScheduler;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsAgent;
import com.kms.katalon.integration.analytics.entity.AnalyticsCircleCIAgent;
import com.kms.katalon.integration.analytics.entity.AnalyticsJob;
import com.kms.katalon.integration.analytics.entity.AnalyticsK8sAgent;
import com.kms.katalon.integration.analytics.entity.AnalyticsPlan;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.composer.integration.analytics.testops.constants.TestOpsMessageConstants;
import com.kms.katalon.composer.integration.analytics.testops.constants.TestOpsStringConstants;
import com.kms.katalon.composer.integration.analytics.testops.utils.TestOpsUtil;

@SuppressWarnings("restriction")
public class PlanViewerPart {

    private Composite parent;

    private MPart part;

    private CTableViewer viewer;

    private Composite planPart;

    private Composite loadingPart;

    private StackLayout viewerLayout;

    private Composite errorPart;

    private Composite viewerPart;

    private Composite emptyPart;

    private static final String TIME_FORMAT_TEMPLATE = "MMM dd, HH:mm";

    private static final String YEAR_FORMAT_TEMPLATE = "yyyy";
    
    private ZonedDateTime timeNow;

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

        createErrorpart();
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
                final List<AnalyticsPlan> plans = getPlan();
                if (plans == null) {
                    return;
                }

                if (plans.isEmpty()) {
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
                        timeNow = ZonedDateTime.now();
                        if (viewer == null) {
                            createPlanPart(plans);
                        } else {
                            viewer.getTable().removeAll();
                            viewer.setInput(plans);
                            viewer.refresh();
                        }
                        viewerLayout.topControl = planPart;
                        viewerPart.layout();
                    }
                });
            }
        });
        getExecutionsThread.start();
    }

    private List<AnalyticsPlan> getPlan() {
        try {
            AnalyticsSettingStore settingStore = new AnalyticsSettingStore(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            AnalyticsProject project = settingStore.getProject();
            String serverUrl = settingStore.getServerEndpoint();
            String email = settingStore.getEmail();
            String password = settingStore.getPassword();

            if (!StringUtils.isBlank(email) && !StringUtils.isBlank(password)) {
                AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                return AnalyticsApiProvider.getPlans(project.getId(), serverUrl, token.getAccess_token());
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
        link.setText(TestOpsStringConstants.LNK_PLAN_EMPTY);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(TestOpsStringConstants.LNK_CREATE_PLAN_GUIDE);
            }
        });

    }

    private void createErrorpart() {
        errorPart = new Composite(viewerPart, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        errorPart.setLayout(rowLayout);

        Label lblError = new Label(errorPart, SWT.NONE);
        lblError.setLayoutData(new RowData());
        setFontStyle(lblError, 16, SWT.NONE);
        lblError.setText(TestOpsMessageConstants.MSG_ANALYTICS_CONNECTION_ERROR);
        lblError.setForeground(new Color(errorPart.getDisplay(), new RGB(228, 84, 108)));
    }

    private void createLoadingPart() {
        loadingPart = new Composite(viewerPart, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        loadingPart.setLayout(rowLayout);

        Label lblLoading = new Label(loadingPart, SWT.NONE);
        lblLoading.setLayoutData(new RowData());
        setFontStyle(lblLoading, 16, SWT.NONE);
        lblLoading.setText(TestOpsMessageConstants.LBL_LOADING);
        lblLoading.setForeground(new Color(loadingPart.getDisplay(), new RGB(22, 204, 142)));

    }

    private void createPlanPart(List<AnalyticsPlan> plans) {
        AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());

        planPart = new Composite(viewerPart, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        planPart.setLayout(gridLayout);
        viewer = new CTableViewer(planPart, SWT.BORDER | SWT.FULL_SELECTION);
        TableLayout tableLayout = new TableLayout();
        viewer.getTable().setLayout(tableLayout);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(viewer);

        TableViewerColumn colStatus = new TableViewerColumn(viewer, SWT.NONE);
        colStatus.getColumn().setText(TestOpsStringConstants.PLAN_STATUS);
        tableLayout.addColumnData(new ColumnWeightData(50));
        colStatus.setLabelProvider(new TypeCheckedStyleCellLabelProvider<AnalyticsPlan>(0) {

            @Override
            protected Image getImage(AnalyticsPlan element) {
                if (element.getLatestJob() == null) {
                    return null;
                }

                switch (element.getLatestJob().getStatus()) {
                    case SUCCESS:
                        return ImageConstants.IMG_16_TESTOPS_EXECUTION_PASSED;
                    case FAILED:
                        return ImageConstants.IMG_16_TESTOPS_EXECUTION_FAILED;
                    case QUEUED:
                        return ImageConstants.IMG_16_TESTOPS_PLAN_QUEUED;
                    case ERROR:
                        return ImageConstants.IMG_16_TESTOPS_PLAN_ERROR;
                    case CANCELED:
                        return ImageConstants.IMG_16_TESTOPS_PLAN_CANCELED;
                    case RUNNING:
                        return ImageConstants.IMG_16_TESTOPS_PLAN_RUNNING;
                    default:
                        break;
                }

                return null;
            }

            @Override
            public String getToolTipText(Object element) {
                if (!(element instanceof AnalyticsPlan)) {
                    return null;
                }

                AnalyticsPlan plan = (AnalyticsPlan) element;
                if (plan.getLatestJob() == null) {
                    return null;
                }

                switch (plan.getLatestJob().getStatus()) {
                    case SUCCESS:
                        return TestOpsStringConstants.LBL_EXECUTION_STATUS_PASSED;
                    case FAILED:
                        return TestOpsStringConstants.LBL_EXECUTION_STATUS_FAILED;
                    case QUEUED:
                        return TestOpsStringConstants.LBL_PLAN_STATUS_QUEUED;
                    case CANCELED:
                        return TestOpsStringConstants.LBL_PLAN_STATUS_CANCELED;
                    case ERROR:
                        return TestOpsStringConstants.LBL_PLAN_STATUS_ERROR;
                    case WAIT_FOR_TRIGGER:
                        return TestOpsStringConstants.LBL_PLAN_STATUS_WAITTING;
                    case RUNNING:
                        return TestOpsStringConstants.LBL_PLAN_STATUS_RUNNING;
                    default:
                        return null;
                }
            }

            @Override
            protected String getText(AnalyticsPlan element) {
                return null;
            }

            @Override
            protected Class<AnalyticsPlan> getElementType() {
                return null;
            }
            
            @Override
            public boolean useNativeToolTip(Object object) {
                return false;
            }
            
        });

        TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
        colName.getColumn().setText(TestOpsStringConstants.PLAN_NAME);
        tableLayout.addColumnData(new ColumnWeightData(325));
        colName.setLabelProvider(new HyperLinkColumnLabelProvider<AnalyticsPlan>(1) {

            @Override
            protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
                if (!(cell.getElement() instanceof AnalyticsPlan)) {
                    return;
                }

                AnalyticsPlan plan = (AnalyticsPlan) cell.getElement();
                String planUrl = getPlanUrl(TestOpsUtil.truncateURL(analyticsSettingStore.getServerEndpoint()),
                        analyticsSettingStore.getTeam().getId(), analyticsSettingStore.getProject().getId(),
                        plan.getId());
                Program.launch(planUrl);
            }

            @Override
            protected Class<AnalyticsPlan> getElementType() {
                return null;
            }

            @Override
            protected Image getImage(AnalyticsPlan element) {
                return null;
            }

            @Override
            protected String getText(AnalyticsPlan element) {
                return element.getName();
            }
            
            @Override
            public boolean useNativeToolTip(Object object) {
                return false;
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                AnalyticsPlan plan = (AnalyticsPlan)element;
                if(StringUtils.isBlank(plan.getName())) {
                    return null;
                }
                
                return plan.getName();
            }

        });

        TableViewerColumn colTestProject = new TableViewerColumn(viewer, SWT.NONE);
        colTestProject.getColumn().setText(TestOpsStringConstants.PLAN_TEST_PROJECT);
        tableLayout.addColumnData(new ColumnWeightData(325));
        colTestProject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                return ((AnalyticsPlan)element).getTestProject().getName();
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                AnalyticsPlan plan = (AnalyticsPlan)element;
                if(StringUtils.isBlank(plan.getTestProject().getName())) {
                    return null;
                }
                
                return plan.getTestProject().getName();
            }
        });

        TableViewerColumn colAgent = new TableViewerColumn(viewer, SWT.NONE);
        colAgent.getColumn().setText(TestOpsStringConstants.PLAN_AGENTS);
        tableLayout.addColumnData(new ColumnWeightData(150));
        colAgent.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                return getAgentNames((AnalyticsPlan)element);
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                String agentName = getAgentNames((AnalyticsPlan)element);
                if(StringUtils.isBlank(agentName)) {
                    return null;
                }
                
                return agentName;
            }
        });

        TableViewerColumn colLastExec = new TableViewerColumn(viewer, SWT.NONE);
        colLastExec.getColumn().setText(TestOpsStringConstants.PLAN_LAST_EXECUTION);
        tableLayout.addColumnData(new ColumnWeightData(130));
        colLastExec.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                return getLastExecution(((AnalyticsPlan)element).getLatestJob());
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                String lastExec = getLastExecution(((AnalyticsPlan)element).getLatestJob());
                if(StringUtils.isBlank(lastExec)) {
                    return null;
                }
                
                return lastExec;
            }
        });

        TableViewerColumn colLastRun = new TableViewerColumn(viewer, SWT.NONE);
        colLastRun.getColumn().setText(TestOpsStringConstants.PLAN_LAST_RUN);
        tableLayout.addColumnData(new ColumnWeightData(130));
        colLastRun.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                return getLastRun(((AnalyticsPlan)element).getLatestJob());
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                String lastRun = getLastRun(((AnalyticsPlan)element).getLatestJob());
                if(StringUtils.isBlank(lastRun)) {
                    return null;
                }
                
                return lastRun;
            }
        });

        TableViewerColumn colNextRun = new TableViewerColumn(viewer, SWT.NONE);
        colNextRun.getColumn().setText(TestOpsStringConstants.PLAN_NEXT_RUN);
        tableLayout.addColumnData(new ColumnWeightData(130));
        colNextRun.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                return getNextRun(((AnalyticsPlan)element).getNextRunScheduler());
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsPlan)) {
                    return null;
                }
                
                String nextRun = getNextRun(((AnalyticsPlan)element).getNextRunScheduler());
                if(StringUtils.isBlank(nextRun)) {
                    return null;
                }
                
                return nextRun;
            }
        });
        viewer.setInput(plans);
        viewer.refresh();
    }

    private void createHeaderPart() {
        Composite headerComposite = new Composite(parent, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, true);
        headerComposite.setLayout(headerLayout);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblExecution = new Label(headerComposite, SWT.NONE);
        lblExecution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lblExecution.setText(TestOpsMessageConstants.LBL_PLANS);
        setFontStyle(lblExecution, 14, SWT.BOLD);

        Composite viewAllComposite = new Composite(headerComposite, SWT.NONE);
        GridLayout viewAllLayout = new GridLayout(5, false);
        viewAllLayout.marginWidth = 0;
        viewAllLayout.marginHeight = 0;
        viewAllLayout.verticalSpacing = 0;
        viewAllLayout.horizontalSpacing = 0;
        viewAllComposite.setLayout(viewAllLayout);
        viewAllComposite.setLayoutData(new GridData(GridData.END, SWT.CENTER, true, false));

        ToolBar tbRefresh = new ToolBar(viewAllComposite, SWT.FLAT);
        tbRefresh.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        ToolItem btnRefresh = new ToolItem(tbRefresh, SWT.PUSH);
        btnRefresh.setToolTipText(StringConstants.REFRESH);
        btnRefresh.setImage(ImageConstants.IMG_16_TESTOPS_REFRESH_NEW);
        btnRefresh.addSelectionListener(new SelectionListener() {

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
        imgTestOps.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        imgTestOps.setImage(ImageConstants.IMG_16_KATALON_TESTOPS);

        Link lnkViewAll = new Link(viewAllComposite, SWT.NONE);
        lnkViewAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        setFontStyle(lnkViewAll, 11, SWT.NONE);
        String testOpsUrl = getViewAllPlanURL();
        lnkViewAll.setText(" <a href=\"" + testOpsUrl + "\">" + TestOpsMessageConstants.LNK_VIEW_ALL_PLANS + "</a>");
        lnkViewAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(testOpsUrl);
            }
        });

        Label lblArrow = new Label(viewAllComposite, SWT.NONE);
        lblArrow.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblArrow.setText(">>");
        setFontStyle(lblArrow, 12, SWT.NONE);
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

    private String getViewAllPlanURL() {
        AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        AnalyticsProject project = analyticsSettingStore.getProject();
        AnalyticsTeam team = analyticsSettingStore.getTeam();
        String serverUrl = TestOpsUtil.truncateURL(analyticsSettingStore.getServerEndpoint());
        return String.format("%s/team/%d/project/%d/grid", serverUrl, team.getId(), project.getId());
    }

    private String getPlanUrl(String serverUrl, long teamId, long projectId, long planId) {
        return String.format("%s/team/%d/project/%d/grid/plan/%d/job", serverUrl, teamId, projectId, planId);
    }

    private String getAgentNames(AnalyticsPlan plan) {
        String delimiter = ", ";
        StringBuffer names = new StringBuffer();
        for (AnalyticsAgent agent : plan.getAgents()) {
            names.append(agent.getName());
            names.append(delimiter);
        }
        
        for (AnalyticsK8sAgent agent : plan.getK8sAgents()) {
            names.append(agent.getName());
            names.append(delimiter);
        }
        
        for (AnalyticsCircleCIAgent agent : plan.getCircleCIAgents()) {
            names.append(agent.getName());
            names.append(delimiter);
        }

        if (names.length() <= 0) {
            return StringUtils.EMPTY;
        }

        names.delete(names.length() - delimiter.length(), names.length() - 1);
        return names.toString();
    }

    private String getNextRun(AnalyticsRunScheduler scheduler) {
        if (scheduler == null) {
            return StringUtils.EMPTY;
        }

        Date nextTime = scheduler.getNextTime();
        if (nextTime == null) {
            return StringUtils.EMPTY;
        }

        return getDateTimeFormat(nextTime, true, false, true);
    }

    private String getLastRun(AnalyticsJob job) {
        if (job == null) {
            return null;
        }

        Date lastRun = job.getTriggerAt();
        if (lastRun == null) {
            return StringUtils.EMPTY;
        }

        return getDateTimeFormat(lastRun, false, true, false);
    }

    private String getLastExecution(AnalyticsJob job) {
        if (job == null || job.getExecution() == null) {
            return StringUtils.EMPTY;
        }

        Date lastExec = job.getExecution().getStartTime();
        if (lastExec == null) {
            return StringUtils.EMPTY;
        }

        return getDateTimeFormat(lastExec, false, true, false);
    }

    private String getDateTimeFormat(Date time, boolean hasPrefix, boolean hasSuffix, boolean isNextTime) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        if (isSameDayWithLocal(time)) {
            return getRecentTimeFormat(time, hasPrefix, hasSuffix, isNextTime);
        }
        boolean isSameYear = dateTime.getYear() == timeNow.getYear();
        return getFullTimeFormat(time, isSameYear);
    }

    private boolean isSameDayWithLocal(Date time) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        return (dateTime.getYear() == timeNow.getYear()) && (dateTime.getMonthValue() == timeNow.getMonthValue())
                && (dateTime.getDayOfMonth() == timeNow.getDayOfMonth());
    }

    private String getRecentTimeFormat(Date time, boolean hasPrefix, boolean hasSuffix, boolean isNextTime) {
        String prefix = hasPrefix ? TestOpsStringConstants.TIME_FORMAT_PREFIX : StringUtils.EMPTY;
        String suffix = hasSuffix ? TestOpsStringConstants.TIME_FORMAT_SUFFIX : StringUtils.EMPTY;

        long seconds = timeNow.toInstant().getEpochSecond() - time.toInstant().getEpochSecond();
        if (isNextTime) {
            seconds *= -1;
        }

        if (seconds <= 60) {
            return String.format("%s %s %s", prefix, TestOpsStringConstants.TIME_FORMAT_AFEW_SECONDS, suffix).trim();
        }

        long minutes = Math.round(seconds / 60.0);
        long hours = minutes / 60;
        minutes %= 60;
        if (hours > 0 && minutes >= 30 ) {
            hours++;
        }

        String formatedHour = getHourFormat(hours, minutes);
        String formatedMinute = hours <= 0 ? getMinuteFormat(minutes, hours > 0) : StringUtils.EMPTY;

        return String.format("%s %s %s %s", prefix, formatedHour, formatedMinute, suffix).trim();
    }

    private String getHourFormat(long hour, long minute) {
        if (hour == 0) {
            return StringUtils.EMPTY;
        }

        if (hour == 1 && minute == 0) {
            return TestOpsStringConstants.TIME_FORMAT_AN_HOUR;
        }

        return String.format(TestOpsStringConstants.TIME_FORMAT_HOURS, hour);
    }

    private String getMinuteFormat(long minute, boolean hasHour) {
        if (hasHour) {
            if (minute == 1) {
                return String.format(TestOpsStringConstants.TIME_FORMAT_MINUTE, minute);
            }
            return String.format(TestOpsStringConstants.TIME_FORMAT_MINUTES, minute);
        }
        if (minute <= 1) {
            return TestOpsStringConstants.TIME_FORMAT_A_MINUTE;
        }
        return String.format(TestOpsStringConstants.TIME_FORMAT_MINUTES, minute);
    }

    private String getFullTimeFormat(Date time, boolean isSameYear) {
        String formatTemplate = TIME_FORMAT_TEMPLATE;
        if (!isSameYear) {
            formatTemplate = YEAR_FORMAT_TEMPLATE + " " + TIME_FORMAT_TEMPLATE;
        }
        return ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(formatTemplate));
    }

}
