package com.kms.katalon.custom.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.internal.GroovyConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
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

    /**
     * Generate GlobalVariable.class inside folder bin/lib/internal within current project
     * using Groovy template string.
     * 
     * @param libFolder The lib folder file
     * @param executionProfiles List of global profiles
     * @throws Exception
     */
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
        Class<?> clazz = Class.forName(TEMPLATE_CLASS_NAME);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod(GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME,
                new Object[] { globalVariableFile, globalVariables });
    }
    
    /**
     * Generate GlobalVariable.class inside folder bin/lib/internal within current project
     * by using pure Java
     * 
     * @param libFolder The lib folder file
     * @param executionProfiles List of global profiles
     * @throws Exception
     */
    public void generateGlobalVariableLibFileV2(File libFolder, List<ExecutionProfileEntity> executionProfiles)
            throws Exception {
        String libFolderPath = libFolder.getAbsolutePath();
        File internalPackageFolder = new File(libFolderPath, INTERNAL_PACKAGE_NAME);
        File internalGlobalVariableFile = new File(internalPackageFolder, GLOBAL_VARIABLE_FILE_NAME);
        generateGlobalVariableFileV2(executionProfiles, internalGlobalVariableFile);
    }

    private void generateGlobalVariableFileV2(List<ExecutionProfileEntity> globalVariables, File globalVariableFile)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if (!globalVariableFile.exists()) {
            globalVariableFile.getParentFile().mkdirs();
            globalVariableFile.createNewFile();
        }
        internallyGenerateGlobalVariableFileV2(globalVariables, globalVariableFile);
    }
    
    private String getGlobalVariableDeclarationsString(GlobalVariableEntity variable) {
        return "\t/**\n" + "*" + escapeHtmlForJavadoc(variable.getDescription()) + "\n" + "\t*/\n" + "\tpublic static Object "
                + variable.getName() + "\n";
    }

    private String getGlobalVariableAssignmentString(GlobalVariableEntity variable) {
        return "\t\n" + variable.getName() + " = " + "selectedVariables['" + variable.getName() + "']";
    }

    private void internallyGenerateGlobalVariableFileV2(List<ExecutionProfileEntity> globalVariables,
            File globalVariableFile) {
        try {
            Map<String, GlobalVariableEntity> declaredGlobalVariables = getDeclaredGlobalVariables(globalVariables);
            String globalVariableDeclarations = declaredGlobalVariables.entrySet()
                    .stream()
                    .map(entry -> getGlobalVariableDeclarationsString(entry.getValue()))
                    .collect(Collectors.joining());

            String globalVariablesAssignment = declaredGlobalVariables.entrySet()
                    .stream()
                    .map(entry -> getGlobalVariableAssignmentString(entry.getValue()))
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
                String globalVariableContent = sub.replace(rawTextGlobalVariableTemplate);

                OutputStreamWriter out = null;
                try {
                    out = new OutputStreamWriter(new FileOutputStream(globalVariableFile), StandardCharsets.UTF_8);
                    out.write(globalVariableContent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage());
                } finally {
                    out.close();
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex.getMessage());
        }
    }
    
    private Map<String, GlobalVariableEntity> getDeclaredGlobalVariables(List<ExecutionProfileEntity> globalVariables) {
        Map<String, GlobalVariableEntity> result = new HashMap<>();
        globalVariables.forEach(p -> {
            p.getGlobalVariableEntities().forEach(it -> {
                String variableName = it.getName();

                boolean isValidVariableName = GroovyConstants.isValidVariableName(variableName);
                if (!isValidVariableName) {
                    return;
                }

                GlobalVariableEntity variable;
                if (result.containsKey(variableName)) {
                    variable = result.get(variableName);
                } else {
                    variable = new GlobalVariableEntity();
                    variable.setName(variableName);
                }

                String concatDes = concatDescriptions(variable.getDescription(), it.getDescription(), p);
                variable.setDescription(concatDes);

                result.put(variableName, variable);
            });
        });
        return result;
    }

    private String escapeHtmlForJavadoc(String description) {
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
    
    private String concatDescriptions(String oldDes, String newDes, ExecutionProfileEntity profile) {
        if (StringUtils.isEmpty(newDes)) {
            return oldDes;
        }
        String newDesForProfile = MessageFormat.format("Profile {0} : {1}", profile.getName(), newDes);
        if (StringUtils.isEmpty(oldDes)) {
            return newDesForProfile;
        }
        return oldDes + "\n" + newDesForProfile;
    }
}
