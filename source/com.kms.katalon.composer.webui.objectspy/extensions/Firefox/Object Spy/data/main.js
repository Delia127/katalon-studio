self.on('message', function(message) {
	if (message.kind && message.kind == "setup" && message.isOn) {
		qAutomate_server_url = KATALON_SERVER_URL_PREFIX + message.port + KATALON_SERVER_URL_SUFFIX;
		$('document').ready( function() {
			startInspection();
		});
	}
});
self.port.emit("getKatalonServerData");