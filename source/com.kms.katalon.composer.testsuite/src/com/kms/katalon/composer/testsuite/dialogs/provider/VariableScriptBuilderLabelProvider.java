package com.kms.katalon.composer.testsuite.dialogs.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableScriptBuilderLabelProvider extends TypeCheckedStyleCellLabelProvider<VariableLink> {

    public static final int CLMN_NAME_IDX = 0;

    public static final int CLMN_TYPE_IDX = 1;

    public static final int CLMN_VALUE_IDX = 2;

    private TestSuiteTestCaseLink selectedTestCaseLink;

    public VariableScriptBuilderLabelProvider(int columnIndex, TestSuiteTestCaseLink testCaseLink) {
        super(columnIndex);
        this.selectedTestCaseLink = testCaseLink;
    }

    @Override
    protected Class<VariableLink> getElementType() {
        return VariableLink.class;
    }

    @Override
    protected Image getImage(VariableLink element) {
        return null;
    }

    @Override
    protected String getText(VariableLink element) {
        switch (columnIndex) {
            case CLMN_NAME_IDX:
                return getVariableName(element);
            case CLMN_TYPE_IDX:
                return getElementTypeAsString(element);
            case CLMN_VALUE_IDX:
                return getElementValue(element);
            default:
                return StringUtils.EMPTY;
        }
    }

    private String getElementValue(VariableLink element) {
        ExpressionWrapper expression = getElementExpression(element);
        return expression != null ? expression.getText() : StringUtils.EMPTY;
    }

    private ExpressionWrapper getElementExpression(VariableLink element) {
        return GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(element.getValue());
    }

    private String getElementTypeAsString(VariableLink element) {
        InputValueType valueType = AstValueUtil.getTypeValue(getElementExpression(element));
        return valueType != null ? TreeEntityUtil.getReadableKeywordName(valueType.getName()) : StringUtils.EMPTY;
    }

    private String getVariableName(VariableLink element) {
        try {
            VariableEntity variable = TestSuiteController.getInstance().getVariable(
                    selectedTestCaseLink.getTestCaseId(), element);
            return variable != null ? variable.getName() : StringUtils.EMPTY;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    protected int getLeftMargin() {
        return 0;
    }
    
    @Override
    protected String getElementToolTipText(VariableLink element) {
        return getText(element);
    }
}
