package com.katalon.plugin.smart_xpath.settings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.util.collections.Pair;

public class SelfHealingExecutionSettingPage extends PreferencePageWithHelp {

	private static final String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD = SmartXPathMessageConstants.LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

	private static final String GRP_LBL_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION = SmartXPathMessageConstants.GRP_LBL_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION;

	private static final String BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER = SmartXPathMessageConstants.BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	private static final String BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER = SmartXPathMessageConstants.BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	private static final String COLUMN_SELECTION_METHOD = SmartXPathMessageConstants.COLUMN_SELECTION_METHOD;
	
	private static final String COLUMN_DETECT_OBJECT_BY = SmartXPathMessageConstants.COLUMN_DETECT_OBJECT_BY;

	private ExecutionDefaultSettingStore defaultSettingStore;

	private Composite container;

	private Group prioritizeGroup;

	private Button checkboxEnableSelfHealing;

	private Composite tableSelectionPriorityOrderComposite;

	private TableViewer tvPrioritizeSelectionMethods;

	private Table tPrioritizeSelectionMethods;

	private TableViewerColumn cvPrioritizeSelectionMethodColumn;

	private TableColumn cPrioritizeSelectionMethodColumn;

	private TableViewerColumn cvMethodsSelected;

	private TableColumn cMethodsSelected;

	private Composite tableExcludeObjectsWithKeywordsComposite;
	
	private ExecutionExcludeWithKeywordsPart excludeWithKeywordsPart;

	private List<Pair<String, Boolean>> methodsPritorityOrder = new ArrayList<Pair<String, Boolean>>();
	
	private List<Pair<String, Boolean>> excludeKeywords = new ArrayList<Pair<String,Boolean>>();

	private List<Pair<String, Boolean>> setDefaultMethodsPriorityOrder() {
		methodsPritorityOrder.add(new Pair<String, Boolean>(SmartXPathMessageConstants.XPATH_METHOD, true));
		methodsPritorityOrder.add(new Pair<String, Boolean>(SmartXPathMessageConstants.ATTRIBUTE_METHOD, true));
		methodsPritorityOrder.add(new Pair<String, Boolean>(SmartXPathMessageConstants.CSS_METHOD, true));
		methodsPritorityOrder.add(new Pair<String, Boolean>(SmartXPathMessageConstants.IMAGE_METHOD, true));
		return methodsPritorityOrder;
	}

	private void setInputForMethodsPriorityOrder(List<Pair<String, Boolean>> input) {
		tvPrioritizeSelectionMethods.setInput(input);
	}

	public SelfHealingExecutionSettingPage() {
		defaultSettingStore = ExecutionDefaultSettingStore.getStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 10;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);

		createExecutionSettings(container);
		return container;
	}

	private void createExecutionSettings(Composite parent) {
		Composite selectionMethodComposite = new Composite(parent, SWT.NONE);
		selectionMethodComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout glSelectionMethodComp = new GridLayout(1, false);
		glSelectionMethodComp.marginHeight = 0;
		glSelectionMethodComp.marginWidth = 0;
		selectionMethodComposite.setLayout(glSelectionMethodComp);

		checkboxEnableSelfHealing = new Button(selectionMethodComposite, SWT.CHECK);
		checkboxEnableSelfHealing.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		checkboxEnableSelfHealing.setText(LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD);

		prioritizeGroup = new Group(selectionMethodComposite, SWT.NONE);
		prioritizeGroup.setLayout(new GridLayout());
		prioritizeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		prioritizeGroup.setText(GRP_LBL_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION);

		setDefaultMethodsPriorityOrder();
		createPrioritizeOrderToolbar(prioritizeGroup);
		createPrioritizeTable(prioritizeGroup);

		setInputForMethodsPriorityOrder(methodsPritorityOrder);
		
		excludeWithKeywordsPart = new ExecutionExcludeWithKeywordsPart();
		excludeWithKeywordsPart.createContent(selectionMethodComposite);
	}

	private void createPrioritizeOrderToolbar(Composite parent) {
		Composite compositeToolbar = new Composite(parent, SWT.NONE);
		compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
		toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

		ToolItem tltmUp = new ToolItem(toolBar, SWT.NONE);
		tltmUp.setText(BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER);
		tltmUp.setImage(ImageManager.getImage(IImageKeys.MOVE_UP_16));
		tltmUp.addListener(SWT.Selection , new Listener() {
			@Override
			public void handleEvent(Event event) {
				int selectedIndex = tPrioritizeSelectionMethods.getSelectionIndex();
				if (selectedIndex > 0 && selectedIndex < methodsPritorityOrder.size()) {
					Pair<String, Boolean> method = methodsPritorityOrder.get(selectedIndex);
					methodsPritorityOrder.remove(selectedIndex);
					methodsPritorityOrder.add(selectedIndex - 1, method);
					tvPrioritizeSelectionMethods.setSelection(new StructuredSelection(method));
					tvPrioritizeSelectionMethods.refresh();
				}
			}
		});

		ToolItem tltmDown = new ToolItem(toolBar, SWT.NONE);
		tltmDown.setText(BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER);
		tltmDown.setImage(ImageManager.getImage(IImageKeys.MOVE_DOWN_16));
		tltmDown.addListener(SWT.Selection , new Listener() {
			@Override
			public void handleEvent(Event event) {
				int selectedIndex = tPrioritizeSelectionMethods.getSelectionIndex();
				if (selectedIndex >= 0 && selectedIndex < (methodsPritorityOrder.size() - 1)) {
					Pair<String, Boolean> method = methodsPritorityOrder.get(selectedIndex);
					methodsPritorityOrder.remove(selectedIndex);
					methodsPritorityOrder.add(selectedIndex + 1, method);
					tvPrioritizeSelectionMethods.setSelection(new StructuredSelection(method));
					tvPrioritizeSelectionMethods.refresh();
				}
			}
		});
	}

	private String getCheckboxSymbol(boolean isChecked) {
		// Unicode symbols
		// Checked box: \u2611
		// Unchecked box: \u2610
		return isChecked ? "\u2611" : "\u2610";
	}

	@SuppressWarnings("unchecked")
	private void createPrioritizeTable(Composite parent) {
		tableSelectionPriorityOrderComposite = new Composite(parent, SWT.NONE);
		GridData ldTableComposite = new GridData(SWT.FILL, SWT.WRAP, true, true);
		ldTableComposite.minimumHeight = 70;
		tableSelectionPriorityOrderComposite.setLayoutData(ldTableComposite);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableSelectionPriorityOrderComposite.setLayout(tableColumnLayout);
		tvPrioritizeSelectionMethods = new TableViewer(tableSelectionPriorityOrderComposite,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tvPrioritizeSelectionMethods.setContentProvider(ArrayContentProvider.getInstance());

		tvPrioritizeSelectionMethods.addDragSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
				new DragSourceAdapter() {

					@Override
					public void dragSetData(DragSourceEvent event) {
						StructuredSelection selection = (StructuredSelection) tvPrioritizeSelectionMethods
								.getSelection();
						Pair<String, Boolean> method = ((Pair<String, Boolean>) selection.getFirstElement());
						event.data = String.valueOf(methodsPritorityOrder.indexOf(method));
					}
				});
		tvPrioritizeSelectionMethods.addDropSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
				new DropTargetAdapter() {

					@Override
					public void drop(DropTargetEvent event) {
						Pair<String, Boolean> item = (Pair<String, Boolean>) ((TableItem) event.item).getData();
						int newIndex = methodsPritorityOrder.indexOf(item);
						String index = (String) event.data;
						if (index != null && newIndex >= 0) {
							int indexVal = Integer.parseInt(index);
							Pair<String, Boolean> method = methodsPritorityOrder.get(indexVal);
							methodsPritorityOrder.remove(indexVal);
							methodsPritorityOrder.add(newIndex, method);
							tvPrioritizeSelectionMethods.setSelection(new StructuredSelection(method));
							tvPrioritizeSelectionMethods.refresh();
						}
					}
				});
		tPrioritizeSelectionMethods = tvPrioritizeSelectionMethods.getTable();
		tPrioritizeSelectionMethods.setHeaderVisible(true);
		tPrioritizeSelectionMethods
				.setLinesVisible(ControlUtils.shouldLineVisble(tPrioritizeSelectionMethods.getDisplay()));

		cvPrioritizeSelectionMethodColumn = new TableViewerColumn(tvPrioritizeSelectionMethods, SWT.LEFT);
		cPrioritizeSelectionMethodColumn = cvPrioritizeSelectionMethodColumn.getColumn();
		cPrioritizeSelectionMethodColumn.setText(COLUMN_SELECTION_METHOD);
		cvPrioritizeSelectionMethodColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Pair<String, Boolean>) element).getLeft();
			}
		});

		cvMethodsSelected = new TableViewerColumn(tvPrioritizeSelectionMethods, SWT.CENTER);
		cMethodsSelected = cvMethodsSelected.getColumn();
		cMethodsSelected.setText(COLUMN_DETECT_OBJECT_BY);

		cvMethodsSelected.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Object property = cell.getElement();
				if (!(property instanceof Pair)) {
					return;
				}
				Boolean isSelected = ((Pair<String, Boolean>) property).getRight();
				FontDescriptor fontDescriptor = FontDescriptor.createFrom(cell.getFont());
				Font font = fontDescriptor.setStyle(SWT.NORMAL).setHeight(12)
						.createFont(tPrioritizeSelectionMethods.getDisplay());
				cell.setFont(font);
				cell.setText(getCheckboxSymbol(isSelected));
			}
		});
		
		cvMethodsSelected.setEditingSupport(new EditingSupport(cvMethodsSelected.getViewer()) {

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new CheckboxCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected Object getValue(Object element) {
				return ((Pair<String, Boolean>) element).getRight();
			}

			@Override
			protected void setValue(Object element, Object value) {
				((Pair<String, Boolean>) element).setRight((boolean) value);
				tvPrioritizeSelectionMethods.update(element, null);
			}
		});

		tableColumnLayout.setColumnData(cPrioritizeSelectionMethodColumn, new ColumnWeightData(80, 100));
		tableColumnLayout.setColumnData(cMethodsSelected, new ColumnWeightData(20, 100));
	}
}
