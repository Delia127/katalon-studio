package com.kms.katalon.composer.global.provider;

import java.util.Map;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.entity.global.GlobalVariableEntity;

public interface TableViewerProvider {
    TableViewer getTableViewer();

    void markDirty();

    Map<GlobalVariableEntity, String> getNeedToUpdateVariables();

    void performOperation(AbstractOperation operation);
}
