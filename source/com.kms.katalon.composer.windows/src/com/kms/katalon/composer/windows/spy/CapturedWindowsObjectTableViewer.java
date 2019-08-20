package com.kms.katalon.composer.windows.spy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.windows.dialog.WindowsSpyObjectDialog;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.CapturedWindowsElementConverter;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class CapturedWindowsObjectTableViewer extends CTableViewer {

    private List<CapturedWindowsElement> capturedElements;

    private WindowsSpyObjectDialog dialog;

    public CapturedWindowsObjectTableViewer(Composite parent, int style, WindowsSpyObjectDialog dialog) {
        super(parent, style);
        this.dialog = dialog;
    }

    public List<CapturedWindowsElement> getCapturedElements() {
        return capturedElements;
    }

    public void setCapturedElements(List<CapturedWindowsElement> capturedElements) {
        this.capturedElements = capturedElements;
        setInput(capturedElements);
    }

    public CapturedWindowsElement getSelectedElement() {
        IStructuredSelection selection = getStructuredSelection();
        if (selection.isEmpty()) {
            return null;
        }
        return (CapturedWindowsElement) selection.getFirstElement();
    }

    public IStructuredSelection getStructuredSelection() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        return selection;
    }

    public CapturedWindowsElement[] getSelectedElements() {
        IStructuredSelection selection = getStructuredSelection();
        if (selection.isEmpty()) {
            return new CapturedWindowsElement[0];
        }
        Object[] rawElements = selection.toArray();
        CapturedWindowsElement[] capturedElements = new CapturedWindowsElement[rawElements.length];
        for (int i = 0; i < rawElements.length; i++) {
            capturedElements[i] = (CapturedWindowsElement) rawElements[i];
        }
        return capturedElements;
    }

    public void addElements(List<WindowsElementEntity> webElements) {
        CapturedWindowsElementConverter converter = new CapturedWindowsElementConverter();
        List<CapturedWindowsElement> newWindowsElements = new ArrayList<>();
        for (WindowsElementEntity webElement : webElements) {
            newWindowsElements.add(converter.revert(webElement));
        }

        addWindowsElements(newWindowsElements);
    }

    public void addWindowsElements(List<CapturedWindowsElement> mobileElements) {
        List<CapturedWindowsElement> added = new ArrayList<CapturedWindowsElement>();
        List<String> currentNames = capturedElements.stream().map(e -> e.getName()).collect(Collectors.toList());
        for (CapturedWindowsElement eachElement : mobileElements) {
            if (currentNames.contains(eachElement.getName())) {
                String suggestedName = findNameForElement(eachElement.getName(), currentNames);
                eachElement.setName(suggestedName);
                currentNames.add(suggestedName);
            }
            int elementIdx = capturedElements.indexOf(eachElement);
            if (elementIdx < 0) {
                capturedElements.add(eachElement);
                added.add(eachElement);
            } else {
                added.add(capturedElements.get(elementIdx));
            }
        }
        refresh();
        setSelection(new StructuredSelection(added));
        showLastItem();
        notifyStateChanged();
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

    public void removeCapturedElements(List<CapturedWindowsElement> elements) {
        capturedElements.removeAll(elements);
        refresh();
        notifyStateChanged();
    }

    public void removeCapturedElement(CapturedWindowsElement element) {
        removeCapturedElement(element, true);
    }

    public void removeCapturedElement(CapturedWindowsElement element, boolean needRefresh) {
        if (!contains(element)) {
            return;
        }
        capturedElements.remove(element);
        if (needRefresh) {
            refresh();
        }
        notifyStateChanged();
    }

    public void checkAllElements(boolean checked) {
        if (capturedElements.isEmpty()) {
            return;
        }
        for (CapturedWindowsElement mobileElement : capturedElements) {
            mobileElement.setChecked(checked);
        }
        refresh();
        notifyStateChanged();
    }

    public boolean contains(CapturedWindowsElement element) {
        if (element == null) {
            return false;
        }
        return capturedElements.contains(element);
    }

    public boolean isAllElementChecked() {
        for (CapturedWindowsElement mobileElement : capturedElements) {
            if (!mobileElement.isChecked()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyElementChecked() {
        for (CapturedWindowsElement mobileElement : capturedElements) {
            if (mobileElement.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public List<CapturedWindowsElement> getAllCheckedElements() {
        List<CapturedWindowsElement> checkedElements = new ArrayList<>();
        for (CapturedWindowsElement mobileElement : capturedElements) {
            if (mobileElement.isChecked()) {
                checkedElements.add(mobileElement);
            }
        }
        return checkedElements;
    }

    public void notifyStateChanged() {
        dialog.updateCapturedElementSelectingColumnHeader();
    }
}
