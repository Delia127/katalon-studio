package com.kms.katalon.composer.testcase.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.internal.Primitives;

public class InputValueTypeUtil {
    
    private InputValueTypeUtil() {
        //Disable default constructor
    }
    
    public static final String ARGUMENT_OPTIONS = "argument";
    public static final String CASE_OPTIONS = "case";
    public static final String FOR_OPTIONS = "for";
    public static final String SWITCH_OPTIONS = "switch";
    public static final String THROW_OPTIONS = "throw";
    
    private static final InputValueType[] closureListOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Binary, InputValueType.Property };

    private static final InputValueType[] argumentOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.Binary, InputValueType.Condition, InputValueType.TestCase,
            InputValueType.TestData, InputValueType.TestObject, InputValueType.MethodCall, InputValueType.Property,
            InputValueType.List, InputValueType.Map, InputValueType.Keys };

    private static final InputValueType[] globalVariableOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.TestDataValue, InputValueType.TestObject,
            InputValueType.TestData, InputValueType.Range, InputValueType.Property, InputValueType.List,
            InputValueType.Map };

    private static final InputValueType[] caseOptions = { InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Condition, InputValueType.Binary,
            InputValueType.Property, InputValueType.List, InputValueType.Map, InputValueType.Range,
            InputValueType.Class, InputValueType.String, InputValueType.Number, InputValueType.Boolean,
            InputValueType.Null };

    private static final InputValueType[] forOptions = { InputValueType.Range, InputValueType.ClosureList,
            InputValueType.List, InputValueType.Map, InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.Property };

    private static final InputValueType[] keysOptions = { InputValueType.String, InputValueType.Key };

    private static final InputValueType[] listOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Property };

    private static final InputValueType[] mapOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Property };

    private static final InputValueType[] rangeOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Binary, InputValueType.Property };

    private static final InputValueType[] switchOptions = { InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Condition, InputValueType.Binary,
            InputValueType.Property, InputValueType.List, InputValueType.Map, InputValueType.Range,
            InputValueType.Class, InputValueType.String, InputValueType.Number, InputValueType.Boolean,
            InputValueType.Null };

    private static final InputValueType[] testDataValueOptions = { InputValueType.Variable, InputValueType.TestData,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null };

    private static final InputValueType[] variableOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.GlobalVariable, InputValueType.TestDataValue,
            InputValueType.TestObject, InputValueType.TestData, InputValueType.Range, InputValueType.Property,
            InputValueType.List,
            InputValueType.Map };

    private static final InputValueType[] methodCallOptions = { InputValueType.Class, InputValueType.This,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null,
            InputValueType.Variable, InputValueType.MethodCall, InputValueType.Property };

    private static final InputValueType[] binaryOptions = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.MethodCall,
            InputValueType.Binary, InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.Property };

    private static final InputValueType[] booleanOptions = { InputValueType.Boolean, InputValueType.Variable,
            InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.MethodCall,
            InputValueType.Condition, InputValueType.Binary, InputValueType.Property };

    private static final InputValueType[] throwOptions = { InputValueType.Throwable, InputValueType.Variable };

    private static final InputValueType[] stringOptions = { InputValueType.String, InputValueType.Null,
            InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Binary, InputValueType.Property };

    private static final InputValueType[] numberOptions = { InputValueType.Number, InputValueType.Null,
            InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Binary, InputValueType.Property };

    private static final InputValueType[] propertyOptions = { InputValueType.Class, InputValueType.Variable };
    
    private static final Map<String, InputValueType[]> valueTypeOptions = ImmutableMap.<String, InputValueType[]> builder()
            .put(InputValueType.ClosureList.getName()    , closureListOptions)
            .put(ARGUMENT_OPTIONS                        , argumentOptions)
            .put(InputValueType.GlobalVariable.getName() , globalVariableOptions)
            .put(CASE_OPTIONS                            , caseOptions)
            .put(FOR_OPTIONS                             , forOptions)
            .put(InputValueType.Keys.getName()           , keysOptions)
            .put(InputValueType.List.getName()           , listOptions)
            .put(InputValueType.Map.getName()            , mapOptions)
            .put(InputValueType.Range.getName()          , rangeOptions)
            .put(SWITCH_OPTIONS                          , switchOptions)
            .put(InputValueType.TestDataValue.getName()  , testDataValueOptions)
            .put(InputValueType.Variable.getName()       , variableOptions)
            .put(InputValueType.MethodCall.getName()     , methodCallOptions)
            .put(InputValueType.Binary.getName()         , binaryOptions)
            .put(InputValueType.Boolean.getName()        , booleanOptions)
            .put(THROW_OPTIONS                           , throwOptions)
            .put(InputValueType.String.getName()         , stringOptions)
            .put(InputValueType.Number.getName()         , numberOptions)
            .put(InputValueType.Property.getName()       , propertyOptions)
            .build();
    
    public static InputValueType[] getValueTypeOptions(String name) {
        return valueTypeOptions.get(name);
    }
    
    public static InputValueType[] getValueTypeOptions(InputValueType inputValueType) {
        return valueTypeOptions.get(inputValueType.getName());
    }
    
    public static InputValueType getAssignableValueType(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        
        if (clazz.isPrimitive()) {
            return getAssignableValueType(Primitives.wrap(clazz));
        }
        
        if (String.class.isAssignableFrom(clazz)) {
            return InputValueType.String;
        }
        
        if (Number.class.isAssignableFrom(clazz)) {
            return InputValueType.Number;
        }
        
        if (Boolean.class.isAssignableFrom(clazz)) {
            return InputValueType.Boolean;
        }
        
        if (List.class.isAssignableFrom(clazz)) {
            return InputValueType.List;
        }
        
        if (Map.class.isAssignableFrom(clazz)) {
            return InputValueType.Map;
        }
        
        if (Throwable.class.isAssignableFrom(clazz)) {
            return InputValueType.Throwable;
        }
        
        if (clazz.isEnum()) {
            return InputValueType.Property;
        }
        
        if (Object.class.isAssignableFrom(clazz)) {
            return InputValueType.Variable;
        }
        
        return null;
    }
}
