package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseEntityUtil {
	public static void copyTestCaseProperties(TestCaseEntity src, TestCaseEntity des) {
		des.setParentFolder(src.getParentFolder());
		des.setProject(src.getProject());

		des.setName(src.getName());
		des.setComment(src.getComment());
		des.setTag(src.getTag());
		des.setDescription(src.getDescription());

		des.getDataFileLocations().clear();
		des.getDataFiles().clear();
		for (DataFileEntity dataFile : src.getDataFiles()) {
			des.getDataFiles().add(dataFile);
			des.getDataFileLocations().add(dataFile.getRelativePath());
		}

		des.getVariables().clear();
		for (VariableEntity variable : src.getVariables()) {
			des.getVariables().add(variable);
		}

		des.getIntegratedEntities().clear();
		for (IntegratedEntity integratedEntity : src.getIntegratedEntities()) {
			des.getIntegratedEntities().add(integratedEntity);
		}
	}

	public static boolean isClassChildOf(String parentClassName, String childClassName) {
		try {
			return Class.forName(parentClassName).isAssignableFrom(Class.forName(childClassName));
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	public static List<TestCaseEntity> getTestCasesFromFolderTree(FolderTreeEntity folderTree) {
		List<TestCaseEntity> lstTestCases = new ArrayList<TestCaseEntity>();
		try {
			for (Object child : folderTree.getChildren()) {
				if (child instanceof TestCaseTreeEntity) {
					lstTestCases.add((TestCaseEntity) ((TestCaseTreeEntity) child).getObject());
				} else if (child instanceof FolderTreeEntity) {
					lstTestCases.addAll(getTestCasesFromFolderTree((FolderTreeEntity) child));
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return lstTestCases;
	}

	public static List<VariableEntity> getCallTestCaseVariables(ArgumentListExpression argumentListExpression)
			throws Exception {
		if (!TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue()
				&& TestCasePreferenceDefaultValueInitializer.isSetAutoExportVariables()) {
			MethodCallExpression methodCallExpression = (MethodCallExpression) argumentListExpression.getExpression(0);
			ConstantExpression constantExpression = (ConstantExpression) AstTreeTableInputUtil
					.getCallTestCaseParam(methodCallExpression);
			String calledTestCaseId = constantExpression.getText();
			MapExpression mapExpression = (MapExpression) argumentListExpression.getExpression(1);
			List<VariableEntity> variableEntities = new ArrayList<VariableEntity>();
			for (MapEntryExpression entryExpression : mapExpression.getMapEntryExpressions()) {
				String variableName = entryExpression.getKeyExpression().getText();

				VariableEntity variableInCalledTestCase = TestCaseController.getInstance().getVariable(
						calledTestCaseId, variableName);

				VariableEntity newVariable = new VariableEntity();
				newVariable.setName(variableName);
				newVariable.setDefaultValue(variableInCalledTestCase.getDefaultValue());

				variableEntities.add(newVariable);
			}

			return variableEntities;
		} else {
			return Collections.emptyList();
		}
	}
}
