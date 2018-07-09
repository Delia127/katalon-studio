package com.kms.katalon.composer.execution.collection.dialog;

import java.net.MalformedURLException;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionItem;

public class TestExecutionItemLabelProvider extends TypeCheckedLabelProvider<TestExecutionItem> {

    @Override
    protected Class<TestExecutionItem> getElementType() {
        return TestExecutionItem.class;
    }

    @Override
    protected Image getColumnImageByIndex(TestExecutionItem element, int columnIndex) {
        try {
            return ImageUtil.loadImage(element.getImageUrlAsString());
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    protected String getColumnTextByIndex(TestExecutionItem element, int columnIndex) {
        return element.getName();
    }

}
