package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NullArgumentException;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.NullAttributeException;
import com.kms.katalon.dal.fileservice.EntityCache;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

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
            String oldLocation = entityCache.getKey(checkpoint);
            if (entityCache.contains(checkpoint) && !checkpoint.getLocation().equals(oldLocation)) {
                entityCache.remove(checkpoint, true);

                // update checkpoint's references
                ProjectEntity project = checkpoint.getProject();
                String oldRelativeLocation = oldLocation.substring(new File(project.getLocation()).getParent().length() + 1);
                String oldCheckpointId = FilenameUtils.removeExtension(oldRelativeLocation)
                        .replace(File.separator, "/");
                TestArtifactScriptRefactor.createForCheckpointEntity(oldCheckpointId).updateReferenceForProject(
                        checkpoint.getIdForDisplay(), project);
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
            String oldCheckpointId = checkpoint.getIdForDisplay();
            FolderEntity originParentFolder = checkpoint.getParentFolder();
            CheckpointEntity movedCheckpoint = EntityFileServiceManager.move(checkpoint, destinationFolder);

            // refresh parent folder
            FolderFileServiceManager.refreshFolder(originParentFolder);
            FolderFileServiceManager.refreshFolder(destinationFolder);

            if (movedCheckpoint != null) {
                // update checkpoint references
                TestArtifactScriptRefactor.createForCheckpointEntity(oldCheckpointId).updateReferenceForProject(
                        movedCheckpoint.getIdForDisplay(), movedCheckpoint.getProject());
            }
            return movedCheckpoint;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static void updateFolderCheckpointReferences(FolderEntity checkpointFolder, String oldDisplayedId)
            throws CoreException, IOException {
        TestArtifactScriptRefactor.createForCheckpointEntity(oldDisplayedId).updateReferenceForProject(
                checkpointFolder.getIdForDisplay(), checkpointFolder.getProject());
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
