package com.kms.katalon.composer.integration.analytics.testops.parts;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.eclipse.swt.widgets.Display;
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
import com.kms.katalon.composer.integration.analytics.testops.constants.TestOpsStringConstants;
import com.kms.katalon.composer.integration.analytics.testops.utils.TestOpsUtil;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsRelease;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class ReleasePart {
    

    private Composite parent;

    private MPart part;

    private CTableViewer viewer;

    private Composite releasePart;

    private Composite loadingPart;

    private StackLayout viewerLayout;

    private Composite errorPart;

    private Composite viewerPart;
    
    private Composite emptyPart;
    
    private AnalyticsSettingStore settingStore;
    
    @PostConstruct
    public void createPartControl(final Composite parent, MCompositePart mpart) {
        this.parent = parent;
        this.part = mpart;

        GridLayout gridLayout = new GridLayout(1, false);
        parent.setLayout(gridLayout);
        settingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        
        createHeaderPart();

        viewerPart = new Composite(parent, SWT.NONE);
        viewerLayout = new StackLayout();
        viewerPart.setLayout(viewerLayout);
        viewerPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createReleasePart();
        
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
                final List<AnalyticsRelease> releases = getReleases();
                if (releases == null) {
                    return;
                }

                if (releases.isEmpty()) {
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
                            createReleasePart();
                        } else {
                            viewer.getTable().removeAll();
                            viewer.setInput(releases);
                            viewer.refresh();
                        }
                        viewerLayout.topControl = releasePart;
                        viewerPart.layout();
                    }
                });
            }
        });
        getExecutionsThread.start();
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
    
    private void createReleasePart() {
        releasePart = new Composite(viewerPart, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        releasePart.setLayout(gridLayout);
        TableLayout tableLayout = new TableLayout();
        viewer = new CTableViewer(releasePart, SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getTable().setLayout(tableLayout);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ColumnViewerToolTipSupport.enableFor(viewer);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM dd");
        
        TableViewerColumn colStatus = new TableViewerColumn(viewer, SWT.NONE);
        colStatus.getColumn().setText(TestOpsStringConstants.RELEASE_STATUS);
        tableLayout.addColumnData(new ColumnWeightData(50));
        colStatus.setLabelProvider(new TypeCheckedStyleCellLabelProvider<AnalyticsRelease>(0) {

            @Override
            protected Class<AnalyticsRelease> getElementType() {
                return null;
            }

            @Override
            protected Image getImage(AnalyticsRelease element) {
                return null;
            }

            @Override
            protected String getText(AnalyticsRelease element) {
                return element.isClosed() ? TestOpsStringConstants.RELEASE_STATUS_CLOSED
                        : TestOpsStringConstants.RELEASE_STATUS_ACTIVE;
            }
            
            @Override
            protected Color getForeground(Color foreground, AnalyticsRelease element) {
                Color active = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
                Color closed = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
                return element.isClosed() ? closed : active;
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsRelease)) {
                    return StringUtils.EMPTY;
                }
                return ((AnalyticsRelease)element).isClosed() ? TestOpsStringConstants.RELEASE_STATUS_CLOSED
                        : TestOpsStringConstants.RELEASE_STATUS_ACTIVE;
            }
            
        });
        
        TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
        colName.getColumn().setText(TestOpsStringConstants.RELEASE_NAME);
        tableLayout.addColumnData(new ColumnWeightData(250));
        colName.setLabelProvider(new HyperLinkColumnLabelProvider<AnalyticsRelease>(1) {

            @Override
            protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
                Program.launch(getReleaseUrl((AnalyticsRelease)cell.getElement()));
            }

            @Override
            protected Class<AnalyticsRelease> getElementType() {
                return null;
            }

            @Override
            protected Image getImage(AnalyticsRelease element) {
                return null;
            }

            @Override
            protected String getText(AnalyticsRelease element) {
                return element.getName();
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsRelease)) {
                    return StringUtils.EMPTY;
                }
                return ((AnalyticsRelease)element).getName();
            }
        });
        
        TableViewerColumn colStartDate = new TableViewerColumn(viewer, SWT.NONE);
        colStartDate.getColumn().setText(TestOpsStringConstants.RELEASE_START_DATE);
        tableLayout.addColumnData(new ColumnWeightData(100));
        colStartDate.setLabelProvider(new TypeCheckedStyleCellLabelProvider<AnalyticsRelease>(2) {

            @Override
            protected Class<AnalyticsRelease> getElementType() {
                return null;
            }

            @Override
            protected Image getImage(AnalyticsRelease element) {
                return null;
            }

            @Override
            protected String getText(AnalyticsRelease element) {
                if(element.getStartTime() == null) {
                    return StringUtils.EMPTY;
                }
                
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(element.getStartTime().toInstant(), ZoneId.systemDefault());
                return zonedDateTime.format(formatter);
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsRelease)) {
                    return StringUtils.EMPTY;
                }
                AnalyticsRelease release = (AnalyticsRelease) element;
                if(release.getStartTime() == null) {
                    return StringUtils.EMPTY;
                }
                return ZonedDateTime
                        .ofInstant(release.getStartTime().toInstant(), ZoneId.systemDefault())
                        .format(formatter);
            }
        });
        
        TableViewerColumn colEndDate = new TableViewerColumn(viewer, SWT.NONE);
        colEndDate.getColumn().setText(TestOpsStringConstants.RELEASE_END_DATE);
        tableLayout.addColumnData(new ColumnWeightData(100));
        colEndDate.setLabelProvider(new TypeCheckedStyleCellLabelProvider<AnalyticsRelease>(2) {

            @Override
            protected Class<AnalyticsRelease> getElementType() {
                return null;
            }

            @Override
            protected Image getImage(AnalyticsRelease element) {
                return null;
            }

            @Override
            protected String getText(AnalyticsRelease element) {
                if(element.getEndTime() == null) {
                    return StringUtils.EMPTY;
                }
                
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(element.getEndTime().toInstant(), ZoneId.systemDefault());
                return zonedDateTime.format(formatter);
            }
            
            @Override
            public String getToolTipText(Object element) {
                if(!(element instanceof AnalyticsRelease)) {
                    return StringUtils.EMPTY;
                }
                AnalyticsRelease release = (AnalyticsRelease) element;
                if(release.getEndTime() == null) {
                    return StringUtils.EMPTY;
                }
                return ZonedDateTime
                        .ofInstant(release.getEndTime().toInstant(), ZoneId.systemDefault())
                        .format(formatter);
            }
        });
        
    }
    
    private String getReleaseUrl(AnalyticsRelease release) {
        return String.format("%s/team/%s/project/%s/releases/%s", TestOpsUtil.truncateURL(settingStore.getServerEndpoint()),
                settingStore.getTeam().getId(), settingStore.getProject().getId(), release.getId());
    }
    
    private String getViewAllLink() {
        return String.format("%s/team/%d/project/%d/releases", TestOpsUtil.truncateURL(settingStore.getServerEndpoint()),
                settingStore.getTeam().getId(), settingStore.getProject().getId());
    }
    
    private void createHeaderPart() {
        Composite headerComposite = new Composite(parent, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, true);
        headerComposite.setLayout(headerLayout);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblExecution = new Label(headerComposite, SWT.NONE);
        lblExecution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lblExecution.setText(TestOpsStringConstants.LBL_RELEASES);
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
        item.setImage(ImageConstants.IMG_16_TESTOPS_REFRESH_NEW);
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
        setFontStyle(lnkViewAll, 11, SWT.NONE);
        String testOpsUrl = getViewAllLink();
        lnkViewAll
                .setText(" <a href=\"" + testOpsUrl + "\">" + TestOpsStringConstants.LNK_VIEW_ALL_RELEASES + "</a>");
        lnkViewAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(getViewAllLink());
            }
        });

        Label lblArrow = new Label(viewAllComposite, SWT.NONE);
        lblArrow.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblArrow.setText(">>");
        setFontStyle(lblArrow, 12, SWT.NONE);
    }
    
    private List<AnalyticsRelease> getReleases() {
        try {
            AnalyticsProject project = settingStore.getProject();
            String serverUrl = settingStore.getServerEndpoint();
            String email = settingStore.getEmail();
            String password = settingStore.getPassword();

            if (!StringUtils.isBlank(email) && !StringUtils.isBlank(password)) {
                AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                return AnalyticsApiProvider.getFirstReleasesPage(project.getId(), serverUrl, token.getAccess_token());
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
    
}
