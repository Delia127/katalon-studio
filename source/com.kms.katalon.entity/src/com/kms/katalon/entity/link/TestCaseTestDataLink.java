package com.kms.katalon.entity.link;

import java.io.Serializable;

import com.kms.katalon.entity.util.Util;

public class TestCaseTestDataLink implements Serializable {
    /**
	 * 
	 */
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
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TestCaseTestDataLink other = (TestCaseTestDataLink) obj;
        if (combinationType != other.combinationType) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (iterationEntity == null) {
            if (other.iterationEntity != null) return false;
        } else if (!iterationEntity.equals(other.iterationEntity)) return false;
        if (testDataId == null) {
            if (other.testDataId != null) return false;
        } else if (!testDataId.equals(other.testDataId)) return false;
        return true;
    }

}
