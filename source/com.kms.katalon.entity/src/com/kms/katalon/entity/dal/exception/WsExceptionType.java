package com.kms.katalon.entity.dal.exception;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum WsExceptionType {
	CancelTaskException(1),
	DataFileColumnNotFoundException(2),
	DataFileNotFoundException(3),
	DataFileSheetNotFoundException(4),
	DuplicatedDataFileNameException(5),
	DuplicatedFileNameException(6),
	DuplicatedFolderException(7),
	DuplicateEntityException(8),
	EntityIsReferencedException(9),
	ImportLinkException(10),
	LogFilesNotFoundException(11),
	MultipleEntitiesException(12),
	NoEntityException(13),
	ProjectVersionNotFoundException(14),
	TestCaseIsReferencedByTestSuiteExepception(15),
	TestSuiteHaveNoTestCaseException(16),
	WebElementNotFoundException(17),
	WrongEntityVersionException(18),
	LengthExceedLimitationException(19),
	Unknown(9999);

	private int value;

	private WsExceptionType(int value) {
		this.setValue(value);
	}

	private static final Map<Integer, WsExceptionType> LOOK_UP = new HashMap<Integer, WsExceptionType>();

	static {
		for (WsExceptionType s : EnumSet.allOf(WsExceptionType.class)) {
			LOOK_UP.put(s.getValue(), s);
		}
	}

	public static WsExceptionType getWsExceptionType(int code) {
		return LOOK_UP.get(code);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
