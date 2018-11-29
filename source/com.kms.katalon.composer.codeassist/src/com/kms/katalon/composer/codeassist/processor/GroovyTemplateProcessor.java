package com.kms.katalon.composer.codeassist.processor;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class GroovyTemplateProcessor {

    private static final String ORG_CODEHAUS_GROOVY_ECLIPSE_QUICKFIX_PLUGIN_ID = "org.codehaus.groovy.eclipse.quickfix";

    public static final String GROOVY_PREF_KEY = "groovy";

    public static final String GROOVY_TEMPLATES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><templates><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Checkpoint\" enabled=\"true\" name=\"cp\">${Checkpoint:newType(com.kms.katalon.core.checkpoint.Checkpoint)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Checkpoint Factory\" enabled=\"true\" name=\"cpf\">${CheckpointFactory:newType(com.kms.katalon.core.checkpoint.CheckpointFactory)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Failure Handling\" enabled=\"true\" name=\"fh\">${FailureHandling:newType(com.kms.katalon.core.model.FailureHandling)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Find Checkpoint\" enabled=\"true\" name=\"fcp\">${findCheckpoint:importStatic(com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint)}findCheckpoint(${word_selection}${null})${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Find Test Case\" enabled=\"true\" name=\"ftc\">${findTestCase:importStatic(com.kms.katalon.core.testcase.TestCaseFactory.findTestCase)}findTestCase(${word_selection}${null})${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Find Test Data\" enabled=\"true\" name=\"ftd\">${findTestData:importStatic(com.kms.katalon.core.testdata.TestDataFactory.findTestData)}findTestData(${word_selection}${null})${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Find Test Object\" enabled=\"true\" name=\"fto\">${findTestObject:importStatic(com.kms.katalon.core.testobject.ObjectRepository.findTestObject)}findTestObject(${word_selection}${null})${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Global Variables\" enabled=\"true\" name=\"gv\">${GlobalVariable:newType(internal.GlobalVariable)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Object Repository\" enabled=\"true\" name=\"tof\">${ObjectRepository:newType(com.kms.katalon.core.testobject.ObjectRepository)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Test Case\" enabled=\"true\" name=\"tc\">${TestCase:newType(com.kms.katalon.core.testcase.TestCase)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Test Case Factory\" enabled=\"true\" name=\"tcf\">${TestCaseFactory:newType(com.kms.katalon.core.testcase.TestCaseFactory)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Test Data\" enabled=\"true\" name=\"td\">${TestData:newType(com.kms.katalon.core.testdata.TestData)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Test Data Factory\" enabled=\"true\" name=\"tdf\">${TestDataFactory:newType(com.kms.katalon.core.testdata.TestDataFactory)}${cursor}</template><template autoinsert=\"true\" context=\"groovy\" deleted=\"false\" description=\"Katalon - Test Object\" enabled=\"true\" name=\"to\">${TestObject:newType(com.kms.katalon.core.testobject.TestObject)}${cursor}</template></templates>";

    @Execute
    public void run() {
        ScopedPreferenceStore prefStore = getGroovyPreferenceStore();
        if (prefStore == null) {
            return;
        }
        
        // prevent user clear all or remove the predefined templates
        prefStore.setDefault(GROOVY_PREF_KEY, GROOVY_TEMPLATES);
        prefStore.setToDefault(GROOVY_PREF_KEY);
    }

    public static ScopedPreferenceStore getGroovyPreferenceStore() {
        return getPreferenceStore(ORG_CODEHAUS_GROOVY_ECLIPSE_QUICKFIX_PLUGIN_ID);
    }
}
