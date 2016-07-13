var data = require("sdk/self").data;
var pageMod = require("sdk/page-mod");
var Request = require("sdk/request").Request;
var preferences = require("sdk/preferences/service");
var simplePrefs = require('sdk/simple-prefs');
var portPrefName = 'katalonServerPort';
var onOffStatusPrefName = 'katalonOnOffStatus';
var CONNECTING_ERROR_MESSAGE = "Cannot connect to Katalon Server. Make sure you have started Recorder on Katalon application."

var panel = require("sdk/panel").Panel(
		{
			width : 170,
			height : 120,
			contentURL : data.url("popup.html"),
			contentScriptFile : [ data.url("jquery-2.2.4.min.js"),
					data.url("popup.js") ],
			onHide : handleHide
		});

var toggleButton = require('sdk/ui/button/toggle').ToggleButton({
	id : "show-panel",
	label : "Katalon Recorder",
	icon : {
		"16" : "./images/record_28.png",
		"32" : "./images/record_28.png",
		"64" : "./images/record_28.png"
	},
	onChange : handleChange
});

function handleChange(state) {
	if (!state.checked) {
		return;
	}
	panel.port.emit("show", {
		katalonServerPort : simplePrefs.prefs[portPrefName],
		katalonOnOffStatus : simplePrefs.prefs[onOffStatusPrefName]
	});
	panel.show({
		position : toggleButton
	});
}

function handleHide() {
	toggleButton.state('window', {
		checked : false
	});
}

panel.port.on("setKatalonServerPort", function(port) {
	simplePrefs.prefs[portPrefName] = port;
});

panel.port.on("setKatalonOnOffStatus", function(onOffStatus) {
	simplePrefs.prefs[onOffStatusPrefName] = onOffStatus;
});

pageMod.PageMod({
	include : "*",
	contentScriptFile : [ data.url("jquery-2.2.4.min.js"),
			data.url("constants.js"), data.url("common.js"),
			data.url("record_common.js"), data.url("dom_recorder.js"),
			data.url("main.js") ],
	onAttach : function(worker) {
		worker.port.on("getKatalonServerData", function() {
			worker.postMessage({
				kind : "setup",
				port : simplePrefs.prefs[portPrefName],
				isOn : simplePrefs.prefs[onOffStatusPrefName]
			});
		});
		worker.port.on("postData", function(message) {
			var object = message.data;
			object['action']['windowId'] = worker.tab.id;
			Request(
					{
						url : message.url,
						content : 'element='
								+ encodeURIComponent(JSON.stringify(object)),
						onComplete : function(response) {
							if (response.status == 200) {
								worker.postMessage({
									kind : "postSuccess"
								});
							} else {
								worker.postMessage({
									kind : "postFail",
									text : CONNECTING_ERROR_MESSAGE
								});
							}
						}
					}).post();
		});
	}
});