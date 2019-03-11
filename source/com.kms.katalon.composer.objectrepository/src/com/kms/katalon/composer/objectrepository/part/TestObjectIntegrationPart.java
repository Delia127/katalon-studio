package com.kms.katalon.composer.objectrepository.part;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class TestObjectIntegrationPart {
	
    private MPart mPart;

    private TestObjectCompositePart testObjectCompositePart;
    
	public MPart getPart() {
		return mPart;
	}

	public void setDirty(boolean dirty) {
		mPart.setDirty(true);
	}

	public boolean isParentDirty() {
		return testObjectCompositePart.getDirty().isDirty();
	}

}
