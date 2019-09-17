package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.dialogs.ApplyingEditingValue;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.dialogs.EncryptedTextDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BinaryCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BooleanCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BooleanConstantComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.CheckpointSelectionMethodCallBuilderDialog;
import com.kms.katalon.composer.testcase.ast.editors.ClosureInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ClosureListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.EnumPropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.GlobalVariablePropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.KeyInputComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.KeysInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MapInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MethodCallInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.NumberConstantCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.PropertyInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.RangeInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.StringConstantCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestCaseSelectionMethodCallBuilderDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestDataSelectionMethodCallBuilderDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestDataValueCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ThrowableInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.VariableComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.WindowsTestObjectCellEditor;
import com.kms.katalon.composer.testcase.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

/**
 * Utility class to handle changing value for ast nodes
 *
 */
public class AstValueUtil {
    public static InputValueType getTypeValue(Object object) {
        if (!(object instanceof ASTNodeWrapper)) {
            return null;
        }
        ASTNodeWrapper astNode = (ASTNodeWrapper) object;
        return getTypeValueFromASTNode(astNode);
    }

    public static InputValueType getTypeValueFromASTNode(ASTNodeWrapper astNode) {
        return AstInputValueTypeProvider.getInputValueTypeForASTNode(astNode);
    }

    public static CellEditor getCellEditorForKeysExpression(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new KeysInputCellEditor(parent, methodCallExpressionWrapper.getObjectExpressionAsString());
    }

    public static CellEditor getCellEditorForKeyExpression(Composite parent) {
        return new KeyInputComboBoxCellEditor(parent);
    }

    public static CellEditor getCellEditorForConstructorCallExpression(Composite parent,
            ConstructorCallExpressionWrapper contructorCallExpressionWrapper) {
        Class<?> throwableClass = AstKeywordsInputUtil.loadType(contructorCallExpressionWrapper.getType().getName(),
                contructorCallExpressionWrapper.getScriptClass());
        if (Throwable.class.isAssignableFrom(throwableClass)) {
            return getCellEditorForThrowable(parent, contructorCallExpressionWrapper);
        }
        return null;
    }

    public static CellEditor getCellEditorForThrowable(Composite parent,
            ConstructorCallExpressionWrapper contructorCallExpressionWrapper) {
        return new ThrowableInputCellEditor(parent, contructorCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForClassExpression(Composite parent,
            ClassExpressionWrapper classExpressionWrapper) {
        return new TypeSelectionDialogCellEditor(parent, classExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForPropertyExpression(Composite parent,
            PropertyExpressionWrapper propertyExpressionWrapper) {
        return new PropertyInputCellEditor(parent, propertyExpressionWrapper.getText());
    }

    public static CellEditor getNewCellEditorForFailureHandling(Composite parent) {
        return new EnumPropertyComboBoxCellEditor(parent, FailureHandling.class);
    }

    public static CellEditor getCellEditorForBinaryExpression(Composite parent,
            BinaryExpressionWrapper binaryExpressionWrapper) {
        return new BinaryCellEditor(parent, binaryExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForMethodCallExpression(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        if (methodCallExpressionWrapper.isFindCheckpointMethodCall()) {
            return getCellEditorForFindCheckpoint(parent, methodCallExpressionWrapper);
        }
        return new MethodCallInputCellEditor(parent, methodCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForTestDataValue(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new TestDataValueCellEditor(parent, methodCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForTestData(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        ArgumentListExpressionWrapper argumentListExpressionWrapper = (ArgumentListExpressionWrapper) methodCallExpressionWrapper
                .getArguments();
        if (argumentListExpressionWrapper.getExpressions().isEmpty()) {
            return null;
        }
        String pk = argumentListExpressionWrapper.getExpressions().get(0).getText();
        return new TestDataSelectionMethodCallBuilderDialogCellEditor(parent, pk);
    }

    public static CellEditor getCellEditorForBooleanExpression(Composite parent,
            BooleanExpressionWrapper booleanExpressionWrapper) {
        return new BooleanCellEditor(parent, booleanExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForRangeExpression(Composite parent,
            RangeExpressionWrapper rangeExpressionWrapper) {
        return new RangeInputCellEditor(parent, rangeExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForClosureListExpression(Composite parent,
            ClosureListExpressionWrapper closureListExpressionWrapper) {
        return new ClosureListInputCellEditor(parent, closureListExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForConstantExpression(Composite parent,
            ConstantExpressionWrapper constantExpressionWrapper) {
        if (constantExpressionWrapper.isFalseExpression() || constantExpressionWrapper.isTrueExpression()) {
            return getCellEditorForBooleanConstantExpression(parent);
        }
        if (constantExpressionWrapper.getValue() instanceof Number) {
            return getCellEditorForNumberConstantExpression(parent);
        }
        return getCellEditorForStringConstantExpression(parent);
    }

    public static CellEditor getCellEditorForStringConstantExpression(Composite parent) {
        return new StringConstantCellEditor(parent);
    }

    public static CellEditor getCellEditorForNumberConstantExpression(Composite parent) {
        return new NumberConstantCellEditor(parent);
    }

    public static CellEditor getCellEditorForBooleanConstantExpression(Composite parent) {
        return new BooleanConstantComboBoxCellEditor(parent);
    }
    
    // Use this when testCasePart's information cannot be retrieved otherwise
    public static CellEditor getCellEditorForVariableExpression(Composite parent,
            VariableExpressionWrapper variableExpressionWrapper, ITestCasePart variablesPart) {
        List<String> variableStringList = Optional.ofNullable(variablesPart)
                .map(ITestCasePart::getTestCase)
                .map(TestCaseEntity::getVariables)
                .orElse(new ArrayList<>())
                .stream()
                .map(VariableEntity::getName)
                .collect(Collectors.toList());
        return new VariableComboBoxCellEditor(parent, variableStringList);
    }
    
    public static CellEditor getCellEditorForVariableExpression(Composite parent,
            VariableExpressionWrapper variableExpressionWrapper) {
    	List<String> variableStringList = new ArrayList<>();
    	ScriptNodeWrapper scriptClass = variableExpressionWrapper.getScriptClass();
    	if(scriptClass != null){
    		for(FieldNodeWrapper field: scriptClass.getFields()){
    			variableStringList.add(field.getName());
    		}
    	}
        return new VariableComboBoxCellEditor(parent, variableStringList);
    }

    public static CellEditor getCellEditorForListExpression(Composite parent,
            ListExpressionWrapper listExpressionWrapper) {
        return new ListInputCellEditor(parent, listExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForMapExpression(Composite parent,
            MapExpressionWrapper mapExpressionWrapper) {
        return new MapInputCellEditor(parent, mapExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForCallTestCase(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        String testCasePk = AstEntityInputUtil
                .findTestCaseIdArgumentFromFindTestCaseMethodCall(methodCallExpressionWrapper);
        return new TestCaseSelectionMethodCallBuilderDialogCellEditor(parent, testCasePk);
    }

    public static CellEditor getCellEditorForFindCheckpoint(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        String checkpointPk = AstEntityInputUtil
                .findCheckpointIdArgumentFromFindCheckpointMethodCall(methodCallExpressionWrapper);
        return new CheckpointSelectionMethodCallBuilderDialog(parent, checkpointPk);
    }

    public static CellEditor getCellEditorForTestObject(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new TestObjectCellEditor(parent, methodCallExpressionWrapper.getText(), false);
    }

    public static CellEditor getCellEditorForWindowsTestObject(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new WindowsTestObjectCellEditor(parent, methodCallExpressionWrapper.getText(), false);
    }

    public static CellEditor getCellEditorForGlobalVariableExpression(Composite parent) {
        try {
            return new GlobalVariablePropertyComboBoxCellEditor(parent);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    public static CellEditor getCellEditorForEncryptedText(Composite parent,
            ConstantExpressionWrapper constantExpressionWrapper) {
        return new EncryptedTextDialogCellEditor(parent, constantExpressionWrapper);
    }

    public static CellEditor getCellEditorForClosureExpression(Composite parent,
            ClosureExpressionWrapper closureExpressionWrapper) {
        return new ClosureInputCellEditor(parent, closureExpressionWrapper.getText(),
                closureExpressionWrapper.getParent());
    }

    public static void applyEditingValue(CellEditor editor) {
        if (editor instanceof ApplyingEditingValue) {
            ((ApplyingEditingValue) editor).applyEditingValue();
        }

    }
}
