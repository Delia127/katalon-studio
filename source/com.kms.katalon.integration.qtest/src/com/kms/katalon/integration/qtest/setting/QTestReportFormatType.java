package com.kms.katalon.integration.qtest.setting;

public enum QTestReportFormatType {
    HTML("HTML file"), CSV("CSV file"), PDF("PDF file"), LOG("Log files");

    private final String text;

    private QTestReportFormatType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getFileExtension() {
        switch (this) {
            case CSV:
                return "csv";
            case HTML:
                return "html";
            case LOG:
                return "log";
            case PDF:
                return "pdf";
        }
        return "";
    }
    
    public static QTestReportFormatType getTypeByExtension(String ext) {
        for (QTestReportFormatType formatType : values()) {
            if (formatType.getFileExtension().equalsIgnoreCase(ext)) {
                return formatType;
            }
        }
        return null;
    }
}
