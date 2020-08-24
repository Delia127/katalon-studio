package com.kms.katalon.core.application;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleException;

import com.kms.katalon.application.ApplicationStarter;
import com.kms.katalon.application.MetadataCorruptedResolver;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.ApplicationSession;

public class WorkbenchApplicationStarter implements ApplicationStarter {

    @Override
    public int start(String[] arguments) {
        startPlatformComposerBundle();
        ApplicationSession.clean();
        Display display = PlatformUI.createDisplay();
        try {
            return PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
        } catch (Exception e) {
            LogUtil.logError(e);
        } catch (Error e) {
            LogUtil.logError(e);
            return resolve();
        } finally {
            ApplicationSession.close();
            display.dispose();
        }
        return PlatformUI.RETURN_OK;
    }

    private void startPlatformComposerBundle() {
        try {
            Platform.getBundle(IdConstants.KATALON_INTERNAL_COMPOSER_PLATFORM_BUNDLE_ID).start();
        } catch (BundleException ex) {
            LogUtil.logError(ex);
        }
    }

    private int resolve() {
        MetadataCorruptedResolver resolver = new MetadataCorruptedResolver();
        if (!resolver.isMetaFolderCorrupted()) {
            return PlatformUI.RETURN_UNSTARTABLE;
        }
        return resolver.resolve() ? PlatformUI.RETURN_RESTART : PlatformUI.RETURN_UNSTARTABLE;
    }

    @Override
    public void stop() {
        if (!PlatformUI.isWorkbenchRunning()) {
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                if (!display.isDisposed()) {
                    workbench.close();
                }
            }
        });
    }

}
