package com.kms.katalon.composer.webservice.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.entity.webservice.HttpBodyContent;

public abstract class HttpBodyEditor extends Composite {

    private boolean contentTypeUpdated = false;

    public HttpBodyEditor(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Returns the Content-Type of the editing editor. This value is used to
     * update HTTP header table.
     */
    public abstract String getContentType();

    /**
     * @return JSON string format of the {@link HttpBodyContent}
     */
    public abstract String getContentData();

    /**
     * Initializes input of the editor.
     * 
     * @param httpBodyContent:
     *            JSON string format of the {@link HttpBodyContent}
     */
    public abstract void setInput(String httpBodyContent);

    /**
     * Notifies the request service part update value of HTTP Header
     * "Content-Type" in HTTP Header table.
     * 
     * @return true if the Content-Type was updated. Otherwise, false.
     */
    public boolean isContentTypeUpdated() {
        return contentTypeUpdated;
    }

    public void setContentTypeUpdated(boolean contentTypeUpdated) {
        this.contentTypeUpdated = contentTypeUpdated;
    }

    public void fireModifyEvent() {
        notifyListeners(SWT.Modify, new Event());
    }

    /**
     * Invokes when users select on a body type button. Children may override
     * this.
     */
    public void onBodyTypeChanged() {
    }
}
