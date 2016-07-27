package com.kms.katalon.dal.fileservice.manager;

import java.io.File;

import org.apache.commons.lang.NullArgumentException;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.NullAttributeException;
import com.kms.katalon.dal.fileservice.EntityCache;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class CheckpointFileServiceManager {

    public static CheckpointEntity getById(String id) throws DALException {
        try {
            FileEntity entity = EntityFileServiceManager.get(new File(id));
            if (entity instanceof CheckpointEntity) {
                return (CheckpointEntity) entity;
            }
            return null;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static CheckpointEntity create(CheckpointEntity checkpoint) throws DALException {
        validateCheckpoint(checkpoint);
        try {
            // Validate name
            getEntityService().validateName(checkpoint.getName());
            getEntityService().saveEntity(checkpoint);
            FolderFileServiceManager.refreshFolder(checkpoint.getParentFolder());
            return checkpoint;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static CheckpointEntity update(CheckpointEntity checkpoint) throws DALException {
        validateCheckpoint(checkpoint);

        try {
            // Validate name
            getEntityService().validateName(checkpoint.getName());

            // Clean up cache if entity exists
            EntityCache entityCache = getEntityService().getEntityCache();
            if (entityCache.contains(checkpoint) && !checkpoint.getLocation().equals(entityCache.getKey(checkpoint))) {
                entityCache.remove(checkpoint, true);
            }

            // Do save
            getEntityService().saveEntity(checkpoint);

            // Refresh changes
            FolderFileServiceManager.refreshFolder(checkpoint.getParentFolder());

            return checkpoint;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static void delete(CheckpointEntity checkpoint) throws DALException {
        if (checkpoint == null) {
            return;
        }

        try {
            EntityFileServiceManager.delete(checkpoint);
            FolderFileServiceManager.refreshFolder(checkpoint.getParentFolder());
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static CheckpointEntity copy(CheckpointEntity checkpoint, FolderEntity destinationFolder)
            throws DALException {
        try {
            CheckpointEntity copiedCheckpoint = EntityFileServiceManager.copy(checkpoint, destinationFolder);
            FolderFileServiceManager.refreshFolder(destinationFolder);
            return copiedCheckpoint;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static CheckpointEntity move(CheckpointEntity checkpoint, FolderEntity destinationFolder)
            throws DALException {
        try {
            FolderEntity originParentFolder = checkpoint.getParentFolder();
            CheckpointEntity movedCheckpoint = EntityFileServiceManager.move(checkpoint, destinationFolder);
            FolderFileServiceManager.refreshFolder(originParentFolder);
            FolderFileServiceManager.refreshFolder(destinationFolder);
            return movedCheckpoint;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    private static EntityService getEntityService() throws Exception {
        return EntityService.getInstance();
    }

    private static void validateCheckpoint(CheckpointEntity checkpoint) throws DALException {
        try {
            if (checkpoint == null) {
                throw new NullArgumentException(StringConstants.MNG_EXC_CHECKPOINT_IS_NULL);
            }
            if (checkpoint.getProject() == null) {
                throw new NullAttributeException(StringConstants.MNG_EXC_PROJECT_IS_NULL);
            }
            if (checkpoint.getParentFolder() == null) {
                throw new NullAttributeException(StringConstants.MNG_EXC_PARENT_FOLDER_IS_NULL);
            }
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

}
