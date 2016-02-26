package com.kms.katalon.composer.testcase.util;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.TryCatchStatementWrapper;

/**
 * Utility class to handle tree table tasks
 *
 */
public class AstTreeTableUtil {

    public static int getIndex(ASTNodeWrapper parentObject, ASTNodeWrapper childObject) {
        if (parentObject instanceof BlockStatementWrapper && childObject instanceof StatementWrapper) {
            return ((BlockStatementWrapper) parentObject).getStatements().indexOf(childObject);
        } else if (parentObject instanceof SwitchStatementWrapper && childObject instanceof CaseStatementWrapper) {
            return ((SwitchStatementWrapper) parentObject).getCaseStatements().indexOf(childObject);
        } else if (parentObject instanceof TryCatchStatementWrapper && childObject instanceof CatchStatementWrapper) {
            return ((TryCatchStatementWrapper) parentObject).getCatchStatements().indexOf(childObject);
        } else if (parentObject instanceof IfStatementWrapper && childObject instanceof ElseIfStatementWrapper) {
            return ((IfStatementWrapper) parentObject).getElseIfStatements().indexOf(childObject);
        } else if (parentObject instanceof ASTHasBlock && childObject instanceof StatementWrapper) {
            return getIndex(((ASTHasBlock) parentObject).getBlock(), childObject);
        } else if (parentObject instanceof ClassNodeWrapper) {
            if (childObject instanceof MethodNodeWrapper) {
                return ((ClassNodeWrapper) parentObject).getMethods().indexOf(childObject);
            } else if (childObject instanceof FieldNodeWrapper) {
                return ((ClassNodeWrapper) parentObject).getMethods().indexOf(childObject);
            }
        }
        return -1;
    }

    public static void addChild(ASTNodeWrapper parentObject, ASTNodeWrapper childObject, int index) {
        if (parentObject instanceof BlockStatementWrapper && childObject instanceof StatementWrapper) {
            addChild((BlockStatementWrapper) parentObject, (StatementWrapper) childObject, index);
        } else if (parentObject instanceof SwitchStatementWrapper && childObject instanceof CaseStatementWrapper) {
            addChild((SwitchStatementWrapper) parentObject, (CaseStatementWrapper) childObject, index);
        } else if (parentObject instanceof TryCatchStatementWrapper && childObject instanceof CatchStatementWrapper) {
            addChild((TryCatchStatementWrapper) parentObject, (CatchStatementWrapper) childObject, index);
        } else if (parentObject instanceof IfStatementWrapper && childObject instanceof ElseIfStatementWrapper) {
            addChild((IfStatementWrapper) parentObject, (ElseIfStatementWrapper) childObject, index);
        } else if (parentObject instanceof ASTHasBlock && childObject instanceof StatementWrapper) {
            addChild(((ASTHasBlock) parentObject).getBlock(), (StatementWrapper) childObject, index);
        } else if (parentObject instanceof ClassNodeWrapper) {
            if (childObject instanceof MethodNodeWrapper) {
                addChild((ClassNodeWrapper) parentObject, (MethodNodeWrapper) childObject, index);
            } else if (childObject instanceof FieldNodeWrapper) {
                addChild((ClassNodeWrapper) parentObject, (FieldNodeWrapper) childObject, index);
            }
        }
    }

    private static void addChild(SwitchStatementWrapper parentSwitchStatementWrapper, CaseStatementWrapper caseStatementWrapper,
            int index) {
        if (index >= 0 && index < parentSwitchStatementWrapper.getCaseStatements().size()) {
            parentSwitchStatementWrapper.getCaseStatements().add(index, caseStatementWrapper);
        } else {
            parentSwitchStatementWrapper.getCaseStatements().add(caseStatementWrapper);
        }
        caseStatementWrapper.setParent(parentSwitchStatementWrapper);
    }

    private static void addChild(IfStatementWrapper parentIfStatementWrapper, ElseIfStatementWrapper elseIfStatementWrapper,
            int index) {
        if (index >= 0 && index < parentIfStatementWrapper.getElseIfStatements().size()) {
            parentIfStatementWrapper.getElseIfStatements().add(index, elseIfStatementWrapper);
        } else {
            parentIfStatementWrapper.getElseIfStatements().add(elseIfStatementWrapper);
        }
        elseIfStatementWrapper.setParent(parentIfStatementWrapper);
    }

    private static void addChild(TryCatchStatementWrapper tryCatchStatementWrapper, CatchStatementWrapper catchStatementWrapper,
            int index) {
        if (index >= 0 && index < tryCatchStatementWrapper.getCatchStatements().size()) {
            tryCatchStatementWrapper.getCatchStatements().add(index, catchStatementWrapper);
        } else {
            tryCatchStatementWrapper.getCatchStatements().add(catchStatementWrapper);
        }
        catchStatementWrapper.setParent(tryCatchStatementWrapper);
    }

    private static void addChild(BlockStatementWrapper parentBlockStatementWrapper, StatementWrapper childStatementWrapper,
            int index) {
        if (index >= 0 && index < parentBlockStatementWrapper.getStatements().size()) {
            parentBlockStatementWrapper.getStatements().add(index, childStatementWrapper);
        } else {
            parentBlockStatementWrapper.getStatements().add(childStatementWrapper);
        }
        childStatementWrapper.setParent(parentBlockStatementWrapper);
    }

    private static void addChild(ClassNodeWrapper classNode, MethodNodeWrapper methodNode, int index) {
        if (index >= 0 && index < classNode.getMethods().size()) {
            classNode.getMethods().add(index, methodNode);
        } else {
            classNode.getMethods().add(methodNode);
        }
        methodNode.setParent(classNode);
    }

    private static void addChild(ClassNodeWrapper classNode, FieldNodeWrapper fieldNode, int index) {
        if (index >= 0 && index < classNode.getFields().size()) {
            classNode.getFields().add(index, fieldNode);
        } else {
            classNode.getFields().add(fieldNode);
        }
        fieldNode.setParent(classNode);
    }

    public static void removeChild(ASTNodeWrapper parentObject, ASTNodeWrapper childObject) {
        if (parentObject instanceof BlockStatementWrapper && childObject instanceof StatementWrapper) {
            removeChild((BlockStatementWrapper) parentObject, (StatementWrapper) childObject);
        } else if (parentObject instanceof SwitchStatementWrapper
                && childObject instanceof CaseStatementWrapper) {
            removeChild((SwitchStatementWrapper) parentObject, (CaseStatementWrapper) childObject);
        } else if (parentObject instanceof TryCatchStatementWrapper
                && childObject instanceof CatchStatementWrapper) {
            removeChild((TryCatchStatementWrapper) parentObject, (CatchStatementWrapper) childObject);
        } else if (parentObject instanceof IfStatementWrapper
                && childObject instanceof ElseIfStatementWrapper) {
            removeChild((IfStatementWrapper) parentObject, (ElseIfStatementWrapper) childObject);
        } else if (parentObject instanceof ASTHasBlock && childObject instanceof StatementWrapper) {
            removeChild(((ASTHasBlock) parentObject).getBlock(), childObject);
        } else if (parentObject instanceof ClassNodeWrapper) {
            if (childObject instanceof MethodNodeWrapper) {
                removeChild((ClassNodeWrapper) parentObject, (MethodNodeWrapper) childObject);
            } else if (childObject instanceof FieldNodeWrapper) {
                removeChild((ClassNodeWrapper) parentObject, (FieldNodeWrapper) childObject);
            }
        }
    }

    private static void removeChild(SwitchStatementWrapper parentSwitchStatementWrapper, CaseStatementWrapper caseStatementWrapper) {
        parentSwitchStatementWrapper.getCaseStatements().remove(caseStatementWrapper);
    }

    private static void removeChild(IfStatementWrapper ifStatementWrapper, ElseIfStatementWrapper elseIfStatementWrapper) {
        ifStatementWrapper.getElseIfStatements().remove(elseIfStatementWrapper);
    }

    private static void removeChild(TryCatchStatementWrapper tryCatchStatementWrapper, CatchStatementWrapper catchStatementWrapper) {
        tryCatchStatementWrapper.getCatchStatements().remove(catchStatementWrapper);
    }

    private static void removeChild(BlockStatementWrapper parentBlockStatementWrapper, StatementWrapper childStatementWrapper) {
        parentBlockStatementWrapper.getStatements().remove(childStatementWrapper);
    }

    private static void removeChild(ClassNodeWrapper classNode, MethodNodeWrapper methodNode) {
        classNode.getMethods().remove(methodNode);
    }

    private static void removeChild(ClassNodeWrapper classNode, FieldNodeWrapper fieldNode) {
        classNode.getFields().remove(fieldNode);
    }

    public static void moveChild(ASTNodeWrapper parentObject, ASTNodeWrapper childObject, int newIndex) {
        if (parentObject instanceof StatementWrapper && childObject instanceof StatementWrapper) {
            moveChild((StatementWrapper) parentObject, (StatementWrapper) childObject, newIndex);
        } else if (parentObject instanceof ASTHasBlock && childObject instanceof StatementWrapper) {
            moveChild(((ASTHasBlock) parentObject).getBlock(), (StatementWrapper) childObject, newIndex);
        } else if (parentObject instanceof ClassNodeWrapper) {
            if (childObject instanceof MethodNodeWrapper) {
                moveChild((ClassNodeWrapper) parentObject, (MethodNodeWrapper) childObject, newIndex);
            } else if (childObject instanceof FieldNodeWrapper) {
                moveChild((ClassNodeWrapper) parentObject, (FieldNodeWrapper) childObject, newIndex);
            }
        }
    }

    public static void moveChild(ClassNodeWrapper classNode, MethodNodeWrapper methodNode, int newIndex) {
        int oldIndex = classNode.getMethods().indexOf(methodNode);
        if (oldIndex >= 0 && oldIndex < classNode.getMethods().size() && newIndex >= 0
                && newIndex < classNode.getMethods().size()) {
            classNode.getMethods().remove(oldIndex);
            classNode.getMethods().add(newIndex, methodNode);
        }
    }

    public static void moveChild(ClassNodeWrapper classNode, FieldNodeWrapper fieldNode, int newIndex) {
        int oldIndex = classNode.getFields().indexOf(fieldNode);
        if (oldIndex >= 0 && oldIndex < classNode.getFields().size() && newIndex >= 0 && newIndex < classNode.getFields().size()) {
            classNode.getFields().remove(oldIndex);
            classNode.getFields().add(newIndex, fieldNode);
        }
    }

    private static void moveChild(StatementWrapper parentStatementWrapper, StatementWrapper childStatementWrapper, int newIndex) {
        if (parentStatementWrapper instanceof BlockStatementWrapper) {
            moveChild((BlockStatementWrapper) parentStatementWrapper, childStatementWrapper, newIndex);
        } else if (parentStatementWrapper instanceof SwitchStatementWrapper
                && childStatementWrapper instanceof CaseStatementWrapper) {
            moveChild((SwitchStatementWrapper) parentStatementWrapper, (CaseStatementWrapper) childStatementWrapper, newIndex);
        } else if (parentStatementWrapper instanceof TryCatchStatementWrapper
                && childStatementWrapper instanceof CatchStatementWrapper) {
            moveChild((TryCatchStatementWrapper) parentStatementWrapper, (CatchStatementWrapper) childStatementWrapper, newIndex);
        } else if (parentStatementWrapper instanceof IfStatementWrapper
                && childStatementWrapper instanceof ElseIfStatementWrapper) {
            moveChild((IfStatementWrapper) parentStatementWrapper, (ElseIfStatementWrapper) childStatementWrapper, newIndex);
        }
    }

    private static void moveChild(IfStatementWrapper ifStatementWrapper, ElseIfStatementWrapper elseIfStatementWrapper,
            int newIndex) {
        int oldIndex = ifStatementWrapper.getElseIfStatements().indexOf(elseIfStatementWrapper);
        if (oldIndex >= 0 && oldIndex < ifStatementWrapper.getElseIfStatements().size() && newIndex >= 0
                && newIndex < ifStatementWrapper.getElseIfStatements().size()) {
            ifStatementWrapper.getElseIfStatements().remove(oldIndex);
            ifStatementWrapper.getElseIfStatements().add(newIndex, elseIfStatementWrapper);
        }
    }

    private static void moveChild(SwitchStatementWrapper switchStatementWrapper, CaseStatementWrapper caseStatementWrapper,
            int newIndex) {
        int oldIndex = switchStatementWrapper.getCaseStatements().indexOf(caseStatementWrapper);
        if (oldIndex >= 0 && oldIndex < switchStatementWrapper.getCaseStatements().size() && newIndex >= 0
                && newIndex < switchStatementWrapper.getCaseStatements().size()) {
            switchStatementWrapper.getCaseStatements().remove(oldIndex);
            switchStatementWrapper.getCaseStatements().add(newIndex, caseStatementWrapper);
        }
    }

    private static void moveChild(TryCatchStatementWrapper tryCatchStatementWrapper, CatchStatementWrapper catchStatementWrapper,
            int newIndex) {
        int oldIndex = tryCatchStatementWrapper.getCatchStatements().indexOf(catchStatementWrapper);
        if (oldIndex >= 0 && oldIndex < tryCatchStatementWrapper.getCatchStatements().size() && newIndex >= 0
                && newIndex < tryCatchStatementWrapper.getCatchStatements().size()) {
            tryCatchStatementWrapper.getCatchStatements().remove(oldIndex);
            tryCatchStatementWrapper.getCatchStatements().add(newIndex, catchStatementWrapper);
        }
    }

    private static void moveChild(BlockStatementWrapper parentBlockStatementWrapper, StatementWrapper childStatementWrapper,
            int newIndex) {
        int oldIndex = parentBlockStatementWrapper.getStatements().indexOf(childStatementWrapper);
        if (oldIndex >= 0 && oldIndex < parentBlockStatementWrapper.getStatements().size() && newIndex >= 0
                && newIndex < parentBlockStatementWrapper.getStatements().size()) {
            parentBlockStatementWrapper.getStatements().remove(oldIndex);
            parentBlockStatementWrapper.getStatements().add(newIndex, childStatementWrapper);
        }
    }
}
