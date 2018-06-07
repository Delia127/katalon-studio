package com.kms.katalon.composer.keyword.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.keyword.constants.ComposerKeywordMessageConstants;
import com.kms.katalon.composer.keyword.constants.StringConstants;

public class DuplicatedImportDialog extends AbstractDialog {

    public static final int KEEP_BOTH_ID = 22;

    private Composite container;

    private String existedFileName;

    private Button appliedToAllBtn;

    private boolean applyToAll;

    public DuplicatedImportDialog(Shell parentShell, String fileName) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_IMPORT_KEYWORD);
        this.existedFileName = fileName;
        this.applyToAll = false;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        String warningMsg = MessageFormat.format(ComposerKeywordMessageConstants.DIA_MSG_FILE_ALREADY_EXIST_WARN,
                existedFileName);

        StyledText text = new StyledText(container, SWT.NONE);
        text.setText(warningMsg);

        StyleRange boldStyle = new StyleRange();
        boldStyle.start = warningMsg.indexOf(existedFileName);
        boldStyle.length = existedFileName.length();
        boldStyle.fontStyle = SWT.BOLD;
        text.setStyleRange(boldStyle);
        text.setBackground(container.getBackground());

        appliedToAllBtn = new Button(container, SWT.CHECK);
        appliedToAllBtn.setText(StringConstants.DIA_BTN_APPLY_THIS_ACTION_TO_ALL);
        appliedToAllBtn.setSelection(false);

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, StringConstants.DIA_BTN_OVERWRITE, true);
        createButton(parent, IDialogConstants.SKIP_ID, StringConstants.DIA_BTN_SKIP, true);
        createButton(parent, KEEP_BOTH_ID, StringConstants.DIA_BTN_KEEP_BOTH, false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
        setReturnCode(buttonId);
        close();
    }

    @Override
    protected void registerControlModifyListeners() {
        appliedToAllBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setApplyToAll(appliedToAllBtn.getSelection());
            }
        });

    }

    @Override
    protected void setInput() {
    }

    public boolean isApplyToAll() {
        return applyToAll;
    }

    public void setApplyToAll(boolean applyToAll) {
        this.applyToAll = applyToAll;
    }
}
