package com.kms.katalon.composer.objectrepository.dialog;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteEntityDialog;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectReferencesDialog extends AbstractDeleteEntityDialog {

    // Controls
    private TableViewer tableViewer;
    private Label lblStatus;

    // Fields
    private WebElementEntity fDeletedTestObject;
    private List<WebElementEntity> fTestObjectReferences;

    public TestObjectReferencesDialog(Shell parentShell, WebElementEntity deletedTestObject,
            List<WebElementEntity> testObjectReferences, AbstractDeleteReferredEntityHandler deleteHandler) {
        super(parentShell, deleteHandler);
        fDeletedTestObject = deletedTestObject;
        fTestObjectReferences = testObjectReferences;
    }

    @Override
    protected Control createDialogComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        glComposite.verticalSpacing = 10;
        composite.setLayout(glComposite);

        Composite compositeHeader = new Composite(composite, SWT.NONE);
        compositeHeader.setLayout(new GridLayout(2, false));
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStatusImg = new Label(compositeHeader, SWT.NONE);
        lblStatusImg.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

        lblStatus = new Label(compositeHeader, SWT.WRAP);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tbvclmOrder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumn = tbvclmOrder.getColumn();
        tblclmnNewColumn.setWidth(40);
        tblclmnNewColumn.setText(StringConstants.NO_);
        tbvclmOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof WebElementEntity)) {
                    return "";
                }
                return Integer.toString(fTestObjectReferences.indexOf(element) + 1);
            }
        });

        TableViewerColumn tbvclmTestSuiteID = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnTestSuiteID = tbvclmTestSuiteID.getColumn();
        tblclmnTestSuiteID.setWidth(350);
        tblclmnTestSuiteID.setText(StringConstants.DIA_FIELD_TEST_OBJECT_ID);
        tbvclmTestSuiteID.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof WebElementEntity)) {
                    return "";
                }

                try {
                    return ObjectRepositoryController.getInstance().getIdForDisplay((WebElementEntity) element);
                } catch (Exception e) {
                    return "";
                }
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        return composite;
    }

    @Override
    protected void registerControlModifyListeners() {
        mainComposite.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                lblStatus.pack(true);
            }
        });
    }

    @Override
    protected void setInput() {
        try {
            lblStatus.setText(MessageFormat.format(StringConstants.DIA_MSG_HEADER_TEST_OBJECT_REFERENCES,
                    ObjectRepositoryController.getInstance().getIdForDisplay(fDeletedTestObject)));

            tableViewer.setInput(fTestObjectReferences);
            getButton(YesNoAllOptions.NO.ordinal()).forceFocus();
            mainComposite.layout(true, true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    @Override
    protected String getDialogTitle() {
        return "Test Object's References";
    }

}
