package com.kms.katalon.execution.logging;

import java.util.List;

import com.kms.katalon.core.logging.XmlLogRecord;

public interface ILogCollection {
    void addLogRecords(List<XmlLogRecord> records);
    
    List<XmlLogRecord> getLogRecords();

    void finish();
}
