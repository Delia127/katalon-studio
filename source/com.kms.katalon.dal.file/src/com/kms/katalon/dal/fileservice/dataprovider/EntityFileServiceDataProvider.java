package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.IEntityDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;

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
	public <T> T toEntity(String xmlString, Class<T> clazz) throws DALException {
		 try {
	            return EntityFileServiceManager.toEntity(xmlString, clazz);
	        } catch (Exception e) {
	            throw new DALException(e);
	        }
	}

}
