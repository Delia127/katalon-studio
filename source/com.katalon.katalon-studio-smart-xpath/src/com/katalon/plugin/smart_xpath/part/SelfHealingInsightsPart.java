package com.katalon.plugin.smart_xpath.part;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.platform.ui.viewer.HyperLinkColumnLabelProvider;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.controller.AutoHealingController;
import com.katalon.plugin.smart_xpath.dialog.provider.CheckBoxColumnEditingSupport;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;

public class SelfHealingInsightsPart implements EventHandler, LauncherListener {

    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;
    
    protected Composite tablePropertyComposite;

    private TableViewer tbViewer;

    private TableColumnLayout tableColumnLayout;

    private Table table;

    private Set<BrokenTestObject> unapprovedBrokenEntities = new HashSet<>();

    private Set<BrokenTestObject> approvedAutoHealingEntities = new HashSet<>();

    public void init(Composite parent) {
        createContent(parent);
        registerEventListeners();
    }

    @PostConstruct
    protected Control createContent(Composite parent) {
        tablePropertyComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.widthHint = 1200;
        ldTableComposite.heightHint = 380;
        tablePropertyComposite.setLayoutData(ldTableComposite);
        tableColumnLayout = new TableColumnLayout();
        tablePropertyComposite.setLayout(tableColumnLayout);

        tbViewer = new TableViewer(tablePropertyComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns();

        tbViewer.setContentProvider(ArrayContentProvider.getInstance());
        loadAutoHealingEntities();

        table = tbViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        tbViewer.setInput(unapprovedBrokenEntities);
        return tablePropertyComposite;
    }

    private void createColumns() {
        TableViewerColumn colObjectId = new TableViewerColumn(tbViewer, SWT.NONE);
        colObjectId.getColumn().setText("Test Object ID");
        colObjectId.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                String testObjectId = ((BrokenTestObject) element).getTestObjectId();
                return testObjectId;
            }
        });

        TableViewerColumn colBrokenLocator = new TableViewerColumn(tbViewer, SWT.NONE);
        colBrokenLocator.getColumn().setText("Broken Locator");
        colBrokenLocator.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                BrokenTestObject brokenTestObject = (BrokenTestObject) element;
                String brokenLocator = MessageFormat.format("{0}: {1}", brokenTestObject.getBrokenLocatorMethod().getName(),
                        brokenTestObject.getBrokenLocator());
                return brokenLocator;
            }
        });

        TableViewerColumn colProposedLocator = new TableViewerColumn(tbViewer, SWT.NONE);
        colProposedLocator.getColumn().setText("Proposed Locator");
        colProposedLocator.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                String proposedLocator = ((BrokenTestObject) element).getProposedLocator();
                return proposedLocator;
            }
        });

        TableViewerColumn colRecoverBy = new TableViewerColumn(tbViewer, SWT.NONE);
        colRecoverBy.getColumn().setText("Recover By");
        colRecoverBy.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                String newXPath = ((BrokenTestObject) element).getProposedLocatorMethod().toString();
                return newXPath;
            }
        });

        TableViewerColumn colScreenshot = new TableViewerColumn(tbViewer, SWT.NONE);
        colScreenshot.getColumn().setText("Screenshot");
        colScreenshot.setLabelProvider(new HyperLinkColumnLabelProvider<BrokenTestObject>(3) {

            @Override
            protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
                BrokenTestObject brokenTestObj = (BrokenTestObject) cell.getElement();
                if (Desktop.isDesktopSupported()) {
                    try {
                        File myFile = new File(brokenTestObj.getPathToScreenshot());
                        // Open the folder containing this image
                        Desktop.getDesktop().open(myFile);
                    } catch (NullPointerException nullPointerEx) {
                        MessageDialog.openError(null, "Error", "This broken object does not have a screenshot.");
                    } catch (IOException ioEx) {
                        MessageDialog.openError(null, "Error", ioEx.getMessage());
                    } catch (IllegalArgumentException illegalArgEx) {
                        MessageDialog.openError(null, "Error", "Screenshot no longer exists at this path.");
                    } catch (UnsupportedOperationException unsupportedOpEx) {
                        MessageDialog.openError(null, "Error", "This platform does not support open action.");
                    } catch (SecurityException secEx) {
                        MessageDialog.openError(null, "Error", "Read access is denied.");
                    }
                }
            }

            @Override
            protected Class<BrokenTestObject> getElementType() {
                return BrokenTestObject.class;
            }

            @Override
            protected Image getImage(BrokenTestObject element) {
                return null;
            }

            @Override
            protected String getText(BrokenTestObject element) {
                return "Preview";
            }
        });

        TableViewerColumn colApproveNewXPath = new TableViewerColumn(tbViewer, SWT.NONE);
        colApproveNewXPath.getColumn().setText("Approve");
        colApproveNewXPath.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                cell.setText(getCheckboxSymbol(((BrokenTestObject) cell.getElement()).getApproved()));
            }
        });

        colApproveNewXPath.setEditingSupport(new CheckBoxColumnEditingSupport(tbViewer));

        tableColumnLayout.setColumnData(colObjectId.getColumn(), new ColumnWeightData(35, 100));
        tableColumnLayout.setColumnData(colBrokenLocator.getColumn(), new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(colProposedLocator.getColumn(), new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(colRecoverBy.getColumn(), new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(colScreenshot.getColumn(), new ColumnWeightData(5, 100));
        tableColumnLayout.setColumnData(colApproveNewXPath.getColumn(), new ColumnWeightData(5, 70));
    }

    public void loadAutoHealingEntities() {
        unapprovedBrokenEntities.clear();
        unapprovedBrokenEntities = AutoHealingController.readUnapprovedBrokenTestObjects();
        unapprovedBrokenEntities = unapprovedBrokenEntities.stream()
                .filter(a -> !a.getApproved())
                .collect(Collectors.toSet());
    }

    public Set<BrokenTestObject> getUnapprovedAutoHealingEntities() {
        return unapprovedBrokenEntities;
    }

    public Set<BrokenTestObject> getApprovedAutoHealingEntities() {
        return approvedAutoHealingEntities;
    }

    private String getCheckboxSymbol(boolean isChecked) {
        return isChecked ? "\u2611" : "\u2610";
    }
    
    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.JOB_UPDATE_PROGRESS, this);
        eventBroker.subscribe(EventConstants.JOB_COMPLETED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH, this);
    }
    
    @Focus
    public void onFocus() {
        refresh();
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject notifiedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleEvent(Event event) {
        refresh();
    }
    
    private void refresh() {
        loadAutoHealingEntities();
        sync.syncExec(() -> {
            tablePropertyComposite.update();
            tablePropertyComposite.redraw();
            tablePropertyComposite.setData("abc", "");
        });
    }
}
