package com.kms.katalon.composer.global.provider;

import org.eclipse.jface.viewers.TableViewer;

public interface TableViewerProvider {
    TableViewer getTableViewer();
    
    void markDirty();

}
