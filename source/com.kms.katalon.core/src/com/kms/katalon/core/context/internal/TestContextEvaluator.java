package com.kms.katalon.core.context.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TestContextEvaluator {
    private String sourceFolder;
    private List<TestHooker> testHookers;

    public TestContextEvaluator(String sourceFolder) {
        this.sourceFolder = sourceFolder;
        collectTestContextInProject();
    }
    
    public void collectTestContextInProject() {
        testHookers = new ArrayList<>();
        File testListenerFolder = new File(sourceFolder);
        if (!testListenerFolder.exists()) {
            return;
        }
        
        try {
            Files.walk(testListenerFolder.toPath())
            .filter(p -> p.toString().endsWith(".groovy"))
            .map(p -> p.toAbsolutePath().toFile())
            .forEach(file -> {
                TestHooker testHooker = new TestHooker(file.getAbsolutePath());
                testHookers.add(testHooker);
            });
        } catch (IOException ignored) {
        }
    }
    
    public void invokeListenerMethod(String listenerAnnotationName, Object[] injectedObjects) {
        testHookers.forEach(hooker -> {
            hooker.invokeContextMethods(listenerAnnotationName, injectedObjects);
        });
    }    
}
