package com.kms.katalon.core.helper.screenrecorder;

public enum VideoQuality {
    LOW("LOW", 8), MEDIUM("MEDIUM", 16), HIGH("HIGH", 24);

    private String name;

    private int depth;

    private VideoQuality(String name, int depth) {
        this.name = name;
        this.setDepth(depth);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
