package com.kms.katalon.controller;

import java.io.File;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.dal.IWindowsElementDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class WindowsElementController extends EntityController {

    private static WindowsElementController instance;

    private WindowsElementController() {
        super();
    }

    public static WindowsElementController getInstance() {
        if (instance == null) {
            instance = new WindowsElementController();
        }
        return instance;
    }

    public WindowsElementEntity getWindowsElementEntity(String id) throws DALException {
        return getWindowsElementDataProvider().get(id);
    }

    private IWindowsElementDataProvider getWindowsElementDataProvider() {
        return getDataProviderSetting().getWindowsElementDataProvider();
    }

    public WindowsElementEntity getWindowsElementByDisplayId(String windowsElementEntityDisplayId) throws DALException {
        if (windowsElementEntityDisplayId == null) {
            return null;
        }
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        String projectDir = currentProject.getFolderLocation();
        String id = projectDir + File.separator
                + windowsElementEntityDisplayId.replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator)
                + WindowsElementEntity.FILE_EXTENSION;
        return getWindowsElementDataProvider().get(id);
    }

    public WindowsElementEntity newWindowsElementEntity(FolderEntity parentFolder, String newName) throws DALException {
        return getWindowsElementDataProvider().add(parentFolder, newName);
    }

    public void updateWindowsElementEntity(WindowsElementEntity windowsElementEntity) throws DALException {
        getWindowsElementDataProvider().update(windowsElementEntity);
    }

    public void renameWindowsElementEntity(String newName, WindowsElementEntity windowsElementEntity)
            throws DALException {
        getWindowsElementDataProvider().rename(windowsElementEntity.getId(), newName);
    }

    public void deleteWindowsElementEntity(WindowsElementEntity windowsElementEntity) throws DALException {
        getWindowsElementDataProvider().delete(windowsElementEntity.getId());
    }

    public WindowsElementEntity moveWindowsElementEntity(WindowsElementEntity windowsElementEntity,
            FolderEntity newLocation) throws DALException {
        return getWindowsElementDataProvider().move(windowsElementEntity.getId(), newLocation);
    }

    public WindowsElementEntity copyWindowsElementEntity(WindowsElementEntity windowsElementEntity,
            FolderEntity newLocation) throws DALException {
        return getWindowsElementDataProvider().copy(windowsElementEntity.getId(), newLocation);
    }
}
