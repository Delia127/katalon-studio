package com.kms.katalon.composer.testcase.parts;

import java.util.List;

import org.eclipse.swt.widgets.Menu;

import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public interface ITestCasePart extends IVariablePart {

    TestCaseEntity getTestCase();

    TestCaseTreeTableInput getTreeTableInput();

    List<AstTreeTableNode> getDragNodes();

    void createDynamicGotoMenu(Menu menu);
}
