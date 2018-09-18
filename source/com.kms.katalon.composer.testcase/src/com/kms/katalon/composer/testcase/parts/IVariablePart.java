package com.kms.katalon.composer.testcase.parts;

import java.util.List;

import com.kms.katalon.entity.variable.VariableEntity;

public interface IVariablePart {
    
    void setDirty(boolean isDirty);

    void addVariables(VariableEntity[] variables);

    VariableEntity[] getVariables();

    void deleteVariables(List<VariableEntity> variableList);

}
