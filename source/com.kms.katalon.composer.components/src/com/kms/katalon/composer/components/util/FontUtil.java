package com.kms.katalon.composer.components.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class FontUtil {

    private static final String FONTS_FOLDER = "/resources/fonts/";

    private static final List<String> FONTS = Arrays.asList(
            "TT Norms/TTNorms-Black.otf",
            "TT Norms/TTNorms-BlackItalic.otf",
            "TT Norms/TTNorms-Bold.otf",
            "TT Norms/TTNorms-BoldItalic.otf",
            "TT Norms/TTNorms-Italic.otf",
            "TT Norms/TTNorms-Light.ttf",
            "TT Norms/TTNorms-Medium.ttf",
            "TT Norms/TTNorms-Regular.otf"
            );

    public static final int SIZE_H1 = 24;

    public static final int SIZE_H2 = 18;

    public static final int SIZE_H3 = 14;

    public static final int SIZE_H4 = 12;

    public static final int SIZE_H5 = 10;

    public static final int SIZE_H6 = 8;

    public static final int SIZE_NORMAL = 9;

    public static final int STYLE_NORMAL = SWT.NORMAL;

    public static final int STYLE_BOLD = SWT.BOLD;

    public static final int STYLE_ITALIC = SWT.ITALIC;

    public static final int STYLE_BOLD_ITALIC = SWT.BOLD | SWT.ITALIC;

    public static final int SIZE_DEFAULT = SIZE_NORMAL;

    public static final int STYLE_DEFAULT = SWT.NORMAL;

    public static final String FONT_FAMILY_DEFAULT;

    static {
        Label label = new Label(new Shell(), SWT.NONE);
        FONT_FAMILY_DEFAULT = label.getFont().getFontData()[0].getName();
    }

    public static final String FONT_FAMILY_TTNORMS = "TTNorms-Regular";

    public static final String FONT_FAMILY_TTNORMS_LIGHT = "TTNorms-Light";

    public static final String FONT_FAMILY_TTNORMS_BOLD = "TTNorms-Bold";

    public static final String FONT_FAMILY_TTNORMS_MEDIUM = "TTNorms-Medium";

    public static final Font REGULAR = build(SIZE_DEFAULT, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font BOLD = build(SIZE_DEFAULT, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font ITALIC = build(SIZE_DEFAULT, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H1 = build(SIZE_H1, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H1_BOLD = build(SIZE_H1, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H1_ITALIC = build(SIZE_H1, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H1_BOLD_ITALIC = build(SIZE_H1, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H2 = build(SIZE_H2, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H2_BOLD = build(SIZE_H2, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H2_ITALIC = build(SIZE_H2, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H2_BOLD_ITALIC = build(SIZE_H2, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H3 = build(SIZE_H3, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H3_BOLD = build(SIZE_H3, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H3_ITALIC = build(SIZE_H3, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H3_BOLD_ITALIC = build(SIZE_H3, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H4 = build(SIZE_H4, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H4_BOLD = build(SIZE_H4, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H4_ITALIC = build(SIZE_H4, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H4_BOLD_ITALIC = build(SIZE_H4, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H5 = build(SIZE_H5, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H5_BOLD = build(SIZE_H5, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H5_ITALIC = build(SIZE_H5, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H5_BOLD_ITALIC = build(SIZE_H5, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H6 = build(SIZE_H6, STYLE_NORMAL, FONT_FAMILY_DEFAULT);

    public static final Font H6_BOLD = build(SIZE_H6, STYLE_BOLD, FONT_FAMILY_DEFAULT);

    public static final Font H6_ITALIC = build(SIZE_H6, STYLE_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font H6_BOLD_ITALIC = build(SIZE_H6, STYLE_BOLD_ITALIC, FONT_FAMILY_DEFAULT);

    public static final Font FONT_TTNORMS = build(SIZE_DEFAULT, STYLE_NORMAL, FONT_FAMILY_TTNORMS);

    public static final Font FONT_TTNORMS_LIGHT = build(SIZE_DEFAULT, STYLE_NORMAL, FONT_FAMILY_TTNORMS_LIGHT);

    public static final Font FONT_TTNORMS_BOLD = build(SIZE_DEFAULT, STYLE_BOLD, FONT_FAMILY_TTNORMS_BOLD);

    public static final Font FONT_TTNORMS_MEDIUM = build(SIZE_DEFAULT, STYLE_NORMAL, FONT_FAMILY_TTNORMS_MEDIUM);

    public static Font build(int fontSize) {
        return build(fontSize, null);
    }

    public static Font build(int fontSize, String fontFamily) {
        fontFamily = StringUtils.isNotBlank(fontFamily)
                ? fontFamily
                : FONT_FAMILY_DEFAULT;
        return build(fontSize, STYLE_DEFAULT, fontFamily);
    }

    public static Font build(int fontSize, int fontStyle) {
        return build(fontSize, fontStyle, FONT_FAMILY_DEFAULT);
    }

    public static Font build(int fontSize, int fontStyle, String fontFamily) {
        fontFamily = StringUtils.isNotBlank(fontFamily)
                ? fontFamily
                : FONT_FAMILY_DEFAULT;
        FontData fontData = new FontData(fontFamily, fontSize, fontStyle);
        return new Font(Display.getCurrent(), fontData);
    }

    public static Font size(Font font, int fontSize) {
        FontData oldFontData = font.getFontData()[0];
        FontData newFontData = new FontData(oldFontData.getName(), fontSize, oldFontData.getStyle());
        return new Font(Display.getCurrent(), newFontData);
    }

    public static Font style(Font font, int fontStyle) {
        FontData oldFontData = font.getFontData()[0];
        FontData newFontData = new FontData(oldFontData.getName(), oldFontData.getHeight(), fontStyle);
        return new Font(Display.getCurrent(), newFontData);
    }

    public static Font family(Font font, String fontFamily) {
        FontData oldFontData = font.getFontData()[0];
        FontData newFontData = new FontData(fontFamily, oldFontData.getHeight(), oldFontData.getStyle());
        return new Font(Display.getCurrent(), newFontData);
    }

    public static void loadFonts() throws IOException {
        for (String fontName : FONTS) {
            String fontPath = FONTS_FOLDER + "/" + fontName;
            InputStream fontStream = FontUtil.class.getResourceAsStream(fontPath);

            File tempFontFile = File.createTempFile("font", "");

            FileUtils.copyInputStreamToFile(fontStream, tempFontFile);

            Display.getCurrent().loadFont(tempFontFile.getAbsolutePath());
        }
    }
}
