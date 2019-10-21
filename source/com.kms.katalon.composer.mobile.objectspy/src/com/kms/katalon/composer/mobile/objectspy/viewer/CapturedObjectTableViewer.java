package com.kms.katalon.composer.mobile.objectspy.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementDialog;
import com.kms.katalon.composer.mobile.objectspy.element.CapturedMobileElementConverter;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.entity.repository.WebElementEntity;

public class CapturedObjectTableViewer extends CTableViewer {

    private List<CapturedMobileElement> capturedElements;

    private MobileElementDialog dialog;

    public CapturedObjectTableViewer(Composite parent, int style, MobileElementDialog dialog) {
        super(parent, style);
        this.dialog = dialog;
        capturedElements = new ArrayList<CapturedMobileElement>();
    }

    public List<CapturedMobileElement> getCapturedElements() {
        return capturedElements;
    }

    public void setCapturedElements(List<CapturedMobileElement> capturedElements) {
        this.capturedElements = capturedElements;
        setInput(capturedElements);
    }

    public CapturedMobileElement getSelectedElement() {
        IStructuredSelection selection = getStructuredSelection();
        if (selection.isEmpty()) {
            return null;
        }
        return (CapturedMobileElement) selection.getFirstElement();
    }

    public IStructuredSelection getStructuredSelection() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        return selection;
    }

    public CapturedMobileElement[] getSelectedElements() {
        IStructuredSelection selection = getStructuredSelection();
        if (selection.isEmpty()) {
            return new CapturedMobileElement[0];
        }
        Object[] rawElements = selection.toArray();
        CapturedMobileElement[] capturedElements = new CapturedMobileElement[rawElements.length];
        for (int i = 0; i < rawElements.length; i++) {
            capturedElements[i] = (CapturedMobileElement) rawElements[i];
        }
        return capturedElements;
    }

    public void addElements(List<WebElementEntity> webElements) {
        CapturedMobileElementConverter converter = new CapturedMobileElementConverter();
        List<CapturedMobileElement> newMobileElements = new ArrayList<>();
        for (WebElementEntity webElement : webElements) {
            newMobileElements.add(converter.revert(webElement));
        }

        addMobileElements(newMobileElements);
    }

    public void addMobileElements(List<CapturedMobileElement> mobileElements) {
        List<CapturedMobileElement> added = new ArrayList<CapturedMobileElement>();
        for (CapturedMobileElement eachElement : mobileElements) {
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

    public void removeCapturedElements(List<CapturedMobileElement> elements) {
        capturedElements.removeAll(elements);
        refresh();
        notifyStateChanged();
    }

    public void removeCapturedElement(CapturedMobileElement element) {
        removeCapturedElement(element, true);
    }

    public void removeCapturedElement(CapturedMobileElement element, boolean needRefresh) {
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
        for (CapturedMobileElement mobileElement : capturedElements) {
            mobileElement.setChecked(checked);
        }
        refresh();
        notifyStateChanged();
    }

    public boolean contains(CapturedMobileElement element) {
        if (element == null) {
            return false;
        }
        return capturedElements.contains(element);
    }

    public boolean isAllElementChecked() {
        for (CapturedMobileElement mobileElement : capturedElements) {
            if (!mobileElement.isChecked()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyElementChecked() {
        for (CapturedMobileElement mobileElement : capturedElements) {
            if (mobileElement.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public List<CapturedMobileElement> getAllCheckedElements() {
        List<CapturedMobileElement> checkedElements = new ArrayList<>();
        for (CapturedMobileElement mobileElement : capturedElements) {
            if (mobileElement.isChecked()) {
                checkedElements.add(mobileElement);
            }
        }
        return checkedElements;
    }

    public void notifyStateChanged() {
        dialog.handleCapturedObjectsTableSelectionChange();
    }
}
