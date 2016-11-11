var data = require("sdk/self").data;
var pageMod = require("sdk/page-mod");
var Request = require("sdk/request").Request;
var preferences = require("sdk/preferences/service");
var simplePrefs = require('sdk/simple-prefs');
var portPrefName = 'katalonServerPort';
var onOffStatusPrefName = 'katalonOnOffStatus';
var CONNECTING_ERROR_MESSAGE = "Cannot connect to Katalon Server. Make sure you have started Object Spy on Katalon application."
var POST_DOM_MAP_SUCCESS = "Load DOM map to Katalon successfully"

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
	label : "Katalon Object Spy",
	icon : {
		"16" : "./images/object_spy_24.png",
		"32" : "./images/object_spy_24.png",
		"64" : "./images/object_spy_24.png"
	},
	onChange : handleChange
});

var tabs = require('sdk/tabs');

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
			data.url("jquery.color-2.1.2.min.js"), data.url("constants.js"),
			data.url("common.js"), data.url("dom_inspector.js"),
			data.url("dom_collector.js"), data.url("main.js"), data.url("process_element.js") ],
	onAttach : function(worker) {
		worker.port.on("getKatalonServerData", function() {
			worker.postMessage({
				kind : "setup",
				port : simplePrefs.prefs[portPrefName],
				isOn : simplePrefs.prefs[onOffStatusPrefName]
			});
		});
		worker.port.on("postData", function(message) {
			postData(message.url, message.data, function(response) {
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
			})
		});
		worker.port.on("postDomMapData", function(message) {
			postData(message.url, message.data, function(response) {
				if (response.status == 200) {
					worker.postMessage({
						kind : "postDomMapSuccess",
						text : POST_DOM_MAP_SUCCESS
					});
				} else {
					worker.postMessage({
						kind : "postFail",
						text : CONNECTING_ERROR_MESSAGE
					});
				}
			})
		});
		worker.port.on("sendRequestToKatalon", function(message) {
		    postData(message.url, message.data, function(response) {
		        if (response.status == 200) {
		            worker.postMessage({
		                kind : "sendRequestToKatalonSuccess",
		                text : response.text
		            });
		        } else {
		            worker.postMessage({
		                kind : "sendRequestToKatalonFail",
		                text : CONNECTING_ERROR_MESSAGE
		            });
		        }
		    })
		});
		worker.port.on("activateTab", function(url) {
			for (let tab of tabs){
				if(tab != undefined && (url.urlString == tab.url || url.urlString == (tab.url + "/"))){
					tab.activate();
					break;
				}
			}
		});
	}
});


function postData(url, data, onPostComplete) {
	Request({
		url : url,
		content : data,
		onComplete : onPostComplete
	}).post();
}