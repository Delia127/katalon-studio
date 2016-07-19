package com.kms.katalon.core.testdata.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.InternalData;

public class HTMLTableData extends ExcelData {

    private InternalData internalData;

    private String sheetName;

    // lookup for changing header mode
    private List<String> columnNames;

    private List<String[]> dataBody;

    public HTMLTableData(String sourceUrl, boolean hasHeaders) throws IOException {
        super(sourceUrl, hasHeaders);
        sheetName = FilenameUtils.getBaseName(sourceUrl);
    }

    @Override
    public String[] getColumnNames() throws IOException {
        return getInternalData().getColumnNames();
    }

    @Override
    public int getRowNumbers() throws IOException {
        return getInternalData().getRowNumbers();
    }

    @Override
    public int getColumnNumbers() throws IOException {
        return getInternalData().getColumnNumbers();
    }

    InternalData getInternalData() throws IOException {
        if (internalData == null) {
            Document doc = Jsoup.parse(new File(sourceUrl), null);
            Elements tables = doc.getElementsByTag("table");
            if (tables == null || tables.isEmpty()) {
                throw new IllegalArgumentException("No table found.");
            }
            Element table = tables.first();

            Elements headers = table.getElementsByTag("thead");

            if (headers == null || headers.isEmpty()) {
                throw new IllegalArgumentException("No thead found.");
            }

            Elements bodys = table.getElementsByTag("tbody");
            if (bodys == null || bodys.isEmpty()) {
                throw new IllegalArgumentException("No tbody found.");
            }

            Elements headerElements = headers.first().getElementsByTag("tr").first().getElementsByTag("th");
            Elements bodyRows = bodys.first().getElementsByTag("tr");

            columnNames = collectColumnNames(headerElements);
            dataBody = collectData(bodyRows);

            createInternalData();
        }
        return internalData;
    }

    private void createInternalData() {
        List<String> columnNamesCoppied = new ArrayList<String>(columnNames);
        List<String[]> dataBodyCoppied = new ArrayList<String[]>(dataBody);
        if (hasHeaders) {
            internalData = new InternalData(getSourceUrl(), dataBodyCoppied, columnNamesCoppied);
        } else {
            dataBodyCoppied.add(0, columnNamesCoppied.toArray(new String[columnNamesCoppied.size()]));
            internalData = new InternalData(getSourceUrl(), dataBodyCoppied,
                    Arrays.asList(ArrayUtils.nullToEmpty(new String[columnNamesCoppied.size()])));
        }
    }

    private List<String> collectColumnNames(Elements headerElements) {
        List<String> columnNames = new ArrayList<String>(headerElements.size());
        for (int colIdx = 0; colIdx < headerElements.size(); colIdx++) {
            columnNames.add(headerElements.get(colIdx).text());
        }
        return columnNames;
    }

    private List<String[]> collectData(Elements bodyRows) {
        List<String[]> data = new ArrayList<String[]>(bodyRows.size());
        for (int rowIdx = 0; rowIdx < bodyRows.size(); rowIdx++) {
            Elements cellsAtRowIdx = bodyRows.get(rowIdx).getElementsByTag("td");
            String[] valuesAtRowIdx = new String[cellsAtRowIdx.size()];
            for (int colIdx = 0; colIdx < cellsAtRowIdx.size(); colIdx++) {
                valuesAtRowIdx[colIdx] = cellsAtRowIdx.get(colIdx).text();
            }
            data.add(valuesAtRowIdx);
        }
        return data;
    }

    @Override
    public String[] getSheetNames() {
        return new String[] { sheetName };
    }

    @Override
    public void changeSheet(String sheetName) {
        // Not thing to change here
    }

    @Override
    public int getMaxColumn(int rowIndex) throws IOException {
        return getInternalData().getColumnNumbers();
    }

    @Override
    protected String internallyGetValue(String columnName, int rowIndex) throws IOException {
        return getInternalData().internallyGetValue(columnName, rowIndex);
    }

    @Override
    protected String internallyGetValue(int columnIdx, int rowIndex) throws IOException {
        return getInternalData().internallyGetValue(columnIdx, rowIndex);
    }

    @Override
    public void activeHeaders(boolean active) throws IOException {
        super.activeHeaders(active);
        if (internalData == null) {
            return;
        }
        createInternalData();
    }

}
