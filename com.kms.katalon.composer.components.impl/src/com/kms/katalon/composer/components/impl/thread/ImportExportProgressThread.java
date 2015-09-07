package com.kms.katalon.composer.components.impl.thread;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.IImportExportController;
import com.kms.katalon.entity.dal.exception.CancelTaskException;

@SuppressWarnings("restriction")
public class ImportExportProgressThread implements IRunnableWithProgress {
	private int maxProgress;
	private IImportExportController importExportController;
	private Logger logger;

	public ImportExportProgressThread(int maxProgress, IImportExportController importExportController) {
		this.maxProgress = maxProgress;
		this.importExportController = importExportController;
		logger = LoggerSingleton.getInstance().getLogger();
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		Thread progressThread = null;
		try {
			monitor.beginTask(importExportController.getDisplayText(), maxProgress);
			progressThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						int progress = 0;
						while (progress < maxProgress) {
							Thread.sleep(100);
							int newProgress = importExportController.getProgress();

							if (newProgress > progress) {
								monitor.worked(newProgress - progress);
								progress = newProgress;
							}
							if (monitor.isCanceled()) {
								importExportController.cancel();
								break;
							}
							if (Thread.currentThread().isInterrupted()) {
								break;
							}
						}
					} catch (InterruptedException interruptedException) {
						return;
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
			progressThread.start();

			importExportController.execute();

		} catch (CancelTaskException cancelTaskException) {

		} catch (final Exception e) {
			logger.error(e);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), StringConstants.ERROR,
							e.getMessage());
				}
			});
		} finally {
			if (progressThread != null && progressThread.isAlive()) {
				progressThread.interrupt();
			}
			monitor.done();
		}
	}
}
