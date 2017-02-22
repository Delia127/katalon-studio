package com.kms.katalon.composer.codeassist.proposal;

public class ReplacementInfo {
    private final int startIndex;

    private final int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public ReplacementInfo(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

}
