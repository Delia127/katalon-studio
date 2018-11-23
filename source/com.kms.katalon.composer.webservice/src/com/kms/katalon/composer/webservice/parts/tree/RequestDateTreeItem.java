package com.kms.katalon.composer.webservice.parts.tree;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.util.DateTimes;

public class RequestDateTreeItem implements IRequestHistoryItem {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private List<RequestHistoryTreeItem> items;

    public RequestDateTreeItem(Date date) {
        this.date = date;
    }

    @Override
    public String getName() {
        return DateTimes.format(date, DateTimeFormatter.ofPattern("MMMM dd").withZone(ZoneId.systemDefault()));
    }

    @Override
    public Image getImage() {
        return null;
    }

    public List<RequestHistoryTreeItem> getItems() {
        return items;
    }

    public void setItems(List<RequestHistoryTreeItem> items) {
        this.items = items;
    }
    
    public void addItem(RequestHistoryTreeItem historyItem) {
        items.add(historyItem);
    }

    @Override
    public List<IRequestHistoryItem> getChildren() {
        return new ArrayList<IRequestHistoryItem>(getItems());
    }

    @Override
    public boolean hasChildren() {
        return !getItems().isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestDateTreeItem other = (RequestDateTreeItem) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        return true;
    }
}
