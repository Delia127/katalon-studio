var data = require("sdk/self").data;
var pageMod = require("sdk/page-mod");
var Request = require("sdk/request").Request;

pageMod.PageMod({
	include: "*",
	contentScriptFile: [data.url("jquery-1.11.2.min.js"),
					  data.url("server.js"),
					  data.url("common.js"),
					  data.url("dom_recorder.js")],
	onAttach: function(worker) {
		var serverUrl = require("sdk/preferences/service").get("serverUrl");
		if (serverUrl) {
			worker.postMessage({kind: "updateServerUrl", url: serverUrl});
		}
		worker.port.on("postData", function(message) {
			var object = message.data;
			object['action']['windowId'] = worker.tab.id;
			Request({
				url: message.url,
				content: 'element=' + encodeURIComponent(JSON.stringify(object)),
				onComplete: function (response) {
					if (response.status == 200) {
						worker.postMessage({kind: "postSuccess"});
					}
				}
			}).post();
		});
	}
});