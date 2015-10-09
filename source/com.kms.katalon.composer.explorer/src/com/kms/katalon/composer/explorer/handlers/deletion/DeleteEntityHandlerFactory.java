package com.kms.katalon.composer.explorer.handlers.deletion;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.composer.components.tree.ITreeEntity;

public class DeleteEntityHandlerFactory {

    private static DeleteEntityHandlerFactory _instance;
    
    //key is entity type name;
    private Map<String, IDeleteEntityHandler> lookup;
    
    private DeleteEntityHandlerFactory() {
         lookup = new HashMap<String, IDeleteEntityHandler>();
    }
    
    public static DeleteEntityHandlerFactory getInstance() {
        if (_instance == null) {
            _instance = new DeleteEntityHandlerFactory();
        }
        
        return _instance;
    }
    
    public void addContributor(IDeleteEntityHandler handler) {
        lookup.put(handler.entityType().getName(), handler);
    }
    
    public IDeleteEntityHandler getDeleteHandler(Class<? extends ITreeEntity> entityType) {
        return lookup.get(entityType.getName());
    }
    
}
