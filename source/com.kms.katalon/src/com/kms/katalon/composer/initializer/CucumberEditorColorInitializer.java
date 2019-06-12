package com.kms.katalon.composer.initializer;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

import com.kms.katalon.composer.components.util.ColorUtil;

import cucumber.eclipse.editor.editors.GherkinColors;

public class CucumberEditorColorInitializer implements ApplicationInitializer {

    @Override
    public void setup() {
        changeCucumberEditorColor();
    }
    
    private void changeCucumberEditorColor() {
        ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
        ColorRegistry registry = currentTheme.getColorRegistry();

        registry.put(GherkinColors.DEFAULT.COLOR_PREFERENCE_ID, ColorUtil.getTextColor().getRGB());
        registry.put(GherkinColors.COMMENT.COLOR_PREFERENCE_ID, ColorUtil.getCucumberCommentColor().getRGB());
    }

}
