package com.kms.katalon.composer.testsuite.parts;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface ParentTestSuiteCompositePart extends SavableCompositePart {
    public TestSuiteEntity getTestSuiteClone();

    public TestSuiteEntity getOriginalTestSuite();
}
