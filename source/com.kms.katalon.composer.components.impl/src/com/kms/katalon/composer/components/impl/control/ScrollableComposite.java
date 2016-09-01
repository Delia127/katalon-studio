package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ScrollableComposite extends ScrolledComposite implements MouseWheelListener {

    private static final int PAGE_INCREMENT = 4;

    public ScrollableComposite(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    public void setContent(Control content) {
        removeMouseScrollListenerForCurrentContent();
        super.setContent(content);
    }

    private void removeMouseScrollListenerForCurrentContent() {
        Control oldContent = getContent();
        if (oldContent == null || oldContent.isDisposed()) {
            return;
        }
        oldContent.removeMouseWheelListener(this);
    }

    @Override
    public void mouseScrolled(MouseEvent event) {
        int wheelCount = event.count;
        wheelCount = (int) Math.ceil(wheelCount / 3.0f);
        while (wheelCount < 0) {
            getVerticalBar().setIncrement(PAGE_INCREMENT);
            wheelCount++;
        }

        while (wheelCount > 0) {
            getVerticalBar().setIncrement(-PAGE_INCREMENT);
            wheelCount--;
        }
    }
    
    @Override
    public void dispose() {
        removeMouseScrollListenerForCurrentContent();
        super.dispose();
    }
}
