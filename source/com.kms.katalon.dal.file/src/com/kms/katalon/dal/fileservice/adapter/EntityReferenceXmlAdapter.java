package com.kms.katalon.dal.fileservice.adapter;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.entity.Entity;

public abstract class EntityReferenceXmlAdapter<K, V extends Entity> extends XmlAdapter<K, V> {

    protected EntityService getEntityService() throws DALException {
        try {
            return EntityService.getInstance();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    /**
     * Get {@link Entity} by <code>referenceId</code>.
     * </p>
     * Please note that if the entity is unloaded, JAXB will unmarshal entity's file but the problem is we are
     * using it to parse referrer file and an {@link Unmarshaller} can only parse one file at the moment. Therefore,
     * we need to create new instance of {@link Unmarshaller} to parse the reference.
     */
    @Override
    public V unmarshal(K referenceId) throws DALException {
        EntityService entityService = getEntityService();
        Unmarshaller oldUnmarshaller = entityService.getUnmarshaller();
        try {
            entityService.changeUnmarshaller(entityService.newUnmarshaller());
            return safelyUnmarshal(referenceId);
        } finally {
            entityService.changeUnmarshaller(oldUnmarshaller);
        }
    }

    protected abstract V safelyUnmarshal(K referenceId) throws DALException;

}
