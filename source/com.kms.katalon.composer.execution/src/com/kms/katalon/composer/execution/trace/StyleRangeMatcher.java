package com.kms.katalon.composer.execution.trace;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;

public interface StyleRangeMatcher {
    List<StyleRange> getStyleRanges(String message);

    void onClick(String message, StyleRange styleRange);
}
