package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.GenericsType;

public class GenericsTypeWrapper extends ASTNodeWrapper {
    protected ClassNodeWrapper[] upperBounds;
    protected ClassNodeWrapper lowerBound;
    protected ClassNodeWrapper type;
    protected String name;

    public GenericsTypeWrapper(GenericsType genericsType, ASTNodeWrapper parentNodeWrapper) {
        super(genericsType, parentNodeWrapper);
        name = genericsType.getName();
        if (genericsType.getUpperBounds() != null) {
            upperBounds = new ClassNodeWrapper[genericsType.getUpperBounds().length];
            for (int index = 0; index < upperBounds.length; index++) {
                upperBounds[index] = new ClassNodeWrapper(genericsType.getUpperBounds()[index], this);
            }
        }
        if (genericsType.getLowerBound() != null) {
            lowerBound = new ClassNodeWrapper(genericsType.getLowerBound(), this);
        }
        type = new ClassNodeWrapper(genericsType.getType(), this);
    }
    
    public GenericsTypeWrapper(GenericsTypeWrapper genericsTypeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(genericsTypeWrapper, parentNodeWrapper);
        name = genericsTypeWrapper.getName();
        if (genericsTypeWrapper.getUpperBounds() != null) {
            upperBounds = new ClassNodeWrapper[genericsTypeWrapper.getUpperBounds().length];
            for (int index = 0; index < upperBounds.length; index++) {
                upperBounds[index] = new ClassNodeWrapper(genericsTypeWrapper.getUpperBounds()[index], this);
            }
        }
        if (genericsTypeWrapper.getLowerBound() != null) {
            lowerBound = new ClassNodeWrapper(genericsTypeWrapper.getLowerBound(), this);
        }
        type = new ClassNodeWrapper(genericsTypeWrapper.getType(), this);
    }

    // Return empty because this will not be used
    @Override
    public String getText() {
        return "";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }

    public ClassNodeWrapper[] getUpperBounds() {
        return upperBounds;
    }

    public ClassNodeWrapper getLowerBound() {
        return lowerBound;
    }
    
    public ClassNodeWrapper getType() {
        return type;
    }

    public void setType(ClassNodeWrapper type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
