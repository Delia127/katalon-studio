package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.annotation.RequireAstTestStepTransformation;
import com.kms.katalon.core.export.ExportTestCaseHelper;
import com.kms.katalon.core.export.ExportTestCaseScript;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.dal.exception.TaskCancelledException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestStepCallTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ExportFileServiceManager {
    private int progress;
    private boolean isCancelled;
    List<TestStepCallTestCaseLink> testStepCallTestCaseLinks;
    List<String> exportedTestCaseLocations;
    Queue<String> logs;
    private static final String TEST_SCRIPT_SOURCE_FOLDER_NAME = "Scripts";
    private static final String TEST_CASE_ROOT_FOLDER_NAME = "Test Cases";
    private static final String LIB_FOLDER_NAME = "Libs";
    private static final String CLASSPATH_FILE_NAME = ".classpath";

    public ExportFileServiceManager() {
    }

    public boolean exportProject(ProjectEntity project, String destination) throws Exception {
        try {
            progress = 0;
            isCancelled = false;
            File currentProjectLocation = new File(project.getFolderLocation());
            File exportProjectLocation = new File(destination);
            FileUtils.copyDirectory(currentProjectLocation, exportProjectLocation);
            validateProgress(1);

            copyResourcesToExportedProject(exportProjectLocation);
            validateProgress(1);

            convertScriptFile(new File(exportProjectLocation.getAbsolutePath() + File.separator
                    + TEST_SCRIPT_SOURCE_FOLDER_NAME), exportProjectLocation);
            validateProgress(1);

            copyLibrariesToExportedProject(project, destination);
            validateProgress(1);
        } catch (Exception e) {
            File destinationFolder = new File(destination);
            if (destinationFolder.exists() && destinationFolder.isDirectory()) {
                FileUtils.deleteDirectory(destinationFolder);
            }
            throw e;
        }
        return true;
    }

    private void copyResourcesToExportedProject(File exportProjectFolder) throws IOException {
        FileUtils.copyDirectory(com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider.getDriverDirectory(),
                new File(exportProjectFolder.getAbsolutePath() + File.separator + "resources" + File.separator
                        + "drivers"));
    }

    private void copyLibrariesToExportedProject(ProjectEntity project, String destination) throws JavaModelException,
            IOException, ParserConfigurationException, SAXException, TransformerException {
        Map<String, String> libsChangedLocation = new HashMap<String, String>();
        File libFolder = new File(destination + File.separator + LIB_FOLDER_NAME);
        IProject iProject = GroovyUtil.getGroovyProject(project);
        IJavaProject javaProject = JavaCore.create(iProject);
        for (IClasspathEntry entry : Arrays.asList(javaProject.getRawClasspath())) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                File libFile = new File(entry.getPath().toOSString());
                if (libFile.exists() && libFile.isFile()) {
                    FileUtils.copyFileToDirectory(libFile, libFolder);
                    libsChangedLocation.put(libFile.getAbsolutePath().replace(File.separator, "/"),
                            (libFolder.getAbsolutePath() + File.separator + libFile.getName()).replace(File.separator,
                                    "/"));
                }
            }
        }

        File classPathFile = new File(project.getFolderLocation() + File.separator + CLASSPATH_FILE_NAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(classPathFile);
        NodeList classPathEntryNodeList = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < classPathEntryNodeList.getLength(); i++) {
            Node classPathEntry = classPathEntryNodeList.item(i);
            if (classPathEntry.getAttributes() != null) {
                Node kindAttributeNode = classPathEntry.getAttributes().getNamedItem("kind");
                if (kindAttributeNode != null && kindAttributeNode.getNodeValue().equals("lib")) {
                    Node pathAttributeNode = classPathEntry.getAttributes().getNamedItem("path");
                    if (pathAttributeNode != null && libsChangedLocation.get(pathAttributeNode.getNodeValue()) != null
                            && !libsChangedLocation.get(pathAttributeNode.getNodeValue()).isEmpty()) {
                        pathAttributeNode.setNodeValue(libsChangedLocation.get(pathAttributeNode.getNodeValue()));
                    }
                }
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(destination + File.separator + CLASSPATH_FILE_NAME));

        transformer.transform(source, result);

    }

    public void convertGroovyScriptIntoExportScript(List<ASTNode> astNodes, String testCaseId, File exportProjectFolder)
            throws IOException {
        ClassNode mainClass = null;
        for (ASTNode astNode : astNodes) {
            if (astNode instanceof ClassNode) {
                mainClass = (ClassNode) astNode;
                break;
            }
        }
        if (mainClass != null) {
            mainClass.setSuperClass(new ClassNode(ExportTestCaseScript.class));
            mainClass.addAnnotation(new AnnotationNode(new ClassNode(RequireAstTestStepTransformation.class)));
            MethodNode method = mainClass.getMethods("main").get(0);
            convertMainMethod(testCaseId, exportProjectFolder, method);
            mainClass.getModule().addImport("ExportTestCaseHelper", new ClassNode(ExportTestCaseHelper.class));
        }
    }

    protected void convertMainMethod(String testCaseId, File exportProjectFolder, MethodNode method) throws IOException {
        BlockStatement blockStatement = new BlockStatement();
        List<Expression> argumentList = new ArrayList<Expression>();
        argumentList.add(new VariableExpression("this"));
        argumentList.add(new ConstantExpression(testCaseId));
        argumentList.add(new ConstantExpression(exportProjectFolder.getAbsolutePath()));
        argumentList.add(new ConstantExpression(getDefaultDriver()));
        switch (getDefaultDriver()) {
        case "Chrome":
            argumentList.add(new ConstantExpression(getDriverNewLocation(
                    SeleniumWebDriverProvider.getChromeDriverPath(), exportProjectFolder)));
            break;
        case "IE":
            argumentList.add(new ConstantExpression(getDriverNewLocation(SeleniumWebDriverProvider.getIEDriverPath(),
                    exportProjectFolder)));
            break;
        default:
            argumentList.add(new ConstantExpression(null));
        }
        argumentList.add(new ConstantExpression(getDefaultPageLoadTimeout()));

        blockStatement.addStatement(new ExpressionStatement(new MethodCallExpression(new VariableExpression("super"),
                "main", new ArgumentListExpression(argumentList))));
        method.setCode(blockStatement);
        method.setLineNumber(1);
        method.setModifiers(Modifier.STATIC);
    }

    protected String getDriverNewLocation(String oldLocation, File exportProjectLocation) {
        String driverPath = oldLocation.substring(oldLocation.lastIndexOf("resources"), oldLocation.length());
        return exportProjectLocation.getAbsolutePath() + File.separator + driverPath;
    }

    private String getDefaultDriver() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        return store.getString(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION);
    }

    public static int getDefaultPageLoadTimeout() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        return store.getInt(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT);
    }

    public void convertScriptFile(File file, File exportProjectFolder) throws IOException, Exception {
        if (file.exists() && file.isFile() && file.getName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
            String script = FileUtils.readFileToString(file);
            script = script.replaceAll(".*.callTestCase", "ExportTestCaseHelper.callTestCase");
            List<ASTNode> groovyAst = GroovyParser.parseGroovyScriptIntoAstNodes(script);
            convertGroovyScriptIntoExportScript(groovyAst, getTestCaseIdFromScriptFile(file, exportProjectFolder),
                    exportProjectFolder);
            StringBuilder stringBuilder = new StringBuilder();
            GroovyParser parser = new GroovyParser(stringBuilder);
            parser.parseGroovyAstIntoClass(groovyAst);
            FileUtils.writeStringToFile(file, stringBuilder.toString());
        } else if (file.exists() && file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                convertScriptFile(childFile, exportProjectFolder);
            }
        }
    }

    private String getTestCaseIdFromScriptFile(File scriptFile, File exportProjectFolder) {
        String testCaseRelativeId = scriptFile.getAbsolutePath()
                .replace(exportProjectFolder.getAbsolutePath() + File.separator, "")
                .replace(TEST_SCRIPT_SOURCE_FOLDER_NAME + File.separator, "")
                .replace(File.separator + scriptFile.getName(), "");
        return TEST_CASE_ROOT_FOLDER_NAME + File.separator + testCaseRelativeId;
    }

    public boolean exportSeletectTestCases(List<TestCaseEntity> testCases, ProjectEntity project, String destination)
            throws Exception {
        try {
            progress = 0;
            isCancelled = false;
            logs = new LinkedList<String>();
            exportedTestCaseLocations = new ArrayList<String>();

            for (TestCaseEntity testCase : testCases) {
                exportSelectedTestCase(testCase, project, destination);
                exportedTestCaseLocations.add(testCase.getLocation());
            }

            exportProjectEntity(project, destination);
            validateProgress(1);
            validateProgress(2);

        } catch (Exception e) {
            File destinationFolder = new File(destination);
            if (destinationFolder.exists() && destinationFolder.isDirectory()) {
                FileUtils.deleteDirectory(destinationFolder);
            }
            throw e;
        }
        return true;
    }

    private void exportSelectedTestCase(TestCaseEntity testCase, ProjectEntity project, String destination)
            throws Exception {
        exportTestCase(testCase, destination);
        validateProgress(4);

        for (DataFileEntity dataFile : testCase.getDataFiles()) {
            exportDataFile(dataFile, destination);
        }
        validateProgress(4);
    }

    private void exportEntity(FileEntity entity, String destination) throws IOException {
        File entityFile = new File(entity.getLocation());
        File destinationFolder = new File(destination);
        if (entityFile.exists() && entityFile.isFile()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            FileUtils.copyFileToDirectory(entityFile, destinationFolder);
        }
    }

    private void exportProjectEntity(ProjectEntity project, String destination) throws Exception {
        File destinationFolder = new File(destination);
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        EntityService.getInstance().saveEntity(project,
                destination + File.separator + project.getName() + ProjectEntity.getProjectFileExtension());
    }

    private void exportTestCase(TestCaseEntity testCase, String destination) throws Exception {
        exportEntity(testCase, destination + File.separator + getRelativePath(testCase));
    }

    private void exportDataFile(DataFileEntity dataFile, String destination) throws Exception {
        exportEntity(dataFile, destination + File.separator + getRelativePath(dataFile));
    }

    private String getRelativePath(FolderEntity folder) {
        return folder.getParentFolder() == null ? folder.getName() : getRelativePath(folder.getParentFolder())
                + File.separator + folder.getName();
    }

    private String getRelativePath(TestCaseEntity testCase) {
        return getRelativePath(testCase.getParentFolder());
    }

    private String getRelativePath(DataFileEntity dataFile) {
        return getRelativePath(dataFile.getParentFolder());
    }

    private boolean checkCancel() {
        return isCancelled;
    }

    public void cancelExport() {
        isCancelled = false;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private void validateProgress(int progressIncrease) throws TaskCancelledException {
        progress += progressIncrease;
        if (checkCancel()) {
            throw new TaskCancelledException();
        }
    }

}
