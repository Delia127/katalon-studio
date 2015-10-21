package com.kms.katalon.composer.webservice.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// NewWebServiceRequestObjectHandler
	public static final String HAND_NEW_REQUEST = "New Request";
	public static final String ERROR_TITLE = ERROR;
	public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_REQ_OBJ = "Unable to create request object";

	// OpenWebServiceRequestObjectHandler
	public static final String BUNDLE_URI_WEBSERVICE = "bundleclass://com.kms.katalon.composer.webservice/";

	// NewWebServiceRequestObjectPopupMenuContribution
	public static final String MENU_CONTEXT_WEBSERVICE_REQ = "Web Service Request";
	public static final String COMMAND_ID_ADD_WEBSERVICE_REQ = "com.kms.katalon.composer.webservice.command.add";

	// RequestObjectPart
	public static final String PA_TITLE_INFO = INFORMATION;
	public static final String PA_TITLE_HTTP = "HTTP";
	public static final String PA_LBL_ID = ID;
	public static final String PA_LBL_DESC = DESCRIPTION;
	public static final String PA_LBL_NAME = NAME;
	public static final String PA_LBL_HTTP_HEADER = "HTTP Header";
	public static final String PA_LBL_HTTP_BODY = "HTTP Body";
	public static final String VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY = "Failed to load Object Repository";

	// RestRequestObjectPart
	public static final String PA_TITLE_REST = "REST";
	public static final String PA_LBL_REST_URL = "REST URL";
	public static final String PA_LBL_REQ_METHOD = "Request Method";
	public static final String PA_LBL_REST_PARAMS = "REST Parameters";

	// SoapRequestObjectPart
	public static final String PA_TITLE_SOAP = "SOAP";
	public static final String PA_LBL_WSDL_ADDR = "WSDL Address";
	public static final String PA_LBL_SERVICE_FUNCTION = "Service Function";
	public static final String PA_LBL_PARAMS = "Parameters";
	public static final String PA_LBL_SOAP_HEADER_OPT = "SOAP Header:\n   (Optional)";
	public static final String PA_LBL_SOAP_BODY = "SOAP Body";

	// NewRequestDialog
	public static final String WIEW_TITLE_NEW = "New";
	public static final String VIEW_LBL_NAME = NAME;
	public static final String VIEW_LBL_REQ_TYPE = "Request Type";
	public static final String VIEW_DIA_TITLE_WEBSERVICE_REQ = "Web Service Request";
	public static final String VIEW_DIA_MSG_CREATE_NEW_WEBSERVICE_REQ = "Create Web Service Request";

	// ParameterTable
	public static final String VIEW_COL_NAME = NAME;
	public static final String VIEW_COL_VALUE = VALUE;
	public static final String VIEW_MENU_CONTEXT_INSERT_PROP = "Insert property";
	public static final String VIEW_MENU_CONTEXT_DEL_PROPS = "Delete properties";
}
