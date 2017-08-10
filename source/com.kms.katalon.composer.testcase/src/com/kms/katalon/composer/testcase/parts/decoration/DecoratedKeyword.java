package com.kms.katalon.composer.testcase.parts.decoration;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;

public interface DecoratedKeyword {
    Image getImage();

    String getLabel();

    String getTooltip();
    
    ExpressionStatementWrapper newStep(ASTNodeWrapper parentNode);
}
