package com.kms.katalon.composer.testsuite.dialogs;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.filters.DataColumnViewerFilter;
import com.kms.katalon.composer.testsuite.providers.DataColumnLabelProvider;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class DataColumnFinderDialog extends Dialog {
	private IEntityLabelProvider labelProvider;
	private ITreeContentProvider contentProvider;
	private AbstractEntityViewerFilter entityViewerFilter;
	private Text searchTestDataText, searchColumnNameText;
	private TreeViewer treeViewer;
	private Object[] testDataTreeEntities;
	private TableViewer dataColumnViewer;
	private DataColumnViewerFilter columnNameFilter;
	private DataColumnLabelProvider dataColumnLabelProvider;

	private String returnValue;
	private String[] selection;

	public DataColumnFinderDialog(Shell parentShell, IEntityLabelProvider labelProvider,
			ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter,
			Object[] testDataTreeEntities, String[] initSelection) {
		super(parentShell);
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
		this.entityViewerFilter = entityViewerFilter;
		this.testDataTreeEntities = testDataTreeEntities;
		this.selection = initSelection;
	}

	@Override
	public void create() {
		super.create();
		treeViewer.setInput(testDataTreeEntities);
		registerListeners();
		initSelection();
	}

	private void initSelection() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					if (selection != null && selection.length == 2) {
						String testDataId = selection[0];
						String columnName = selection[1];
						DataFileEntity dataFileEntity = TestDataController.getInstance().getTestDataByDisplayId(
								testDataId);
						FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(
								ProjectController.getInstance().getCurrentProject());
						FolderTreeEntity parentTreeEntity = createSelectedTreeEntityHierachy(
								dataFileEntity.getParentFolder(), rootFolder);

						TestDataTreeEntity selectedTreeEntity = new TestDataTreeEntity(dataFileEntity, parentTreeEntity);
						treeViewer.setSelection(new StructuredSelection(selectedTreeEntity));

						dataColumnViewer.setSelection(new StructuredSelection(columnName));
					} else {
						getButton(Dialog.OK).setEnabled(false);
					}
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				}
			}
		});

	}

	private FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
		if (folderEntity == null || folderEntity.equals(rootFolder)) {
			return null;
		}
		return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
				rootFolder));
	}

	private void registerListeners() {
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ITreeEntity entity = (ITreeEntity) ((TreeSelection) event.getSelection()).getFirstElement();
				if (entity != null) {
					try {
						DataFileEntity dataFile = (DataFileEntity) entity.getObject();
						String testDataId = TestDataController.getInstance().getIdForDisplay(dataFile);
						String[] columnNames = TestDataFactory.findTestDataForExternalBundleCaller(testDataId,
								dataFile.getProject().getFolderLocation()).getColumnNames();
						dataColumnViewer.setInput(columnNames);
						getButton(Dialog.OK).setEnabled(false);
					} catch (Exception e) {
						dataColumnViewer.setInput(Collections.EMPTY_LIST);
						getButton(Dialog.OK).setEnabled(false);
					}
				}
			}
		});

		dataColumnViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection != null && selection.size() == 1) {
					String selectedColumnName = (String) selection.getFirstElement();
					if (selectedColumnName != null) {
						getButton(Dialog.OK).setEnabled(true);
					} else {
						getButton(Dialog.OK).setEnabled(false);
					}
				} else {
					getButton(Dialog.OK).setEnabled(false);
				}
			}
		});

		searchColumnNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String searchString = ((Text) e.getSource()).getText();
				columnNameFilter.setSearchString(searchString);
				dataColumnLabelProvider.setSearchString(searchString);

				dataColumnViewer.refresh();
				dataColumnViewer.setSelection(null);
			}
		});

		searchTestDataText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String searchString = ((Text) e.getSource()).getText();

				entityViewerFilter.setSearchString(searchString);
				labelProvider.setSearchString(searchString);
				entityViewerFilter.setSearchString(searchString);

				treeViewer.refresh();
				treeViewer.setSelection(null);
			}
		});
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		SashForm sashForm = new SashForm(container, SWT.NONE);
		sashForm.setSashWidth(5);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite testDataHierarchyComposite = new Composite(sashForm, SWT.NONE);
		testDataHierarchyComposite.setLayout(new GridLayout(1, false));

		Label lblSelectTestData = new Label(testDataHierarchyComposite, SWT.NONE);
		lblSelectTestData.setText(StringConstants.DIA_LBL_SELECT_TEST_DATA);

		searchTestDataText = new Text(testDataHierarchyComposite, SWT.BORDER);
		searchTestDataText.setMessage(StringConstants.DIA_TXT_ENTER_TEXT_TO_SEARCH);
		searchTestDataText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTestDataHierarchy = new Label(testDataHierarchyComposite, SWT.NONE);
		lblTestDataHierarchy.setText(StringConstants.DIA_LBL_TEST_DATA_HIERARCHY);

		treeViewer = new TreeViewer(testDataHierarchyComposite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.addFilter(entityViewerFilter);

		Composite columnNameComposite = new Composite(sashForm, SWT.NONE);
		columnNameComposite.setLayout(new GridLayout(1, false));

		Label lblSelectAColumn = new Label(columnNameComposite, SWT.NONE);
		lblSelectAColumn.setText(StringConstants.DIA_LBL_SELECT_A_COL);

		searchColumnNameText = new Text(columnNameComposite, SWT.BORDER);
		searchColumnNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		searchColumnNameText.setMessage(StringConstants.DIA_TXT_ENTER_TEXT_TO_SEARCH);

		Label lblListOfColumn = new Label(columnNameComposite, SWT.NONE);
		lblListOfColumn.setText(StringConstants.DIA_LBL_LIST_OF_COL_NAMES);

		dataColumnViewer = new TableViewer(columnNameComposite, SWT.BORDER | SWT.V_SCROLL);
		Table dataColumnTable = dataColumnViewer.getTable();
		dataColumnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		dataColumnViewer.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn tableViewerColumn = new TableViewerColumn(dataColumnViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(250);
		tableViewerColumn.setLabelProvider(new DataColumnLabelProvider());
		dataColumnLabelProvider = new DataColumnLabelProvider();
		tableViewerColumn.setLabelProvider(dataColumnLabelProvider);

		columnNameFilter = new DataColumnViewerFilter();
		dataColumnViewer.addFilter(columnNameFilter);

		sashForm.setWeights(new int[] { 1, 1 });
		return container;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 500);
	}

	@Override
	protected void okPressed() {
		prepareReturnValue();
		super.okPressed();
	}

	private void prepareReturnValue() {
		try {
			ITreeEntity entity = (ITreeEntity) ((TreeSelection) treeViewer.getSelection()).getFirstElement();
			DataFileEntity dataFile = (DataFileEntity) entity.getObject();
			String testDataId = TestDataController.getInstance().getIdForDisplay(dataFile);

			IStructuredSelection selection = (IStructuredSelection) dataColumnViewer.getSelection();
			String selectedColumnName = (String) selection.getFirstElement();
			returnValue = Arrays.toString(new String[] { testDataId, selectedColumnName });
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	public String getReturnValue() {
		return returnValue;
	}

}
