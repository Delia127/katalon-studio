package com.kms.katalon.composer.components.part;

/**
 * Composer Part Interface
 * <p>
 * Should use for main entity part.
 * <p>
 * <i>For example, part of Test Case, Test Object, Test Data, Test Suite, and Report</i>
 */
public interface IComposerPart {
    /**
     * Get entity ID
     * 
     * @return Opened Entity ID
     */
    public String getEntityId();

    default boolean isDraft() {
        return false;
    }

    default String getPartId() {
        return null;
    }
}
