package com.kms.katalon.entity.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.webservice.util.WSDLHelper;
import com.kms.katalon.composer.webservice.util.XmlUtils;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
	@SuppressWarnings({ "static-access", "finally" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(String requestMethod, String url) throws Exception{
		List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
		
		try{
			new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell()).run(true, true,
					new IRunnableWithProgress() {
						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {

								WSDLHelper wsdlHelperInstance = WSDLHelper.newInstance(url, null);
								List<String> operationNames = wsdlHelperInstance.getOperationNamesByRequestMethod(requestMethod);
								monitor.beginTask(
										"Background operations are running...", IProgressMonitor.UNKNOWN);
								Map<String, List<String>> paramMap = wsdlHelperInstance.getParamMap();
								monitor.worked(1);
								for(Object objOperationName: SafeUtils.safeList(operationNames)){
									if(objOperationName != null){
										String operationName = (String) objOperationName;
										WebServiceRequestEntity newWSREntity = new WebServiceRequestEntity();
										newWSREntity.setWsdlAddress(url);
										newWSREntity.setName(operationName);
										newWSREntity.setSoapRequestMethod(requestMethod);
										newWSREntity.setSoapServiceFunction(operationName);
										monitor.worked(1);
				                        if (monitor.isCanceled()) {
				                            return;
				                        }
										String SOAPBodyMessage = wsdlHelperInstance.generateInputSOAPMessageText(url, null, requestMethod, operationName, paramMap);
										if(SOAPBodyMessage != null){
											newWSREntity.setSoapBody(XmlUtils.prettyFormat(SOAPBodyMessage));
										}
										monitor.worked(1);
										newWSTestObjects.add(newWSREntity);
									}
								}
								monitor.worked(1);
							} catch (Exception ex1) {
								throw new InterruptedException();
							} finally {
								monitor.done();
							}
						}
			});

			
		} catch (Exception ex) {
			throw ex;
	    } finally {
	    	if(newWSTestObjects.size() > 0 ) { 	    		
	    		return newWSTestObjects;
	    	} else 
	    		return null;
	    }
	}
}
