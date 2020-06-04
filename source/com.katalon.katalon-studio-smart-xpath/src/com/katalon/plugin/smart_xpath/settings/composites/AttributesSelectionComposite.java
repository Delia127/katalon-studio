package com.katalon.plugin.smart_xpath.settings.composites;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TypedListener;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.util.collections.Pair;

public class AttributesSelectionComposite extends Composite {

    private final String MSG_PROPERTY_NAME_IS_EXISTED = "Property name is existed!";

    private final String COL_LBL_DETECT_OBJECT_BY = "Detect object by?";

    private TableViewer tvProperty;

    private ToolItem tiPropertyDelete;

    private List<Pair<String, Boolean>> selectedAttributes = Collections.emptyList();

    public AttributesSelectionComposite(Composite parent, int style) {
        super(parent, style);
        createContents();
    }

    private void createContents() {
        this.setLayout(new GridLayout());
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createAttributeTableToolbar(this);
        createAttributesTable(this);
    }

    private Control createAttributeTableToolbar(Composite parent) {
        Composite compositeAttributeTableToolBar = new Composite(parent, SWT.NONE);
        compositeAttributeTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeAttributeTableToolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tb = new ToolBar(compositeAttributeTableToolBar, SWT.FLAT | SWT.RIGHT);
        tb.setForeground(ColorUtil.getToolBarForegroundColor());
        ToolItem tiPropertyAdd = new ToolItem(tb, SWT.PUSH);
        tiPropertyAdd.setText(StringConstants.ADD);
        tiPropertyAdd.setImage(ImageConstants.IMG_16_ADD);

        tiPropertyDelete = new ToolItem(tb, SWT.PUSH);
        tiPropertyDelete.setText(StringConstants.DELETE);
        tiPropertyDelete.setImage(ImageConstants.IMG_16_REMOVE);
        tiPropertyDelete.setEnabled(false);

        ToolItem tiPropertyClear = new ToolItem(tb, SWT.PUSH);
        tiPropertyClear.setText(StringConstants.CLEAR);
        tiPropertyClear.setImage(ImageConstants.IMG_16_CLEAR);

        // Register Listeners

        tiPropertyAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Pair<String, Boolean> element = Pair.of(StringConstants.EMPTY, true);
                selectedAttributes.add(element);
                tvProperty.refresh();
                tvProperty.editElement(element, 0);
            }

        });

        tiPropertyDelete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int[] selectedPropertyIndices = tvProperty.getTable().getSelectionIndices();
                if (selectedPropertyIndices.length == 0) {
                    return;
                }

                List<Pair<String, Boolean>> selectedProperties = Arrays.stream(selectedPropertyIndices)
                        .boxed()
                        .map(i -> selectedAttributes.get(i))
                        .collect(Collectors.toList());
                selectedAttributes.removeAll(selectedProperties);
                tvProperty.refresh();
                
                handleSelectionChange(event);
            }

        });

        tiPropertyClear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                selectedAttributes.clear();
                tvProperty.refresh();
                
                handleSelectionChange(event);
            }

        });

        return compositeAttributeTableToolBar;
    }

    @SuppressWarnings("unchecked")
    private Control createAttributesTable(Composite parent) {
        Composite tablePropertyComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.minimumHeight = 70;
        ldTableComposite.heightHint = 380;
        tablePropertyComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tablePropertyComposite.setLayout(tableColumnLayout);

        tvProperty = new TableViewer(tablePropertyComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvProperty.setContentProvider(ArrayContentProvider.getInstance());
        Table tProperty = tvProperty.getTable();
        tProperty.setHeaderVisible(true);
        tProperty.setLinesVisible(ControlUtils.shouldLineVisble(tProperty.getDisplay()));

        TableViewerColumn cvPropertyName = new TableViewerColumn(tvProperty, SWT.LEFT);
        TableColumn cName = cvPropertyName.getColumn();
        cName.setText(StringConstants.NAME);
        cvPropertyName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }
        });

        cvPropertyName.setEditingSupport(new EditingSupport(cvPropertyName.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                String newName = String.valueOf(value);

                if (StringUtils.isBlank(newName)) {
                    selectedAttributes.remove(element);
                    tvProperty.refresh();
                    return;
                }

                if (StringUtils.equals(((Pair<String, Boolean>) element).getLeft(), newName)) {
                    return;
                }

                boolean isExisted = selectedAttributes.stream().filter(i -> i.getLeft().equals(newName)).count() > 0;

                if (isExisted) {
                    MessageDialog.openWarning(getShell(), StringConstants.WARN, MSG_PROPERTY_NAME_IS_EXISTED);
                    tvProperty.refresh();
                    return;
                }
                ((Pair<String, Boolean>) element).setLeft(newName);
                tvProperty.update(element, null);
                
                handleSelectionChange(null);
            }

            @Override
            protected Object getValue(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tProperty);
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });

        TableViewerColumn cvPropertySelected = new TableViewerColumn(tvProperty, SWT.CENTER);
        TableColumn cSelected = cvPropertySelected.getColumn();
        cSelected.setText(COL_LBL_DETECT_OBJECT_BY);

        cvPropertySelected.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Object property = cell.getElement();
                if (!(property instanceof Pair)) {
                    return;
                }
                Boolean isSelected = ((Pair<String, Boolean>) property).getRight();
                ((TableItem) cell.getViewerRow().getItem()).setChecked(isSelected);
                tProperty.redraw();
            }
        });
        cvPropertySelected.setEditingSupport(new EditingSupport(cvPropertySelected.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                ((Pair<String, Boolean>) element).setRight((boolean) value);
                tvProperty.update(element, null);
                handleSelectionChange(null);
            }

            @Override
            protected Object getValue(Object element) {
                return ((Pair<String, Boolean>) element).getRight();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new CheckboxCellEditor();
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(80, 100));
        tableColumnLayout.setColumnData(cSelected, new ColumnWeightData(20, 100));

        tProperty.addListener(SWT.PaintItem, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (event.index == 1) {
                    Pair<String, Boolean> data = (Pair<String, Boolean>) ((TableItem) event.item).getData();
                    Image tmpImage = getCheckboxSymbol(data.getRight());
                    int tmpWidth = 0;
                    int tmpHeight = 0;
                    int tmpX = 0;
                    int tmpY = 0;

                    tmpWidth = tProperty.getColumn(event.index).getWidth();
                    tmpHeight = ((TableItem) event.item).getBounds().height;

                    tmpX = tmpImage.getBounds().width;
                    tmpX = (tmpWidth / 2 - tmpX / 2);
                    tmpY = tmpImage.getBounds().height;
                    tmpY = (tmpHeight / 2 - tmpY / 2);
                    if (tmpX <= 0) tmpX = event.x;
                    else tmpX += event.x;
                    if (tmpY <= 0) tmpY = event.y;
                    else tmpY += event.y;
                    event.gc.drawImage(tmpImage, tmpX, tmpY);
                }
            }
            
        });
        // Register Listeners

        tvProperty.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection) tvProperty.getSelection();
                tiPropertyDelete.setEnabled(selection != null && selection.getFirstElement() != null);
            }
        });

        return tablePropertyComposite;
    }

    protected Image getCheckboxSymbol(boolean isChecked) {
        return isChecked ? ImageManager.getImage(IImageKeys.CHECKBOX_CHECKED_16)
                : ImageManager.getImage(IImageKeys.CHECKBOX_UNCHECKED_16);
    }

    public void setInput(List<Pair<String, Boolean>> input) {
        selectedAttributes = input;
        tvProperty.setInput(selectedAttributes);
    }

    public List<Pair<String, Boolean>> getInput() {
        List<Pair<String, Boolean>> emptyAttributeItems = selectedAttributes.stream()
                .filter(item -> item.getLeft().isEmpty())
                .collect(Collectors.toList());
        selectedAttributes.removeAll(emptyAttributeItems);

        return selectedAttributes;
    }

    public boolean compareInput(List<Pair<String, Boolean>> selectedAttributes) {
        List<Pair<String, Boolean>> _selectedAttributes = getInput();
        return _selectedAttributes != null && _selectedAttributes.equals(selectedAttributes);
    }

    private void handleSelectionChange(TypedEvent selectionEvent) {
        dispatchSelectionEvent(selectionEvent);
    }

    private void dispatchSelectionEvent(TypedEvent selectionEvent) {
        notifyListeners(SWT.Selection, null);
        notifyListeners(SWT.DefaultSelection, null);
    }

    public void addSelectionListener(SelectionListener listener) {
        checkWidget();
        if (listener == null) {
            return;
        }
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Selection, typedListener);
        addListener(SWT.DefaultSelection, typedListener);
    }
}
