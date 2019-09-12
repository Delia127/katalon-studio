package com.kms.katalon.groovy.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.kms.katalon.core.checkpoint.Checkpoint;
import com.kms.katalon.core.checkpoint.CheckpointFactory;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.parser.GlobalVariableParser;

public class GroovyConstants {
    public static final String DF_CHARSET = "UTF-8";

    public static final String GROOVY_FILE_EXTENSION = ".groovy";

    public static final String CUSTOM_KEYWORD_LIB_FILE_NAME = "CustomKeywords";

    public static final Pattern VARIABLE_NAME_REGEX = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");

    public static final String GROOVY_KEYWORDS[] = { "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try",
            "void", "volatile", "while", "as", "in", "def", "trait" };

    public static final String FIND_TEST_CASE_METHOD_NAME = "findTestCase";

    public static final String FIND_TEST_DATA_METHOD_NAME = "findTestData";

    public static final String FIND_TEST_OBJECT_METHOD_NAME = "findTestObject";

    public static final String FIND_WINDOWS_OBJECT_METHOD_NAME = "findWindowsObject";

    public static final String FIND_CHECKPOINT_METHOD_NAME = "findCheckpoint";

    public static Map<String, String> DEFAULT_STATIC_METHOD_IMPORTS = new HashMap<>();

    public static Map<String, String> DEFAULT_KEYWORD_CONTRIBUTOR_IMPORTS = new HashMap<>();

    static {
        DEFAULT_STATIC_METHOD_IMPORTS.put(FIND_CHECKPOINT_METHOD_NAME, CheckpointFactory.class.getName());
        DEFAULT_STATIC_METHOD_IMPORTS.put(FIND_TEST_OBJECT_METHOD_NAME, ObjectRepository.class.getName());
        DEFAULT_STATIC_METHOD_IMPORTS.put(FIND_WINDOWS_OBJECT_METHOD_NAME, ObjectRepository.class.getName());
        DEFAULT_STATIC_METHOD_IMPORTS.put(FIND_TEST_CASE_METHOD_NAME, TestCaseFactory.class.getName());
        DEFAULT_STATIC_METHOD_IMPORTS.put(FIND_TEST_DATA_METHOD_NAME, TestDataFactory.class.getName());

        for (IKeywordContributor keywordContribution : KeywordContributorCollection.getKeywordContributors()) {
            DEFAULT_KEYWORD_CONTRIBUTOR_IMPORTS.put(keywordContribution.getAliasName(),
                    keywordContribution.getKeywordClass().getName());
        }
    }

    public static String[] getStartingClassesName() {
        List<String> classList = new ArrayList<String>();
        classList.add(FailureHandling.class.getName());
        classList.add(TestCase.class.getName());
        classList.add(TestData.class.getName());
        classList.add(TestObject.class.getName());
        classList.add(Checkpoint.class.getName());
        classList.add(GlobalVariableParser.INTERNAL_PACKAGE_NAME + "."
                + GlobalVariableParser.GLOBAL_VARIABLE_CLASS_NAME);
        DEFAULT_KEYWORD_CONTRIBUTOR_IMPORTS.entrySet().forEach(e -> {
            classList.add(e.getValue() + " as " + e.getKey());
        });
        return classList.toArray(new String[classList.size()]);
    }

    public static boolean isValidVariableName(String variableName) {
        if (variableName == null || variableName.isEmpty())
            return false;

        for (String groovyKeyword : GROOVY_KEYWORDS) {
            if (groovyKeyword.equals(variableName)) {
                return false;
            }
        }

        return GroovyConstants.VARIABLE_NAME_REGEX.matcher(variableName).find();
    }
}
