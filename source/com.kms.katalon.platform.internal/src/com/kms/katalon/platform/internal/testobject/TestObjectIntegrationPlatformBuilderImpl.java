package com.kms.katalon.platform.internal.testobject;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.objectrepository.integration.TestObjectIntegrationPlatformBuilder;
import com.kms.katalon.composer.objectrepository.integration.TestObjectIntegrationViewBuilder;

public class TestObjectIntegrationPlatformBuilderImpl implements TestObjectIntegrationPlatformBuilder {
    @Inject
    private IEclipseContext context;
    
    private TestObjectIntegrationViewBuilder getViewerBuilder(TestObjectIntegrationViewDescription desc){
    	
    }
    
	@Override
	public List<TestObjectIntegrationViewBuilder> getBuilders() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
