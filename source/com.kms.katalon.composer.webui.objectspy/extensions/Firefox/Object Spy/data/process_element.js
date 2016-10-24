var registeredRequest = false;
var requestId = 0;
var clientId = -1;
var REQUEST_SEPARATOR = "_|_";
var waitAnswer = true;

/**For Highlight Object feature **/

function onKatalonRequestSuccess(message){
    if(message.text == 'NO_REQUEST'){
        return;
    }
    var requestParts = message.text.split(REQUEST_SEPARATOR);
    if (clientId == -1 || requestParts.length < 2) {
        clientId = parseInt(message.text);
        return;
    }
    requestId = parseInt(requestParts[0]);
    var requestType = requestParts[1];
    var requestData = (requestParts[2]);
    processRequest(requestType, requestData);
}

function startGetRequestSchedule() {

    if (registeredRequest) {
        return;
    }
    registeredRequest = true;

    setInterval(function() {
        //Call Katalon server
        sendRequest("GET_REQUEST");
    }, 200);

}

function sendRequest(action){
    if(Number.isNaN(clientId)){
        clientId = -1;
    }
    if (clientId == -1) {
        action = "GET_CLIENT_ID";
    }
    var requestData = action + "=" + clientId + REQUEST_SEPARATOR + requestId;

    self.port.emit("sendRequestToKatalon", {
        url : qAutomate_server_url,
        data : requestData
    });
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
    	window.focus();
    	self.port.emit("activateTab", {
            urlString : element.baseURI.toString()
        });
    	element.scrollIntoView(false);
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
