package com.kms.katalon.composer.testsuite.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * Test Case Selection Dialog for Test Suite
 * 
 * @author antruongnguyen
 *
 */
public class TestCaseSelectionDialog extends TreeEntitySelectionDialog {
	private TestCaseTableViewer tableViewer;

	private List<Object> tcTreeEntities;

	private List<Object> checkedItems;

	/**
	 * Test Case Selection Dialog for Test Suite
	 * 
	 * @param parent
	 *            parent shell
	 * @param labelProvider
	 *            entity label provider
	 * @param contentProvider
	 *            tree content provider
	 * @param entityViewerFilter
	 *            entity viewer filter
	 * @param tableViewer
	 *            test case table viewer
	 */
	public TestCaseSelectionDialog(Shell parent, IEntityLabelProvider labelProvider,
			ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter,
			TestCaseTableViewer tableViewer) {
		super(parent, labelProvider, contentProvider, entityViewerFilter);
		this.tableViewer = tableViewer;
		setTitle(StringConstants.DIA_TITLE_TEST_CASE_BROWSER);
		setAllowMultiple(false);
		
		setDoubleClickSelects(false);
		updateTestCaseTreeEntities();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.SelectionDialog#createButtonsForButtonBar(org.
	 * eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.SELECT_TYPES_ID, StringConstants.DIA_BTN_ADD_N_CONTINUE, false);
		super.createButtonsForButtonBar(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.SELECT_TYPES_ID == buttonId) {
			addSelectedTestCasesPressed();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		computeResult();
		setReturnCode(OK);
		try {
			updateTestCaseTableViewer();
		} catch (Exception e) {
		}
		close();
	}

	/**
	 * "Add selected test cases" button pressed listener
	 */
	private void addSelectedTestCasesPressed() {
		computeResult();

		if (getResult() == null || getResult().length == 0) {
			MessageDialog.openWarning(getParentShell(), StringConstants.WARN_TITLE,
					StringConstants.DIA_WARN_NO_TEST_CASE_SELECTION);
			return;
		}
		setReturnCode(IDialogConstants.SELECT_TYPES_ID);
		try {
			updateTestCaseTableViewer(); 
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#computeResult()
	 */
	@Override
	protected void computeResult() {
		ContainerCheckedTreeViewer treeViewer = (ContainerCheckedTreeViewer) getTreeViewer();
		List<Object> grayedItems = Arrays.asList(treeViewer.getGrayedElements());
		checkedItems.removeAll(grayedItems);
		setResult(checkedItems);
	}

    /**
     * Update test case table viewer
     * 
     * @throws Exception
     */
    public void updateTestCaseTableViewer() throws Exception {
        List<Object> selectedObjects = new ArrayList<Object>(Arrays.asList(getResult()));
        List<TestSuiteTestCaseLink> links = tableViewer.getInput();
        // add new checked items
        new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                int selectedObjectsSize = selectedObjects.size();
                monitor.beginTask("", selectedObjectsSize);
                SubMonitor subMonitor = SubMonitor.convert(monitor);
                subMonitor.beginTask("", selectedObjectsSize);
                for (Object object : selectedObjects) {
                    try {
                        SubMonitor subSubMonitor = subMonitor.split(1, SubMonitor.SUPPRESS_NONE);
                        if (!(object instanceof ITreeEntity)) {
                            continue;
                        }
                        ITreeEntity treeEntity = (ITreeEntity) object;
                        if (treeEntity instanceof FolderTreeEntity) {
                            FolderEntity fEntity = (FolderEntity) treeEntity.getObject();
                            addTestCaseFolderToTable(fEntity);
                        } else if (treeEntity instanceof TestCaseTreeEntity) {
                            TestCaseEntity tcEntity = (TestCaseEntity) treeEntity.getObject();
                            subSubMonitor.beginTask("Adding test case " + tcEntity.getIdForDisplay() + " to test suite",
                                    1);
                            UISynchronizeService.syncExec(() -> {
                                try {
                                    tableViewer.addTestCase(tcEntity);
                                } catch (Exception e) {}
                            });
                            subSubMonitor.done();
                        }
                    } catch (Exception e) {

                    }
                }
                monitor.done();
            }
        });
        // finally, update test case tree entity list
        updateTestCaseTreeEntities();
    }
    

	/**
	 * Add test case folder into test case table viewer
	 * 
	 * @param folderEntity 
	 * @throws Exception
	 */
    private void addTestCaseFolderToTable(FolderEntity folderEntity) throws Exception {
        if (folderEntity.getFolderType() == FolderType.TESTCASE) {
            FolderController folderController = FolderController.getInstance();
            List<FileEntity> childObjects = folderController.getChildren(folderEntity);
            for (Object childObject : childObjects) {
                UISynchronizeService.syncExec(() -> {
                    try {
                        if (childObject instanceof TestCaseEntity) {

                            tableViewer.addTestCase((TestCaseEntity) childObject);

                        } else if (childObject instanceof FolderEntity) {
                            addTestCaseFolderToTable((FolderEntity) childObject);
                        }
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                });
            }
        }
    }

	/**
	 * Keep track of Test Case list in table viewer
	 */
	private void updateTestCaseTreeEntities() {
		try {
			tcTreeEntities = new ArrayList<Object>(Arrays.asList(getAddedTestCase(tableViewer.getTestCasesPKs())));
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	@Override
	public TreeViewer createTreeViewer(Composite parent) {
		final ContainerCheckedTreeViewer treeViewer = (ContainerCheckedTreeViewer) super.createTreeViewer(parent);
		treeViewer.getTree().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					treeViewer.getTree().setSelection(item);
					onStageChangedTreeItem(item.getData(), item.getChecked());
				}
			}
		});
		Object[] addedTestCases = getAddedTestCase(tableViewer.getTestCasesPKs());
		treeViewer.setCheckedElements(addedTestCases);
		checkedItems = new ArrayList<Object>(Arrays.asList(addedTestCases));
		return treeViewer;
	}

	/**
	 * Check/Uncheck TreeItem action
	 * 
	 * @param element
	 *            {@link TreeItem#getData()}
	 * @param isChecked
	 *            whether TreeItem is checked or not
	 */
	private void onStageChangedTreeItem(Object element, boolean isChecked) {
		if (element instanceof TestCaseTreeEntity) {
			if (isChecked) {
				checkedItems.add(element);
			} else {
				checkedItems.remove(element);
			}
			return;
		}

		if (element instanceof FolderTreeEntity) {
			try {
				for (Object childElement : TreeEntityUtil.getChildren((FolderTreeEntity) element)) {
					onStageChangedTreeItem(childElement, isChecked);
				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.ElementTreeSelectionDialog#doCreateTreeViewer(org.
	 * eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new ContainerCheckedTreeViewer(new Tree(parent, SWT.CHECK | style));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kms.katalon.composer.components.impl.dialogs.
	 * TreeEntitySelectionDialog#filterSearchedText()
	 */
	@Override
	protected void filterSearchedText() {
		super.filterSearchedText();
		ContainerCheckedTreeViewer treeViewer = (ContainerCheckedTreeViewer) getTreeViewer();
		treeViewer.setCheckedElements(checkedItems.toArray());
	}

	/**
	 * Get all added Test Case(s) in Test Suite
	 * 
	 * @param ids
	 *            Entity ID list
	 * @return Added Test Case(s)
	 */
	private Object[] getAddedTestCase(List<String> ids) {
		List<TestCaseTreeEntity> testCaseList = new ArrayList<TestCaseTreeEntity>();
		try {
			TestCaseController c = TestCaseController.getInstance();
			for (String id : ids) {
				TestCaseEntity tc = c.getTestCase(id);
				TestCaseTreeEntity tcTree = TreeEntityUtil.getTestCaseTreeEntity(tc, tc.getProject());		
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return testCaseList.toArray();
	}

}
