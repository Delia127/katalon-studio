package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class ImportNodeCollection {

    private List<ImportNodeWrapper> importNodes;

    /**
     * Key: class full name
     * Value: List of imported alias names. Ordered by declared import in script. The last one will be the best match.
     */
    private Map<String, Set<String>> importedAliasNamesForClassFullName;

    /**
     * Key: alias name
     * Value: class full name
     */
    private Map<String, String> importedClassNameForAlias;

    private Map<String, ImportNodeWrapper> importedNodeForClassFullName;

    public List<ImportNodeWrapper> getImportNodes() {
        return importNodes;
    }

    public void setImportNodes(List<ImportNodeWrapper> importNodes) {
        this.importNodes = importNodes;
    }

    public ImportNodeCollection() {
        importNodes = new ArrayList<>();
        importedAliasNamesForClassFullName = new HashMap<>();
        importedClassNameForAlias = new HashMap<>();
        importedNodeForClassFullName = new HashMap<>();
    }

    public boolean hasAlias(String qualifiedName) {
        return getBestMatchForAliasName(qualifiedName) != null;
    }

    public boolean isImported(String aliasName) {
        return importedClassNameForAlias.get(aliasName) != null;
    }

    public String getQualifierForAlias(String aliasName) {
        return importedClassNameForAlias.get(aliasName);
    }

    public String getBestMatchForAliasName(String qualifier) {
        Set<String> aliasNames = getAliasNames(qualifier);
        if (aliasNames.isEmpty()) {
            return null;
        }
        int size = aliasNames.size();
        return aliasNames.toArray(new String[size])[size - 1];
    }

    public void clear() {
        importNodes.clear();
        importedAliasNamesForClassFullName.clear();
        importedClassNameForAlias.clear();
        importedNodeForClassFullName.clear();
    }

    public void addImportNode(ImportNodeWrapper newImportWrapper) {
        if (importNodes.contains(newImportWrapper)) {
            return;
        }
        importNodes.add(newImportWrapper);
        ImportNodeEntry entry = analyze(newImportWrapper);
        if (entry == null) {
            return;
        }

        String qualifiedName = entry.getQualifiedName();
        Set<String> aliasNames = getAliasNames(qualifiedName);
        String newAliasName = entry.getAliasName();
        aliasNames.add(newAliasName);
        importedAliasNamesForClassFullName.put(qualifiedName, aliasNames);
        importedClassNameForAlias.put(newAliasName, qualifiedName);
        importedNodeForClassFullName.put(qualifiedName, newImportWrapper);
    }

    public Class<?> resolve(String qualifiedName) {
        ImportNodeWrapper wrapper = importedNodeForClassFullName.get(qualifiedName);
        if (wrapper == null) {
            return null;
        }

        Class<?> typeClass = wrapper.getType().getTypeClass();
        String fieldName = wrapper.getFieldName();
        if (ImportNodeStatistics.evaluate(wrapper) != ImportNodeStatistics.SIMPLE_STATIC_IMPORT
                && StringUtils.isEmpty(fieldName)) {
            return typeClass;
        }
        return AstKeywordsInputUtil.getFirstAccessibleMethodReturnType(typeClass, fieldName, true);
    }

    private Set<String> getAliasNames(String qualifiedName) {
        Set<String> aliasNames = importedAliasNamesForClassFullName.get(qualifiedName);
        if (aliasNames == null) {
            aliasNames = new LinkedHashSet<>();
            importedAliasNamesForClassFullName.put(qualifiedName, aliasNames);
        }
        return aliasNames;
    }

    public ImportNodeEntry analyze(ImportNodeWrapper newImportWrapper) {
        if (newImportWrapper == null) {
            return null;
        }
        switch (ImportNodeStatistics.evaluate(newImportWrapper)) {
            case SIMPLE_IMPORT:
                return new ImportNodeEntry(newImportWrapper.getClassName(), newImportWrapper.getKnownAlias());
            case SIMPLE_STATIC_IMPORT:
                return new ImportNodeEntry(newImportWrapper.getClassName() + "." + newImportWrapper.getFieldName(),
                        newImportWrapper.getKnownAlias());
            default:
                return null;
        }
    }

    private class ImportNodeEntry {
        private final String qualifiedName;

        private final String aliasName;

        public ImportNodeEntry(String qualifiedName, String aliasName) {
            this.qualifiedName = qualifiedName;
            this.aliasName = aliasName;
        }

        public String getAliasName() {
            return aliasName;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }
    }

    private enum ImportNodeStatistics {
        SIMPLE_IMPORT, SIMPLE_STATIC_IMPORT, STAR_IMPORT, STATIC_STAR_IMPORT;

        private static ImportNodeStatistics evaluate(ImportNodeWrapper newImportWrapper) {
            if (newImportWrapper.isStatic() && newImportWrapper.isStar()) {
                return STATIC_STAR_IMPORT;
            }
            
            if (newImportWrapper.isStar()) {
                return STAR_IMPORT;
            }
            
            if (newImportWrapper.isStatic()) {
                return SIMPLE_STATIC_IMPORT;
            }
            
            return SIMPLE_IMPORT;
        }
    }
}
