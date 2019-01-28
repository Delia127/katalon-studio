package com.kms.katalon.composer.parts;

import java.io.PrintStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.logging.LogManager;

public class EventLogPart {

    private static final int MAX_LENGTH = 80000;

    @Inject
    private IEventBroker eventBroker;

    private StyledText text;

    @PostConstruct
    public void createPart(Composite parent, MPart mpart) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBackground(ColorUtil.getWhiteBackgroundColor());
        container.setLayout(new GridLayout());

        text = new StyledText(container, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));

        LogManager.getOutputLogger().setWriter(new PrintStream(LogManager.getOutputLogger()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                writeLog(buf, off, len);
            }
        });

        LogManager.getErrorLogger().setWriter(new PrintStream(LogManager.getOutputLogger()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                writeErrorLog(buf, off, len);
            }
        });

        text.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                LogManager.getOutputLogger().setWriter(null);
                LogManager.getErrorLogger().setWriter(null);
            }
        });

        eventBroker.subscribe("KATALON_STUDIO/EVENT_LOG/CLEAR_LOG", new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (text.isDisposed()) {
                    return;
                }
                text.getDisplay().syncExec(() -> {
                    text.setText("");
                });
            }
        });
    }

    private void writeLog(byte[] buf, int off, int len) {
        if (text == null || text.isDisposed()) {
            return;
        }
        Display currentDisplay = Display.getCurrent();
        if (currentDisplay != null && Thread.currentThread() == currentDisplay.getThread()) {
            clearTextIfReachMaxLength();
            String string = new String(ArrayUtils.subarray(buf, off, len));
            text.append(string);
            text.setTopIndex(text.getLineCount() - 1);
        }
    }

    private void writeErrorLog(byte[] buf, int off, int len) {
        if (text == null || text.isDisposed()) {
            return;
        }
        Display currentDisplay = Display.getCurrent();
        if (currentDisplay != null && Thread.currentThread() == currentDisplay.getThread()) {
            clearTextIfReachMaxLength();
            String string = new String(ArrayUtils.subarray(buf, off, len));
            StyleRange range = new StyleRange();
            range.start = text.getText().length();
            range.length = string.length();
            range.foreground = ColorUtil.getTextErrorColor();

            text.append(string);
            text.setStyleRange(range);
            text.setTopIndex(text.getLineCount() - 1);
        }
    }
    
    private void clearTextIfReachMaxLength() {
        if (text.getText().length() >= MAX_LENGTH) {
            text.setText("");
        }
    }
}
