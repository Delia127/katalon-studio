package com.kms.katalon.composer.handlers;

import org.codehaus.groovy.eclipse.GroovyPlugin;
import org.codehaus.groovy.eclipse.editor.GroovyColorManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

import com.kms.katalon.composer.components.ComponentBundleActivator;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.StringConstants;

import cucumber.eclipse.editor.editors.GherkinColors;

@SuppressWarnings("restriction")
public class SwitchThemeHandler {

    @Execute
    public void switchTheme(MDirectMenuItem directMenuItem, Shell activeShell) {
        IThemeEngine engine = ComponentBundleActivator.getThemeEngine(activeShell.getDisplay());
        if (directMenuItem.getElementId().contains("light")) {
            engine.setTheme("com.kms.katalon.theme.default", true);
        } else {
            engine.setTheme("org.eclipse.e4.ui.css.theme.e4_dark", true);
        }
        
        changeGroovyEditorColor();
        changeCucumberEditorColor();

        if (MessageDialog.openQuestion(activeShell, StringConstants.INFO,
                "Katalon Studio requires restart to take fully effect. Do you want to restart now?")) {
            PlatformUI.getWorkbench().restart(true);
        }
    }
    
    private void changeGroovyEditorColor() {
        IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
        RGB rgb = PreferenceConverter.getColor(store, "java_keyword");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.gjdk.color", rgb);
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.groovykeywords.color", rgb);
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.javatypes.color", rgb);
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.javakeywords.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_string");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.strings.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_bracket");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.bracket.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_operator");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.operator.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_annotation");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.annotation.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_keyword_return");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.return.color", rgb);

        rgb = PreferenceConverter.getColor(store, "java_default");
        PreferenceConverter.setValue(getGroovyPreferenceStore(), "groovy.editor.highlight.default.color", rgb);

        GroovyColorManager colorManager = GroovyPlugin.getDefault().getTextTools().getColorManager();
        colorManager.uninitialize();
        colorManager.initialize();
    }

    private void changeCucumberEditorColor() {
        ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
        ColorRegistry registry = currentTheme.getColorRegistry();

        registry.put(GherkinColors.DEFAULT.COLOR_PREFERENCE_ID, ColorUtil.getTextColor().getRGB());
        registry.put(GherkinColors.COMMENT.COLOR_PREFERENCE_ID, ColorUtil.getCucumberCommentColor().getRGB());
    }

    private IPreferenceStore getGroovyPreferenceStore() {
        return GroovyPlugin.getDefault().getPreferenceStore();
    }
}
