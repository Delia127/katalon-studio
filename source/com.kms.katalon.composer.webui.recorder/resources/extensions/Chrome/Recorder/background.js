chrome.runtime.onMessage.addListener(function(request, sender, callback) {
    if (request.action == "xhttp") {
        var xhttp = new XMLHttpRequest();
        var method = request.method ? request.method.toUpperCase() : 'GET';

        xhttp.onload = function() {
            callback(xhttp);
        };
        xhttp.onerror = function() {
            // Do whatever you want on error. Don't forget to invoke the
            // callback to clean up the communication port.
            callback();
        };
        xhttp.open(method, request.url, true);
        if (method == 'POST') {
            xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        }
		
		chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
			var object = request.data;
			object['action']['windowId'] = tabs[0].id;
			xhttp.send('element=' + encodeURIComponent(JSON.stringify(object)));
		});
        return true; // prevents the callback from being called too early on return
    }
});