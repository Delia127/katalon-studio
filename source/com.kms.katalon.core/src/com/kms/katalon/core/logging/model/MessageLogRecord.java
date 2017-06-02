package com.kms.katalon.core.logging.model;

import org.apache.commons.lang.StringUtils;

public class MessageLogRecord extends AbstractLogRecord {
    private TestStatus testStatus;

    private String attachment;

    public MessageLogRecord() {
        super("");
        setType(ILogRecord.LOG_TYPE_MESSAGE);
    }

    public void setStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
    }

    @Override
    public TestStatus getStatus() {
        return testStatus;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getJUnitMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getJUnitMessage());
        if (StringUtils.isNotBlank(getAttachment())) {
            sb.append("[[ATTACHMENT|" + getAttachment() + "]]");
            sb.append(LINE_SEPARATOR);
        }
        return sb.toString();
    }
}
