package com.kms.katalon.composer.webservice.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.webservice.viewmodel.HttpBodyEditorViewModel;
import com.kms.katalon.entity.webservice.HttpBodyContent;

public abstract class HttpBodyEditor extends Composite {

    private HttpBodyEditorViewModel viewModel;

    public HttpBodyEditor(Composite parent, int style) {
        super(parent, style);
        viewModel = new HttpBodyEditorViewModel();
    }

    /**
     * @return JSON string format of the {@link HttpBodyContent}
     */
    public String getContentData() {
        return viewModel.getContentData();
    }
    
    public void setContentData(String data) {
        viewModel.setContentData(data);
    }

    /**
     * Initializes input of the editor.
     * 
     * @param httpBodyContent:
     * JSON string format of the {@link HttpBodyContent}
     */
    public abstract void setInput(String httpBodyContent);

    /**
     * Notifies the request service part update value of HTTP Header
     * "Content-Type" in HTTP Header table.
     * 
     * @return true if the Content-Type was updated. Otherwise, false.
     */
    public boolean isContentTypeUpdated() {
        return viewModel.isContentTypeUpdated();
    }

    public void setContentTypeUpdated(boolean contentTypeUpdated) {
        viewModel.setContentTypeUpdated(contentTypeUpdated);
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

    public HttpBodyEditorViewModel getViewModel() {
        return viewModel;
    }
    
    public String getContentType() {
        return viewModel.getContentType();
    }
}
