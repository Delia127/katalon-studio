package com.kms.katalon.entity.link;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.util.Util;

public class TestCaseTestDataLink implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String testDataId;
	private IterationEntity iterationEntity;
	private TestDataCombinationType combinationType;
	private List<TestCaseTestDataLink> childrenLink;
	private String id;
	
	public TestCaseTestDataLink() {
		setCombinationType(TestDataCombinationType.ONE);
	}

	public List<TestCaseTestDataLink> getChildrenLink() {
		if (childrenLink == null) {
			childrenLink = new ArrayList<TestCaseTestDataLink>();
		}
		return childrenLink;
	}

	public void setChildrenLink(List<TestCaseTestDataLink> childrenLink) {
		this.childrenLink = childrenLink;
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
}
