package com.kms.katalon.composer.components.impl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.entity.file.FileEntity;

public class MenuUtils {
    private MenuUtils() {
        // Disable default constructor.
    }

    public static void createOpenTestArtifactsMenu(
            HashMap<FileEntity, SelectionAdapter> mapFileEntityToSelectionAdapter, Menu tableContextMenu) {
        MenuItem openMenuItem = new MenuItem(tableContextMenu, SWT.CASCADE);
        openMenuItem.setText(ComposerComponentsImplMessageConstants.MENU_OPEN);
        openMenuItem.setID(ControlUtils.MENU_OPEN_ID);
        Menu subMenu = new Menu(openMenuItem);
        HashMap<FileEntity, String> mapEntityToName = getListOfDifferentTestArtifactName(
                mapFileEntityToSelectionAdapter.keySet());
        for (Entry<FileEntity, String> entry : mapEntityToName.entrySet()) {
            FileEntity fileEntity = entry.getKey();
            ControlUtils.createSubMenuOpen(subMenu, fileEntity, mapFileEntityToSelectionAdapter.get(fileEntity),
                    entry.getValue());
        }
        openMenuItem.setMenu(subMenu);
    }

    /**
     * Get the hashmap with map each file entity with its name with the condition that if all the names
     * contains only the part of different path between the entities having the same names
     * Example :
     * NewFolder/TC01
     * NewFoler/Gmail1/TC02_VerifyComposeButtonAttributes
     * NewFolder/Gmail2/TC02_VerifyComposeButtonAttributes
     * --> TC01
     * Gmail1/TC02_VerifyComposeButtonAttributes
     * Gmail2/TC02_VerifyComposeButtonAttributes
     * 
     * @param entities : list of file entity that need to get name
     * @return hashmap with map each file entity with its name
     */
    private static HashMap<FileEntity, String> getListOfDifferentTestArtifactName(Set<FileEntity> entities) {
        HashMap<String, List<FileEntity>> mapNameEntity = new HashMap<>();
        HashMap<FileEntity, String> mapEntityToName = new HashMap<>();
        for (FileEntity fileEntity : entities) {
            String fileName = fileEntity.getName();
            if (!mapNameEntity.containsKey(fileName)) {
                List<FileEntity> list = new ArrayList<>();
                list.add(fileEntity);
                mapNameEntity.put(fileName, list);
                continue;
            }
            mapNameEntity.get(fileName).add(fileEntity);
        }
        for (Entry<String, List<FileEntity>> entry : mapNameEntity.entrySet()) {
            List<FileEntity> fileEntities = entry.getValue();
            if (fileEntities.size() == 1) {
                FileEntity entity = fileEntities.get(0);
                mapEntityToName.put(entity, entry.getKey());
                continue;
            }
            mapEntityToName.putAll(getRelativeIDofListSameNameFileEntity(fileEntities));
        }
        return mapEntityToName;
    }

    /**
     * Get the relative id of all the file entities that have the same name
     * 
     * @param list of file entities that have same name
     * @return hashmap with map each file entity with its relative id
     */
    private static HashMap<FileEntity, String> getRelativeIDofListSameNameFileEntity(List<FileEntity> fileEntities) {
        HashMap<FileEntity, String> mapEntityToName = new HashMap<>();
        for (FileEntity fileEntity : fileEntities) {
            String displayID = fileEntity.getIdForDisplay();
            mapEntityToName.put(fileEntity, displayID.substring(displayID.indexOf("/") + 1, displayID.length()));
        }
        return mapEntityToName;
    }

}
