package com.kms.katalon.custom.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.logging.LogUtil;

import groovy.lang.GroovyObject;

public class GlobalVariableParser {
    public static final String GLOBAL_VARIABLE_CLASS_NAME = "GlobalVariable";

    public static final String GLOBAL_VARIABLE_FILE_NAME = GLOBAL_VARIABLE_CLASS_NAME + ".groovy";

    private static GlobalVariableParser _instance;

    private final static String TEMPLATE_CLASS_NAME = IdConstants.KATALON_CUSTOM_BUNDLE_ID
            + ".generation.GlobalVariableTemplate";

    private final static String GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME = "generateGlobalVariableFile";

    public final static String INTERNAL_PACKAGE_NAME = "internal";
    
    private final static String RAW_TEXT_GLOBAL_VARIABLE_TEMPLATE = File.separator + "resources/globalVariableTemplate.txt";

    private GlobalVariableParser() {
    }

    public static GlobalVariableParser getInstance() {
        if (_instance == null) {
            _instance = new GlobalVariableParser();
        }
        return _instance;
    }

    public void generateGlobalVariableLibFile(IFolder libFolder, List<ExecutionProfileEntity> executionProfiles)
            throws Exception {
        String libFolderPath = libFolder.getRawLocation().toString();
        File internalPackageFolder = new File(libFolderPath, INTERNAL_PACKAGE_NAME);
        File internalGlobalVariableFile = new File(internalPackageFolder, GLOBAL_VARIABLE_FILE_NAME);

        generateGlobalVariableFile(executionProfiles, internalGlobalVariableFile);
        libFolder.getFolder(INTERNAL_PACKAGE_NAME).refreshLocal(IResource.DEPTH_INFINITE, null);
    }

    private void generateGlobalVariableFile(List<ExecutionProfileEntity> globalVariables, File globalVariableFile)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if (!globalVariableFile.exists()) {
            globalVariableFile.getParentFile().mkdirs();
            globalVariableFile.createNewFile();
        }
        generateGlobalVariableFile2(globalVariables, globalVariableFile);
        Class<?> clazz = Class.forName(TEMPLATE_CLASS_NAME);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod(GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME,
                new Object[] { globalVariableFile, globalVariables });
    }
    
    private void generateGlobalVariableFile2(List<ExecutionProfileEntity> globalVariables, File globalVariableFile)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if (!globalVariableFile.exists()) {
            globalVariableFile.getParentFile().mkdirs();
            globalVariableFile.createNewFile();
        }
        internallyGenerateGlobalVariableFile(globalVariables, globalVariableFile);
    }

    private void internallyGenerateGlobalVariableFile(List<ExecutionProfileEntity> globalVariables,
            File globalVariableFile) {
        try {
            String globalVariableDeclarations = globalVariables.stream()
                    .map(variable -> "/**\n" + "*" + escapeHtmlForJavadoc(variable.getDescription()) + "\n"
                            + "*/\n" + "public static Object " + variable.getName() + "\n")
                    .collect(Collectors.joining());

            String globalVariablesAssignment = globalVariables.stream()
                    .map(variable -> "\n" + variable.getName() + " = " + "selectedVariables['" + variable.getName()
                            + "']")
                    .collect(Collectors.joining());
            File rawTextGlobalVariableTemplateFile = getRawTextGlobbalVariableTemplateFile();
            if (rawTextGlobalVariableTemplateFile != null) {
                String rawTextGlobalVariableTemplate = readFile(rawTextGlobalVariableTemplateFile,
                        Charset.defaultCharset());

                HashMap<String, String> valuesMap = new HashMap<>();
                valuesMap.put("globalVariablesDeclaration", globalVariableDeclarations);
                valuesMap.put("globalVariableAssignments", globalVariablesAssignment);
                valuesMap.put("packageName", INTERNAL_PACKAGE_NAME);

                StringSubstitutor sub = new StringSubstitutor(valuesMap);
                System.out.println(sub.replace(rawTextGlobalVariableTemplate));
                System.out.println(globalVariableDeclarations);
                System.out.println(globalVariablesAssignment);
            }
        } catch (Exception ex) {
            LogUtil.logError(ex.getMessage());
        }
    }
    
    private static String escapeHtmlForJavadoc(String description) {
        return StringEscapeUtils.escapeHtml(StringUtils.defaultString(description)).replace("/", "&#47;");
    }
    
    private File getRawTextGlobbalVariableTemplateFile() throws IOException {
        File bundleFile = FileLocator.getBundleFile(FrameworkUtil.getBundle(GlobalVariableParser.class));
        if (bundleFile.exists() && bundleFile.isDirectory()) {
            return new File(bundleFile + RAW_TEXT_GLOBAL_VARIABLE_TEMPLATE);
        }
        return null;
    }
    
    private String readFile(File rawTextGlobalVariableTemplateFile, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(rawTextGlobalVariableTemplateFile.getAbsolutePath()));
        return new String(encoded, encoding);
    }
}
