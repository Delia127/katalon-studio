package com.kms.katalon.entity.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WSDLParserUtil {
	@SuppressWarnings({ "unchecked", "finally" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocationOrUrl){
		List<WebServiceRequestEntity> newWSTestObject = new ArrayList<WebServiceRequestEntity>();
		
		try{
			
			
		} catch (Exception ex) {
			// Do nothing			
	    } finally {
	    	if(newWSTestObject.size() > 0 ) { 
	    		return newWSTestObject;
	    	} 
			MessageDialog.openError(Display.getCurrent().getActiveShell(), com.kms.katalon.entity.constants.StringConstants.ERROR,
					com.kms.katalon.entity.constants.StringConstants.EXC_INVALID_WSDL_FILE);
			return null;
	    }		
	}
}
