package com.kms.katalon.composer.testsuite.collection.part.provider;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public interface TableViewerProvider {
    TableViewer getTableViewer();
    
    List<TestSuiteRunConfiguration> getTableItems();
    
    boolean containsTestSuite(TestSuiteEntity testSuite);
    
    void markDirty();
    
    void updateRunColumn();
    
    void executeTestRun();
}
