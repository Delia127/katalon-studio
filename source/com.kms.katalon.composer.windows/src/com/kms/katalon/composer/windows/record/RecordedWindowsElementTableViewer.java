package com.kms.katalon.composer.windows.record;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public class RecordedWindowsElementTableViewer extends CTableViewer {

    private static final String RUNTIME_ID_PROP = "RuntimeId";
    public List<CapturedWindowsElement> capturedElements;

    public RecordedWindowsElementTableViewer(Composite parent, int style) {
        super(parent, style);
    }

    public void setCaptureElements(List<CapturedWindowsElement> input) {
        capturedElements = new ArrayList<>(input);
        super.setInput(input);
    }

    public List<CapturedWindowsElement> getCapturedElements() {
        return capturedElements;
    }

    public CapturedWindowsElement getDuplicatedObject(CapturedWindowsElement element) {
        for (CapturedWindowsElement captured : capturedElements) {
            if (captured.getProperties().containsKey(RUNTIME_ID_PROP) && element.getProperties().containsKey(RUNTIME_ID_PROP)
                    && captured.getProperties().get(RUNTIME_ID_PROP).equals(element.getProperties().get(RUNTIME_ID_PROP))
                    && StringUtils.isNotEmpty(captured.getProperties().get(RUNTIME_ID_PROP))) {
                return captured;
            }
        }
        return null;
    }

    public CapturedWindowsElement addCapturedObject(CapturedWindowsElement element) {
        CapturedWindowsElement duplicatedElement = getDuplicatedObject(element);
        if (duplicatedElement != null) {
            return duplicatedElement;
        }

        List<String> currentNames = capturedElements.stream().map(e -> e.getName()).collect(Collectors.toList());
        if (currentNames.contains(element.getName())) {
            String suggestedName = findNameForElement(element.getName(), currentNames);
            element.setName(suggestedName);
        }
        capturedElements.add(element);
        refresh();
        setSelection(new StructuredSelection(element));
        showLastItem();

        return element;
    }

    private String findNameForElement(String name, List<String> currentNames) {
        String suggestedName = name;
        int index = 0;
        while (currentNames.contains(suggestedName)) {
            index++;
            suggestedName = name + "(" + index + ")";
        }
        return suggestedName;
    }

    @Override
    public Object getInput() {
        return capturedElements;
    }

    public CapturedWindowsElement getElementByName(String name) {
        return capturedElements.stream().filter(e -> e.getName().equals(name)).findAny().orElseGet(null);
    }
}
