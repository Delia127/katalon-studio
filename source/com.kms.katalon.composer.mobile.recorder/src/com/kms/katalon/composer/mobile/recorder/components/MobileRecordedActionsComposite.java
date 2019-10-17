package com.kms.katalon.composer.mobile.recorder.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionParamValueType;
import com.kms.katalon.composer.mobile.recorder.constants.ImageConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.utils.MobileCompositeUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.execution.mobile.constants.StringConstants;

public class MobileRecordedActionsComposite extends Composite {

    private Dialog parentDialog;

    private ToolItem tltmDelete;

    private TableViewer actionTableViewer;

    public TableViewer getActionTableViewer() {
        return actionTableViewer;
    }

    private List<MobileActionMapping> recordedActions = new ArrayList<>();

    public List<MobileActionMapping> getRecordedActions() {
        List<AstTreeTableNode> zz = stepView.getNodes();
        return new ArrayList<>();
    }
    
    private MobileRecordedStepsView stepView;
    
    public MobileRecordedStepsView getStepView() {
        return stepView;
    }

    public MobileRecordedActionsComposite(Dialog parentDialog, Composite parent, int style) {
        super(parent, style | SWT.NONE);
        this.parentDialog = parentDialog;
        this.createComposite();
    }

    public MobileRecordedActionsComposite(Dialog parentDialog, Composite parent) {
        this(parentDialog, parent, 0);
    }
    
    private void createComposite() {
        setLayout(new GridLayout());
        createCompositeLabel(this);
//        createActionToolbar(this);
//        createActionTable(this);
        createRecordedActionComposite(this);
    }

    private void createRecordedActionComposite(Composite parent) {
        stepView = new MobileRecordedStepsView(this.parentDialog, parent);
    }

    private void createCompositeLabel(Composite parent) {
        Label lblRecordedActions = new Label(parent, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRecordedActions.setFont(MobileCompositeUtil.getFontBold(lblRecordedActions));
        lblRecordedActions.setText(MobileRecoderMessagesConstants.LBL_RECORDED_ACTIONS);
    }

    private void createActionToolbar(Composite parent) {
        ToolBar actionToolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        actionToolBar.setForeground(ColorUtil.getToolBarForegroundColor());
        actionToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        tltmDelete = new ToolItem(actionToolBar, SWT.PUSH);
        tltmDelete.setImage(ImageConstants.IMG_16_DELETE);
        tltmDelete.setEnabled(false);
        tltmDelete.setText(StringConstants.DELETE);
        tltmDelete.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (!(actionTableViewer.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
                for (Object selectedObject : selection.toArray()) {
                    if (!(selectedObject instanceof MobileActionMapping)) {
                        continue;
                    }
                    MobileActionMapping selectedActionMapping = (MobileActionMapping) selectedObject;
                    recordedActions.remove(selectedActionMapping);
                }
                actionTableViewer.refresh();
            }
        });
    }

    private void createActionTable(Composite parent) {
        Composite actionTableComposite = new Composite(parent, SWT.None);
        actionTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        actionTableComposite.setLayout(new GridLayout());
        
        actionTableViewer = createActionTableViewer(actionTableComposite);

        TableColumn noColumn = createNoColumn(actionTableViewer);
        TableColumn actionColumn = createActionColumn(actionTableViewer);
        TableColumn elementColumn = createElementColumn(actionTableViewer);
        
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(noColumn, new ColumnWeightData(0, 30));
        tableLayout.setColumnData(actionColumn, new ColumnWeightData(25, 100));
        tableLayout.setColumnData(elementColumn, new ColumnWeightData(40, 120));

        actionTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        actionTableViewer.setInput(recordedActions);
        actionTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                tltmDelete.setEnabled(isAnyTableItemSelected());
            }

            private boolean isAnyTableItemSelected() {
                if (actionTableViewer == null) {
                    return false;
                }

                ISelection selection = actionTableViewer.getSelection();
                return selection != null && !selection.isEmpty();
            }
        });
        
        actionTableComposite.setLayout(tableLayout);
    }
    
    private TableViewer createActionTableViewer(Composite actionTableComposite) {
        actionTableViewer = new TableViewer(actionTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        actionTableViewer.getTable().setHeaderVisible(true);
        actionTableViewer.getTable()
                .setLinesVisible(ControlUtils.shouldLineVisble(actionTableViewer.getTable().getDisplay()));
        ColumnViewerToolTipSupport.enableFor(actionTableViewer);
        ColumnViewerUtil.setTableActivation(actionTableViewer);
        return actionTableViewer;
    }
    
    private TableColumn createNoColumn(TableViewer actionTableViewer) {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn noColumn = tableViewerColumnNo.getColumn();
        noColumn.setText(MobileRecoderMessagesConstants.COL_HEADER_NO);
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MobileActionMapping) {
                    return String.valueOf(recordedActions.indexOf(element) + 1);
                }
                return "";
            }
        });
        return noColumn;
    }
    
    private TableColumn createActionColumn(TableViewer actionTableViewer) {
        TableViewerColumn tableViewerColumnAction = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn actionColumn = tableViewerColumnAction.getColumn();
        actionColumn.setText(MobileRecoderMessagesConstants.COL_HEADER_ACTION);
        tableViewerColumnAction.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MobileActionMapping) {
                    MobileActionMapping mobileActionMapping = (MobileActionMapping) element;
                    StringBuilder stringBuilder = new StringBuilder(mobileActionMapping.getAction().getReadableName());
                    MobileActionParamValueType[] data = mobileActionMapping.getData();
                    if (data != null && data.length > 0) {
                        String dataString = Arrays.asList(data)
                                .stream()
                                .map(dataItem -> dataItem.getParamName() + ": " + dataItem.getValueToDisplay())
                                .collect(Collectors.joining(", "));
                        stringBuilder.append(" [" + dataString + "]");
                    }
                    return stringBuilder.toString();
                }
                return "";
            }
        });
        return actionColumn;
    }
    
    private TableColumn createElementColumn(TableViewer actionTableViewer) {
        TableViewerColumn tableViewerColumnElement = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn elementColumn = tableViewerColumnElement.getColumn();
        elementColumn.setText(MobileRecoderMessagesConstants.COL_HEADER_ELEMENT);
        tableViewerColumnElement.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MobileActionMapping
                        && ((MobileActionMapping) element).getTargetElement() != null) {
                    return ((MobileActionMapping) element).getTargetElement().getName();
                }
                return "";
            }
        });
        return elementColumn;
    }
}
