package com.kms.katalon.objectspy.components;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLElement.HTMLStatus;
import com.kms.katalon.objectspy.element.HTMLElement.MatchedStatus;
import com.kms.katalon.objectspy.element.HTMLPageElement;

public class CapturedHTMLElementsComposite extends Composite {

    private Text elementNameText;

    private Text elementTypeText;

    private TableViewer attributesTableViewer;

    private CapturedHTMLElementsTreeComposite capturedHTMLElementsTreeComposite;
    
    private HTMLElement selectedElement;

    public CapturedHTMLElementsComposite(Composite parent, int style) {
        super(parent, style);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        GridLayout gl_capturedObjectComposite = new GridLayout();
        gl_capturedObjectComposite.marginBottom = 5;
        gl_capturedObjectComposite.horizontalSpacing = 0;
        gl_capturedObjectComposite.marginWidth = 0;
        gl_capturedObjectComposite.marginHeight = 0;
        setLayout(gl_capturedObjectComposite);
        
        SashForm vSashForm = new SashForm(this, SWT.NONE);
        vSashForm.setSashWidth(5);
        vSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        capturedHTMLElementsTreeComposite = new CapturedHTMLElementsTreeComposite(vSashForm, SWT.NONE);

        Composite objectPropertiesComposite = new Composite(vSashForm, SWT.NONE);
        objectPropertiesComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
        GridLayout gl_objectPropertiesComposite = new GridLayout(1, false);
        gl_objectPropertiesComposite.marginWidth = 0;
        gl_objectPropertiesComposite.marginHeight = 0;
        gl_objectPropertiesComposite.horizontalSpacing = 0;
        objectPropertiesComposite.setLayout(gl_objectPropertiesComposite);

        Label lblObjectProperties = new Label(objectPropertiesComposite, SWT.NONE);
        lblObjectProperties.setFont(getFontBold(lblObjectProperties));
        lblObjectProperties.setText(StringConstants.DIA_LBL_OBJECT_PROPERTIES);

        Label nameLabel = new Label(objectPropertiesComposite, SWT.NONE);
        nameLabel.setText(StringConstants.DIA_LBL_NAME);

        elementNameText = new Text(objectPropertiesComposite, SWT.BORDER);
        elementNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label typeLabel = new Label(objectPropertiesComposite, SWT.NONE);
        typeLabel.setText(StringConstants.DIA_LBL_TAG);

        elementTypeText = new Text(objectPropertiesComposite, SWT.BORDER);
        elementTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite attributesTableComposite = new Composite(objectPropertiesComposite, SWT.NONE);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        attributesTableComposite.setLayout(tableColumnLayout);

        GridData attributesTableCompositeGridData = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        attributesTableCompositeGridData.heightHint = 10000;
        attributesTableCompositeGridData.widthHint = 10000;
        attributesTableComposite.setLayoutData(attributesTableCompositeGridData);

        attributesTableViewer = new CTableViewer(attributesTableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns(attributesTableViewer, tableColumnLayout);

        // make lines and header visible
        final Table table = attributesTableViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        attributesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        attributesTableViewer.setInput(Collections.emptyList());

        ColumnViewerToolTipSupport.enableFor(attributesTableViewer);
        vSashForm.setOrientation(SWT.VERTICAL);
        vSashForm.setWeights(new int[] { 4, 5 });
        
        addControlListeners();
    }

    private void addControlListeners() {
        getElementTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof TreeSelection) {
                    TreeSelection treeSelection = (TreeSelection) event.getSelection();
                    Object selectedObject = treeSelection.getFirstElement();
                    if (selectedObject instanceof HTMLElement) {
                        selectedElement = (HTMLElement) selectedObject;
                        refreshAttributesTable(selectedElement);
                    }
                }
            }
        });

        elementNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedElement != null) {
                    selectedElement.setName(elementNameText.getText());
                    refreshElementTree(null);
                }
            }
        });

        elementTypeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedElement != null) {
                    selectedElement.setType(elementTypeText.getText());
                }
            }
        });
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void createColumns(TableViewer viewer, TableColumnLayout tableColumnLayout) {
        TableViewerColumn keyColumn = new TableViewerColumn(viewer, SWT.NONE);
        keyColumn.getColumn().setWidth(30);
        keyColumn.getColumn().setText(StringConstants.DIA_COL_NAME);
        keyColumn.setLabelProvider(new TypeCheckedStyleCellLabelProvider<Entry>(0) {
            @Override
            protected Class<Entry> getElementType() {
                return Entry.class;
            }

            @Override
            protected Image getImage(Entry element) {
                return null;
            }

            @Override
            protected String getText(Entry element) {
                Entry<String, String> entry = (Entry<String, String>) element;
                if (entry.getKey() != null) {
                    return entry.getKey().toString();
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected Rectangle getTextBounds(Rectangle originalBounds) {
                Rectangle textBounds = super.getTextBounds(originalBounds);
                textBounds.x = 5;
                return textBounds;
            }
        });

        TableViewerColumn valueColumn = new TableViewerColumn(viewer, SWT.NONE);
        valueColumn.getColumn().setWidth(50);
        valueColumn.getColumn().setText(StringConstants.DIA_COL_VALUE);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public Image getImage(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    return hasValueChanged(entry) ? ImageManager.getImage(IImageKeys.WARNING_16) : null;
                }
                return null;
            }

            private boolean hasValueChanged(Entry<String, String> attribute) {
                Element matchedElement = getMatchedElement();
                String attributeKey = attribute.getKey();
                if (matchedElement == null || getMatchedStatus().getStatus() != HTMLStatus.Changed  
                        || !matchedElement.hasAttribute(attributeKey)) {
                    return false;
                }
                return ObjectUtils.notEqual(attribute.getValue(), matchedElement.getAttribute(attributeKey));
            }

            private MatchedStatus getMatchedStatus() {
                return selectedElement.getMatchedStatus();
            }

            private Element getMatchedElement() {
                return getMatchedStatus().getMatchedElement();
            }

            @Override
            public String getText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return StringConstants.EMPTY;
            }
            
            @Override
            public String getToolTipText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    return hasValueChanged(entry)
                            ? MessageFormat.format(ObjectspyMessageConstants.TABLE_ELEMENT_TIP_VALUE_CHANGED,
                                    getMatchedElement().getAttribute(entry.getKey()))
                            : null;
                }
                return null;
            }
        });

        valueColumn.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof Entry && value instanceof String) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    entry.setValue(String.valueOf(value));
                    attributesTableViewer.refresh(element);
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return StringConstants.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof Entry) {
                    return new TextCellEditor(attributesTableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof Entry) {
                    return true;
                }
                return false;
            }
        });

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 70, true));
        tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(80, 120, true));
    }

    public void refreshAttributesTable(HTMLElement selectedElement) {
        if (selectedElement != null) {
            elementNameText.setText(selectedElement.getName());
            elementTypeText.setText(selectedElement.getType());
            elementTypeText.setEditable(!(selectedElement instanceof HTMLPageElement));
            elementNameText.setEditable(true);
            attributesTableViewer.setInput(new ArrayList<Entry<String, String>>(selectedElement.getAttributes()
                    .entrySet()));
        } else {
            elementNameText.setText(StringConstants.EMPTY);
            elementTypeText.setText(StringConstants.EMPTY);
            elementNameText.setEditable(false);
            elementTypeText.setEditable(false);
            attributesTableViewer.setInput(Collections.emptyList());
        }
        attributesTableViewer.refresh();
    }

    public TreeViewer getElementTreeViewer() {
        return capturedHTMLElementsTreeComposite.getElementTreeViewer();
    }

    public void refreshElementTree(Object object) {
        capturedHTMLElementsTreeComposite.refreshElementTree(object);
    }
    
    public void refreshAttributesTable() {
        attributesTableViewer.refresh();
    }

    public Text getElementNameText() {
        return elementNameText;
    }

    public Text getElementTypeText() {
        return elementTypeText;
    }
    
    public HTMLElement getSelectedElement() {
        return selectedElement;
    }
}
