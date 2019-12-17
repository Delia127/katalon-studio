package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.util.List;

import com.kms.katalon.dal.IWindowsElementDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

public class WindowsElementFileServiceDataProvider implements IWindowsElementDataProvider {
    
    private EntityService getEntityService() throws DALException {
        try {
            return EntityService.getInstance();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public WindowsElementEntity get(String id) throws DALException {
        try {
            WindowsElementEntity testSuiteCollection = (WindowsElementEntity) EntityFileServiceManager
                    .get(new File(id));
            if (testSuiteCollection != null) {
                testSuiteCollection.setProject(DataProviderState.getInstance().getCurrentProject());
            }
            return testSuiteCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public List<WindowsElementEntity> getChildren(FolderEntity parentFolder) throws DALException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String id) throws DALException {
        WindowsElementEntity windowsElementEntity = get(id);
        if (windowsElementEntity == null) {
            return;
        }

        try {
            EntityService.getInstance().deleteEntity(windowsElementEntity);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public WindowsElementEntity add(FolderEntity parentFolder, String newName) throws DALException {
        try {
            getEntityService().validateName(newName);
            checkDuplicate(parentFolder, newName);

            WindowsElementEntity windowsElementEntity = new WindowsElementEntity();
            windowsElementEntity.setParentFolder(parentFolder);
            windowsElementEntity.setProject(parentFolder.getProject());
            windowsElementEntity.setName(newName);

            getEntityService().saveEntity(windowsElementEntity);

            return windowsElementEntity;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    private void checkDuplicate(FolderEntity parentFolder, String newName) throws DALException {
        if (!newName.equals(getEntityService().getAvailableName(parentFolder.getId(), newName, true))) {
            throw new InvalidNameException(StringConstants.DP_EXC_NAME_ALREADY_EXISTED);
        }
    }

    @Override
    public WindowsElementEntity rename(String id, String newName) throws DALException {
        try {
            getEntityService().validateName(newName);

            WindowsElementEntity currentWindowsElementEntity = get(id);
            if (currentWindowsElementEntity == null) {
                return null;
            }

            checkDuplicate(currentWindowsElementEntity.getParentFolder(), newName);

            getEntityService().deleteEntity(currentWindowsElementEntity);

            currentWindowsElementEntity.setName(newName);

            getEntityService().saveEntity(currentWindowsElementEntity);

            return currentWindowsElementEntity;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public WindowsElementEntity update(WindowsElementEntity windowsElement) throws DALException {
        try {
            getEntityService().saveEntity(windowsElement);
            return get(windowsElement.getId());
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public WindowsElementEntity move(String id, FolderEntity newLocation) throws DALException {
        WindowsElementEntity windowsElementEntity = get(id);

        if (windowsElementEntity.getParentFolder().equals(newLocation)) {
            return null;
        }

        String oldId = windowsElementEntity.getIdForDisplay();
        try {
            WindowsElementEntity movedElement = EntityFileServiceManager.move(windowsElementEntity, newLocation);
            TestArtifactScriptRefactor.createForWindowsObjectEntity(oldId)
                    .updateReferenceForProject(movedElement.getIdForDisplay(), windowsElementEntity.getProject());
            return movedElement;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public WindowsElementEntity copy(String id, FolderEntity location) throws DALException {
        WindowsElementEntity windowsElementEntity = get(id);

        try {
            return EntityFileServiceManager.copy(windowsElementEntity, location);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

}
