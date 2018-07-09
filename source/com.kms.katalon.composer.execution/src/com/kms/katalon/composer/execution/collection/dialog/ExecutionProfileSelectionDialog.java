package com.kms.katalon.composer.execution.collection.dialog;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileSelectionDialog extends AbstractDialog {

    private CTableViewer tbvExecutionProfile;

    private List<ExecutionProfileEntity> profiles;

    private ExecutionProfileEntity selectedProfile;

    public ExecutionProfileSelectionDialog(Shell parentShell, List<ExecutionProfileEntity> profiles,
            ExecutionProfileEntity selectedProfile) {
        super(parentShell);
        this.profiles = profiles;
        this.selectedProfile = selectedProfile;
    }

    @Override
    protected void registerControlModifyListeners() {
        tbvExecutionProfile.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedProfile = (ExecutionProfileEntity) tbvExecutionProfile.getStructuredSelection()
                        .getFirstElement();
                validateSelectedProfile();
            }
        });
        
        tbvExecutionProfile.addDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (getButton(OK).isEnabled()) {
                    okPressed();
                }
            }
        });
    }

    private void validateSelectedProfile() {
        getButton(OK).setEnabled(selectedProfile != null);
    }

    @Override
    protected void setInput() {
        tbvExecutionProfile.setInput(profiles);
        if (selectedProfile == null) {
            selectedProfile = profiles.get(0);
        }
        tbvExecutionProfile.setSelection(new StructuredSelection(selectedProfile));
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        TableColumnLayout tableLayout = new TableColumnLayout();
        container.setLayout(tableLayout);

        tbvExecutionProfile = new CTableViewer(container, SWT.BORDER);
        tbvExecutionProfile.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        TableViewerColumn tbvclmProfile = new TableViewerColumn(tbvExecutionProfile, SWT.NONE);
        tbvclmProfile.setLabelProvider(new ExecutionProfileLabelProvider());
        tableLayout.setColumnData(tbvclmProfile.getColumn(), new ColumnWeightData(98, 380));

        tbvExecutionProfile.setContentProvider(ArrayContentProvider.getInstance());
        return container;
    }

    @Override
    public void create() {
        super.create();
        validateSelectedProfile();
    }

    public ExecutionProfileEntity getSelectedProfile() {
        return selectedProfile;
    }

    @Override
    public String getDialogTitle() {
        return ComposerExecutionMessageConstants.DIA_TITLE_EXECUTION_PROILE;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 370);
    }

    private class ExecutionProfileLabelProvider extends TypeCheckedStyleCellLabelProvider<ExecutionProfileEntity> {

        public ExecutionProfileLabelProvider() {
            super(0);
        }

        @Override
        protected Class<ExecutionProfileEntity> getElementType() {
            return ExecutionProfileEntity.class;
        }

        @Override
        protected Image getImage(ExecutionProfileEntity element) {
            return ImageConstants.IMG_16_PROFILE;
        }

        @Override
        protected String getText(ExecutionProfileEntity element) {
            return element.getName();
        }
    }
}
