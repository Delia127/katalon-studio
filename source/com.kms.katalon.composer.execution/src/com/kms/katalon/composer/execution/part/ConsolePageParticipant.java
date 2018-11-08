package com.kms.katalon.composer.execution.part;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import mnita.ansiconsole.participants.AnsiConsoleStyleListener;

public class ConsolePageParticipant implements IConsolePageParticipant {

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }

    @Override
    public void init(IPageBookViewPage page, IConsole console) {
        if (page.getControl() instanceof StyledText) {
            StyledText viewer = (StyledText) page.getControl();
            viewer.addLineStyleListener(new HarHyperlinkStyleListener());
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void activated() {
    }

    @Override
    public void deactivated() {
    }

    private class HarHyperlinkStyleListener extends AnsiConsoleStyleListener {
        
        private static final String HAR = "HAR: ";
        
        private static final String HAR_EXTENSION = ".har";

        @Override
        public void lineGetStyle(LineStyleEvent event) {
            super.lineGetStyle(event);
            if (event == null || event.lineText == null || event.lineText.length() == 0)
                return;
            
            String lineText = event.lineText;
            if (lineText.contains(HAR)) {
                int harLogStart = lineText.indexOf(HAR);
                int harLogEnd = lineText.lastIndexOf(HAR_EXTENSION);
                if (harLogStart > 0 && harLogEnd > 0 && harLogEnd > harLogStart) {
                    int harFilePathStart = harLogStart + HAR.length();
                    int harFilePathEnd = harLogEnd + HAR_EXTENSION.length() - 1;
                    
                    StyleRange styleRange = new StyleRange();
                    styleRange.start = event.lineOffset + harFilePathStart;
                    styleRange.length = harFilePathEnd - harFilePathStart;
                    styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
                    styleRange.underline = true;
                    List<StyleRange> styleRanges = new ArrayList<StyleRange>();
                    styleRanges.addAll(Arrays.asList(event.styles));
                    styleRanges.add(styleRange);
                    event.styles = styleRanges.toArray(new StyleRange[styleRanges.size()]);
                }
            }
        }
        
    }
}
