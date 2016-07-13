addEventHandler();

self.port.on("show", function onShow(data) {
	initOnOffButton(data.katalonOnOffStatus);
	initPortText(data.katalonServerPort);
});

function initOnOffButton(katalonOnOffStatus) {
	document.getElementById("katalon_onoffswitch").checked = katalonOnOffStatus;
	$('#katalon_onoffswitch').data('clicks', katalonOnOffStatus);
	$("#katalon_onoffswitch_label").addClass("onoffswitch-label"); 
}

function initPortText(port) {
	document.getElementById("port").value = port;
}

function addEventHandler() {
	$('#katalon_onoffswitch').click(function() {
		var clicks = $(this).data('clicks');
		self.port.emit("setKatalonOnOffStatus", !clicks);
		$(this).data("clicks", !clicks);
	});
	$('#port').on('input', function() {
		self.port.emit("setKatalonServerPort", $(this).val());
	});
}