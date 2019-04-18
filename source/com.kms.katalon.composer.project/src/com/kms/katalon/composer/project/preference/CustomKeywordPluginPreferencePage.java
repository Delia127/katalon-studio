package com.kms.katalon.composer.project.preference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.keyword.ActionProviderFactory;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.keyword.BuiltinKeywords;
import com.kms.katalon.core.keyword.IActionProvider;
import com.kms.katalon.core.keyword.IPluginEventHandler;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.custom.keyword.CustomKeywordSettingPage.SettingPageComponent;
import com.kms.katalon.custom.keyword.KeywordsManifest;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class CustomKeywordPluginPreferencePage extends PreferencePage {

	private final KeywordsManifest keywordsManifest;

	private static final List<String> acceptedTypes = Arrays.asList(new String[] { "text", "secret", "generator" });

	private Map<String, Pair<SettingPageComponent, Text>> txtComponentCollection = new HashMap<>();

	private ClassLoader classLoader;

	private IActionProvider actionProvider = ActionProviderFactory.getInstance().getActionProvider();

	public CustomKeywordPluginPreferencePage(KeywordsManifest keywordsManifest) {
		this.keywordsManifest = keywordsManifest;
		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		try {
			classLoader = GroovyUtil.getClassLoaderFromParent(projectEntity, BuiltinKeywords.class.getClassLoader());
		} catch (MalformedURLException | CoreException e2) {
			LoggerSingleton.logError(e2);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 15;
		container.setLayout(gridLayout);

		for (SettingPageComponent entry : keywordsManifest.getConfiguration().getSettingPage().getComponents()) {
			String key = entry.getKey();
			String type = entry.getType();
			String label = entry.getLabel();

			if (acceptedTypes.indexOf(type) == -1) {
				continue;
			}

			switch (type) {
			case "text": {
				Label lblComponentLabel = new Label(container, SWT.NONE);
				lblComponentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
				lblComponentLabel.setText(label);

				Text txtComponentText = new Text(container, SWT.BORDER);
				txtComponentText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				txtComponentCollection.put(key, Pair.of(entry, txtComponentText));
				break;
			}
			case "secret": {
				Label lblComponentLabel = new Label(container, SWT.NONE);
				lblComponentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
				lblComponentLabel.setText(label);

				Text txtComponentSecret = new Text(container, SWT.BORDER);
				txtComponentSecret.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				txtComponentSecret.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
				txtComponentCollection.put(key, Pair.of(entry, txtComponentSecret));
				break;
			}
			}
		}

		keywordsManifest.getConfiguration().getSettingPage().getComponents().stream()
				.filter(entry -> entry.getType().equals("generator")).forEach(entry -> {
					Button btnOperationExecute = new Button(container, SWT.PUSH);
					btnOperationExecute.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
					btnOperationExecute.setText(entry.getLabel());
					btnOperationExecute.setData(entry.getImplementationClassPath());
					reigsterListenerForOperationButton(btnOperationExecute);
				});

		setInput();

		return container;
	}

	private void reigsterListenerForOperationButton(Button btnOperationExecute) {
		btnOperationExecute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (classLoader != null) {
					String implementationClassPath = (String) btnOperationExecute.getData();

					try {
						// Persist data fields first so that plug-ins can use
						// them in their implementations
						persistPluginDataFields();
						Class<?> clazz = classLoader.loadClass(implementationClassPath);
						Object pluginRuntimeInstance = clazz.newInstance();
						if (pluginRuntimeInstance instanceof IPluginEventHandler) {
							IPluginEventHandler pluginEventHandler = (IPluginEventHandler) pluginRuntimeInstance;
							pluginEventHandler.handle(actionProvider, getSettingStore());
						}
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
						LoggerSingleton.logError(e1);
					}
				}
			}
		});

	}

	private BundleSettingStore getSettingStore() {
		return new BundleSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation(),
				keywordsManifest.getConfiguration().getSettingId(), true);
	}

	private void setInput() {
		BundleSettingStore settingStore = getSettingStore();
		for (Entry<String, Pair<SettingPageComponent, Text>> componentEntry : txtComponentCollection.entrySet()) {
			try {
				String key = componentEntry.getKey();
				String storedValue = settingStore.getString(key,
						StringUtils.defaultString(componentEntry.getValue().getLeft().getDefaultValue()));
				if (StringUtils.isEmpty(storedValue)) {
					continue;
				}
				componentEntry.getValue().getRight().setText(storedValue);
			} catch (IOException e) {
				LoggerSingleton.logError(e);
			}
		}
	}

	private void persistPluginDataFields() {
		BundleSettingStore settingStore = getSettingStore();
		for (Entry<String, Pair<SettingPageComponent, Text>> componentEntry : txtComponentCollection.entrySet()) {
			try {
				settingStore.setProperty(componentEntry.getKey(), componentEntry.getValue().getRight().getText());
			} catch (IOException e) {
				LoggerSingleton.logError(e);
			}
		}
	}

	@Override
	public boolean performOk() {
		if (!isControlCreated()) {
			return true;
		}
		persistPluginDataFields();
		return true;
	}

}
