package com.kms.katalon.composer.parts;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.logging.LogManager;

public class EventLogPart {

    private static final int MAX_LENGTH = 80000;

    @Inject
    private IEventBroker eventBroker;

    private StyledText text;

    private Collection<ColorString> bufferredStrings = new ConcurrentLinkedQueue<>();

    boolean isOnTop = false;

    @PostConstruct
    public void createPart(Composite parent, MPart mpart) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBackground(ColorUtil.getWhiteBackgroundColor());
        container.setLayout(new GridLayout());

        text = new StyledText(container, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        text.setFont(JFaceResources.getTextFont());
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
                UISynchronizeService.syncExec(() -> {
                    text.setText("");
                    bufferredStrings.clear();
                });
            }
        });
    }

    private void writeLog(byte[] buf, int off, int len) {
        String string = new String(ArrayUtils.subarray(buf, off, len));
        bufferredStrings.add(new ColorString(string, false));
        safelyPrintLog();
    }

    private void writeErrorLog(byte[] buf, int off, int len) {
        String string = new String(ArrayUtils.subarray(buf, off, len));
        bufferredStrings.add(new ColorString(string, true));
        safelyPrintLog();
    }

    private void safelyPrintLog() {
        if (text == null || text.isDisposed()) {
            return;
        }
        UISynchronizeService.asyncExec(() -> {
            printBufferredLog();
        });
    }

    private void truncateTextIfReachMaxLength() {
        if (text.getText().length() >= MAX_LENGTH) {
            text.setText(StringUtils.substring(text.getText(), text.getText().length() - MAX_LENGTH));
        }
    }

    private void printBufferredLog() {
        if (bufferredStrings.isEmpty()) {
            return;
        }
        Iterator<ColorString> iterator = bufferredStrings.iterator();
        while (iterator.hasNext()) {
            ColorString colorString = iterator.next();
            StyleRange range = new StyleRange();
            range.start = text.getText().length();
            range.length = colorString.getString().length();
            range.foreground = colorString.isError() ? ColorUtil.getTextErrorColor() : ColorUtil.getDefaultTextColor();

            text.append(colorString.getString());
            text.setStyleRange(range);
        }
        bufferredStrings.clear();

        truncateTextIfReachMaxLength();
        text.setTopIndex(text.getLineCount() - 1);
    }

    private class ColorString {
        private final boolean isError;

        private final String string;

        public ColorString(String string, boolean isError) {
            this.string = string;
            this.isError = isError;
        }

        public boolean isError() {
            return isError;
        }

        public String getString() {
            return string;
        }
    }
}
