package com.kms.katalon.composer.testsuite.filters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.dialogs.TestCaseSelectionDialog;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * This is used in {@link TestCaseSelectionDialog} to filter Test Cases already
 * added to Test Suites. The implementation uses
 * {@link AlreadyAddedTestCaseFilter}
 *
 */
public class AlreadyAddedTestCaseViewerFilter extends EntityViewerFilter {
	private boolean needToCheckAlreadyAddedTestCases = false;
	private AlreadyAddedTestCaseFilter alreadyAddedTestCaseFilter;
	private ITreeEntity[] treeEntities;

	public AlreadyAddedTestCaseViewerFilter(EntityProvider entityProvider, ITreeEntity[] treeEntities) {
		super(entityProvider);
		alreadyAddedTestCaseFilter = new AlreadyAddedTestCaseFilter();
		this.treeEntities = treeEntities;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ITreeEntity) {
			try {
				if (needToCheckAlreadyAddedTestCases) {
					String entityId = getEntityIdForDisplay((ITreeEntity) element);
					if (alreadyAddedTestCaseFilter.isEntityAlreadyAddedToTestSuites(entityId)) {
						return false;
					}
				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		}
		return super.select(viewer, parentElement, element);
	}

	private void setAlreadyAddedTestCasesCondition(boolean val) {
		needToCheckAlreadyAddedTestCases = val;
	}

	/**
	 * Turn on "Filter already added test cases" and initialize cache if cache
	 * is empty. During cache initialization, check if elements of the array of
	 * {@link ITreeEntity} (given via constructor) are added to Test Suites and
	 * cache the results.
	 * 
	 */
	public void enableAlreadyAddedTestCases() {
		setAlreadyAddedTestCasesCondition(true);
		if (!alreadyAddedTestCaseFilter.isResultNotEmpty()) {
			try {
				new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false,
						new IRunnableWithProgress() {
							@Override
							public void run(final IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
								initializeAlreadyAddedEntitiesCache(monitor);
							}
						});
			} catch (InvocationTargetException | InterruptedException e1) {
				LoggerSingleton.logError(e1);
			}
		}
	}

	/**
	 * Turn off "Filter already added test cases" mode. Cached results are
	 * retained
	 */
	public void disableAlreadyAddedTestCases() {
		setAlreadyAddedTestCasesCondition(false);
	}

	private void initializeAlreadyAddedEntitiesCache(IProgressMonitor monitor) {
		monitor.beginTask("Check if entities are added to any Test Suite", treeEntities.length);
		alreadyAddedTestCaseFilter.filterElements(treeEntities, monitor);
		monitor.done();
	}

	private String getEntityIdForDisplay(ITreeEntity entity) throws Exception {
		String entityId = "";
		if (entity instanceof FolderTreeEntity) {
			FolderEntity folderEntity = ((FolderTreeEntity) entity).getObject();
			if (folderEntity.getFolderType() == FolderType.TESTCASE) {
				entityId = folderEntity.getIdForDisplay();
			}
		}
		if (entity instanceof TestCaseTreeEntity) {
			TestCaseEntity tcEntity = ((TestCaseTreeEntity) entity).getObject();
			entityId = tcEntity.getIdForDisplay();
		}
		return entityId;
	}
}
