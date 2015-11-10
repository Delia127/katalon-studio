package com.kms.katalon.composer.webservice.support;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kms.katalon.composer.webservice.editors.StringComboBoxCellEditor;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyNameEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private MDirtyable dirtyable;

    private boolean isHeaderField = false;

    /**
     * List of HTTP header request fields (standard and non-standard)
     * 
     * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_header_fields">List of HTTP header fields</a>
     */
    public static final String[] headerRequestFieldName = new String[] { "Accept", "Accept-Charset", "Accept-Encoding",
            "Accept-Language", "Authorization", "Cache-Control", "Connection", "Content-Length", "Content-Type",
            "Cookie", "DNT", "Date", "Expect", "From", "Front-End-Https", "Host", "If-Match", "If-Modified-Since",
            "If-None-Match", "If-Range", "If-Unmodified-Since", "Max-Forwards", "Origin", "Pragma",
            "Proxy-Authorization", "Proxy-Connection", "Range", "Referer", "TE", "Upgrade", "User-Agent", "Via",
            "Warning", "X-ATT-DeviceId", "X-Csrf-Token", "X-Forwarded-For", "X-Forwarded-Host", "X-Forwarded-Proto",
            "X-Http-Method-Override", "X-Requested-With", "X-UIDH", "X-Wap-Profile" };

    public PropertyNameEditingSupport(TableViewer viewer, MDirtyable dirtyable) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
    }

    public PropertyNameEditingSupport(TableViewer viewer, MDirtyable dirtyable, boolean isHeaderField) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
        this.isHeaderField = isHeaderField;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (isHeaderField) {
            return new StringComboBoxCellEditor(viewer.getTable(), headerRequestFieldName);
        }
        return new TextCellEditor(viewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            return property.getName();
        }
        return "";
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getName())) {
                property.setName((String) value);
                if (this.dirtyable != null) this.dirtyable.setDirty(true);
                this.viewer.update(element, null);
            }
        }
    }

}