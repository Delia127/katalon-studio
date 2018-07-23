package com.kms.katalon.composer.objectrepository.provider;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.view.ObjectXpathTableRow;
import com.kms.katalon.entity.repository.WebElementXpathEntity;

public class ObjectXpathsTableViewer extends TableViewer {

    private IEventBroker eventBroker;

    private List<WebElementXpathEntity> data;
   
    private Table xpathTable;

    public ObjectXpathsTableViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        xpathTable = getTable();
        this.eventBroker = eventBroker;
    }

    public void setInput(List<WebElementXpathEntity> data) {
        this.data = data;
        super.setInput(data);

        // update image header of isSelected Column
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);

    }

    public void addRow(WebElementXpathEntity xpath) {
        data.add(xpath);
        update(xpath, null);
        refresh();
        xpathTable.setSelection(data.size() - 1);
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void addRows(List<WebElementXpathEntity> xpaths) {
        int lastIndex = data.size();
        data.addAll(xpaths);
        update(xpaths, null);
        refresh();
        xpathTable.setSelection(lastIndex, data.size() - 1); 
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    
    public void addRowsWithPosition(List<ObjectXpathTableRow> xpaths) {
        for (ObjectXpathTableRow row : xpaths) {
            data.add(row.getPosition(), row.getWebElementXpathEntity());
        }
        refresh();
        xpathTable.deselectAll();
        for (ObjectXpathTableRow row : xpaths) {
            xpathTable.select(row.getPosition());
        }
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRows(List<WebElementXpathEntity> properties) {
        data.removeAll(properties);
        refresh();
        xpathTable.deselectAll();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRow(WebElementXpathEntity property) {
        data.remove(property);
        refresh();
        xpathTable.deselectAll();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void clear() {
        data.clear();
        refresh();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    

    public List<WebElementXpathEntity> getInput() {
        return data;
    }
}
