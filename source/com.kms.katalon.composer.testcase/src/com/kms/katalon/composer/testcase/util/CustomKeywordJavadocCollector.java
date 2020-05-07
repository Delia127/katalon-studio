package com.kms.katalon.composer.testcase.util;

public class CustomKeywordJavadocCollector {
//    
//    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
//    
//    private ProjectEntity project;
//    
//    private File javadocDir;
//    
//    public CustomKeywordJavadocCollector(ProjectEntity project) {
//        this.project = project;
//    }
//    
//    public Map<String, String> collect() {
//        try {
//            javadocDir = Files.createTempDirectory("custom-keywords-javadoc").toFile();
//            initGroovyProjectClassPath(true);
//            generateCustomKeywordJavadoc();
//            Map<String, String> javadocs = doCollectJavadoc();
//            initGroovyProjectClassPath(false);  
//            return javadocs;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            FileUtils.deleteQuietly(javadocDir);
//            javadocDir = null;
//        }
//    }
//    
//    private void initGroovyProjectClassPath(boolean generateKeywordJavadoc)
//            throws CoreException, IOException, BundleException, ControllerException {
//        
//        boolean allowSourceAttachment = featureService.canUse(KSEFeature.SOURCE_CODE_FOR_DEBUGGING);
//        GroovyUtil.initGroovyProjectClassPath(
//                project,
//                ProjectController.getInstance().getCustomKeywordPlugins(project),
//                false,
//                allowSourceAttachment,
//                javadocDir.getAbsolutePath(),
//                null);
//    }
//    
//    private void generateCustomKeywordJavadoc() throws Exception {
//        CustomKeywordJavadocGenerator javadocGenerator = new CustomKeywordJavadocGenerator(project);
//        javadocGenerator.generate(javadocDir.getAbsolutePath());
//    }
//    
//    private Map<String, String> doCollectJavadoc() {
//        IProject groovyProject = GroovyUtil.getGroovyProject(project);
//        IJavaProject javaProject = JavaCore.create(groovyProject);
//        Map<String, String> customKeywordJavaDocMap = new HashMap<>();
//        Map<String, List<MethodNode>> customKeywordMethodNodeMap = CustomMethodNodeFactory.getInstance()
//                .getMethodNodesMap();
//        for (String keywordClass : customKeywordMethodNodeMap.keySet()) {
//            try {
//                Class<?> keywordType = AstKeywordsInputUtil.loadType(keywordClass, null);
//                if (keywordType == null) {
//                    continue;
//                }
//                IType customKeywordType = javaProject.findType(keywordType.getName());
//                List<MethodNode> customKeywordMethods = customKeywordMethodNodeMap.get(keywordClass);
//                for (MethodNode method : customKeywordMethods) {
//                    IMethod customMethod = findMethods(customKeywordType, method.getName(), javaProject);
//                    if (customMethod != null) {
//                        String attachedJavaDoc = customMethod.getAttachedJavadoc(null);
//                        attachedJavaDoc = attachedJavaDoc == null ? "" : attachedJavaDoc;
//                        customKeywordJavaDocMap.put(keywordClass + "." + method.getName(), attachedJavaDoc);
//                    }
//                }
//            } catch (JavaModelException e) {
//                LoggerSingleton.logError(e);
//            }
//        }
//        return customKeywordJavaDocMap;
//    }
//    
//    private IMethod findMethods(IType type, String methodName, IJavaProject javaProject)
//            throws JavaModelException {
//        for (IMethod keywordMethod : type.getMethods()) {
//            if (keywordMethod.getElementName().equals(methodName)) {
//                return keywordMethod;
//            }
//        }
//        if (!type.getSuperclassName().equals(Object.class.getName())) {
//            return findMethods(javaProject.findType(type.getSuperclassName()), methodName, javaProject);
//        }
//        return null;
//    }

}
