package com.kms.katalon.execution.logging;

import java.util.List;

import com.kms.katalon.core.logging.XmlLogRecord;

public interface ILogCollection {
    public void addLogRecords(List<XmlLogRecord> records);
    
    public List<XmlLogRecord> getLogRecords();
}
