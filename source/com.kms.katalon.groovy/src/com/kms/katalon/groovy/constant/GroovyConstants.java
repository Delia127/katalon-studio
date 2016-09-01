package com.kms.katalon.groovy.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.custom.parser.GlobalVariableParser;

public class GroovyConstants {
    public static final String GROOVY_FILE_EXTENSION = ".groovy";
    public static final String CUSTOM_KEYWORD_LIB_FILE_NAME = "CustomKeywords";
    public static final Pattern VARIABLE_NAME_REGEX = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
    public static final String GROOVY_KEYWORDS[] = { "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try",
            "void", "volatile", "while", "as", "in", "def", "trait" };

    public static String[] getStartingClassesName() {
        List<String> classList = new ArrayList<String>();
        classList.add(FailureHandling.class.getName());
        classList.add(ObjectRepository.class.getName());
        classList.add(TestCaseFactory.class.getName());
        classList.add(TestDataFactory.class.getName());
        for (IKeywordContributor keywordContribution : KeywordContributorCollection.getKeywordContributors()) {
            classList.add(keywordContribution.getKeywordClass().getName());
        }
        classList.add(GlobalVariableParser.INTERNAL_PACKAGE_NAME + "." + GlobalVariableParser.GLOBAL_VARIABLE_CLASS_NAME);
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
