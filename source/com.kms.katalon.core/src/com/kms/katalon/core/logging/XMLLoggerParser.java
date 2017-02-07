package com.kms.katalon.core.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.core.constants.CoreMessageConstants;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestStepLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;

public class XMLLoggerParser {

    public static final String LOG_RECORD_PROP_NAME_ATTRIBUTE = "name";

    public static final String LOG_RECORD_NODE_NAME = "record";

    public static final String LEVEL_NODE_NAME = "level";

    public static final String MESSAGE_NODE_NAME = "message";

    public static final String MILLIS_NODE_NAME = "millis";

    public static final String METHOD_NODE_NAME = "method";

    // private static final String NESTED_LEVEL_NODE_NAME = "nestedLevel";
    // private static final String START_TIME_NODE_NAME = "startTime";
    public static final String EXCEPTION_NODE_NAME = "exception";

    public static final String EXCEPTION_FRAME_NODE_NAME = "frame";

    public static final String EXCEPTION_CLASS_NODE_NAME = "class";

    public static final String EXCEPTION_METHOD_NODE_NAME = "method";

    public static final String EXCEPTION_LINE_NODE_NAME = "line";

    public static final String LOG_RECORD_PROP_NODE_NAME = "property";

    private static final String EXECUTION_LOG_FILE_BASE = "execution";

    public static String unescapeString(String text) {
        return StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeXml(text));
    }

    public static String getRecordDate(LogRecord record) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
        return format.format(new Date(record.getMillis()));
    }

    public static List<XmlLogRecord> readFromString(String xmlString) throws XMLStreamException, FileNotFoundException {
        if (StringUtils.isEmpty(xmlString)) {
            return Collections.emptyList();
        }
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(new StringReader(xmlString));
            return readDocument(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static File[] getSortedLogFile(String logFolder) {
        File folder = new File(logFolder);
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return Pattern.matches(EXECUTION_LOG_FILE_BASE + "\\d+\\.log", name);
            }
        });
        // Descending sort file names
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                int num1 = Integer
                        .parseInt(FilenameUtils.getBaseName(f1.getName()).replace(EXECUTION_LOG_FILE_BASE, ""));
                int num2 = Integer
                        .parseInt(FilenameUtils.getBaseName(f2.getName()).replace(EXECUTION_LOG_FILE_BASE, ""));
                return num2 - num1;
            }
        });
        return files;
    }

    public static TestSuiteLogRecord readTestSuiteLogFromXMLFiles(String logFolder, IProgressMonitor progressMonitor)
            throws XMLStreamException, FileNotFoundException {
        File[] xmlFiles = getSortedLogFile(logFolder);
        if (xmlFiles == null || xmlFiles.length == 0) {
            return null;
        }
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        TestSuiteLogRecord testSuiteLogRecord = null;
        Deque<Object> stack = new ArrayDeque<Object>();
        progressMonitor.beginTask(CoreMessageConstants.MSG_INFO_PARSING_LOG_FILES, xmlFiles.length);
        for (File xmlFile : xmlFiles) {
            progressMonitor.subTask(MessageFormat.format(CoreMessageConstants.MSG_INFO_PARSING_X, xmlFile.getName()));
            if (progressMonitor.isCanceled()) {
                return null;
            }
            try {
                reader = inputFactory.createXMLStreamReader(new FileReader(xmlFile));
                while (reader.hasNext()) {
                    int eventType = reader.next();
                    switch (eventType) {
                        case XMLStreamReader.START_ELEMENT:
                            String elementName = reader.getLocalName();
                            if (!elementName.equals(LOG_RECORD_NODE_NAME)) {
                                continue;
                            }
                            XmlLogRecord xmlLogRecord = readRecord(reader);
                            final String sourceMethodName = xmlLogRecord.getSourceMethodName();
                            if (LogLevel.START.toString().equals(xmlLogRecord.getLevel().getName())) {
                                if (StringConstants.LOG_START_SUITE_METHOD.equals(sourceMethodName)) {
                                    testSuiteLogRecord = processStartTestSuiteLog(stack, logFolder, xmlLogRecord);
                                } else if (StringConstants.LOG_START_TEST_METHOD.equals(sourceMethodName)) {
                                    processStartTestCaseLog(stack, xmlLogRecord);
                                } else if (StringConstants.LOG_START_KEYWORD_METHOD.equals(sourceMethodName)) {
                                    processStartKeywordLog(stack, xmlLogRecord);
                                }
                            } else if (LogLevel.END.toString().equals(xmlLogRecord.getLevel().getName())) {
                                if (StringConstants.LOG_END_KEYWORD_METHOD.equals(sourceMethodName)
                                        || StringConstants.LOG_END_TEST_METHOD.equals(sourceMethodName)
                                        || StringConstants.LOG_END_SUITE_METHOD.equals(sourceMethodName)) {
                                    processEndLog(stack, xmlLogRecord);
                                }
                            } else if (LogLevel.RUN_DATA.toString().equals(xmlLogRecord.getLevel().getName())) {
                                testSuiteLogRecord.addRunData(xmlLogRecord.getProperties());
                            } else {
                                Object object = stack.peekLast();
                                if (object instanceof ILogRecord) {
                                    processStepMessageLog(xmlLogRecord, (ILogRecord) object);
                                }
                            }
                            break;
                        case XMLStreamReader.END_ELEMENT:
                            break;
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
                progressMonitor.worked(1);
            }
        }
        // If execution process crashed before completed
        while (stack.size() > 0) {
            Object object = stack.pollLast();
            if (object instanceof ILogRecord) {
                processInterruptedLog((ILogRecord) object);
            }
        }
        return testSuiteLogRecord;
    }

    private static void processInterruptedLog(ILogRecord logRecord) {
        ILogRecord[] childRecords = logRecord.getChildRecords();
        if (childRecords != null && childRecords.length > 0) {
            ILogRecord lastLogRecord = childRecords[childRecords.length - 1];
            logRecord.setEndTime(
                    lastLogRecord.getEndTime() != 0 ? lastLogRecord.getEndTime() : lastLogRecord.getStartTime());
        } else {
            logRecord.setEndTime(logRecord.getStartTime());
        }
        logRecord.setInterrupted(true);
    }

    private static void processStepMessageLog(XmlLogRecord xmlLogRecord, ILogRecord logRecord) {
        MessageLogRecord messageLogRecord = new MessageLogRecord();
        messageLogRecord.setStartTime(xmlLogRecord.getMillis());
        messageLogRecord.setMessage(xmlLogRecord.getMessage());

        if (xmlLogRecord.getProperties() != null
                && xmlLogRecord.getProperties().get(StringConstants.XML_LOG_ATTACHMENT_PROPERTY) != null) {
            messageLogRecord
                    .setAttachment(xmlLogRecord.getProperties().get(StringConstants.XML_LOG_ATTACHMENT_PROPERTY));
        }
        LogLevel logLevel = LogLevel.valueOf(xmlLogRecord.getLevel().toString());
        TestStatus testStatus = evalTestStatus(logRecord, logLevel);
        messageLogRecord.setStatus(testStatus);
        logRecord.addChildRecord(messageLogRecord);
    }

    private static void processEndLog(Deque<Object> stack, XmlLogRecord xmlLogRecord) {
        Object object = stack.pollLast();
        if (object != null && object instanceof ILogRecord) {
            ((ILogRecord) object).setEndTime(xmlLogRecord.getMillis());
        }
    }

    private static String getTestLogName(XmlLogRecord xmlLogRecord) {
        String testLogName = xmlLogRecord.getMessage();
        if (testLogName == null) {
            return "";
        }

        String startKeywordString = StringConstants.LOG_START_KEYWORD + " : ";
        if (testLogName.startsWith(startKeywordString)) {
            return testLogName.substring(startKeywordString.length(), testLogName.length());
        } else {
            return testLogName;
        }
    }

    private static void processStartKeywordLog(Deque<Object> stack, XmlLogRecord xmlLogRecord) {
        TestStepLogRecord testStepLogRecord = new TestStepLogRecord(getTestLogName(xmlLogRecord));
        testStepLogRecord.setStartTime(xmlLogRecord.getMillis());
        testStepLogRecord
                .setDescription(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_DESCRIPTION_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        try {
            testStepLogRecord
                    .setIndex(Integer.valueOf(xmlLogRecord.getProperties().get(StringConstants.XML_LOG_STEP_INDEX)));
            testStepLogRecord.setIgnoredIfFailed(
                    Boolean.valueOf(xmlLogRecord.getProperties().get(StringConstants.XML_LOG_IS_IGNORED_IF_FAILED)));
        } catch (NumberFormatException e) {
            // error with log, set -1 to indicate error
            testStepLogRecord.setIndex(-1);
        }
        Object object = stack.peekLast();
        if (object instanceof TestCaseLogRecord || object instanceof TestStepLogRecord) {
            ((ILogRecord) object).addChildRecord(testStepLogRecord);
        }

        stack.add(testStepLogRecord);
    }

    private static void processStartTestCaseLog(Deque<Object> stack, XmlLogRecord xmlLogRecord) {
        TestCaseLogRecord testCaseLogRecord = new TestCaseLogRecord(
                xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_NAME_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_NAME_PROPERTY)
                        : getTestLogName(xmlLogRecord));
        testCaseLogRecord.setStartTime(xmlLogRecord.getMillis());
        testCaseLogRecord.setId(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_ID_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_ID_PROPERTY) : "");
        testCaseLogRecord.setSource(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_SOURCE_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_SOURCE_PROPERTY) : "");
        testCaseLogRecord
                .setDescription(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_DESCRIPTION_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        testCaseLogRecord.setOptional(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_IS_OPTIONAL)
                ? Boolean.valueOf(xmlLogRecord.getProperties().get(StringConstants.XML_LOG_IS_OPTIONAL)) : false);
        Object object = stack.peekLast();
        if (object instanceof TestSuiteLogRecord || object instanceof TestStepLogRecord) {
            ((ILogRecord) object).addChildRecord(testCaseLogRecord);
        }
        stack.add(testCaseLogRecord);
    }

    private static TestSuiteLogRecord processStartTestSuiteLog(Deque<Object> stack, String logFolder,
            XmlLogRecord xmlLogRecord) {
        TestSuiteLogRecord testSuiteLogRecord = new TestSuiteLogRecord("", logFolder);
        testSuiteLogRecord.setStartTime(xmlLogRecord.getMillis());
        testSuiteLogRecord.setName(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_NAME_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_NAME_PROPERTY)
                : getTestLogName(xmlLogRecord));
        testSuiteLogRecord.setId(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_ID_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_ID_PROPERTY) : "");
        testSuiteLogRecord.setSource(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_SOURCE_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_SOURCE_PROPERTY) : "");
        testSuiteLogRecord
                .setDeviceName(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_DEVICE_ID_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_DEVICE_ID_PROPERTY) : "");
        testSuiteLogRecord.setDevicePlatform(
                xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_DEVICE_PLATFORM_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_DEVICE_PLATFORM_PROPERTY) : "");
        testSuiteLogRecord
                .setDescription(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_DESCRIPTION_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        stack.add(testSuiteLogRecord);
        return testSuiteLogRecord;
    }

    private static TestStatus evalTestStatus(ILogRecord logRecord, LogLevel level) {
        TestStatus testStatus = new TestStatus();
        testStatus.setStatusValue(TestStatusValue.valueOf(level.name()));
        return testStatus;
    }

    public static List<XmlLogRecord> readFromXMLFile(File xmlFile) throws XMLStreamException, FileNotFoundException {
        if (xmlFile == null || !xmlFile.exists()) {
            return Collections.emptyList();
        }
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(new FileInputStream(xmlFile), "UTF-8");
            return readDocument(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static List<XmlLogRecord> readFromLogFolder(String logFolder)
            throws XMLStreamException, FileNotFoundException {
        File[] files = getSortedLogFile(logFolder);
        List<XmlLogRecord> xmlLogRecords = new ArrayList<>();
        for (File file : files) {
            xmlLogRecords.addAll(readFromXMLFile(file));
        }
        return xmlLogRecords;
    }

    private static List<XmlLogRecord> readDocument(XMLStreamReader reader) throws XMLStreamException {
        List<XmlLogRecord> logRecords = new ArrayList<>();
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals(LOG_RECORD_NODE_NAME)) {
                        logRecords.add(readRecord(reader));
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    break;
            }
        }
        return logRecords;
    }

    private static XmlLogRecord readRecord(XMLStreamReader reader) throws XMLStreamException {
        XmlLogRecord record = new XmlLogRecord(Level.ALL, "");
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    switch (elementName) {
                        case LEVEL_NODE_NAME:
                            record.setLevel(LogLevel.valueOf(readCharacters(reader)).getLevel());
                            break;
                        case MESSAGE_NODE_NAME:
                            record.setMessage(unescapeString(readCharacters(reader)));
                            break;
                        case MILLIS_NODE_NAME:
                            record.setMillis(readLong(reader));
                            break;
                        case METHOD_NODE_NAME:
                            record.setSourceMethodName(readCharacters(reader));
                            break;
                        case EXCEPTION_NODE_NAME:
                            record.setExceptions(readExceptions(reader));
                            break;
                        case LOG_RECORD_PROP_NODE_NAME:
                            String propName = reader.getAttributeValue(null, LOG_RECORD_PROP_NAME_ATTRIBUTE);
                            String propVal = readCharacters(reader);
                            record.getProperties().put(propName, propVal);
                        default:
                            break;
                    }
                case XMLStreamReader.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if (elementName.equals(LOG_RECORD_NODE_NAME)) {
                        return record;
                    }
                    break;
            }
        }
        return record;
    }

    private static List<XmlLogRecordException> readExceptions(XMLStreamReader reader) throws XMLStreamException {
        List<XmlLogRecordException> exceptionLogRecords = new ArrayList<>();
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    if (EXCEPTION_FRAME_NODE_NAME.equals(reader.getLocalName())) {
                        exceptionLogRecords.add(readException(reader));
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (EXCEPTION_NODE_NAME.equals(reader.getLocalName())) {
                        return exceptionLogRecords;
                    }
                    break;
            }
        }
        return exceptionLogRecords;

    }

    private static XmlLogRecordException readException(XMLStreamReader reader) throws XMLStreamException {
        XmlLogRecordException logException = new XmlLogRecordException();
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    switch (elementName) {
                        case EXCEPTION_CLASS_NODE_NAME:
                            logException.setClassName(readCharacters(reader));
                            break;
                        case EXCEPTION_METHOD_NODE_NAME:
                            logException.setMethodName(readCharacters(reader));
                            break;
                        case EXCEPTION_LINE_NODE_NAME:
                            logException.setLineNumber(readInt(reader));
                            break;
                        default:
                            break;
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (EXCEPTION_FRAME_NODE_NAME.equals(reader.getLocalName())) {
                        return logException;
                    }
                    break;
            }
        }
        return logException;
    }

    private static long readLong(XMLStreamReader reader) throws XMLStreamException {
        String characters = readCharacters(reader);
        try {
            return Long.valueOf(characters);
        } catch (NumberFormatException e) {
            throw new XMLStreamException("Invalid long " + characters);
        }
    }

    private static int readInt(XMLStreamReader reader) throws XMLStreamException {
        String characters = readCharacters(reader);
        try {
            return Integer.valueOf(characters);
        } catch (NumberFormatException e) {
            throw new XMLStreamException("Invalid integer " + characters);
        }
    }

    private static String readCharacters(XMLStreamReader reader) throws XMLStreamException {
        StringBuilder result = new StringBuilder();
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.CHARACTERS:
                case XMLStreamReader.CDATA:
                    result.append(reader.getText());
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return result.toString();
                default:
                    break;
            }
        }
        return result.toString();
    }
}
