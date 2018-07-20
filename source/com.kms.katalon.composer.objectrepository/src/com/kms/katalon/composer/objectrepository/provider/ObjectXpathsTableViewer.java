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

    boolean isSelectedAll;
    
    private Table xpathTable;

    public ObjectXpathsTableViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        xpathTable = getTable();
        this.eventBroker = eventBroker;
    }

    public void setInput(List<WebElementXpathEntity> data) {
        this.data = data;
        super.setInput(data);

        isSelectedAll = true;
        for (Object object : data) {
        	WebElementXpathEntity pro = (WebElementXpathEntity) object;
            if (!pro.getIsSelected()) {
                isSelectedAll = false;
            }
        }
        // update image header of isSelected Column
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);

    }

    public void addRow(WebElementXpathEntity property) {
        data.add(property);
        refreshIsSelected();
        update(property, null);
        refresh();
        xpathTable.setSelection(data.size() - 1);
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void addRows(List<WebElementXpathEntity> props) {
        int lastIndex = data.size();
        data.addAll(props);
        refreshIsSelected();
        update(props, null);
        refresh();
        xpathTable.setSelection(lastIndex, data.size() - 1); 
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    
    public void addRowsWithPosition(List<ObjectXpathTableRow> props) {
        for (ObjectXpathTableRow row : props) {
            data.add(row.getPosition(), row.getWebElementXpathEntity());
        }
        refreshIsSelected();
        refresh();
        xpathTable.deselectAll();
        for (ObjectXpathTableRow row : props) {
            xpathTable.select(row.getPosition());
        }
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRows(List<WebElementXpathEntity> properties) {
        data.removeAll(properties);
        refreshIsSelected();
        refresh();
        xpathTable.deselectAll();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRow(WebElementXpathEntity property) {
        data.remove(property);
        refreshIsSelected();
        refresh();
        xpathTable.deselectAll();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void clear() {
        data.clear();
        refreshIsSelected();
        refresh();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void setSelectedAll() {
        isSelectedAll = !isSelectedAll;
        for (Object o : data) {
        	WebElementXpathEntity pro = (WebElementXpathEntity) o;
            pro.setIsSelected(isSelectedAll);
        }
        this.refresh();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);

    }

    public boolean getIsSelectedAll() {
        return isSelectedAll;
    }

    public void refreshIsSelected() {
        boolean check = true;
        for (Object object : data) {
        	WebElementXpathEntity pro = (WebElementXpathEntity) object;
            if (!pro.getIsSelected()) {
                check = false;
                break;
            }
        }

        if (check != isSelectedAll) {
            isSelectedAll = check;
            eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);
        }
    }

    public List<WebElementXpathEntity> getInput() {
        return data;
    }
}
