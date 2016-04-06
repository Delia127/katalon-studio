package com.kms.katalon.composer.testcase.ast.treetable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.List;

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
		return new HashCodeBuilder(7, 31).appendSuper(super.hashCode()).append(this.getASTObject().hashCode())
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
}
