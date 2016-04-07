package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public abstract class AstAbstractTreeTableNode implements AstTreeTableNode {
    protected AstTreeTableNode parentNode;

    public AstAbstractTreeTableNode(AstTreeTableNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public AstTreeTableNode getParent() {
        return parentNode;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AstTreeTableNode)) {
            return false;
        }
        AstTreeTableNode that = (AstTreeTableNode) object;
        return new EqualsBuilder().append(this.getASTObject(), that.getASTObject()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).appendSuper(super.hashCode())
                .append(this.getASTObject().hashCode())
                .toHashCode();
    }

    @Override
    public String getItemTooltipText() {
        return getItemText();
    }

    @Override
    public boolean canHaveChildren() {
        return false;
    }

    public boolean hasChildren() {
        return false;
    };

    public List<AstTreeTableNode> getChildren() {
        return Collections.emptyList();
    };

    public void reloadChildren() {
        // Do nothing
    };
    
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return false;
    }

    public boolean addChild(ASTNodeWrapper childObject) {
        return false;
    }

    public boolean addChild(ASTNodeWrapper childObject, int index) {
        return false;
    }

    public boolean removeChild(ASTNodeWrapper childObject) {
        return false;
    }

    public int indexOf(ASTNodeWrapper childObject) {
        return -1;
    }

    @Override
    public boolean isDescendantNode(AstTreeTableNode otherNode) {
        if (otherNode == null) {
            return false;
        } else if (!canHaveChildren() || !hasChildren()) {
            return false;
        }
        for (AstTreeTableNode childNode : getChildren()) {
            return otherNode.equals(childNode) || childNode.isDescendantNode(otherNode);
        }
        return false;
    }
}
