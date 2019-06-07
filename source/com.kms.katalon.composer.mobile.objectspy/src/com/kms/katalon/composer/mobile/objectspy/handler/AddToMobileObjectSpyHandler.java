package com.kms.katalon.composer.mobile.objectspy.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.AddTestObjectHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class AddToMobileObjectSpyHandler extends AddTestObjectHandler {

    @Override
    public void execute() {
        List<WebElementEntity> webElements = new ArrayList<>();
        ObjectRepositoryController objectController = ObjectRepositoryController.getInstance();
        for (ITreeEntity treeElement : getElementSelection(ITreeEntity.class)) {
            try {
                Object entityObject = treeElement.getObject();
                if (entityObject instanceof FolderEntity) {
                    webElements.addAll(objectController.getAllDescendantWebElements((FolderEntity) entityObject));
                }

                if (entityObject instanceof WebElementEntity) {
                    webElements.add((WebElementEntity) entityObject);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        MobileObjectSpyDialog dialog = MobileObjectSpyDialog.getInstance();
        if (dialog != null) {
            dialog.addElements(webElements);
        } else {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Please open Mobile Spy first");
        }
    }
}
