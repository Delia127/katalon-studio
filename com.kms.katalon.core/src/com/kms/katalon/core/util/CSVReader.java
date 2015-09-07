package com.kms.katalon.core.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CSVReader {
    private ICsvListReader listReader;
    private List<String[]> data;
    private String[] columnNames;
    private boolean containsHeader;

    public CSVReader(String sourceUrl, CSVSeperator seperator, boolean containHeader) throws Exception {
        try {
            this.containsHeader = containHeader;
            FileReader reader = new FileReader(new File(sourceUrl));
            switch (seperator) {
            case COMMA:
                listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
                break;
            case SEMICOLON:
                listReader = new CsvListReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
                break;
            case TAB:
                listReader = new CsvListReader(reader, CsvPreference.TAB_PREFERENCE);
                break;
            }
            if (containHeader) {
                columnNames = listReader.getHeader(containHeader);
            }
            data = new ArrayList<String[]>();
            List<String> rowValues;
            while ((rowValues =  listReader.read()) != null) {
                data.add(rowValues.toArray(new String[rowValues.size()]));
            }
            
            if (!containHeader) {
                if (data.size() > 0) {
                    columnNames = new String[data.get(0).length];
                } else {
                    columnNames = new String[0];
                }
            }
        } finally {
            if (listReader != null) {
                listReader.close();
            }
        }
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public List<String[]> getData() {
        return data;
    }
    
    public int getColumnIndex(String columnName) {
        if (columnName != null && !columnName.isEmpty()) {
            for (int i = 0;  i < columnNames.length; i++) {
                if (columnNames[i] != null && columnNames[i].equals(columnName)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getColumnCount() throws IOException {
        if (containsHeader) {
            return columnNames.length;
        } else {
            if (data != null && data.size() > 0) {
                return data.get(0).length;
            }
            return 0;
        }
    }

}
