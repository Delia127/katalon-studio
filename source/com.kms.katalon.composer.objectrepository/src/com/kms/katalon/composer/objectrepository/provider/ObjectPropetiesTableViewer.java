package com.kms.katalon.composer.objectrepository.provider;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.view.ObjectPropertyTableRow;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropetiesTableViewer extends TableViewer {

    private IEventBroker eventBroker;

    private List<WebElementPropertyEntity> data;

    boolean isSelectedAll;
    
    private Table propertyTable;

    public ObjectPropetiesTableViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        propertyTable = getTable();
        this.eventBroker = eventBroker;
    }

    public void setInput(List<WebElementPropertyEntity> data) {
        this.data = data;
        super.setInput(data);

        isSelectedAll = true;
        for (Object object : data) {
            WebElementPropertyEntity pro = (WebElementPropertyEntity) object;
            if (!pro.getIsSelected()) {
                isSelectedAll = false;
            }
        }
        // update image header of isSelected Column
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);

    }

    public void addRow(WebElementPropertyEntity property) {
        data.add(property);
        refreshIsSelected();
        update(property, null);
        refresh();
        propertyTable.setSelection(data.size() - 1);
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void addRows(List<WebElementPropertyEntity> props) {
        int lastIndex = data.size();
        data.addAll(props);
        refreshIsSelected();
        update(props, null);
        refresh();
        propertyTable.setSelection(lastIndex, data.size() - 1); 
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    
    public void addRowsWithPosition(List<ObjectPropertyTableRow> props) {
        for (ObjectPropertyTableRow row : props) {
            data.add(row.getPosition(), row.getWebElementPropertyEntity());
        }
        refreshIsSelected();
        refresh();
        propertyTable.deselectAll();
        for (ObjectPropertyTableRow row : props) {
            propertyTable.select(row.getPosition());
        }
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRows(List<WebElementPropertyEntity> properties) {
        data.removeAll(properties);
        refreshIsSelected();
        refresh();
        propertyTable.deselectAll();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }

    public void deleteRow(WebElementPropertyEntity property) {
        data.remove(property);
        refreshIsSelected();
        refresh();
        propertyTable.deselectAll();
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
            WebElementPropertyEntity pro = (WebElementPropertyEntity) o;
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
            WebElementPropertyEntity pro = (WebElementPropertyEntity) object;
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

    public List<WebElementPropertyEntity> getInput() {
        return data;
    }
}
