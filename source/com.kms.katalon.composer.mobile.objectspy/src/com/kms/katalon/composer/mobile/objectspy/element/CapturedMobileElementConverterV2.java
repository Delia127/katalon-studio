package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class CapturedMobileElementConverterV2 implements Converter<CapturedMobileElement, MobileElementEntity> {

	@Override
	public MobileElementEntity convert(CapturedMobileElement capturedElement) {
		MobileElementEntity mobileElement = new MobileElementEntity();
		mobileElement.setName(capturedElement.getName());
		mobileElement.setLocator(capturedElement.getLocator());
		mobileElement.setLocatorStrategy(capturedElement.getLocatorStrategy());

		List<WebElementPropertyEntity> properties = new ArrayList<>();
		for (Entry<String, String> attributes : capturedElement.getAttributes().entrySet()) {
			WebElementPropertyEntity prop = new WebElementPropertyEntity(attributes.getKey(), attributes.getValue());
			properties.add(prop);
		}
		mobileElement.setWebElementProperties(properties);
		return mobileElement;
	}

	public MobileElementEntity convert(CapturedMobileElement mobileElement, FolderEntity folder,
			MobileDriverType currentMobileType) throws Exception {
		MobileElementEntity newMobileElement = convert(mobileElement);
		newMobileElement.setName(ObjectRepositoryController.getInstance().getAvailableWebElementName(folder,
				ObjectRepositoryController.toValidFileName(StringUtils.trim(mobileElement.getName()))));
		newMobileElement.setParentFolder(folder);
		newMobileElement.setProject(folder.getProject());
		return newMobileElement;
	}

	@Override
	public CapturedMobileElement revert(MobileElementEntity mobileElement) {
		CapturedMobileElement capturedElement = new CapturedMobileElement();
		mobileElement.setName(mobileElement.getName());
		Map<String, String> attributes = new HashMap<>();
		for (WebElementPropertyEntity propertyEntity : mobileElement.getWebElementProperties()) {
			attributes.put(propertyEntity.getName(), propertyEntity.getValue());
		}
		capturedElement.setAttributes(attributes);
		capturedElement.setLocator(mobileElement.getLocator());
		capturedElement.setLocatorStrategy(mobileElement.getLocatorStrategy());
		return capturedElement;
	}

}
