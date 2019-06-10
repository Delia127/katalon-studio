package com.kms.katalon.composer.testsuite.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.dialogs.provider.ScriptVariableTypeEditingSupport;
import com.kms.katalon.composer.testsuite.dialogs.provider.ScriptVariableValueEditingSupport;
import com.kms.katalon.composer.testsuite.dialogs.provider.VariableScriptBuilderLabelProvider;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;

public class VariableBindingScriptBuilderDialog extends AbstractDialog {

    private static final Point MIN_SIZE = new Point(500, 300);

    private TestSuiteTestCaseLink testCaseLink;

    private VariableLink variableLink;

    private CTableViewer tableViewer;

    private Composite composite;
    
    private ScriptVariableValueEditingSupport editingSupport;

    public VariableBindingScriptBuilderDialog(Shell parentShell, TestSuiteTestCaseLink testCaseLink,
            VariableLink variableLink) {
        super(parentShell);
        this.testCaseLink = testCaseLink;
        this.variableLink = variableLink;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_VARIABLE_BINDING_VALUE_BUILDER;
    }

    @Override
    protected void registerControlModifyListeners() {
        // Do nothing
    }

    @Override
    protected void setInput() {
        tableViewer.setInput(new VariableLink[] { variableLink });
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        TableColumnLayout tableLayout = new TableColumnLayout();
        composite.setLayout(tableLayout);

        tableViewer = new CTableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);

        TableColumn tblclmnName = createColumn(StringConstants.NAME, 
                newCellLabelProvider(VariableScriptBuilderLabelProvider.CLMN_NAME_IDX), 
                null);
        tableLayout.setColumnData(tblclmnName, new ColumnWeightData(30));

        TableColumn tblclmnType = createColumn(StringConstants.TYPE,
                newCellLabelProvider(VariableScriptBuilderLabelProvider.CLMN_TYPE_IDX),
                new ScriptVariableTypeEditingSupport(tableViewer));
        tableLayout.setColumnData(tblclmnType, new ColumnWeightData(28, 110));

        TableColumn tblclmnValue = createColumn(StringConstants.VALUE,
                newCellLabelProvider(VariableScriptBuilderLabelProvider.CLMN_VALUE_IDX),
                new ScriptVariableValueEditingSupport(tableViewer));
        tableLayout.setColumnData(tblclmnValue, new ColumnWeightData(40));

        tableViewer.setContentProvider(new ArrayContentProvider());
        addToolTipSupportForTable(table);
        return composite;
    }

    private void addToolTipSupportForTable(Table table) {
        table.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(tableViewer, ColumnViewerToolTipSupport.NO_RECREATE);
    }
    
    private CellLabelProvider newCellLabelProvider(int columnIndex) {
        return new VariableScriptBuilderLabelProvider(columnIndex, testCaseLink);
    }

    private TableColumn createColumn(String tableName, CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumn = tableViewerColumn.getColumn();

        tableColumn.setText(StringUtils.defaultString(tableName));

        if (labelProvider != null) {
            tableViewerColumn.setLabelProvider(labelProvider);
        }

        if (editingSupport != null) {
            tableViewerColumn.setEditingSupport(editingSupport);
            if (StringConstants.VALUE.equals(tableName)) {
                this.editingSupport = (ScriptVariableValueEditingSupport)editingSupport;
            }
        }
        return tableColumn;
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setMinimumSize(MIN_SIZE);
    }

    @Override
    protected Point getInitialSize() {
        return MIN_SIZE;
    }

    public VariableLink getNewValue() {
        return variableLink;
    }
    
    @Override
    protected void okPressed() {
        if (editingSupport != null) {
            AstValueUtil.applyEditingValue(editingSupport.getEditor());
        }
        super.okPressed();
    }
    
    public ScriptVariableValueEditingSupport getEditor() {
        return editingSupport;
    }
}
