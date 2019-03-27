package com.kms.katalon.platform.internal.controller;

import java.util.ArrayList;
import java.util.List;
import com.katalon.platform.api.controller.TestSuiteCollectionController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestSuiteCollectionEntity;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.platform.internal.entity.TestSuiteCollectionEntityImpl;

public class TestSuiteCollectionControllerImpl implements TestSuiteCollectionController {

	private static com.kms.katalon.controller.TestSuiteCollectionController testSuiteCollectionController = com.kms.katalon.controller.TestSuiteCollectionController.getInstance();
	
	@Override
	public List<TestSuiteCollectionEntity> getAllTestSuiteCollectionsInProject(ProjectEntity arg0)
			throws ResourceException {
		List<TestSuiteCollectionEntity> retEntities = new ArrayList<>();
		try {
			com.kms.katalon.entity.project.ProjectEntity projectEntity 
				= ProjectController.getInstance().getProject(arg0.getId());
			List<com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity> entities = 
					testSuiteCollectionController.getAllTestSuiteCollectionsInProject(projectEntity);
		entities.forEach(entity -> {
			retEntities.add(new TestSuiteCollectionEntityImpl(entity));
		});
		} catch (ControllerException | DALException e) {
			throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
		}
		return retEntities;
	}

	@Override
	public TestSuiteCollectionEntity addTestSuiteToTestSuiteCollection(ProjectEntity arg0, String arg1, String arg2)
			throws ResourceException {
		com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity entity;
		try {
			entity = com.kms.katalon.controller.TestSuiteCollectionController.getInstance().getTestSuiteCollection(arg1);
			TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuite(arg2);
			TestSuiteRunConfiguration newTestSuiteRunConfig = TestSuiteRunConfiguration.newInstance(
					testSuite, TestExecutionGroupCollector.getInstance().getDefaultConfiguration(
                            ProjectController.getInstance().getCurrentProject()));
			entity.getTestSuiteRunConfigurations().add(newTestSuiteRunConfig);
			return new TestSuiteCollectionEntityImpl(entity);
		} catch (Exception e) {
			throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
		}
	}

	@Override
	public TestSuiteCollectionEntity getTestSuiteCollection(ProjectEntity arg0, String arg1) throws ResourceException {
		com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity entity;
		try {
			entity = com.kms.katalon.controller.TestSuiteCollectionController.getInstance().getTestSuiteCollection(arg1);
			return new TestSuiteCollectionEntityImpl(entity);
		} catch (DALException e) {
			throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
		}
	}
}
