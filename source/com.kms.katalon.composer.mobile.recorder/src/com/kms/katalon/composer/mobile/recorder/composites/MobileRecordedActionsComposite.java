package com.kms.katalon.composer.mobile.recorder.composites;

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
    
    private MobileRecordedStepsViewComposite stepView;
    
    public MobileRecordedStepsViewComposite getStepView() {
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
        createRecordedActionComposite(this);
    }

    private void createRecordedActionComposite(Composite parent) {
        stepView = new MobileRecordedStepsViewComposite(this.parentDialog, parent);
    }

    private void createCompositeLabel(Composite parent) {
        Label lblRecordedActions = new Label(parent, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ControlUtils.setFontToBeBold(lblRecordedActions);
        lblRecordedActions.setText(MobileRecoderMessagesConstants.LBL_RECORDED_ACTIONS);
    }
}
