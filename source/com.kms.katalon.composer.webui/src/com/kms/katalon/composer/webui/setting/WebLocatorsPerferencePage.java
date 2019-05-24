package com.kms.katalon.composer.webui.setting;

import java.io.IOException;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class WebLocatorsPerferencePage extends PreferencePageWithHelp {

    private static final String MSG_PROPERTY_NAME_IS_EXISTED = ComposerWebuiMessageConstants.MSG_PROPERTY_NAME_IS_EXISTED;

    private static final String GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT = ComposerWebuiMessageConstants.GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT;
    
    private static final String GRP_LBL_DEFAULT_XPATHS_USAGE_TIPS = ComposerWebuiMessageConstants.GRP_LBL_DEFAULT_XPATHS_USAGE_TIPS;
    
    private static final String COL_LBL_DETECT_OBJECT_BY = ComposerWebuiMessageConstants.COL_LBL_DETECT_OBJECT_BY;
    
    private static final String LBL_XPATH_SELECTION_METHOD = ComposerWebuiMessageConstants.LBL_XPATH_SELECTION_METHOD;
    
    private static final String LBL_ATTRIBUTE_SELECTION_METHOD = ComposerWebuiMessageConstants.LBL_ATTRIBUTE_SELECTION_METHOD;

    private WebUiExecutionSettingStore store;
    
    private Group locatorGroup;

    private Composite container;
    
    private Composite tablePropertyComposite, tableXpathComposite;

    ToolItem tiPropertyAdd, tiPropertyDelete, tiPropertyClear;

    private Button radioXpath, radioAttribute;

	private Button resetDefault;    
    
    private Table tProperty, tXpath;        
    
    private TableViewer tvProperty, tvXpath;

    private TableViewerColumn cvPropertyName, cvPropertySelected, cvXpathName;
    
    private Composite compositeAttributeTableToolBar;
    
    private Composite compositeXpathTableToolBar;
    
    private TableColumn cName, cSelected;

    private List<Pair<String, Boolean>> defaultSelectingCapturedObjectProperties;
    
    private List<Pair<String, Boolean>> defaultSelectingCapturedObjectXpaths;
       
    
    private SelectorMethod defaultSelectingCapturedObjecSelectionMethods;

    public WebLocatorsPerferencePage() {
        store = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
        defaultSelectingCapturedObjectProperties = Collections.emptyList();
        defaultSelectingCapturedObjectXpaths = Collections.emptyList();
        defaultSelectingCapturedObjecSelectionMethods = SelectorMethod.XPATH;
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        createTestObjectLocatorSettings(container);

        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        registerListeners();

        return container;
    }
    
    private void createSelectionMethodComposite(Composite parent) {
		Composite selectionMethodComposite = new Composite(parent, SWT.NONE);
		selectionMethodComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout glSelectionMethodComp = new GridLayout(3, false);
		glSelectionMethodComp.marginHeight = 0;
		glSelectionMethodComp.marginWidth = 0;
		selectionMethodComposite.setLayout(glSelectionMethodComp);

		Label lblSelectionMethod = new Label(selectionMethodComposite, SWT.NONE);
		
		lblSelectionMethod.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));


		Composite radioSelectionComposite = new Composite(selectionMethodComposite, SWT.NONE);
		radioSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout glRadioSelection = new GridLayout(3, false);
		glRadioSelection.marginHeight = 0;
		glRadioSelection.marginWidth = 0;
		glRadioSelection.marginLeft = 10;
		radioSelectionComposite.setLayout(glRadioSelection);

		radioXpath = new Button(radioSelectionComposite, SWT.RADIO);
		radioXpath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		radioXpath.setText(LBL_XPATH_SELECTION_METHOD);

		radioAttribute = new Button(radioSelectionComposite, SWT.RADIO);
		radioAttribute.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		radioAttribute.setText(LBL_ATTRIBUTE_SELECTION_METHOD);
	}
    
    

    private void createTestObjectLocatorSettings(Composite container) {
        locatorGroup = new Group(container, SWT.NONE);        
        locatorGroup.setLayout(new GridLayout());
        locatorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Composite locatorContainer = new Composite(locatorGroup, SWT.NONE);
        locatorContainer.setLayout(new GridLayout());
        locatorContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createSelectionMethodComposite(locatorContainer);
        
        createAttributeTableToolbar(locatorContainer);      
        
        createXpathTableToolbar(locatorContainer);
                
        createPropertyTable(locatorContainer);        
        
        createXpathTable(locatorContainer);


    }
    
    private void createAttributeTableToolbar(Composite parent) {
		compositeAttributeTableToolBar = new Composite(parent, SWT.NONE);
		compositeAttributeTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeAttributeTableToolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tb = new ToolBar(compositeAttributeTableToolBar, SWT.FLAT | SWT.RIGHT);
        tb.setForeground(ColorUtil.getToolBarForegroundColor());
        tiPropertyAdd = new ToolItem(tb, SWT.PUSH);
        tiPropertyAdd.setText(StringConstants.ADD);
        tiPropertyAdd.setImage(ImageConstants.IMG_16_ADD);

        tiPropertyDelete = new ToolItem(tb, SWT.PUSH);
        tiPropertyDelete.setText(StringConstants.DELETE);
        tiPropertyDelete.setImage(ImageConstants.IMG_16_DELETE);
        tiPropertyDelete.setEnabled(false);

        tiPropertyClear = new ToolItem(tb, SWT.PUSH);
        tiPropertyClear.setText(StringConstants.CLEAR);
        tiPropertyClear.setImage(ImageConstants.IMG_16_CLEAR);

	}
    
    private void createXpathTableToolbar(Composite parent) {
		compositeXpathTableToolBar = new Composite(parent, SWT.NONE);
		compositeXpathTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeXpathTableToolBar.setLayout(new GridLayout(1, false));

		resetDefault = new Button(compositeXpathTableToolBar, SWT.WRAP);
		resetDefault.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		resetDefault.setText(StringConstants.RESET_DEFAULT);    	
	}
    
   
    @SuppressWarnings("unchecked")
    private void createPropertyTable(Composite parent) {

        tablePropertyComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.minimumHeight = 70;
        ldTableComposite.heightHint = 380;
        tablePropertyComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tablePropertyComposite.setLayout(tableColumnLayout);       

        tvProperty = new TableViewer(tablePropertyComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvProperty.setContentProvider(ArrayContentProvider.getInstance());
        tProperty = tvProperty.getTable();
        tProperty.setHeaderVisible(true);
        tProperty.setLinesVisible(true);

        cvPropertyName = new TableViewerColumn(tvProperty, SWT.LEFT);
        cName = cvPropertyName.getColumn();
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
                    defaultSelectingCapturedObjectProperties.remove(element);
                    tvProperty.refresh();
                    return;
                }

                if (StringUtils.equals(((Pair<String, Boolean>) element).getLeft(), newName)) {
                    return;
                }

                boolean isExisted = defaultSelectingCapturedObjectProperties.stream()
                        .filter(i -> i.getLeft().equals(newName))
                        .count() > 0;

                if (isExisted) {
                    MessageDialog.openWarning(getShell(), StringConstants.WARN, MSG_PROPERTY_NAME_IS_EXISTED);
                    tvProperty.refresh();
                    return;
                }
                ((Pair<String, Boolean>) element).setLeft(newName);
                tvProperty.update(element, null);
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

        cvPropertySelected = new TableViewerColumn(tvProperty, SWT.CENTER);
        cSelected = cvPropertySelected.getColumn();
        cSelected.setText(COL_LBL_DETECT_OBJECT_BY);

        cvPropertySelected.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Object property = cell.getElement();
                if (!(property instanceof Pair)) {
                    return;
                }
                Boolean isSelected = ((Pair<String, Boolean>) property).getRight();
                FontDescriptor fontDescriptor = FontDescriptor.createFrom(cell.getFont());
                Font font = fontDescriptor.setStyle(SWT.NORMAL).setHeight(13).createFont(tProperty.getDisplay());
                cell.setFont(font);
                cell.setText(getCheckboxSymbol(isSelected));
            }
        });
        cvPropertySelected.setEditingSupport(new EditingSupport(cvPropertySelected.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                ((Pair<String, Boolean>) element).setRight((boolean) value);
                tvProperty.update(element, null);
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

    }
    
    @SuppressWarnings("unchecked")
    private void createXpathTable(Composite parent) {

    	tableXpathComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.minimumHeight = 70;
        ldTableComposite.heightHint = 380;
        tableXpathComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableXpathComposite.setLayout(tableColumnLayout);       
    
        tvXpath = new TableViewer(tableXpathComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvXpath.setContentProvider(ArrayContentProvider.getInstance());
		
		tvXpath.addDragSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
		        new DragSourceAdapter() {

		            @Override
		            public void dragSetData(DragSourceEvent event) {
		                StructuredSelection selection = (StructuredSelection) tvXpath.getSelection();
		                Pair<String,Boolean> xpath = ((Pair<String,Boolean>) selection.getFirstElement());
		                event.data = String.valueOf(defaultSelectingCapturedObjectXpaths.indexOf(xpath));
		            }
		        });
		tvXpath.addDropSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
		        new DropTargetAdapter(){        	
		    		
		            @Override
		            public void drop(DropTargetEvent event) {
		                Pair<String,Boolean> item = (Pair<String,Boolean>) ((TableItem) event.item).getData();
		                int newIndex = defaultSelectingCapturedObjectXpaths.indexOf(item);
		                String index = (String) event.data;
		                if (index != null && newIndex >= 0) {
		                    int indexVal = Integer.parseInt(index);
		                    Pair<String, Boolean> xpath = defaultSelectingCapturedObjectXpaths.get(indexVal);
		                    defaultSelectingCapturedObjectXpaths.remove(indexVal);
		                    defaultSelectingCapturedObjectXpaths.add(newIndex, xpath);
		                    tvXpath.setSelection(new StructuredSelection(xpath));
		                    tvXpath.refresh();
		                }
		            }
		    	});
        
        tXpath = tvXpath.getTable();
        tXpath.setHeaderVisible(true);
        tXpath.setLinesVisible(true);
        
        cvXpathName = new TableViewerColumn(tvXpath, SWT.LEFT);
        cName = cvXpathName.getColumn();
        cName.setText(StringConstants.NAME);
        cvXpathName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }
        });

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(80, 100));

    }
       
    
    private String getCheckboxSymbol(boolean isChecked) {
        // Unicode symbols
        // Checked box: \u2611
        // Unchecked box: \u2610
        return isChecked ? "\u2611" : "\u2610";
    }

    protected void registerListeners() {

    	
        tiPropertyAdd.addSelectionListener(new SelectionAdapter() {        	

            @Override
            public void widgetSelected(SelectionEvent e) {
                Pair<String, Boolean> element = Pair.of(StringConstants.EMPTY, false);
                defaultSelectingCapturedObjectProperties.add(element);
                tvProperty.refresh();
                tvProperty.editElement(element, 0);
            }

        });
        tiPropertyDelete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] selectedPropertyIndices = tProperty.getSelectionIndices();
                if (selectedPropertyIndices.length == 0) {
                    return;
                }

                List<Pair<String, Boolean>> selectedProperties = Arrays.stream(selectedPropertyIndices)
                        .boxed()
                        .map(i -> defaultSelectingCapturedObjectProperties.get(i))
                        .collect(Collectors.toList());
                defaultSelectingCapturedObjectProperties.removeAll(selectedProperties);
                tvProperty.refresh();
            }

        });

        tiPropertyClear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultSelectingCapturedObjectProperties.clear();
                tvProperty.refresh();
            }

        });
        
        

        tvProperty.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection) tvProperty.getSelection();
                tiPropertyDelete.setEnabled(selection != null && selection.getFirstElement() != null);
            }
        });

        
        radioAttribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				defaultSelectingCapturedObjecSelectionMethods = SelectorMethod.BASIC;
				locatorGroup.setText(GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT);
				showComposite(compositeAttributeTableToolBar, true);
				showComposite(compositeXpathTableToolBar, false);
				showComposite(tablePropertyComposite, true);
				showComposite(tableXpathComposite, false);
				resetDefault.setVisible(false);
			}
		});
		
		radioXpath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				defaultSelectingCapturedObjecSelectionMethods = SelectorMethod.XPATH;
				locatorGroup.setText(GRP_LBL_DEFAULT_XPATHS_USAGE_TIPS);
				showComposite(compositeAttributeTableToolBar, false);
				showComposite(compositeXpathTableToolBar, true);
				showComposite(tablePropertyComposite, false);
				showComposite(tableXpathComposite, true);
				resetDefault.setVisible(true);
			}
		});
		
		resetDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					setInputForCapturedObjectXpathSetting(store.getDefaultCapturedObjectXpathLocators());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
    }

    private void initialize() throws IOException {
    	// Set guidance text
    	if(store.getCapturedTestObjectSelectorMethod() != null){
    		if(store.getCapturedTestObjectSelectorMethod() == SelectorMethod.BASIC){
        		locatorGroup.setText(GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT);
        		resetDefault.setVisible(false);
    		}else if(store.getCapturedTestObjectSelectorMethod() == SelectorMethod.XPATH){
    			locatorGroup.setText(GRP_LBL_DEFAULT_XPATHS_USAGE_TIPS);
    			resetDefault.setVisible(true);
    		}
    	}
    	    	
        setInputForCapturedObjectPropertySetting(store.getCapturedTestObjectAttributeLocators());
        setSelectionForCapturedObjectSelectionSetting(store.getCapturedTestObjectSelectorMethod());
        setInputForCapturedObjectXpathSetting(store.getCapturedTestObjectXpathLocators());
        
        showComposite(tablePropertyComposite,
        		store.getCapturedTestObjectSelectorMethod() != null && 
        				store.getCapturedTestObjectSelectorMethod() == SelectorMethod.BASIC
        		);
        

        showComposite(tableXpathComposite, 
        		store.getCapturedTestObjectSelectorMethod() != null && 
        				store.getCapturedTestObjectSelectorMethod() == SelectorMethod.XPATH
        		);
        
        showComposite(compositeAttributeTableToolBar, store.getCapturedTestObjectSelectorMethod() != null && 
				store.getCapturedTestObjectSelectorMethod() == SelectorMethod.BASIC);
        
        showComposite(compositeXpathTableToolBar, store.getCapturedTestObjectSelectorMethod() != null && 
				store.getCapturedTestObjectSelectorMethod() == SelectorMethod.XPATH);
        
        radioXpath.setSelection(store.getCapturedTestObjectSelectorMethod() == SelectorMethod.XPATH);
        radioAttribute.setSelection(store.getCapturedTestObjectSelectorMethod() == SelectorMethod.BASIC);
    }

    private void setInputForCapturedObjectPropertySetting(List<Pair<String, Boolean>> input) {
        defaultSelectingCapturedObjectProperties = input;
        tvProperty.setInput(defaultSelectingCapturedObjectProperties);
    }
    
    private void setInputForCapturedObjectXpathSetting(List<Pair<String, Boolean>> input) {
        defaultSelectingCapturedObjectXpaths = input;
        tvXpath.setInput(defaultSelectingCapturedObjectXpaths);
    }
    
    private void setSelectionForCapturedObjectSelectionSetting(SelectorMethod defaultSelectionMethod) {
    	defaultSelectingCapturedObjecSelectionMethods = defaultSelectionMethod;
        switch(defaultSelectionMethod){
	        case XPATH: 
	        	radioXpath.setSelection(true);
	        	radioAttribute.setSelection(false);
	        	break;
	        case BASIC:
	        	radioXpath.setSelection(false);
	        	radioAttribute.setSelection(true);
	        	break;
	        default:
	        	break;
        }
    }
    
    @Override
    protected void performDefaults() {
        if (container == null) {
            return;
        }
        try {
            store.setDefaultCapturedTestObjectAttributeLocators();
            store.setDefaultCapturedTestObjectXpathLocators();
            store.setDefaultCapturedTestObjectSelectorMethods();
            setInputForCapturedObjectPropertySetting(store.getCapturedTestObjectAttributeLocators());
            setInputForCapturedObjectXpathSetting(store.getCapturedTestObjectXpathLocators());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (super.performOk() && isValid()) {
            if (tvProperty != null) {
                try {
                    List<Pair<String, Boolean>> emptyPropertyItems = defaultSelectingCapturedObjectProperties.stream()
                            .filter(i -> i.getLeft().isEmpty())
                            .collect(Collectors.toList());
                    defaultSelectingCapturedObjectProperties.removeAll(emptyPropertyItems);
                    
                    List<Pair<String, Boolean>> emptyXpathItems = defaultSelectingCapturedObjectXpaths.stream()
                            .filter(i -> i.getLeft().isEmpty())
                            .collect(Collectors.toList());
                    defaultSelectingCapturedObjectXpaths.removeAll(emptyXpathItems);
                   
                    store.setCapturedTestObjectAttributeLocators(defaultSelectingCapturedObjectProperties);
                    store.setCapturedTestObjectXpathLocators(defaultSelectingCapturedObjectXpaths);
                    store.setCapturedTestObjectSelectorMethod(defaultSelectingCapturedObjecSelectionMethods);
                    
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        }
        return true;
    }
    
	private void showComposite(Composite composite, boolean isVisible) {
		composite.setVisible(isVisible);
		((GridData) composite.getLayoutData()).exclude = !isVisible;
		composite.getParent().layout();
	}

	@Override
    public boolean hasDocumentation() {
		return true;
	}

	@Override
    public String getDocumentationUrl() {
		return DocumentationMessageConstants.SETTINGS_WEBLOCATORS;
	}
	
	
}
