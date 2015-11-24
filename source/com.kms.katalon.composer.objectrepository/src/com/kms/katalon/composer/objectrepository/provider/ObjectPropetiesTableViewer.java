package com.kms.katalon.composer.objectrepository.provider;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropetiesTableViewer extends TableViewer {

    private IEventBroker eventBroker;
    
    private List<WebElementPropertyEntity> data;
    boolean isSelectedAll;
    
    public ObjectPropetiesTableViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        this.eventBroker = eventBroker;
    }
    
    public void setInput(List<WebElementPropertyEntity> data) {
        this.data = data;
        super.setInput(data);
        
        isSelectedAll = true;
        for (Object object : data) {
            WebElementPropertyEntity pro = (WebElementPropertyEntity) object;
            if (!pro.getIsSelected()) isSelectedAll = false;
        }
        //update image header of isSelected Column
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_IS_SELECTED_COLUMN_HEADER, this);
        
    }
    
    public void addRow(WebElementPropertyEntity property) {
        data.add(property);
        refreshIsSelected();
        this.update(property, null);
        this.getTable().select(data.size() - 1);
        this.refresh();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    
    public void deleteRows(List<WebElementPropertyEntity> properties) {
        for (WebElementPropertyEntity pro : properties) {
            data.remove(pro);
        }
        refreshIsSelected();
        this.refresh();
        eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this);
    }
    
    public void clear() {
        data.clear();
        refreshIsSelected();
        this.refresh();
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
