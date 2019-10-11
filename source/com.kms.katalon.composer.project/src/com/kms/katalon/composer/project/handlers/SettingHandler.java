package com.kms.katalon.composer.project.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import com.katalon.platform.api.Extension;
import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.extension.PluginPreferencePage;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.exception.MissingProjectSettingPageException;
import com.kms.katalon.composer.project.preference.CustomKeywordPluginPreferenceNodeDescription;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.KeywordsManifest;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.preferences.PreferenceNodeDescription;
import com.kms.katalon.preferences.internal.PreferenceNodeDescriptionImpl;
import com.kms.katalon.preferences.internal.PreferencesRegistry;

public class SettingHandler {

    private static final String PARENT_PLUGIN_PREFERENCE_PAGE_ID = "com.kms.katalon.composer.project.preference.pluginPreferencePage";

    @Inject
    private IEclipseContext eclipseContext;

    private static SettingHandler instance;
    
    @PostConstruct
    public void onPostConstruct() {
        instance = this;
    }

    @CanExecute
    public boolean canExecute() {
        return (ProjectController.getInstance().getCurrentProject() != null
                && !LauncherManager.getInstance().isAnyLauncherRunning());
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, PreferencesRegistry preferencesRegistry) {
        openSettingsDialogToPage(shell, preferencesRegistry, null);
    }

    @Inject
    @Optional
    public void openSettingsPage(@UIEventTopic(EventConstants.PROJECT_SETTINGS_PAGE) Object eventData) {
        if (!canExecute()) {
            return;
        }

        PreferencesRegistry preferencesRegistry = ContextInjectionFactory.make(PreferencesRegistry.class,
                eclipseContext);

        String pageId = (String) eventData;

        openSettingsDialogToPage(Display.getCurrent().getActiveShell(), preferencesRegistry, pageId);
    }

    private List<PreferenceNodeDescription> getCustomKeywordPluginPreferences() {
        return CustomKeywordPluginFactory.getInstance()
                .getPlugins()
                .stream()
                .filter(f -> f.getKeywordsManifest() != null && f.getKeywordsManifest().getConfiguration() != null
                        && f.getKeywordsManifest().getConfiguration().getSettingPage() != null
                        && StringUtils.isNotEmpty(f.getKeywordsManifest().getConfiguration().getSettingId()))
                .map(plugin -> {
                    KeywordsManifest keywordsManifest = plugin.getKeywordsManifest();
                    return new CustomKeywordPluginPreferenceNodeDescription(plugin.getId(), plugin.getId(),
                            StringUtils.defaultIfEmpty(keywordsManifest.getConfiguration().getSettingPage().getName(),
                                    plugin.getId()),
                            PARENT_PLUGIN_PREFERENCE_PAGE_ID, keywordsManifest);
                })
                .collect(Collectors.toList());
    }

    private List<PreferenceNodeDescription> getPluginPreferences() {
        Collection<Extension> extensions = ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(PluginPreferencePage.EXTENSION_POINT_ID);
        return extensions.stream()
                .map(e -> getPreferenceNodeDescription(e))
                .filter(node -> node != null)
                .collect(Collectors.toList());
    }

    private PreferenceNodeDescription getPreferenceNodeDescription(Extension e) {
        if (e.getImplementationClass() instanceof PluginPreferencePage) {
            PluginPreferencePage pluginPreferencePage = (PluginPreferencePage) e.getImplementationClass();
            String bundleId = e.getPluginId();
            String nodeId = pluginPreferencePage.getPageId();
            String nodeName = pluginPreferencePage.getName();
            String parentNodeId = PARENT_PLUGIN_PREFERENCE_PAGE_ID;
            return new PreferenceNodeDescriptionImpl(bundleId, nodeId, nodeName, parentNodeId,
                    pluginPreferencePage.getPreferencePageClass().getName());
        }
        return null;
    }

    private void openSettingsDialogToPage(Shell shell, PreferencesRegistry preferencesRegistry, String pageId) {
        List<PreferenceNodeDescription> allNodeDescriptions = new ArrayList<>();
        allNodeDescriptions.addAll(getPluginPreferences());
        allNodeDescriptions.addAll(getCustomKeywordPluginPreferences());

        Map<String, List<PreferenceNodeDescription>> pluginPreferences = new HashMap<>();
        pluginPreferences.put(PARENT_PLUGIN_PREFERENCE_PAGE_ID, allNodeDescriptions);
        PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PROJECT_XP,
                pluginPreferences);

        hideQTestIntegrationPageIfQTestPluginNotInstalled(pm);

        PreferenceDialog dialog = new PreferenceDialog(shell, pm) {

            private Composite helpComposite;

            @Override
            protected TreeViewer createTreeViewer(Composite parent) {
                TreeViewer treeViewer = super.createTreeViewer(parent);
                treeViewer.setLabelProvider(new PreferenceLabelProvider());
                return treeViewer;
            }

            @Override
            protected void addListeners(TreeViewer viewer) {
                super.addListeners(viewer);
                registerPageChangeListener();
            }

            private void registerPageChangeListener() {
                EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.SETTINGS_PAGE_CHANGE,
                        new EventServiceAdapter() {

                            @Override
                            public void handleEvent(Event event) {
                                String pageId = (String) getObject(event);
                                getTreeViewer().setSelection(new StructuredSelection(findNodeMatching(pageId)));
                            }
                        });
            }

            @Override
            protected Point getInitialSize() {
                return new Point(800, 600);
            }

            @Override
            protected boolean showPage(IPreferenceNode node) {
                boolean success = super.showPage(node);
                if (success) {
                    IPreferencePage shownPage = getCurrentPage();
                    showHelpButtonForPage(shownPage);
                }
                return success;
            }

            private void showHelpButtonForPage(IPreferencePage page) {
                Control[] helpCompositeChildren = helpComposite.getChildren();
                if (helpCompositeChildren.length > 0) {
                    Composite oldHelpButton = (Composite) helpCompositeChildren[0];
                    oldHelpButton.dispose(); // dispose old help button
                }

                if (page instanceof PreferencePageWithHelp) {
                    PreferencePageWithHelp prefPageWithHelp = (PreferencePageWithHelp) page;
                    if (prefPageWithHelp.hasDocumentation()) {
                        String documentationUrl = prefPageWithHelp.getDocumentationUrl();
                        createAndShowHelpButton(helpComposite, documentationUrl);
                    } else {
                        helpComposite.setVisible(false);
                    }
                } else {
                    helpComposite.setVisible(false);
                }

            }

            private void createAndShowHelpButton(Composite helpComposite, String documentationUrl) {
                new HelpCompositeForDialog(helpComposite, documentationUrl) {

                    @Override
                    protected GridData createGridData() {
                        return new GridData(SWT.RIGHT, SWT.CENTER, true, false);
                    }

                    @Override
                    protected GridLayout createLayout() {
                        GridLayout layout = new GridLayout();
                        layout.marginHeight = 0;
                        layout.marginBottom = 0;
                        layout.marginWidth = 0;
                        return layout;
                    }
                };

                helpComposite.setVisible(true);
                helpComposite.getParent().layout(true, true);
            }

            @Override
            protected void createButtonsForButtonBar(Composite parent) {
                ((GridLayout) parent.getLayout()).numColumns++;
                helpComposite = new Composite(parent, SWT.NONE);
                GridLayout glHelp = new GridLayout();
                glHelp.marginWidth = 0;
                glHelp.marginHeight = 0;
                helpComposite.setLayout(glHelp);
                helpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

                super.createButtonsForButtonBar(parent);
            }
        };
        String initialSelectedPage = pageId != null ? pageId : StringConstants.PROJECT_INFORMATION_SETTINGS_PAGE_ID;
        dialog.setSelectedNode(initialSelectedPage);
        dialog.create();
        dialog.getTreeViewer().setComparator(new DefinedOrderedPageComparator());
        dialog.getShell().setText(StringConstants.HAND_PROJ_SETTING);
        dialog.open();
    }

    @Inject
    @Optional
    public void openSettings(@UIEventTopic(EventConstants.PROJECT_SETTINGS) Object eventData) {
        if (!canExecute()) {
            return;
        }

        PreferencesRegistry preferencesRegistry = ContextInjectionFactory.make(PreferencesRegistry.class,
                eclipseContext);

        execute(Display.getCurrent().getActiveShell(), preferencesRegistry);
    }

    private void hideQTestIntegrationPageIfQTestPluginNotInstalled(PreferenceManager pm) {
        Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(IdConstants.QTEST_PLUGIN_ID);
        if (plugin != null) {
            return;
        }
        
        try {
            IPreferenceNode integrationSettings = null;
            for (IPreferenceNode node : pm.getRootSubNodes()) {
                if (StringConstants.PROJECT_INTEGRATION_SETTINGS_PAGE_ID.equals(node.getId())) {
                    integrationSettings = node;
                    break;
                }
            }
            
            if (integrationSettings == null) {
                return;
            }
            
            IPreferenceNode qTestNode = integrationSettings.remove(StringConstants.PROJECT_QTEST_INTEGRATION_SETTINGS_PAGE_ID);
            if (qTestNode == null) {
                throw new MissingProjectSettingPageException(StringConstants.PROJECT_QTEST_INTEGRATION_SETTINGS_PAGE_ID);
            }
        } catch (MissingProjectSettingPageException e) {
            LoggerSingleton.logError(e);
        }
    }

    public static SettingHandler getInstance() {
        return instance;
    }

    private final class PreferenceLabelProvider extends TypeCheckedStyleCellLabelProvider<PreferenceNode> {
        private PreferenceLabelProvider() {
            super(0);
        }

        @Override
        protected Class<PreferenceNode> getElementType() {
            return PreferenceNode.class;
        }

        @Override
        protected Image getImage(PreferenceNode element) {
            return null;
        }

        @Override
        protected String getText(PreferenceNode element) {
            return element.getLabelText();
        }
    }
}
