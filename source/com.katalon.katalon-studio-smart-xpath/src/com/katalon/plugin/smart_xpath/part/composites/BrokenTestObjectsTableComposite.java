package com.katalon.plugin.smart_xpath.part.composites;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TypedListener;

import com.katalon.platform.ui.viewer.HyperLinkColumnLabelProvider;
import com.katalon.plugin.smart_xpath.dialog.provider.ApproveCheckBoxColumnEditingSupport;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class BrokenTestObjectsTableComposite extends Composite {

    private final int SCREENSHOT_COLUMN_INDEX = 4;
    
    private ProjectEntity project;

    private TableViewer tbViewer;

    private TableColumnLayout tableColumnLayout;

    private Table table;

    private Set<BrokenTestObject> brokenTestObjects = new HashSet<>();

    public BrokenTestObjectsTableComposite(Composite parent, int style) {
        super(parent, style);
        createContents(this);
    }
    
    private void createContents(Composite container) {
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.widthHint = 1200;
        ldTableComposite.heightHint = 380;
        container.setLayoutData(ldTableComposite);
        tableColumnLayout = new TableColumnLayout();
        container.setLayout(tableColumnLayout);

        tbViewer = new TableViewer(container, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns();

        tbViewer.setContentProvider(ArrayContentProvider.getInstance());

        table = tbViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
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
        colScreenshot.setLabelProvider(new HyperLinkColumnLabelProvider<BrokenTestObject>(SCREENSHOT_COLUMN_INDEX) {

            @Override
            protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
                BrokenTestObject brokenTestObj = (BrokenTestObject) cell.getElement();
                if (Desktop.isDesktopSupported()) {
                    try {
                        String filePath = FilenameUtils.separatorsToSystem(brokenTestObj.getPathToScreenshot());
                        File myFile = new File(filePath);
                        if (!myFile.isAbsolute()) {
                            String absoluteFilePath = FilenameUtils.concat(project.getFolderLocation(), filePath);
                            myFile = new File(absoluteFilePath);
                        }
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

        colApproveNewXPath.setEditingSupport(new ApproveCheckBoxColumnEditingSupport(tbViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(element, value);
                handleSelectionChange(null);
            }
        });

        tableColumnLayout.setColumnData(colObjectId.getColumn(), new ColumnWeightData(40, 100));
        tableColumnLayout.setColumnData(colBrokenLocator.getColumn(), new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(colProposedLocator.getColumn(), new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(colRecoverBy.getColumn(), new ColumnWeightData(5, 100));
        tableColumnLayout.setColumnData(colScreenshot.getColumn(), new ColumnWeightData(5, 100));
        tableColumnLayout.setColumnData(colApproveNewXPath.getColumn(), new ColumnWeightData(5, 70));
    }

    private String getCheckboxSymbol(boolean isChecked) {
        return isChecked ? "\u2611" : "\u2610";
    }
    
    public void refresh() {
        tbViewer.refresh();
    }
    
    public void setInput(Set<BrokenTestObject> brokenTestObjects) {
        brokenTestObjects = brokenTestObjects.stream()
                .filter(brokenTestObject -> !brokenTestObject.getApproved())
                .collect(Collectors.toSet());
        tbViewer.setInput(brokenTestObjects);
        refresh();
    }
    
    public Set<BrokenTestObject> getInput() {
        return brokenTestObjects;
    }
    
    public Set<BrokenTestObject> getApprovedTestObjects() {
        return brokenTestObjects.stream()
                .filter(brokenTestObject -> brokenTestObject.getApproved())
                .collect(Collectors.toSet());
    }

    private void handleSelectionChange(TypedEvent selectionEvent) {
        dispatchSelectionEvent(selectionEvent);
    }

    private void dispatchSelectionEvent(TypedEvent selectionEvent) {
        notifyListeners(SWT.Selection, null);
        notifyListeners(SWT.DefaultSelection, null);
    }

    public void addSelectionListener(SelectionListener listener) {
        checkWidget();
        if (listener == null) {
            return;
        }
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Selection, typedListener);
        addListener(SWT.DefaultSelection, typedListener);
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }
}
