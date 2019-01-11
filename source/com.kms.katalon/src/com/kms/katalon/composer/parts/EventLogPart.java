package com.kms.katalon.composer.parts;

import java.io.PrintStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.logging.LogManager;

public class EventLogPart {

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

        LogManager.getOutputLogger().addWriter(new PrintStream(LogManager.getOutputLogger()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                if (text.isDisposed()) {
                    return;
                }
                text.getDisplay().syncExec(() -> {
                    text.append(new String(ArrayUtils.subarray(buf, off, len)));
                    text.setTopIndex(text.getLineCount() - 1);
                });
            }
        });

        text.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                LogManager.getOutputLogger().addWriter(null);
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
}
