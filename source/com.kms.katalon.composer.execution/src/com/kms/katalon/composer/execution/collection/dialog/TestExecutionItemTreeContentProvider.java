package com.kms.katalon.composer.execution.collection.dialog;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedTreeContentProvider;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionItem;

public class TestExecutionItemTreeContentProvider extends TypeCheckedTreeContentProvider<TestExecutionItem> {

    @Override
    protected Class<TestExecutionItem> getElementType() {
        return TestExecutionItem.class;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    protected Object[] getChildElements(TestExecutionItem parentElement) {
        return parentElement.getChildren();
    }

    @Override
    protected Object getParentElement(TestExecutionItem element) {
        return null;
    }

    @Override
    protected boolean hasChildElements(TestExecutionItem element) {
        return ArrayUtils.isNotEmpty(element.getChildren());
    }

}
