package com.kms.katalon.composer.objectrepository.addons;
import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.objectrepository.handlers.AddToObjectSpyHandler;
import com.kms.katalon.composer.objectrepository.handlers.DeleteTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handlers.OpenTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handlers.RefreshTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handlers.RenameTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handlers.SpyObjectHandler;

public class TestObjectInjectionManagerAddon {
    
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenTestObjectHandler.class, context);
        ContextInjectionFactory.make(DeleteTestObjectHandler.class, context);
        ContextInjectionFactory.make(RenameTestObjectHandler.class, context);
        ContextInjectionFactory.make(RefreshTestObjectHandler.class, context);
        ContextInjectionFactory.make(SpyObjectHandler.class, context);
        ContextInjectionFactory.make(AddToObjectSpyHandler.class, context);
	}
}
