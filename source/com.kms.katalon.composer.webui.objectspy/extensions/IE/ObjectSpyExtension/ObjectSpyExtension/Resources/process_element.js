
var registeredRequest = false;
var requestId = 0;
var clientId = -1;
var REQUEST_SEPARATOR = "_|_";
var waitAnswer = true;

/**For Highlight Object feature **/

function startGetRequestSchedule() {

    if (registeredRequest) {
        return;
    }
    registeredRequest = true;

    //Register XPATH lib (implemented by Google: github.com/google/wicked-good-xpath)
    wgxpath.install();

    setInterval(function () {
        //Call Katalon server
        sendRequest("GET_REQUEST");
    }, 200);

}

function sendRequest(action) {

    if (typeof (clientId) == 'undefined' || isNaN(clientId)) {
        clientId = -1;
    }
    if (clientId == -1) {
        action = "GET_CLIENT_ID";
    }
    var requestData = action + "=" + clientId + REQUEST_SEPARATOR + requestId;

    var response = window.httpRequestExtension.sendRequestToKatalon(requestData, qAutomate_server_url);

    var arrResults = response.split(REQUEST_SEPARATOR);

    if (arrResults[0] === '200') {
        var text = "";
        for (i = 1; i < arrResults.length; i++) {
            text += arrResults[i];
            if (i < arrResults.length -1) {
                text += REQUEST_SEPARATOR;
            }
        }
        onKatalonRequestSuccess(text);
    } else {
        console.log("Failed to contact Katalon server at: " + qAutomate_server_url);
    }
}

function onKatalonRequestSuccess(text) {
    if (text == "NO_REQUEST") {
        return;
    }
    var requestParts = text.split(REQUEST_SEPARATOR);
    if (clientId == -1 || requestParts.length < 2) {
        clientId = parseInt(text);
        return;
    }
    requestId = parseInt(requestParts[0]);
    var requestType = requestParts[1];
    var requestData = (requestParts[2]);
    processRequest(requestType, requestData);
}

function processRequest(requestType, requestData) {
    if (requestType == "FIND_TEST_OBJECT") {
        findObject(requestData);
    }
    else if (requestType == "HIGHLIGHT_TEST_OBJECT") {
        highlightObject(requestData);
    }
}

function findObject(xpathExpression) {
    var element = findElement(xpathExpression);
    if (isElementVisible(element)) {
        sendRequest("FOUND");
    }
}

function highlightObject(xpathExpression) {
    var element = findElement(xpathExpression);
    if (isElementVisible(element)) {
        element.scrollIntoView(false);
        window.focus();
		setTimeout(function() {
			$(element)
				.css({
					outline : ELEMENT_FLASHING_OUTLINE_STYLE
				})
				.animate({
					outlineColor : ELEMENT_FLASHING_OUTLINE_COLOR_1
				}, 100)
				.animate({
					outlineColor : ELEMENT_FLASHING_OUTLINE_COLOR_2
				}, 100)
				.animate({
					outlineColor : ELEMENT_FLASHING_OUTLINE_COLOR_1
				}, 100)
				.animate({
					outlineColor : ELEMENT_FLASHING_OUTLINE_COLOR_2
				}, 100, function() {
					element.style.outline = '';
				});
		}, 500);
    }
}

function findElement(xpathExp) {
    var xpaths = xpathExp.split('_\\|\\_');
    var doc = document;
    var node = null;
    for (i = 0; i < xpaths.length; ++i) {
        var nodes = doc.evaluate(xpaths[i], doc, null, XPathResult.ANY_TYPE, null);
        node = nodes.iterateNext();
        if (!node) {
            break;
        }
        if (i < xpaths.length - 1) {
            doc = node.contentDocument || node.contentWindow.document;
        }
    }
    return node;
}

function isElementVisible(element) {
    if (!element) {
        return false;
    }
    if (element.style && element.style.display == 'none') {
        return false;
    }
    if (element.style && element.style.visibility == 'hidden') {
        return false;
    }
    return true;
};