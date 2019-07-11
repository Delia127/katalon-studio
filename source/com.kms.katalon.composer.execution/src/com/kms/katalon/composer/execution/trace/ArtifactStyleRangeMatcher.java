package com.kms.katalon.composer.execution.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.kms.katalon.composer.components.util.ColorUtil;

public abstract class ArtifactStyleRangeMatcher implements StyleRangeMatcher {

    public abstract String getPattern();

    @Override
    public List<StyleRange> getStyleRanges(String message) {
        if (StringUtils.isEmpty(message)) {
            return Collections.emptyList();
        }
        Matcher matcher = Pattern.compile(getPattern()).matcher(message);
        List<StyleRange> styleRanges = new ArrayList<>();
        while (matcher.find()) {
            StyleRange range = new StyleRange();
            range.start = matcher.start() + 1;
            range.length = matcher.end() - matcher.start() - 2;
            range.underline = true;
            range.foreground = ColorUtil.getHyperlinkTextColor();
            range.underlineStyle = SWT.UNDERLINE_LINK;
            range.data = this;
            styleRanges.add(range);
        }
        return styleRanges;
    }

    @Override
    public void onClick(String message, StyleRange styleRange) {
        String artifactId = message.substring(styleRange.start, styleRange.length + styleRange.start);
        internalClick(artifactId);
    }

    protected void internalClick(String artifactId) {
        //Children may override this
    }
}
