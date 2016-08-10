package com.kms.katalon.composer.components.impl.handler;

import static org.eclipse.ui.handlers.HandlerUtil.getActivePartId;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.kms.katalon.constants.IdConstants;

public abstract class CommonExplorerHandler extends AbstractHandler {

    protected boolean isExplorerPartNotActive() {
        if (getExecutionEvent() == null) {
            return false;
        }
        return !StringUtils.equals(IdConstants.EXPLORER_PART_ID, getActivePartId(getExecutionEvent()));
    }

    /**
     * Get explorer selected Tree Entities.
     * 
     * @return Object[] selected Tree Entities. NULL will not be returned.
     */
    protected final Object[] getExplorerSelection() {
        Object o = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (o == null || !o.getClass().isArray()) {
            return new Object[0];
        }

        return (Object[]) o;
    }

    protected boolean isExplorerPartActive() {
        MPart activePart = getService(EPartService.class).getActivePart();
        return activePart != null && IdConstants.EXPLORER_PART_ID.equals(activePart.getElementId());
    }

    @SuppressWarnings("unchecked")
    protected final <T> List<T> getElementSelection(Class<? extends T> elementType) {
        List<T> list = new ArrayList<T>();
        for (Object o : getExplorerSelection()) {
            if (elementType.isInstance(o)) {
                list.add((T) o);
            }
        }
        return list;
    }
}
