package com.kms.katalon.core.testdata.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CSVReader {
    private ICsvListReader listReader;
    private List<String[]> data;
    private String[] columnNames;
    private boolean containsHeader;

    public CSVReader(String sourceUrl, CSVSeperator seperator, boolean containHeader) throws IOException {
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
            while ((rowValues = listReader.read()) != null) {
                data.add(rowValues.toArray(new String[rowValues.size()]));
            }

            if (!containHeader) {
                if (data.size() > 0) {
                    columnNames = new String[data.get(0).length];
                } else {
                    columnNames = new String[0];
                }
            }

            if (data.size() > 0) {
                if (containHeader) {
                    columnNames = data.get(0);
                } else {
                    columnNames = new String[data.get(0).length];
                }
            }
        } finally {
            IOUtils.closeQuietly(listReader);
        }
    }

    public String[] getColumnNames() {
        if (columnNames == null) {
            columnNames = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return columnNames;
    }

    /**
     * Get all available data, include header row, care should be taken when call this method
     * 
     * @return
     */
    public List<String[]> getData() {
        return data;
    }

    public int getColumnIndex(String columnName) {
        if (columnName != null && !columnName.isEmpty()) {
            for (int i = 0; i < getColumnNames().length; i++) {
                if (columnNames[i] != null && getColumnNames()[i].equals(columnName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getColumnCount() throws IOException {
        if (containsHeader) {
            return getColumnNames().length;
        } else {
            if (data != null && data.size() > 0) {
                return data.get(0).length;
            }
            return 0;
        }
    }

}
