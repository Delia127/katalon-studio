var katalonServerPortStorage = "katalonServerPortStorage";

function setKatalonServerPort(port) {
	var setting = browser.storage.local.set({ katalonServerPortStorage : port });
	setting.then(function() {
        console.log("Katalon server port set to " + port);
    }, function(error) {
        console.log(error);
        return;
	});
}

function getKatalonServerPort(callback) {
	var gettingPort = browser.storage.local.get(katalonServerPortStorage);
	gettingPort.then(function(result) {
        var port;
        if (!(katalonServerPortStorage in result)) {
            port = katalonServerPort;
            setKatalonServerPort(port);
        } else {
            port = result[katalonServerPortStorage];
        }
        console.log("Katalon server port is " + port);
        callback(port);
    }, function(error) {
        console.log(error);
        return;
	});
}

function chromePostData(url, data, callback) {
	browser.runtime.sendMessage({
		method: XHTTP_POST_METHOD,
		action: XHTTP_ACTION,
		url: url,
		data: data
	}, callback);
}