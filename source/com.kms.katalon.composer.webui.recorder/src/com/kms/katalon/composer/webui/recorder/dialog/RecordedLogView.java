package com.kms.katalon.composer.webui.recorder.dialog;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.logging.RecordingOutputStreamHandler.OutputType;
import com.kms.katalon.execution.logging.RecordingOutputStreamHandler.RecordedOutputLine;

import mnita.ansiconsole.participants.AnsiConsoleStyleListener;

public class RecordedLogView {
    private StyledText txtVerificationLog;
    private Label lblVerificationResultStatus;
    private Composite resultStatusComposite;
    private EventServiceAdapter eventHandler;

    public Composite createLogsView(Composite parent) {
        Composite logView = new Composite(parent, SWT.NONE);
        logView.setLayout(new GridLayout());

        resultStatusComposite = new Composite(logView, SWT.NONE);
        resultStatusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glResultStatus = new GridLayout(2, false);
        glResultStatus.marginWidth = 0;
        glResultStatus.marginHeight = 0;
        resultStatusComposite.setLayout(glResultStatus);
        
        Label lblVerificationResult = new Label(resultStatusComposite, SWT.NONE);
        lblVerificationResult.setText("Status");
        
        lblVerificationResultStatus = new Label(resultStatusComposite, SWT.NONE);
        lblVerificationResultStatus.setForeground(ColorUtil.getTextWhiteColor());
        
        txtVerificationLog = new StyledText(logView, SWT.READ_ONLY| SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        txtVerificationLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        txtVerificationLog.setFont(JFaceResources.getTextFont());
        txtVerificationLog.setBackground(ColorUtil.getWhiteBackgroundColor());
        txtVerificationLog.addLineStyleListener(new AnsiConsoleStyleListener());
        
        registerEventListeners();
        
        return logView;
    }
    
    private void registerEventListeners() {
        eventHandler = new EventServiceAdapter() {
            
            @Override
            public void handleEvent(Event event) {
                switch (event.getTopic()) {
                    case EventConstants.WEBUI_VERIFICATION_LOG_UPDATED: {
                        RecordedOutputLine output = (RecordedOutputLine) getObject(event);

                        int start = txtVerificationLog.getCharCount();
                        String text = output.getText();
                        txtVerificationLog.append(text + "\n");
                        if (output.getType() ==  OutputType.ERROR) {
                            StyleRange errorStyleRange = new StyleRange();
                            errorStyleRange.foreground = ColorUtil.getTextErrorColor();
                            errorStyleRange.start = start;
                            errorStyleRange.length = text.length();
                            txtVerificationLog.setStyleRange(errorStyleRange);
                        }
                        int lineCount = txtVerificationLog.getLineCount();
                        if (lineCount >= 1) {
                            txtVerificationLog.setTopIndex(lineCount - 1);
                        }
                        return;
                    }
                    case EventConstants.WEBUI_VERIFICATION_EXECUTION_FINISHED: {
                        setVerificationResultStatus((TestStatusValue) getObject(event));
                        return;
                    }
                    case EventConstants.WEBUI_VERIFICATION_START_EXECUTION: {
                        txtVerificationLog.getDisplay().asyncExec(() -> {
                            lblVerificationResultStatus.setText("RUNNING");
                            lblVerificationResultStatus.setForeground(ColorUtil.getBlackBackgroundColor());
                            lblVerificationResultStatus.setBackground(ColorUtil.getRunningLogBackgroundColor());
                            resultStatusComposite.layout(true, true);

                            txtVerificationLog.setText("");
                            txtVerificationLog.setStyleRanges(new StyleRange[0]);
                        });
                        return;
                    }
                    case EventConstants.WEBUI_VERIFICATION_STOP_EXECUTION: {
                        setVerificationResultStatus(TestStatusValue.INCOMPLETE);
                        return;
                    }
                    default:
                        break;
                }
            }
        };
        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_LOG_UPDATED, eventHandler);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_EXECUTION_FINISHED, eventHandler);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_START_EXECUTION, eventHandler);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_STOP_EXECUTION, eventHandler);
        
        resultStatusComposite.addDisposeListener(new DisposeListener() {
            
            @Override
            public void widgetDisposed(DisposeEvent e) {
                eventBroker.unsubscribe(eventHandler);
            }
        });
    }

    private void setVerificationResultStatus(TestStatusValue value) {
        lblVerificationResultStatus.setText(value.toString());
        lblVerificationResultStatus.setForeground(ColorUtil.getTextWhiteColor());
        lblVerificationResultStatus.setBackground(getBackgroundColorForVerificationResultStatus(value));
        resultStatusComposite.layout(true, true);
    }

    private Color getBackgroundColorForVerificationResultStatus(TestStatusValue value) {
        if (TestStatusValue.PASSED.equals(value)) {
            return ColorUtil.getPassedLogBackgroundColor();
        } else if (TestStatusValue.WARNING.equals(value)) {
            return ColorUtil.getWarningLogBackgroundColor();
        } else if (TestStatusValue.ERROR.equals(value) || TestStatusValue.FAILED.equals(value)){
            return ColorUtil.getErrorLogBackgroundColor();
        } else if (TestStatusValue.INCOMPLETE.equals(value)) {
            return ColorUtil.getIncompleteLogColor();
        }
        return ColorUtil.getDefaultBackgroundColor();
    }
}
