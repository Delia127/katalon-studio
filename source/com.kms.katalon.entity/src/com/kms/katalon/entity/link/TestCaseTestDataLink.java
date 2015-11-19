package com.kms.katalon.entity.link;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.util.Util;

public class TestCaseTestDataLink implements Serializable {
    private static final long serialVersionUID = 1L;
    private String testDataId;
    private IterationEntity iterationEntity;
    private TestDataCombinationType combinationType;
    private String id;

    public TestCaseTestDataLink() {
        setCombinationType(TestDataCombinationType.ONE);
    }

    public IterationEntity getIterationEntity() {
        if (iterationEntity == null) {
            iterationEntity = new IterationEntity();
        }
        return iterationEntity;
    }

    public void setIterationEntity(IterationEntity iterationEntity) {
        this.iterationEntity = iterationEntity;
    }

    public String getTestDataId() {
        return testDataId;
    }

    public void setTestDataId(String testDataId) {
        this.testDataId = testDataId;
    }

    public TestDataCombinationType getCombinationType() {
        return combinationType;
    }

    public void setCombinationType(TestDataCombinationType combinationType) {
        this.combinationType = combinationType;
    }

    public String getId() {
        if (id == null) {
            id = Util.generateGuid();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((combinationType == null) ? 0 : combinationType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((iterationEntity == null) ? 0 : iterationEntity.hashCode());
        result = prime * result + ((testDataId == null) ? 0 : testDataId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestCaseTestDataLink)) {
            return false;
        }
        TestCaseTestDataLink that = (TestCaseTestDataLink) obj;
        return new EqualsBuilder().append(this.getCombinationType(), that.getCombinationType())
                .append(this.getId(), that.getId()).append(this.getIterationEntity(), that.getIterationEntity())
                .append(this.getTestDataId(), that.getTestDataId()).isEquals();
    }

}
