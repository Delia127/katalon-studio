package com.kms.katalon.composer.webservice.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.util.SafeUtils;
import com.kms.katalon.composer.webservice.util.WSDLHelper;
import com.kms.katalon.composer.webservice.util.XmlUtils;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
	@SuppressWarnings({ "static-access" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(String requestMethod, String url) 
			throws InterruptedException, InvocationTargetException, WSDLException{
		List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();

			WSDLHelper wsdlHelperInstance = WSDLHelper.newInstance(url, null);
			List<String> operationNames = wsdlHelperInstance.getOperationNamesByRequestMethod(requestMethod);
			Map<String, List<String>> paramMap = wsdlHelperInstance.getParamMap();
			new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell()).run(true, true,
					new IRunnableWithProgress() {
						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								monitor.beginTask(
										"Background operations are running...", IProgressMonitor.UNKNOWN);

								monitor.worked(1);
								
								WSDLHelper helper = WSDLHelper.newInstance(url, null);
								String location = getLocation(wsdlHelperInstance);

								for(Object objOperationName: SafeUtils.safeList(operationNames)){
									if(objOperationName != null){
										String operationName = (String) objOperationName;
										WebServiceRequestEntity newWSREntity = new WebServiceRequestEntity();
										newWSREntity.setWsdlAddress(location);
										newWSREntity.setName(operationName);
										newWSREntity.setSoapRequestMethod(requestMethod);
										newWSREntity.setSoapServiceFunction(operationName);
										monitor.worked(1);
				                        if (monitor.isCanceled()) {
				                            return;
				                        }
										String SOAPBodyMessage = wsdlHelperInstance.generateInputSOAPMessageText(helper, requestMethod, operationName, paramMap);
										if(SOAPBodyMessage != null){
											newWSREntity.setSoapBody(XmlUtils.prettyFormat(SOAPBodyMessage));
										}
										monitor.worked(1);
										newWSTestObjects.add(newWSREntity);
									}
								}
								monitor.worked(1);
							} catch (Exception ex){
									LoggerSingleton.getInstance().logError(ex.getMessage());
									if(ex instanceof InterruptedException){
										throw new InterruptedException();
									}else {									
										throw new InvocationTargetException(ex);		
									}							
							}
							finally {
								monitor.done();
							}
						}
			});
			return newWSTestObjects;

	}
	
    public static String getLocation(WSDLHelper wsdlHelperInstance) throws WSDLException {
        String location = null;
        String definition = wsdlHelperInstance.getDefinition().toString();
        String[] lines = definition.split("\n");
        if (lines != null) {
            for (String line : lines) {
                if (line.contains("locationURI=")) {
                    String[] locationURI = line.split("locationURI=");
                    location = locationURI[1];
                }
            }
        }
        if (!location.contains("?wsdl")) {
            location = location + "?wsdl";
        }
        return location;
    }

	public static List<WebServiceRequestEntity> newWSTestObjectsFromWSDL(String requestMethod, String directory) 
			throws InvocationTargetException, InterruptedException, WSDLException {
        List<WebServiceRequestEntity> newWSTestObjects = WSDLParserUtil.parseFromFileLocationToWSTestObject(requestMethod, directory);
        return newWSTestObjects;
    }

}
