package com.kms.katalon.composer.components.impl.handler;

import static org.eclipse.ui.handlers.HandlerUtil.getActivePartId;

import org.apache.commons.lang.StringUtils;

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

}
