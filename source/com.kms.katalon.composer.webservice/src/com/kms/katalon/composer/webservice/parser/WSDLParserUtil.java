package com.kms.katalon.composer.webservice.parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.WSDLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.webservice.helper.WsdlLocatorProvider;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionLocator;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlImporter;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
    @SuppressWarnings({ "static-access" })
    public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(String requestMethod, String url)
            throws InterruptedException, InvocationTargetException, WSDLException, IOException {
        List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
        WsdlDefinitionLocator wsdlLocator = WsdlLocatorProvider.getLocator(url);
        WsdlImporter importer = new WsdlImporter(wsdlLocator);

        new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell()).run(true, true,
                new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor)
                            throws InvocationTargetException, InterruptedException {
                        try {
                            monitor.beginTask("Background operations are running...", IProgressMonitor.UNKNOWN);

                            monitor.worked(1);

                            List<WebServiceRequestEntity> importedRequestEntities = importer.getImportedEtities(requestMethod);
                            newWSTestObjects.addAll(importedRequestEntities);

                            monitor.worked(1);
                        } catch (Exception ex) {
                            LoggerSingleton.getInstance().logError(ex.getMessage());
                            if (ex instanceof InterruptedException) {
                                throw new InterruptedException();
                            } else {
                                throw new InvocationTargetException(ex);
                            }
                        } finally {
                            monitor.done();
                        }
                    }
                });
        return newWSTestObjects;

    }

    public static List<WebServiceRequestEntity> newWSTestObjectsFromWSDL(String requestMethod, String directory)
            throws InvocationTargetException, InterruptedException, WSDLException, IOException {
        List<WebServiceRequestEntity> newWSTestObjects = WSDLParserUtil
                .parseFromFileLocationToWSTestObject(requestMethod, directory);
        return newWSTestObjects;
    }

}
