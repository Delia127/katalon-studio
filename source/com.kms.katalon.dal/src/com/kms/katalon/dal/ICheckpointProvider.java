package com.kms.katalon.dal;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public interface ICheckpointProvider {

    CheckpointEntity getById(String id) throws DALException;

    CheckpointEntity create(CheckpointEntity checkpoint) throws DALException;

    CheckpointEntity update(CheckpointEntity checkpoint) throws DALException;

    void delete(CheckpointEntity checkpoint) throws DALException;

    CheckpointEntity copy(CheckpointEntity checkpoint, FolderEntity destinationFolder) throws DALException;

    CheckpointEntity move(CheckpointEntity checkpoint, FolderEntity destinationFolder) throws DALException;

}
