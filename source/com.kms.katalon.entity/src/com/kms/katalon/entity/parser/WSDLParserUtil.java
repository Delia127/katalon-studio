package com.kms.katalon.entity.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.webservice.util.WSDLHelper;
import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.dialog.ImportErrorDialog;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
	@SuppressWarnings({ "unchecked", "finally" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocationOrUrl){
		List<WebServiceRequestEntity> newWSTestObject = new ArrayList<WebServiceRequestEntity>();
		
		try{
			
			WSDLHelper
                    .newInstance(fileLocationOrUrl, null);
			
		} catch (Exception ex) {
			// Do nothing			
	    } finally {
	    	if(newWSTestObject.size() > 0 ) { 
	    		return newWSTestObject;
	    	} 
	    	ImportErrorDialog dialog = new ImportErrorDialog(Display.getCurrent().getActiveShell(), StringConstants.EXC_INVALID_WSDL_FILE);
            dialog.open();
			return null;
	    }		
	}
}
