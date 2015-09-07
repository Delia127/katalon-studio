package com.kms.katalon.composer.objectrepository.providers;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.objectrepository.constants.ImageConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;


public class PropertyLabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex) {
		Image theImage = null;
		if(columnIndex == 3){
			if(element instanceof WebElementPropertyEntity){
				WebElementPropertyEntity theProperty = (WebElementPropertyEntity)element;
				if(theProperty.getIsSelected()){
					theImage = ImageConstants.IMG_16_CHECKBOX_CHECKED;		
				}
				else{
					theImage = ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
				}
			}
		}
		return theImage;
	}

	public String getColumnText(Object element, int columnIndex) {
		String name = "";
		String value = "";
		String condition = WebElementPropertyEntity.defaultMatchCondition; //Default value
		if(element instanceof PropertyGroup){
			name = ((PropertyGroup)element).getName();
		}
		else if(element instanceof WebElementPropertyEntity){
			name = ((WebElementPropertyEntity)element).getName();
			value = ((WebElementPropertyEntity)element).getValue();
			if(((WebElementPropertyEntity)element).getMatchCondition() != null){
				condition = ((WebElementPropertyEntity)element).getMatchCondition();
			}
		}
		if(columnIndex == 0){
			return name;
		}
		else if(columnIndex == 1){
			return condition;
		}
		else if(columnIndex == 2){
			return value;
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener) {}

	public void dispose() {}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {}

	public static Image loadImage(Bundle bundle, String imageURI) {
		URL url = FileLocator.find(bundle, new Path(imageURI), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
	
}
