package com.kms.katalon.composer.folder.handlers.deletion;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class DeleteFolderHandlerFactory {
    private static DeleteFolderHandlerFactory _instance;

    // key is entity type name;
    private Map<FolderType, IDeleteFolderHandler> lookup;

    private DeleteFolderHandlerFactory() {
        lookup = new HashMap<FolderType, IDeleteFolderHandler>();
    }

    public static DeleteFolderHandlerFactory getInstance() {
        if (_instance == null) {
            _instance = new DeleteFolderHandlerFactory();
        }

        return _instance;
    }

    public void addContributor(IDeleteFolderHandler handler) {
        lookup.put(handler.getFolderType(), handler);
    }

    public IDeleteFolderHandler getDeleteHandler(FolderType folderType) {
        return lookup.get(folderType);
    }
}
