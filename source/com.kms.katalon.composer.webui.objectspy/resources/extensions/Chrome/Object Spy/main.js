getKatalonOnOffStatus(function(isOn) {
	if (!isOn) {
		return;
	}
	$('document').ready( function(){
		startInspection();
		startDomCollector();
	});
});