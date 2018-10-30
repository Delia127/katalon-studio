package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.IEntityDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class EntityFileServiceDataProvider implements IEntityDataProvider {

    @Override
    public boolean update(FileEntity entity) throws DALException {
        try {
            return EntityFileServiceManager.update(entity);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

	@Override
	public String toXmlString(Object entity) throws DALException {
		 try {
	            return EntityFileServiceManager.toXmlString(entity);
	        } catch (Exception e) {
	            throw new DALException(e);
	        }
	}

	@Override
<<<<<<< HEAD
	public <T> T toEntity(String xmlString, Class<T> clazz) throws DALException {
		 try {
	            return EntityFileServiceManager.toEntity(xmlString, clazz);
=======
	public Object toObject(String xmlString) throws DALException {
		 try {
	            return EntityFileServiceManager.toObject(xmlString);
>>>>>>> origin/KAT-3778
	        } catch (Exception e) {
	            throw new DALException(e);
	        }
	}

}
