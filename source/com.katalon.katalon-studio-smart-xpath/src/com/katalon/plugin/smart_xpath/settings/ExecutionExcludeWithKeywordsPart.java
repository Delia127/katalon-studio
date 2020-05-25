package com.katalon.plugin.smart_xpath.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Entity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class ExecutionExcludeWithKeywordsPart {

	private Composite tableExcludeObjectsWithKeywordsComposite;

	private ToolItem tltmAddVariable, tltmRemoveVariable;

	private TreeViewer treeViewer;

	private TableViewer tableViewer;

//	private List<KeywordMethod> excludeKeywords;
	
	private List<String> excludeKeywordNames;
	
	public ExecutionExcludeWithKeywordsPart(List<String> excludeKeywordNames) {
		this.excludeKeywordNames = excludeKeywordNames;
	}
	
	public TreeViewer getTreeTable() {
		return treeViewer;
	}
	
	private void getExcludeKeywordsFromPluginPreference() {
		try {
			Entity currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
			SelfHealingSetting preferenceStore = SelfHealingSetting.getStore(currentProject);
			excludeKeywordNames = preferenceStore.getExcludeKeywordList();
			if (excludeKeywordNames == null) {
				excludeKeywordNames = new ArrayList<String>();
			}
		} catch (ResourceException e1) {
			LoggerSingleton.logError(e1);
		}
	}

	public List<String> setUpdatedExcludeKeywordsIntoPluginPreference() {
		try {
			// Retrieve PreferenceStore on click in case user installed
			// this plug-in when no project was opened
			Entity currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
			SelfHealingSetting preferenceStore = SelfHealingSetting.getStore(currentProject);
			preferenceStore.setExcludeKeywordList(excludeKeywordNames);
			return excludeKeywordNames;
		} catch (ResourceException e1) {
			LoggerSingleton.logError(e1);
			return null;
		}
	}

	public Composite createContent(Composite parent) {
		this.getExcludeKeywordsFromPluginPreference();
		Composite excludeKeywordsComposite = new Composite(parent, SWT.NONE);
		excludeKeywordsComposite.setLayout(new GridLayout(1, false));
		excludeKeywordsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group excludeKeywordsGroup = new Group(excludeKeywordsComposite, SWT.NONE);
		excludeKeywordsGroup.setLayout(new GridLayout());
		excludeKeywordsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		excludeKeywordsGroup.setText(SmartXPathMessageConstants.LABEL_EXCLUDE_OBJECTS_USED_WITH_KEYWORDS);

		Composite compositeToolbar = new Composite(excludeKeywordsGroup, SWT.NONE);
		compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
		toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

		tltmAddVariable = new ToolItem(toolBar, SWT.NONE);
		tltmAddVariable.setText("Add");
		tltmAddVariable.setImage(ImageManager.getImage(IImageKeys.ADD_16));
		Menu addMenu = new Menu(tltmAddVariable.getParent().getShell());
		tltmAddVariable.setData(addMenu);
		tltmAddVariable.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int newDefaultKeywordIndex = 0;
				List<KeywordMethod> webUIKeywordList = KeywordController.getInstance().getBuiltInKeywords(
						SmartXPathMessageConstants.WEB_UI_BUILT_IN_KEYWORDS_SIMPLE_CLASS_NAME, true);
				KeywordMethod newDefaultKeyword = webUIKeywordList.get(newDefaultKeywordIndex);
				/// Avoid double Keyword
				for (int i = 0; i < excludeKeywordNames.size(); i++) {
					if (newDefaultKeyword.getName() == excludeKeywordNames.get(i)) {
						newDefaultKeyword = webUIKeywordList.get(++newDefaultKeywordIndex);
						i = 0;
					}
				}
				excludeKeywordNames.add(newDefaultKeyword.getName());
				tableViewer.refresh();
			}
		});

		tltmRemoveVariable = new ToolItem(toolBar, SWT.NONE);
		tltmRemoveVariable.setText("Remove");
		tltmRemoveVariable.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
		tltmRemoveVariable.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tableViewer.getTable().setRedraw(false);
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

				for (Iterator< ?>iterator = selection.iterator(); iterator.hasNext();) {
					String selectedObject = (String) iterator.next();
					tableViewer.remove(selectedObject);
					excludeKeywordNames.remove(selectedObject);
				}
				tableViewer.getTable().setRedraw(true);
			}
		});

		tableExcludeObjectsWithKeywordsComposite = new Composite(excludeKeywordsGroup, SWT.NONE);
		GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		ldTableComposite.minimumHeight = 100;
		tableExcludeObjectsWithKeywordsComposite.setLayoutData(ldTableComposite);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableExcludeObjectsWithKeywordsComposite.setLayout(tableColumnLayout);

		tableViewer = new TableViewer(tableExcludeObjectsWithKeywordsComposite,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.NO_SCROLL | SWT.V_SCROLL);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(excludeKeywordNames);

		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tvcName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tName = tvcName.getColumn();
		tName.setText(SmartXPathMessageConstants.COLUMN_KEYWORD);
		tvcName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		tvcName.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element != null && element instanceof String && value != null && value instanceof String) {
					String property = (String) element;
					if (!value.equals(property)) {
						KeywordMethod newProperty = KeywordController.getInstance().getBuiltInKeywordByName(
								SmartXPathMessageConstants.WEB_UI_BUILT_IN_KEYWORDS_CLASS_NAME, (String) value);
						int changedKeywordIndex = excludeKeywordNames.indexOf(property);
						if (changedKeywordIndex >= 0) {
							excludeKeywordNames.set(changedKeywordIndex, newProperty.getName());
						}
						tableViewer.refresh();
					}
				}
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				final StringComboBoxCellEditor editor = new StringComboBoxCellEditor(table,
						getWebUIKeywordsStringList());
				CCombo combo = (CCombo) editor.getControl();
				combo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						String text = combo.getText();
						setValue(element, text);
					}
				});
				combo.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
					}
				});
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected Object getValue(Object element) {
				String keyword = (String) element;
				return keyword;
			}
		});
		tvcName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String keyword = (String) element;
				return keyword;
			}
		});
		tableColumnLayout.setColumnData(tName, new ColumnWeightData(80, 100));

		return excludeKeywordsComposite;
	}

	private String[] getWebUIKeywordsStringList() {
		List<KeywordMethod> webUIKeywordList = KeywordController.getInstance()
				.getBuiltInKeywords(SmartXPathMessageConstants.WEB_UI_BUILT_IN_KEYWORDS_SIMPLE_CLASS_NAME, true);
		List<String> webUIKeywordStringList = new ArrayList<>();
		for (KeywordMethod keyword : webUIKeywordList) {
			if (excludeKeywordNames.contains(keyword)) {
				continue;
			}
			webUIKeywordStringList.add(keyword.getName());
		}
		return webUIKeywordStringList.toArray(new String[0]);
	}
}
