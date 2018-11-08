package com.kms.katalon.composer.testcase.handlers;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public abstract class AbstractAddKeywordsHandler extends AbstractHandler {
    protected String testCaseCompositePartId;

    @Override
    public boolean canExecute() {
        testCaseCompositePartId = getPartService().getActivePart().getElementId();
        return EntityPartUtil.getEntityByPartId(testCaseCompositePartId) instanceof TestCaseEntity;
    }

    @Override
    public void execute() {
        eventBroker.post(EventConstants.TESTCASE_ADD_STEP,
                new Object[] { testCaseCompositePartId, TreeTableMenuItemConstants.getBuildInKeywordID(getKeywordAliasName()) });
    }

    protected abstract String getKeywordAliasName();
}
