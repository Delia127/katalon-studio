package com.kms.katalon.composer.testsuite.providers;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;

public class IsRunColumnLabelProvider extends OwnerDrawLabelProvider {
    
    public IsRunColumnLabelProvider() {
	}
    
    @Override
    protected void measure(Event event, Object element) {
    }

    @Override
    protected void paint(Event event, Object element) {
        if (element != null && element instanceof TestSuiteTestCaseLink) {
            if (((TestSuiteTestCaseLink) element).getIsRun()) {
            	event.gc.drawImage(ImageConstants.IMG_16_CHECKBOX_CHECKED, event.getBounds().x + 5, event.getBounds().y);
            }
            else{
            	event.gc.drawImage(ImageConstants.IMG_16_CHECKBOX_UNCHECKED, event.getBounds().x + 5, event.getBounds().y);
            }
        }
    }
}
