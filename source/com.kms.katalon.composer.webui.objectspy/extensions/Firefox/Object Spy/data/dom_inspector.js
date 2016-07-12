//GLOBALS
var gHoverElement;		//whatever element the mouse is over

//extended	
var infoDiv;		//currently just container for InfoDivHover, might add more here
var infoDivHover;	//container for hoverText text node.
var instructionDiv; //instruction element for user
var hoverText;		//show information about current element that the mouse is over
var currentEventOrigin; //store event origin for posting message

// setup
function setupEventListeners() {
	document.onkeyup = keyUp;
	document.onmouseover = mouseOver;
	document.onmouseout = mouseOut;
	window.onmousemove = mouseMoveWindow;
	window.onmouseout = mouseOutWindow;
}

//setup informational div to show which element the mouse is over.
function createInfoDiv() {
	infoDiv = document.createElement('div');
	var s = infoDiv.style;
	s.position = 'fixed';
	s.top = '0';
	s.right = '0';
	s.display = 'block';
	s.padding = '0px';
	s.zIndex = "9999999";
	document.body.appendChild(infoDiv);
	
	infoDivHover = document.createElement('div');
	s=infoDivHover.style;
	s.fontWeight = 'bold';			
	s.padding = '2px';
	s.Opacity = '0.8';
	s.borderWidth = 'thin';
	s.borderStyle = 'solid';
	s.borderColor = INFO_DIV_DEFAULT_BORDER_COLOR;
	s.backgroundColor = INFO_DIV_DEFAULT_BACKGROUND_COLOR;
	s.color = INFO_DIV_DEFAULT_FOREGROUND_COLOR;
	infoDiv.appendChild(infoDivHover);			
	hoverText = document.createTextNode('selecting');
	infoDivHover.appendChild(hoverText);
}

// create instruction div to guide user how to capture object
function createInstructionDiv() {
	instructionDiv = document.createElement('div');
	instructionDiv.style.position = 'fixed';
	instructionDiv.style.zIndex = '99999999';
	instructionDiv.style.display = 'none';
	instructionDiv.style.opacity = '0.9';
	instructionDiv.innerHTML = INSTRUCTION_IMAGE;
	document.body.appendChild(instructionDiv);
}
	
function setupDOMSelection() {
	setupEventListeners();
	createInfoDiv();
	createInstructionDiv();
}

// Update info div with the new text, color, background color or border color
function updateInfoDiv(text, color, bgColor, borderColor) {
	var s = infoDivHover.style;
	if (color) {
		s.color = color;
	}
	if (bgColor) {
		s.backgroundColor = bgColor;
	}
	if (borderColor) {
		s.borderColor = borderColor;
	}
	if (text) {
		hoverText.data=text;
	}
}

function getElementInfo(element) {
	if (!element) {
		return '';
	}
	var txt = element.nodeName.toLowerCase();
	txt = appendAttributeToText(txt, element, 'id');
	txt = appendAttributeToText(txt, element, 'class');	
	txt = '//' + txt;
	return txt;
	
	function appendAttributeToText(txt,element,a) {			
		if ((element.getAttribute(a) != null) && (element.getAttribute(a) !== '')) {
			txt += "[@"+a+"='"+element.getAttribute(a)+"']";
		}
		return txt;
	}
	
}

function mouseMoveWindow(e) {
	var x = e.clientX, y = e.clientY - 10;
	var windowWidth = Math.max(
			document.documentElement.clientWidth,
			window.innerWidth || 0);
	if ((e.clientX + 260) >= windowWidth) {
		x = e.clientX - 260;
	} else {
		x = e.clientX + 20;
	}
	instructionDiv.style.display = 'block';
	instructionDiv.style.left = x + 'px';
	instructionDiv.style.top = y + 'px';
}

function mouseOutWindow() {
	instructionDiv.style.display = 'none';
}

function mouseOver(e) {
	var selectedElement = e ? e.target : window.event.srcElement;
	if (selectedElement.nodeName.toLowerCase() == 'iframe' || selectedElement.nodeName.toLowerCase() == 'frame') {
		var iframeContentWindow = selectedElement.contentWindow;
		if (iframeContentWindow) {
			iframeContentWindow.focus();
		}
	} else {
		var doc = selectedElement.ownerDocument;
		var win = doc.defaultView || doc.parentWindow;
		win.focus();
	}
	
	if (selectedElement == gHoverElement) {	
		return;
	}
	gHoverElement = selectedElement;
	gHoverElement.style.outline = ELEMENT_HOVER_OUTLINE_STYLE;
	updateInfoDiv(getElementInfo(gHoverElement), INFO_DIV_HOVER_FOREGROUND_COLOR, 
		INFO_DIV_HOVER_BACKGROUND_COLOR, INFO_DIV_HOVER_BORDER_COLOR);
}

function mouseOut(e) {
	var selectedElement = e ? e.target : window.event.srcElement;
	if (gHoverElement != selectedElement) {
		return;
	}
	gHoverElement.style.outline = '';
	updateInfoDiv('-','white','black','white');	
	gHoverElement=null;
}

function flashElement() {
	if (!gHoverElement) {	
		return;
	}
	$(gHoverElement).css({outline: ELEMENT_FLASHING_OUTLINE_STYLE
		}).animate({outlineColor: ELEMENT_FLASHING_OUTLINE_COLOR_1}, 100)
		.animate({outlineColor: ELEMENT_FLASHING_OUTLINE_COLOR_2}, 100)
		.animate({outlineColor: ELEMENT_FLASHING_OUTLINE_COLOR_1}, 100)
		.animate({outlineColor: ELEMENT_FLASHING_OUTLINE_COLOR_2}, 100)
		.animate({outlineColor: ELEMENT_FLASHING_OUTLINE_COLOR_3}, 100, function() {
			if (gHoverElement && (gHoverElement.nodeName.toLowerCase() == 'iframe' || gHoverElement.nodeName.toLowerCase() == 'frame')) {
				var iframeContentWindow = gHoverElement.contentWindow;
				if (iframeContentWindow && currentEventOrigin) {
					iframeContentWindow.postMessage("responseSuccess",  currentEventOrigin);
				}
			}
		});
}

function sendData() {
	if (!gHoverElement) {
		return;
	}
	if (gHoverElement.nodeName.toLowerCase() == 'iframe') {
		gHoverElement.contentWindow.postMessage("keyboardTriggerEvent",  "*");
	} else {
		var jsonObject = mapDOM(gHoverElement, window);
		processObject(jsonObject);
	}
}

function keyUp(e) {
	keyCode = document.all ? window.event.keyCode : e.keyCode;
	var isAltKeyPressed = e ? e.altKey : window.event.altKey;
	var isCtrlKeyPressed = e ? e.ctrlKey : window.event.ctrlKey;
	if (!isAltKeyPressed) {
		return;
	}
	// Ctrl + Alt + '~'
	if (isCtrlKeyPressed && keyCode === 192) {
		forwardPostDomMapEvent();
		return;
	}
	// Alt + '~'
	if (keyCode === 192) {
		sendData();
	} 
}

function postData(url, object) {
	if (!object) {
		return;
	}
	var data = 'element=' + encodeURIComponent(JSON.stringify(object));
	if (detectChrome()) {
		chromePostData(url, data, function(response) {
			if (response) {
				console.log(response)
				// error happenened
				alert(response);
				setTimeout(function() {
					window.focus();
				}, 1);
				return;
			}
			flashElement();
		});
		return;
	}
	if (detectIE() && window.httpRequestExtension) {
		var response = window.httpRequestExtension.postRequest(data, url);
		if (response === '200') {
			flashElement();
		} else {
			alert(response);
		}
		return;
	}
	self.port.emit("postData", { url : url, data : data });
}

function setParentJson(object, parentJson) {
	if ('parent' in object) {
		setParentJson(object['parent'], parentJson);
		return;
	}
	object['parent'] = parentJson;
}

function processObject(object) {
	if (window.location !== window.parent.location) {
		var event = {};
		event['name'] = 'forwardObject';
		event['data'] = object;
		window.parent.postMessage(JSON.stringify(event), "*");
		return;
	}
	postData(qAutomate_server_url, object);
}

function receiveMessage(event) {
	// Check if sender is from parent frame
	if (event.source === window.parent) {
		if (event.data == "responseSuccess" && gHoverElement) {
			flashElement();
		} else if (event.data == "keyboardTriggerEvent") {
			sendData();
		} else if (event.data == "parentLoadCompleted") {
			isParentReady = true;
		}
		return;
	}
	// Check if sender is from any child frame belong to this window
	var childFrame = null;
	var arrFrames = document.getElementsByTagName("IFRAME");
	for (var i = 0; i < arrFrames.length; i++) {
		if (arrFrames[i].contentWindow === event.source) {
			childFrame = arrFrames[i];
			break;
		}
	}
	arrFrames = document.getElementsByTagName("FRAME");
	for (var i = 0; i < arrFrames.length; i++) {
		if (arrFrames[i].contentWindow === event.source) {
			childFrame = arrFrames[i];
			break;
		}
	}
	if (!childFrame) {
		return;
	}
	currentEventOrigin = event.origin;
	var eventObject = JSON.parse(event.data);
	switch (eventObject['name']) {
	case 'forwardObject':
		var object = eventObject['data'];
		var json = mapDOM(childFrame, window);
		if (json) {
			setParentJson(object, json);
		}
		processObject(object);
		break;
	case 'loadCompleted':
		var object = eventObject['data'];
		childFrame.domData = object;
		sendDomMap();
		break;
	case 'postDomMap':
		var object = eventObject['data'];
		childFrame.domData = object;
		forwardPostDomMapEvent();
		break;
	}
}
	
function startInspection() {
	if (!window.console) console = {log: function() {
	}};
	//START
	setupDOMSelection();

	// for Firefox
	if (!detectChrome() && !detectIE() && !(typeof self === 'undefined')) {
		self.on('message', function(message) {
			if (message.kind == "postSuccess") {
				flashElement();
			} else if (message.kind == "postFail") {
				alert(message.text);
			} 
		});
	}
	
	if (window.addEventListener) {
		window.addEventListener("message", receiveMessage, false);
	} else {
		window.attachEvent("onmessage", receiveMessage);
	}
};