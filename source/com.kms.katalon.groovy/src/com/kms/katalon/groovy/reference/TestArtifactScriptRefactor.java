package com.kms.katalon.groovy.reference;

import static com.kms.katalon.constants.GlobalStringConstants.CR_DOUBLE_PRIMES;
import static com.kms.katalon.constants.GlobalStringConstants.CR_LEFT_PARENTHESIS;
import static com.kms.katalon.constants.GlobalStringConstants.CR_PRIME;
import static com.kms.katalon.constants.GlobalStringConstants.CR_RIGHT_PARENTHESIS;
import static com.kms.katalon.constants.GlobalStringConstants.ENTITY_ID_SEPARATOR;
import static com.kms.katalon.constants.GlobalStringConstants.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestArtifactScriptRefactor {

    private FolderType parentType;

    private String entityId;

    private boolean hasRightBrackets;

    public TestArtifactScriptRefactor(FolderType parentType, String entityId, boolean hasRightBrackets) {
        this.parentType = parentType;
        this.entityId = entityId;
        this.hasRightBrackets = hasRightBrackets;
    }

    public FolderType getParentType() {
        return parentType;
    }

    public String getEntityId() {
        return entityId;
    }

    private String getReferencePrefix() {
        switch (parentType) {
            case DATAFILE:
                return "findTestData";
            case TESTCASE:
                return "findTestCase";
            case WEBELEMENT:
                return "findTestObject";
            default:
                return StringUtils.EMPTY;
        }
    }

    private String getRelativeId(String s) {
        int firstSeparatorIdx = s.indexOf(ENTITY_ID_SEPARATOR);
        if (firstSeparatorIdx < 0) {
            return s;
        }
        return s.substring(firstSeparatorIdx + 1, s.length());
    }

    private String buildBrackets(String s, String left, String right) {
        String output = left + s;
        if (hasRightBrackets) {
            output += right;
        }
        return output;
    }

    private String buildParentheses(String s) {
        return buildBrackets(s, CR_LEFT_PARENTHESIS, CR_RIGHT_PARENTHESIS);
    }

    private String buildQuote(String s) {
        return buildBrackets(s, CR_PRIME, CR_PRIME);
    }

    private String buildDoubleQuotes(String s) {
        return buildBrackets(s, CR_DOUBLE_PRIMES, CR_DOUBLE_PRIMES);
    }

    public List<String> getReferenceStrings() {
        List<String> referenceString = new ArrayList<>();
        referenceString.addAll(getQuotedReferences());
        referenceString.addAll(getDoubleQuotedReferenceStrings());
        return referenceString;
    }

    private List<String> getQuotedReferences() {
        String prefix = getReferencePrefix();
        String relativeId = getRelativeId(entityId);
        return Arrays.asList(new String[] { prefix + buildParentheses(buildQuote(entityId)),
                prefix + buildParentheses(buildQuote(relativeId)), });
    }

    private List<String> getDoubleQuotedReferenceStrings() {
        String prefix = getReferencePrefix();
        String relativeId = getRelativeId(entityId);
        return Arrays.asList(new String[] { prefix + buildParentheses(buildDoubleQuotes(entityId)),
                prefix + buildParentheses(buildDoubleQuotes(relativeId)), });
    }

    public void replace(String newEntityId, IFile scriptFile) throws IOException, CoreException {
        String scriptContent = getScriptContent(scriptFile);
        String relativeId = getRelativeId(newEntityId);
        String referencePrefix = getReferencePrefix();

        boolean updated = false;
        String newQuotedScript = referencePrefix + buildParentheses(buildQuote(relativeId));
        for (String potentialQuote : getQuotedReferences()) {
            if (!scriptContent.contains(potentialQuote)) {
                continue;
            }

            scriptContent = scriptContent.replace(potentialQuote, newQuotedScript);
            updated = true;
        }

        String newDoubleQuotedScript = referencePrefix + buildParentheses(buildDoubleQuotes(relativeId));
        for (String potentialDoubleQuotes : getDoubleQuotedReferenceStrings()) {
            if (!scriptContent.contains(potentialDoubleQuotes)) {
                continue;
            }

            scriptContent = scriptContent.replace(potentialDoubleQuotes, newDoubleQuotedScript);
            updated = true;
        }
        
        if (!updated) {
            return;
        }

        try (InputStream is = IOUtils.toInputStream(scriptContent)){
            scriptFile.setContents(is, true, true, null);
            scriptFile.refreshLocal(IResource.DEPTH_ZERO, null);
        }
    }

    private String getScriptContent(IFile scriptFile) throws IOException, CoreException {
        try (InputStream scriptFileStreamContent = scriptFile.getContents()) {
            return IOUtils.toString(scriptFileStreamContent);
        }
    }

    public boolean hasReferenceInScript(IFile scriptFile) throws IOException, CoreException {
        String scriptContent = getScriptContent(scriptFile);
        for (String potentialRef : getReferenceStrings()) {
            if (scriptContent.contains(potentialRef)) {
                return true;
            }
        }
        return false;
    }

    public List<IFile> findReferrers(List<IFile> files) throws IOException, CoreException {
        List<IFile> referrers = new ArrayList<>();
        for (IFile scriptFile : files) {
            if (hasReferenceInScript(scriptFile)) {
                referrers.add(scriptFile);
            }
        }
        return referrers;
    }
    
    public List<IFile> findReferrersInTestCaseScripts(ProjectEntity projectEntity) throws IOException, CoreException {
        return findReferrers(GroovyUtil.getAllTestCaseScripts(projectEntity));
    }

    public void updateReferenceForProject(String newScript, ProjectEntity projectEntity) throws IOException,
            CoreException {
        updateReferenceForTestCaseFolder(newScript, projectEntity);
        updateReferenceForCustomKeywords(newScript, projectEntity);
    }

    public void updateReferenceForTestCaseFolder(String newScript, ProjectEntity projectEntity) throws IOException,
            CoreException {
        updateReferences(newScript, GroovyUtil.getAllTestCaseScripts(projectEntity));
    }

    public void updateReferenceForCustomKeywords(String newScript, ProjectEntity projectEntity) throws IOException,
            CoreException {
        updateReferences(newScript, GroovyUtil.getAllCustomKeywordsScripts(projectEntity));
    }

    public void updateReferences(String newScript, List<IFile> files) throws IOException, CoreException {
        for (IFile scriptFile : files) {
            replace(newScript, scriptFile);
        }
    }
    
    public void removeReferences(List<IFile> files) throws IOException, CoreException {
        updateReferences(NULL, files);
    }
    
    public static TestArtifactScriptRefactor createForTestDataEntity(String testDataId) {
        return new TestArtifactScriptRefactor(FolderType.DATAFILE, testDataId, true);
    }
    
    public static TestArtifactScriptRefactor createForTestCaseEntity(String testCaseId) {
        return new TestArtifactScriptRefactor(FolderType.TESTCASE, testCaseId, true);
    }
    
    public static TestArtifactScriptRefactor createForTestObjectEntity(String testObjectId) {
        return new TestArtifactScriptRefactor(FolderType.WEBELEMENT, testObjectId, true);
    }
    
    public static TestArtifactScriptRefactor createForFolderEntity(FolderEntity folder) {
        return new TestArtifactScriptRefactor(folder.getFolderType(), folder.getIdForDisplay() + ENTITY_ID_SEPARATOR,
                false);
    }
}
