package com.kms.katalon.composer.quickstart;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.MessageConstants;

public class QuickPrepareProjectDialog extends AbstractDialog {

    public QuickPrepareProjectDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.DIALOG_TITLE_QUICK_PREPARE_PROJECT;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(1, true);
        containerLayout.marginWidth = 20;
        containerLayout.marginHeight = 10;
        container.setLayout(containerLayout);

        createQuestionLabel(container);
        createButtons(container);

        return container;
    }

    private void createQuestionLabel(Composite parent) {
        Label lblCreateProjectQuestion = new Label(parent, SWT.NONE);
        lblCreateProjectQuestion.setAlignment(SWT.CENTER);
        lblCreateProjectQuestion.setText(MessageConstants.LBL_PREPARE_PROJECT_QUESTION);
        lblCreateProjectQuestion.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
    }

    private void createButtons(Composite parent) {
        RowLayout rowLayout = new RowLayout();
        rowLayout.marginTop = 10;
        rowLayout.spacing = 25;
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        buttonsComposite.setLayout(rowLayout);
        buttonsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

        Button btnCreateProject = new Button(buttonsComposite, SWT.NONE);
        btnCreateProject.setText(MessageConstants.BTN_CREATE_PROJECT);
        btnCreateProject.addListener(SWT.Selection, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                try {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
                    handlerService.executeCommand(IdConstants.NEW_PROJECT_COMMAND_ID, null);
                    close();
                } catch (CommandException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });;

        Button btnOpenProject = new Button(buttonsComposite, SWT.NONE);
        btnOpenProject.setText(MessageConstants.BTN_OPEN_PROJECT);
        btnOpenProject.addListener(SWT.Selection, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                try {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
                    handlerService.executeCommand(IdConstants.OPEN_PROJECT_COMMAND_ID, null);
                    close();
                } catch (CommandException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }

    @Override
    protected void setInput() {
        //
    }

    @Override
    protected void registerControlModifyListeners() {
        //
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return null;
    }
}
