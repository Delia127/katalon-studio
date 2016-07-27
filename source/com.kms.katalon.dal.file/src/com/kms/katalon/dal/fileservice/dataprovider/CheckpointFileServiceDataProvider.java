package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.ICheckpointProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.manager.CheckpointFileServiceManager;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class CheckpointFileServiceDataProvider implements ICheckpointProvider {

    @Override
    public CheckpointEntity getById(String id) throws DALException {
        return CheckpointFileServiceManager.getById(id);
    }

    @Override
    public CheckpointEntity create(CheckpointEntity checkpoint) throws DALException {
        return CheckpointFileServiceManager.create(checkpoint);
    }

    @Override
    public CheckpointEntity update(CheckpointEntity checkpoint) throws DALException {
        return CheckpointFileServiceManager.update(checkpoint);
    }

    @Override
    public void delete(CheckpointEntity checkpoint) throws DALException {
        CheckpointFileServiceManager.delete(checkpoint);
    }

    @Override
    public CheckpointEntity copy(CheckpointEntity checkpoint, FolderEntity destinationFolder) throws DALException {
        return CheckpointFileServiceManager.copy(checkpoint, destinationFolder);
    }

    @Override
    public CheckpointEntity move(CheckpointEntity checkpoint, FolderEntity destinationFolder) throws DALException {
        return CheckpointFileServiceManager.move(checkpoint, destinationFolder);
    }

}
