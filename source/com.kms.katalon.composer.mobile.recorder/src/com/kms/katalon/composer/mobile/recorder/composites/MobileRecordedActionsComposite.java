package com.kms.katalon.composer.mobile.recorder.composites;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;

public class MobileRecordedActionsComposite extends Composite {

    private Dialog parentDialog;

    public List<AstTreeTableNode> getRecordedActions() {
        return stepView.getNodes();
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
    
    private void refresh() {
        
    }
}
