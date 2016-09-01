package com.kms.katalon.core.reporting;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XMLParserException;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestStepLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.template.ResourceLoader;
import com.kms.katalon.core.testdata.reader.CsvWriter;

public class ReportUtil {

    private static final String LOG_END_TAG = "</log>";
    private static final String EXECUTION_LOG_FILE_BASE = "execution";

    private static StringBuilder generateVars(List<String> strings, TestSuiteLogRecord suiteLogEntity,
            StringBuilder model) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines =
                IOUtils.readLines(ResourceLoader.class.getResourceAsStream(ResourceLoader.HTML_TEMPLATE_VARS));
        for (String line : lines) {
            if (line.equals(ResourceLoader.HTML_TEMPLATE_SUITE_MODEL_TOKEN)) {
                sb.append(model);
            } else if (line.equals(ResourceLoader.HTML_TEMPLATE_STRINGS_CONSTANT_TOKEN)) {
                StringBuilder stringSb = listToStringArray(strings);
                sb.append(stringSb);
            } else if (line.equals(ResourceLoader.HTML_TEMPLATE_EXEC_ENV_TOKEN)) {
                StringBuilder envInfoSb = new StringBuilder();
                envInfoSb.append("{");
                envInfoSb.append(String.format("\"host\" : \"%s\", ", suiteLogEntity.getHostName()));
                envInfoSb.append(String.format("\"os\" : \"%s\", ", suiteLogEntity.getOs()));
                envInfoSb.append(String.format("\"" + GlobalStringConstants.APP_VERSION + "\" : \"%s\", ",
                        suiteLogEntity.getAppVersion()));
                if (suiteLogEntity.getBrowser() != null && !suiteLogEntity.getBrowser().equals("")) {
                    if (suiteLogEntity.getRunData().containsKey("browser")) {
                        envInfoSb.append(String.format("\"browser\" : \"%s\",",
                                suiteLogEntity.getRunData().get("browser")));
                    } else {
                        envInfoSb.append(String.format("\"browser\" : \"%s\",", suiteLogEntity.getBrowser()));
                    }
                }
                if (suiteLogEntity.getDeviceName() != null && !suiteLogEntity.getDeviceName().equals("")) {
                    envInfoSb.append(String.format("\"deviceName\" : \"%s\",", suiteLogEntity.getDeviceName()));
                }
                if (suiteLogEntity.getDeviceName() != null && !suiteLogEntity.getDeviceName().equals("")) {
                    envInfoSb.append(String.format("\"devicePlatform\" : \"%s\",", suiteLogEntity.getDevicePlatform()));
                }
                envInfoSb.append("\"\" : \"\"");

                envInfoSb.append("}");
                sb.append(envInfoSb);
            } else {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb;
    }

    public static String getOs() {
        return System.getProperty("os.name") + " " + System.getProperty("sun.arch.data.model") + "bit";
    }

    public static String getHostName() {
        String hostName = "Unknown";
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostName = addr.getCanonicalHostName();
        } catch (UnknownHostException ex) {
        }
        return hostName;
    }

    private static StringBuilder listToStringArray(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < strings.size(); idx++) {
            if (idx > 0) {
                sb.append(",");
            }
            sb.append("\""
                    + (strings.get(idx) == null ? "" : strings.get(idx).equals("*") ? strings.get(idx) : ("*" + strings
                            .get(idx))) + "\"");
        }
        return sb;
    }

    private static void collectInfoLines(ILogRecord logRecord, List<ILogRecord> rmvLogs) {
        if (logRecord instanceof MessageLogRecord) {
        	if(logRecord.getStatus().getStatusValue() == TestStatusValue.INCOMPLETE || logRecord.getStatus().getStatusValue() == TestStatusValue.INFO){
        		rmvLogs.add(logRecord);	
        	}
        }
        for (ILogRecord childLogRecord : logRecord.getChildRecords()) {
            collectInfoLines(childLogRecord, rmvLogs);
        }
    }

    public static void writeLogRecordToFiles(String logFolder) throws Exception {
        TestSuiteLogRecord testSuiteLogRecord = generate(logFolder);
        if (testSuiteLogRecord != null) {
            writeLogRecordToFiles(testSuiteLogRecord, new File(logFolder));
        }
    }

    public static void writeLogRecordToCSVFile(TestSuiteLogRecord suiteLogEntity, File destFile,
            List<ILogRecord> filteredTestCases) throws IOException {
        CsvWriter.writeCsvReport(suiteLogEntity, destFile, filteredTestCases);
    }

    public static void writeLogRecordToFiles(TestSuiteLogRecord suiteLogEntity, File logFolder) throws Exception {
        List<String> strings = new LinkedList<String>();

        JsSuiteModel jsSuiteModel = new JsSuiteModel(suiteLogEntity, strings);
        StringBuilder sbModel = jsSuiteModel.toArrayString();

        StringBuilder htmlSb = new StringBuilder();
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_FILE, htmlSb);
        htmlSb.append(generateVars(strings, suiteLogEntity, sbModel));
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_CONTENT, htmlSb);

        // Write main HTML Report
        FileUtils.writeStringToFile(new File(logFolder, logFolder.getName() + ".html"), htmlSb.toString());

        
        // Write CSV file
        CsvWriter.writeCsvReport(suiteLogEntity, new File(logFolder, logFolder.getName() + ".csv"),
                Arrays.asList(suiteLogEntity.getChildRecords()));

        List<ILogRecord> infoLogs = new ArrayList<ILogRecord>();
        collectInfoLines(suiteLogEntity, infoLogs);
        for (ILogRecord infoLog : infoLogs) {
            infoLog.getParentLogRecord().removeChildRecord(infoLog);
        }
        strings = new LinkedList<String>();
        jsSuiteModel = new JsSuiteModel(suiteLogEntity, strings);
        sbModel = jsSuiteModel.toArrayString();
        htmlSb = new StringBuilder();
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_FILE, htmlSb);
        htmlSb.append(generateVars(strings, suiteLogEntity, sbModel));
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_CONTENT, htmlSb);
        FileUtils.writeStringToFile(new File(logFolder, "Report.html"), htmlSb.toString());
        
    }

    public static void writeLogRecordToHTMLFile(TestSuiteLogRecord suiteLogEntity, File destFile,
            List<ILogRecord> filteredTestCases) throws IOException, URISyntaxException {

        List<String> strings = new LinkedList<String>();

        JsSuiteModel jsSuiteModel = new JsSuiteModel(suiteLogEntity, strings);
        StringBuilder sbModel = jsSuiteModel.toArrayString();

        StringBuilder htmlSb = new StringBuilder();
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_FILE, htmlSb);
        htmlSb.append(generateVars(strings, suiteLogEntity, sbModel));
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_CONTENT, htmlSb);

        List<ILogRecord> infoLogs = new ArrayList<ILogRecord>();
        collectInfoLines(suiteLogEntity, infoLogs);
        for (ILogRecord infoLog : infoLogs) {
            infoLog.getParentLogRecord().removeChildRecord(infoLog);
        }
        strings = new LinkedList<String>();
        jsSuiteModel = new JsSuiteModel(suiteLogEntity, strings, filteredTestCases);
        sbModel = jsSuiteModel.toArrayString();
        htmlSb = new StringBuilder();
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_FILE, htmlSb);
        htmlSb.append(generateVars(strings, suiteLogEntity, sbModel));
        readFileToStringBuilder(ResourceLoader.HTML_TEMPLATE_CONTENT, htmlSb);
        FileUtils.writeStringToFile(destFile, htmlSb.toString());
    }

    public static TestSuiteLogRecord generate(String logFolder) throws XMLParserException, IOException {
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
                int num1 =
                        Integer.parseInt(FilenameUtils.getBaseName(f1.getName()).replace(EXECUTION_LOG_FILE_BASE, ""));
                int num2 =
                        Integer.parseInt(FilenameUtils.getBaseName(f2.getName()).replace(EXECUTION_LOG_FILE_BASE, ""));
                return num2 - num1;
            }
        });

        Deque<Object> stack = new ArrayDeque<Object>();
        TestSuiteLogRecord testSuiteLogRecord = null;
        for (File file : files) {
            StringBuilder sb = new StringBuilder();
            sb.append(FileUtils.readFileToString(file, "UTF-8"));
            
            if (sb.toString().isEmpty()) return null;
            if (sb.indexOf(LOG_END_TAG) == -1) {
                sb.append(LOG_END_TAG);
            }            
            List<XmlLogRecord> xmlLogRecords = XMLLoggerParser.parseLogString(sb.toString());
            for (XmlLogRecord xmlLogRecord : xmlLogRecords) {
                if (xmlLogRecord.getLevel().getName().equals(LogLevel.START.toString())) {
                    switch (xmlLogRecord.getSourceMethodName()) {
                        case StringConstants.LOG_START_SUITE_METHOD:
                            testSuiteLogRecord = processStartTestSuiteLog(stack, logFolder, xmlLogRecord);
                            break;
                        case StringConstants.LOG_START_TEST_METHOD:
                            processStartTestCaseLog(stack, xmlLogRecord);
                            break;
                        case StringConstants.LOG_START_KEYWORD_METHOD:
                            processStartKeywordLog(stack, xmlLogRecord);
                            break;
                    }
                } else if (xmlLogRecord.getLevel().getName().equals(LogLevel.END.toString())) {
                    switch (xmlLogRecord.getSourceMethodName()) {
                        case StringConstants.LOG_END_KEYWORD_METHOD:
                        case StringConstants.LOG_END_TEST_METHOD:
                        case StringConstants.LOG_END_SUITE_METHOD:
                            processEndLog(stack, xmlLogRecord);
                            break;
                    }
                } else if (xmlLogRecord.getLevel().getName().equals(LogLevel.RUN_DATA.toString())) {
                    testSuiteLogRecord.addRunData(xmlLogRecord.getProperties());
                } else {
                    Object object = stack.peekLast();
                    if (object instanceof ILogRecord) {
                        processStepMessageLog(xmlLogRecord, (ILogRecord) object);
                    }
                }
            }
        }
        // If execution process crashed before completed
        if (stack.size() > 0) {
            while (stack.size() > 0) {
                Object object = stack.pollLast();
                if (object instanceof ILogRecord) {
                    processInterruptedLog((ILogRecord) object);
                }
            }
        }
        return testSuiteLogRecord;
    }

    private static void processInterruptedLog(ILogRecord logRecord) {
        ILogRecord[] childRecords = logRecord.getChildRecords();
        if (childRecords != null && childRecords.length > 0) {
            ILogRecord lastLogRecord = childRecords[childRecords.length - 1];
            logRecord.setEndTime(lastLogRecord.getEndTime() != 0 ? lastLogRecord.getEndTime() : lastLogRecord
                    .getStartTime());
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
            messageLogRecord.setAttachment(xmlLogRecord.getProperties()
                    .get(StringConstants.XML_LOG_ATTACHMENT_PROPERTY));
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
        testStepLogRecord.setDescription(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) ? xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        try {
            testStepLogRecord.setIndex(Integer.valueOf(xmlLogRecord.getProperties().get(
                    StringConstants.XML_LOG_STEP_INDEX)));
            testStepLogRecord.setIgnoredIfFailed(Boolean.valueOf(xmlLogRecord.getProperties().get(
                    StringConstants.XML_LOG_IS_IGNORED_IF_FAILED)));
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
        TestCaseLogRecord testCaseLogRecord =
                new TestCaseLogRecord(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_NAME_PROPERTY)
                        ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_NAME_PROPERTY)
                        : getTestLogName(xmlLogRecord));
        testCaseLogRecord.setStartTime(xmlLogRecord.getMillis());
        testCaseLogRecord.setId(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_ID_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_ID_PROPERTY) : "");
        testCaseLogRecord.setSource(xmlLogRecord.getProperties().containsKey(StringConstants.XML_LOG_SOURCE_PROPERTY)
                ? xmlLogRecord.getProperties().get(StringConstants.XML_LOG_SOURCE_PROPERTY) : "");
        testCaseLogRecord.setDescription(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) ? xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        testCaseLogRecord.setOptional(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_IS_OPTIONAL) ? Boolean.valueOf(xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_IS_OPTIONAL)) : false);
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
        testSuiteLogRecord.setDeviceName(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_DEVICE_ID_PROPERTY) ? xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_DEVICE_ID_PROPERTY) : "");
        testSuiteLogRecord.setDevicePlatform(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_DEVICE_PLATFORM_PROPERTY) ? xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_DEVICE_PLATFORM_PROPERTY) : "");
        testSuiteLogRecord.setDescription(xmlLogRecord.getProperties().containsKey(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) ? xmlLogRecord.getProperties().get(
                StringConstants.XML_LOG_DESCRIPTION_PROPERTY) : "");
        stack.add(testSuiteLogRecord);
        return testSuiteLogRecord;
    }

    private static TestStatus evalTestStatus(ILogRecord logRecord, LogLevel level) {
        TestStatus testStatus = new TestStatus();
        testStatus.setStatusValue(TestStatusValue.valueOf(level.name()));
        return testStatus;
    }

    private static void readFileToStringBuilder(String fileName, StringBuilder sb) throws IOException,
            URISyntaxException {
        String path = ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(fileName)) {
                    StringBuilderWriter sbWriter = new StringBuilderWriter(new StringBuilder());
                    IOUtils.copy(jar.getInputStream(jarEntry), sbWriter);
                    sbWriter.flush();
                    sbWriter.close();
                    sb.append(sbWriter.getBuilder());
                    break;
                }
            }
            jar.close();
        } else { // Run with IDE
                 // sb.append(FileUtils.readFileToString(new
                 // File(ResourceLoader.class.getResource(fileName).getContent()
                 // )));
            InputStream is = (InputStream) ResourceLoader.class.getResource(fileName).getContent();
            sb.append(IOUtils.toString(is, "UTF-8"));
        }
    }
}
