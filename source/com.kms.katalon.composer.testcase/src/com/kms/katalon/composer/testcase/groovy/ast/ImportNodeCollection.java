package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ImportNodeCollection {

    private List<ImportNodeWrapper> importNodes;

    /**
     * Key: class full name
     * Value: List of imported alias names. Ordered by declared import in script. The last one will be the best match.
     */
    private Map<String, SortedSet<String>> importedAliasNamesForClassFullName;

    /**
     * Key: alias name
     * Value: class full name
     */
    private Map<String, String> importedClassNameForAlias;

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
        SortedSet<String> aliasNames = getAliasNames(qualifier);
        if (aliasNames.isEmpty()) {
            return null;
        }
        return aliasNames.last();
    }

    public void clear() {
        importNodes.clear();
        importedAliasNamesForClassFullName.clear();
        importedClassNameForAlias.clear();
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
        SortedSet<String> aliasNames = getAliasNames(qualifiedName);
        String newAliasName = entry.getAliasName();
        aliasNames.add(newAliasName);
        importedAliasNamesForClassFullName.put(qualifiedName, aliasNames);
        importedClassNameForAlias.put(newAliasName, qualifiedName);
    }

    private SortedSet<String> getAliasNames(String qualifiedName) {
        SortedSet<String> aliasNames = importedAliasNamesForClassFullName.get(qualifiedName);
        if (aliasNames == null) {
            aliasNames = new TreeSet<>();
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
            int value = (booleanToInt(newImportWrapper.isStatic()) & 1 >> 0)
                    | (booleanToInt(newImportWrapper.isStar()) & 1 >> 1);
            return ImportNodeStatistics.values()[value];
        }

        private static int booleanToInt(boolean b) {
            return b ? 1 : 0;
        }

    }
}
