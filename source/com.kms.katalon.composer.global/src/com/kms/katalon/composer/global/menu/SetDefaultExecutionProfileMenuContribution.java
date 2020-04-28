package com.kms.katalon.composer.global.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.global.constants.ComposerGlobalMessageConstants;
import com.kms.katalon.composer.global.handler.ExecutionProfileTreeRootCatcher;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class SetDefaultExecutionProfileMenuContribution extends ExecutionProfileTreeRootCatcher {

    private static final String ITEM_LBL_SET_DEFAULT_EXECUTION_PROFILE = ComposerGlobalMessageConstants.ITEM_LBL_SET_DEFAULT_EXECUTION_PROFILE;

    private static final String SET_DEFAULT_EXECUTION_PROFILE_COMMAND_ID = "com.kms.katalon.composer.global.command.setDefaultExecutionProfile";
    
    @Inject
    private ECommandService commandService;
    
    @Inject
    private ESelectionService selectionService;
    
    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
                if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {
                    selectionService.setSelection(selection);
                } else {
                    selectionService.setSelection(null);
                }
            }
        });
    }
    
    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            ITreeEntity selectedTree = (ITreeEntity) getFirstSelection(selectionService);
            if (selectedTree == null || !(selectedTree instanceof ProfileTreeEntity)) {
                return;
            }

            MHandledMenuItem setDefaultProfileMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(SET_DEFAULT_EXECUTION_PROFILE_COMMAND_ID, null),
                    ITEM_LBL_SET_DEFAULT_EXECUTION_PROFILE, ConstantsHelper.getApplicationURI());
            if (setDefaultProfileMenuItem != null) {
                menuItems.add(MMenuFactory.INSTANCE.createMenuSeparator());
                menuItems.add(setDefaultProfileMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
