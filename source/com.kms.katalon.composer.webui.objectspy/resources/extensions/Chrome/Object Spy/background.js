var CONNECTING_ERROR_MESSAGE = "Cannot connect to Katalon Server. Make sure you have started Object Spy on Katalon application."

function processXHTTPAction(request, callback) {
	var xhttp = new XMLHttpRequest();
	var method = request.method ? request.method.toUpperCase() : XHTTP_GET_METHOD;
	xhttp.onload = function() {
		callback();
	};
	xhttp.onerror = function() {
		callback(CONNECTING_ERROR_MESSAGE);
	}
	try {
		xhttp.open(method, request.url, true);
		if (method == XHTTP_POST_METHOD) {
			xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		}
		xhttp.send(request.data);
	} catch (exception) {
		console.log(JSON.stringify(exception))
	}
	return true; // prevents the callback from being called too early on return
}

chrome.runtime.onMessage.addListener(function(request, sender, callback) {
    if (request.action == XHTTP_ACTION) {
        return processXHTTPAction(request, callback);
    }
});