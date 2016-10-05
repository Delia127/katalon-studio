package com.kms.katalon.about.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.internal.about.AboutItem;
import org.eclipse.ui.internal.about.AboutTextManager;

import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.util.VersionUtil;

@SuppressWarnings({ "restriction" })
public class VersionAboutTextManager extends AboutTextManager {

    public VersionAboutTextManager(StyledText text) {
        super(text);
    }

    public static AboutItem scan(String s) {
        ArrayList<int[]> linkRanges = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        // slightly modified version of jface url detection
        // see org.eclipse.jface.text.hyperlink.URLHyperlinkDetectors
        int urlSeparatorOffset = s.indexOf("://"); //$NON-NLS-1$
        while (urlSeparatorOffset >= 0) {

            boolean startDoubleQuote = false;

            // URL protocol (left to "://")
            int urlOffset = urlSeparatorOffset;
            char ch;
            do {
                urlOffset--;
                ch = ' ';
                if (urlOffset > -1) {
                    ch = s.charAt(urlOffset);
                }
                startDoubleQuote = ch == '"';
            } while (Character.isUnicodeIdentifierStart(ch));
            urlOffset++;

            // Right to "://"
            StringTokenizer tokenizer = new StringTokenizer(s.substring(urlSeparatorOffset + 3), " \t\n\r\f<>", false); //$NON-NLS-1$
            if (!tokenizer.hasMoreTokens()) {
                return null;
            }

            int urlLength = tokenizer.nextToken().length() + 3 + urlSeparatorOffset - urlOffset;

            if (startDoubleQuote) {
                int endOffset = -1;
                int nextDoubleQuote = s.indexOf('"', urlOffset);
                int nextWhitespace = s.indexOf(' ', urlOffset);
                if (nextDoubleQuote != -1 && nextWhitespace != -1) {
                    endOffset = Math.min(nextDoubleQuote, nextWhitespace);
                } else if (nextDoubleQuote != -1) {
                    endOffset = nextDoubleQuote;
                } else if (nextWhitespace != -1) {
                    endOffset = nextWhitespace;
                }
                if (endOffset != -1) {
                    urlLength = endOffset - urlOffset;
                }
            }

            linkRanges.add(new int[] { urlOffset, urlLength });
            links.add(s.substring(urlOffset, urlOffset + urlLength));

            urlSeparatorOffset = s.indexOf("://", urlOffset + urlLength + 1); //$NON-NLS-1$
        }
        Matcher matcher = Pattern.compile(
                MessageFormat.format(MessageConstants.NEW_VERSION_AVAIABLE, "").trim() + "\\s*\\S+\\s").matcher(s);
        if (matcher.find()) {
            int i = s.lastIndexOf(":", matcher.end());
            if (i >= 0) {
                if (s.charAt(i + 1) == ' ') {
                    i += 2;
                } else {
                    i++;
                }
                linkRanges.add(new int[] { i, matcher.end() - i });
                links.add(VersionUtil.URL_NEW_VERSION);
            }
        }

        return new AboutItem(s, (int[][]) linkRanges.toArray(new int[linkRanges.size()][2]),
                (String[]) links.toArray(new String[links.size()]));
    }
}
