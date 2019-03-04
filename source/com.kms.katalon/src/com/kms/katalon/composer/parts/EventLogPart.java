package com.kms.katalon.composer.parts;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.handlers.ActiveEventLogPartHandler;
import com.kms.katalon.composer.handlers.ActiveEventLogPartHandler.ColorString;

public class EventLogPart {

    private static final int MAX_LENGTH = 80000;

    @Inject
    private IEventBroker eventBroker;

    private StyledText text;

    boolean isOnTop = false;

    @PostConstruct
    public void createPart(Composite parent, MPart mpart) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBackground(ColorUtil.getWhiteBackgroundColor());
        container.setLayout(new GridLayout());

        text = new StyledText(container, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        text.setFont(JFaceResources.getTextFont());
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        eventBroker.subscribe("KATALON_STUDIO/EVENT_LOG/CLEAR_LOG", new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (text.isDisposed()) {
                    return;
                }
                UISynchronizeService.syncExec(() -> {
                    text.setText("");
                    ActiveEventLogPartHandler.getInstance().clearBufferredStrings();
                });
            }
        });
        
        text.addPaintListener(new PaintListener() {
            
            @Override
            public void paintControl(PaintEvent e) {
                safelyPrintLog();
            }
        });
    }

    private void safelyPrintLog() {
        if (isNotSafetyToPrint()) {
            return;
        }
        UISynchronizeService.asyncExec(() -> {
            printBufferredLog();
        });
    }

    private boolean isNotSafetyToPrint() {
        return text == null || text.isDisposed();
    }

    private void truncateTextIfReachMaxLength() {
        if (text.getText().length() >= MAX_LENGTH) {
            text.setText(StringUtils.substring(text.getText(), text.getText().length() - MAX_LENGTH));
        }
    }

    private void printBufferredLog() {
        ActiveEventLogPartHandler logHandler = ActiveEventLogPartHandler.getInstance();
        Collection<ColorString> bufferredStrings = logHandler.getBufferredStrings();
        logHandler.clearBufferredStrings();
        if (bufferredStrings.isEmpty()) {
            return;
        }
        Iterator<ColorString> iterator = bufferredStrings.iterator();
        while (iterator.hasNext()) {
            if (isNotSafetyToPrint()) {
                return;
            }
            ColorString colorString = iterator.next();
            StyleRange range = new StyleRange();
            range.start = text.getText().length();
            range.length = colorString.getString().length();
            range.foreground = colorString.isError() ? ColorUtil.getTextErrorColor() : ColorUtil.getDefaultTextColor();

            text.append(colorString.getString());
            text.setStyleRange(range);
        }

        truncateTextIfReachMaxLength();
        text.setTopIndex(text.getLineCount() - 1);
    }
}
