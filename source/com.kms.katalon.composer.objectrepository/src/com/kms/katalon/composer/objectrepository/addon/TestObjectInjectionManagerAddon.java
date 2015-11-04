package com.kms.katalon.composer.objectrepository.addon;
import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.objectrepository.handler.AddToObjectSpyHandler;
import com.kms.katalon.composer.objectrepository.handler.DeleteTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handler.OpenTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handler.RefreshTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handler.RenameTestObjectHandler;
import com.kms.katalon.composer.objectrepository.handler.SpyObjectHandler;

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
