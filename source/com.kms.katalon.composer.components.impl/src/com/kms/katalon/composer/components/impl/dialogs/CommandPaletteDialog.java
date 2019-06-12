package com.kms.katalon.composer.components.impl.dialogs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.command.KCommand;
import com.kms.katalon.composer.components.impl.command.KatalonCommands;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.control.StyledTextMessage;
import com.kms.katalon.composer.components.impl.util.EntityIndexingUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.HttpRequestController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.dal.exception.NullAttributeException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class CommandPaletteDialog extends Dialog {

    private Composite mainComposite;

    private ListViewer listCommand;

    private StyledText txtCommand;

    private IEventBroker eventBroker;

    private EventHandler loadCommandsEventHandler;

    private EntityIndexingUtil entityIndexingUtil;

    private KatalonCommands katalonCommands;

    public CommandPaletteDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL);
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        katalonCommands = KatalonCommands.getInstance();
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return;
            }
            entityIndexingUtil = EntityIndexingUtil.getInstance(currentProject);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        mainComposite = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        mainComposite.setLayout(layout);

        Composite mainContainer = new Composite(mainComposite, SWT.NONE);
        mainContainer.setBackground(ColorUtil.getToolBarBackgroundColor());
        mainContainer.setLayout(new GridLayout());
        mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        txtCommand = new StyledText(mainContainer, SWT.BORDER | SWT.SINGLE);
        txtCommand.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        txtCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        StyledTextMessage styledTextMessage = new StyledTextMessage(txtCommand);
        styledTextMessage.setMessage(ComposerComponentsImplMessageConstants.DIA_TXT_MSG_COMMAND_PALETTE);

        listCommand = new ListViewer(mainContainer, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        org.eclipse.swt.widgets.List list = listCommand.getList();
        list.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listCommand.setContentProvider(new ArrayContentProvider());
        listCommand.setComparator(new ViewerComparator());
        listCommand.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                if (isKCommand(element)) {
                    return ((KCommand) element).getName();
                }
                return super.getText(element);
            }
        });
        listCommand.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (!isKCommand(element)) {
                    return false;
                }
                String commandName = ((KCommand) element).getName().toLowerCase();
                String inputText = txtCommand.getText().toLowerCase();
                return commandName.contains(inputText);
            }
        });

        Label lblHint = new Label(mainContainer, SWT.NONE);
        lblHint.setText(ComposerComponentsImplMessageConstants.DIA_LBL_PRESS_ESC_TO_CLOSE);
        lblHint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        lblHint.setBackground(ColorUtil.getToolBarBackgroundColor());

        addListeners();
        populateCommand(katalonCommands.getRootCommand().getChildren());

        return mainComposite;
    }

    private void addListeners() {
        org.eclipse.swt.widgets.List list = listCommand.getList();
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                performCommand();
            }
        });

        list.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                onEnterPressed(e);
            }
        });

        txtCommand.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (listCommand == null) {
                    return;
                }
                listCommand.refresh();
                list.setSelection(0);
            }
        });

        txtCommand.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int currentIndex = list.getSelectionIndex();
                if (e.keyCode == SWT.ARROW_DOWN) {
                    e.doit = false;
                    int maxIndex = list.getItemCount() - 1;
                    int downIndex = currentIndex < maxIndex ? currentIndex + 1 : maxIndex;

                    Object item = listCommand.getElementAt(downIndex);
                    if (item == null) {
                        return;
                    }

                    listCommand.setSelection(new StructuredSelection(item), true);
                    return;
                }

                if (e.keyCode == SWT.ARROW_UP) {
                    e.doit = false;
                    int upIndex = currentIndex > 0 ? currentIndex - 1 : 0;

                    Object item = listCommand.getElementAt(upIndex);
                    if (item == null) {
                        return;
                    }

                    listCommand.setSelection(new StructuredSelection(item), true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                onEnterPressed(e);
            }
        });

        getShell().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ESC) {
                    close();
                }
            }
        });

        getShell().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                txtCommand.forceFocus();
            }
        });

        loadCommandsEventHandler = new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                KCommand command = (KCommand) EventUtil.getData(event);
                if (command == null) {
                    return;
                }

                BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {

                    @Override
                    public void run() {
                        String endpointEventName = (String) command.getEventData();
                        if (endpointEventName == null) {
                            return;
                        }

                        switch (endpointEventName) {
                            case EventConstants.TESTCASE_OPEN:
                            case EventConstants.TESTCASE_ADD_STEP_CALL_TESTCASE:
                                populateCommand(KatalonCommands.createCommands(getTestCaseIds(), endpointEventName));
                                break;

                            case EventConstants.TEST_SUITE_OPEN:
                                populateCommand(KatalonCommands.createCommands(getTestSuiteIds(), endpointEventName));
                                break;

                            case EventConstants.TEST_OBJECT_OPEN:
                                populateCommand(KatalonCommands.createCommands(getTestObjectIds(), endpointEventName));
                                break;

                            case EventConstants.TEST_DATA_OPEN:
                                populateCommand(KatalonCommands.createCommands(getTestDataIds(), endpointEventName));
                                break;

                            case EventConstants.CHECKPOINT_OPEN:
                                populateCommand(KatalonCommands.createCommands(getCheckpointIds(), endpointEventName));
                                break;

                            case EventConstants.KATALON_UPDATE_HELP_CONTENTS:
                                List<KCommand> onlineHelpContents = getOnlineHelpContents();
                                if (onlineHelpContents.isEmpty()) {
                                    close();
                                    break;
                                }
                                populateCommand(onlineHelpContents);
                                break;

                            default:
                                close();
                                break;
                        }
                    }
                });
            }
        };

        eventBroker.subscribe(EventConstants.KATALON_LOAD_COMMANDS, loadCommandsEventHandler);
    }

    protected void performCommand() {
        try {
            KCommand command = (KCommand) ((StructuredSelection) listCommand.getSelection()).getFirstElement();
            if (command == null) {
                return;
            }

            if (command.hasChildren()) {
                populateCommand(command.getChildren());
                return;
            }

            performEventAction(command);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void performEventAction(KCommand command) throws Exception {
        String eventName = command.getEventName();
        if (eventName == null) {
            close();
            return;
        }

        String commandName = command.getName();
        switch (eventName) {
            case EventConstants.TESTCASE_OPEN: {
                EventUtil.post(eventName, TestCaseController.getInstance().getTestCaseByDisplayId(commandName));
                close();
                return;
            }

            case EventConstants.TEST_OBJECT_OPEN: {
                WebElementEntity testobject = ObjectRepositoryController.getInstance()
                        .getWebElementByDisplayPk(commandName);
                String event = (testobject instanceof WebServiceRequestEntity)
                        ? EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN : eventName;
                EventUtil.post(event, testobject);
                close();
                return;
            }

            case EventConstants.TEST_DATA_OPEN: {
                EventUtil.post(eventName, TestDataController.getInstance().getTestDataByDisplayId(commandName));
                close();
                return;
            }

            case EventConstants.TEST_SUITE_OPEN: {
                TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(commandName,
                        ProjectController.getInstance().getCurrentProject());
                if (testSuite == null) {
                    EventUtil.post(EventConstants.TEST_SUITE_COLLECTION_OPEN, commandName);
                } else {
                    EventUtil.post(eventName, testSuite);
                }
                close();
                return;
            }

            case EventConstants.CHECKPOINT_OPEN: {
                EventUtil.post(eventName, CheckpointController.getInstance().getByDisplayedId(commandName));
                close();
                return;
            }

            case EventConstants.KATALON_LOAD_COMMANDS: {
                // load children command
                EventUtil.post(eventName, command);
                return;
            }

            case EventConstants.KATALON_OPEN_URL: {
                String url = ObjectUtils.toString(command.getEventData());
                if (StringUtils.isNotBlank(url)) {
                    Program.launch(url);
                }
                close();
                return;
            }

            case EventConstants.TESTCASE_ADD_STEP_CALL_TESTCASE: {
                EventUtil.post(eventName, new Object[] { getActivePartId(),
                        TestCaseController.getInstance().getTestCaseByDisplayId(commandName) });
                close();
                return;
            }

            case EventConstants.TESTCASE_ADD_STEP: {
                EventUtil.post(eventName, new Object[] { getActivePartId(), command.getEventData() });
                close();
                return;
            }

            default: {
                EventUtil.post(eventName, command.getEventData());
                close();
                return;
            }
        }
    }

    private String getActivePartId() {
        return katalonCommands.getSelectedPartIdInComposerContentArea();
    }

    private void populateCommand(List<KCommand> commands) {
        if (txtCommand == null || listCommand == null) {
            return;
        }
        txtCommand.setText(StringConstants.EMPTY);
        listCommand.getList().removeAll();
        listCommand.setInput(commands.toArray(new KCommand[0]));
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 300);
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        int y = ApplicationSingleton.getInstance().getApplication().getChildren().get(0).getY() + 90;
        return new Point(super.getInitialLocation(initialSize).x, y);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        // We do not need button bar
        return parent;
    }

    private void onEnterPressed(KeyEvent e) {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
            performCommand();
        }
    }

    @Override
    public boolean close() {
        eventBroker.unsubscribe(loadCommandsEventHandler);
        return super.close();
    }

    private List<String> getTestCaseIds() {
        if (entityIndexingUtil == null) {
            return Collections.emptyList();
        }
        return entityIndexingUtil.getIndexedEntityIds(TestCaseEntity.getTestCaseFileExtension());
    }

    private List<String> getTestSuiteIds() {
        if (entityIndexingUtil == null) {
            return Collections.emptyList();
        }
        return entityIndexingUtil.getIndexedEntityIds(TestSuiteEntity.getTestSuiteFileExtension());
    }

    private List<String> getTestObjectIds() {
        if (entityIndexingUtil == null) {
            return Collections.emptyList();
        }
        return entityIndexingUtil.getIndexedEntityIds(WebElementEntity.getWebElementFileExtension());
    }

    private List<String> getTestDataIds() {
        if (entityIndexingUtil == null) {
            return Collections.emptyList();
        }
        return entityIndexingUtil.getIndexedEntityIds(DataFileEntity.getTestDataFileExtension());
    }

    private List<String> getCheckpointIds() {
        if (entityIndexingUtil == null) {
            return Collections.emptyList();
        }
        return entityIndexingUtil.getIndexedEntityIds(CheckpointEntity.getCheckpointFileExtension());
    }

    private List<KCommand> getOnlineHelpContents() {
        try {
            String jsonContent = HttpRequestController.getInstance()
                    .get(ComposerComponentsImplMessageConstants.DIA_KATALON_HELP_JSON_URL)
                    .text();
            KCommand onlineHelpCommand = JsonUtil.fromJson(jsonContent, KCommand.class);
            if (onlineHelpCommand == null || !onlineHelpCommand.hasChildren()) {
                throw new NullAttributeException(ComposerComponentsImplMessageConstants.DIA_NO_ONLINE_HELP_CONTENT);
            }

            List<KCommand> childrenCommands = onlineHelpCommand.getChildren();
            childrenCommands.sort(new Comparator<KCommand>() {

                @Override
                public int compare(KCommand cmd1, KCommand cmd2) {
                    return cmd1.getName().compareTo(cmd2.getName());
                }
            });

            // Cache the content
            PlatformUI.getPreferenceStore().setValue(PreferenceConstants.GENERAL_ONLINE_HELP_CONTENT,
                    JsonUtil.toJson(onlineHelpCommand, false));

            return childrenCommands;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialogWithLink.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                    ComposerComponentsImplMessageConstants.DIA_EXC_MSG_UNABLE_TO_LOAD_ONLINE_HELP_CONTENT);
            return Collections.emptyList();
        }
    }

    private boolean isKCommand(Object object) {
        return object != null && KCommand.class.getName().equals(object.getClass().getName());
    }

}
