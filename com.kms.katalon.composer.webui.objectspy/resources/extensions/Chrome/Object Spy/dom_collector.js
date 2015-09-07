var needPost = true;
var isParentReady = false;

$('document').ready( function(){
	// if window is top page
	if (window.location === window.parent.location) {
		setInterval(function(){ 
			if (needPost) {
				console.log('Start posting');
				postDomMap(qAutomate_server_url, createDomMap()); 
				needPost = false;
				console.log('End posting');
			}
		}, 5000);
	} else {
		var sendDomMapInterval = setInterval(function(){ 
			if (isParentReady) {
				sendDomMap();
				clearInterval(sendDomMapInterval);
			}
		}, 5000);
	}
	var arrFrames = document.getElementsByTagName("IFRAME");
	for (var i = 0; i < arrFrames.length; i++) {
		arrFrames[i].contentWindow.postMessage("parentLoadCompleted",  "*");
	}
	arrFrames = document.getElementsByTagName("FRAME");
	for (var i = 0; i < arrFrames.length; i++) {
		arrFrames[i].contentWindow.postMessage("parentLoadCompleted",  "*");
	}
});

function sendDomMap() {
	if (window.location === window.parent.location) {
		needPost = true;
	} else {
		var event = {};
		event['name'] = 'loadCompleted';
		event['data'] = createDomMap();
		window.parent.postMessage(JSON.stringify(event), "*");
	}
}

function forwardPostDomMapEvent() {
	if (window.location === window.parent.location) {
		postDomMap(qAutomate_server_url, createDomMap());
	} else {
		var event = {};
		event['name'] = 'postDomMap';
		event['data'] = createDomMap();
		window.parent.postMessage(JSON.stringify(event), "*");
	}
}

function postDomMap(url, object) {
	if (object) {
		var data = 'elementsMap=' + encodeURIComponent(JSON.stringify(object));
		if (detectChrome()) {
			chrome.runtime.sendMessage({
				method: 'POST',
				action: 'xhttp',
				url: url,
				data: data
			}, function() {
				console.log("Post DOM Map successful");
			});
		} else if (detectIE()) {
			if (window.httpRequestExtension) {
				var response = window.httpRequestExtension.postRequest(data, url);
				if (response === '200') {
					console.log("Post DOM Map successful");
				} else {
					console.log(response);
				}
			}
		} else {
			self.port.emit("postData", { url : url, data : data });
		}
	}
}