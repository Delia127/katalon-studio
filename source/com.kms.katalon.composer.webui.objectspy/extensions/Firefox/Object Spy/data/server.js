self.on('message', function(message) {
	if (message.kind && message.kind == "updateServerUrl") {
		qAutomate_server_url = message.url;
	}
});