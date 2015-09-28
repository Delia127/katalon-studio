(function() {
	//GLOBALS
	var gHoverElement;		//whatever element the mouse is over
	
	//extended	
	var infoDiv;		//currently just container for InfoDivHover, might add more here
	var infoDivHover;	//container for hoverText text node.
	var hoverText;		//show information about current element that the mouse is over
	
	var currentEventOrigin;
		
	if (!window.console) console = {log: function() {
	}};
	//START
	SetupDOMSelection();	
	
	//(Section 1) Element Selection
	function SetupDOMSelection()
	{
		{
			//setup event listeners
			document.onkeyup = keyUp;
			document.onmouseover = mouseOver;
			document.onmouseout = mouseOut;
		}
		{
			//setup informational div to show which element the mouse is over.
			infoDiv=document.createElement('div');
			var s=infoDiv.style;
			s.position='fixed';
			s.top='0';
			s.right='0';
			s.display='block';
			s.padding = '0px';
			s.zIndex = "9999999";

			document.body.appendChild(infoDiv);
			infoDivHover=document.createElement('div');

			s=infoDivHover.style;
			s.fontWeight='bold';			
			s.padding='2px';
			s.Opacity='0.8';
			s.borderWidth='thin';
			s.borderStyle='solid';
			s.borderColor='white';
			s.backgroundColor='black';
			s.color='white';
			
			infoDiv.appendChild(infoDivHover);			
			hoverText=document.createTextNode('selecting');
			infoDivHover.appendChild(hoverText);
		}
	}
	
	function InfoMSG(text,color,bgcolor,border)
	{
		var s = infoDivHover.style;
		if (color) s.color=color;
		if (bgcolor) s.backgroundColor=bgcolor;
		if (border) s.borderColor=border;
		if (text) hoverText.data=text;
	}
	
	function ElementInfo(element)
	{
		var txt='';
		if(element)
		{
			txt=element.nodeName.toLowerCase();
			txt=attrib(txt,element,'id');
			txt=attrib(txt,element,'class');	
			txt='//'+txt;
		}
		return txt;
		
		function attrib(t,e,a)
		{			
			if ((e.getAttribute(a) != null) && (e.getAttribute(a) !== ''))
			{
				t+="[@"+a+"='"+e.getAttribute(a)+"']";
			}
			return t;
		}
		
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
		
		if (selectedElement != gHoverElement) {	
			gHoverElement = selectedElement;
			gHoverElement.style.outline = '2px solid #f00';
			InfoMSG(ElementInfo(gHoverElement),'yellow','blue','yellow');
		}
	}
	
	function mouseOut(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		if (gHoverElement != selectedElement) {
			return;
		}
		gHoverElement.style.outline = '';
		InfoMSG('-','white','black','white');	
		gHoverElement=null;
	}
	
	function flashElement() {
		if (gHoverElement) {	
			$(gHoverElement).css({outline: "2px solid #0F0"
				}).animate({outlineColor: "#FFF"}, 100)
				.animate({outlineColor: "#0F0"}, 100)
				.animate({outlineColor: "#FFF"}, 100)
				.animate({outlineColor: "#0F0"}, 100)
				.animate({outlineColor: "#F00"}, 100, function() {
					if (gHoverElement && (gHoverElement.nodeName.toLowerCase() == 'iframe' || gHoverElement.nodeName.toLowerCase() == 'frame')) {
						var iframeContentWindow = gHoverElement.contentWindow;
						if (iframeContentWindow && currentEventOrigin) {
							iframeContentWindow.postMessage("responseSuccess",  currentEventOrigin);
						}
					}
				});
		}
	}
	
	function sendData() {
		if (gHoverElement) {
			if (gHoverElement.nodeName.toLowerCase() == 'iframe') {
				gHoverElement.contentWindow.postMessage("keyboardTriggerEvent",  "*");
			} else {
				var jsonObject = mapDOM(gHoverElement, window);
				processObject(jsonObject);
			}
		}
	}
	
	function keyUp(e) {
		keyCode = document.all ? window.event.keyCode : e.keyCode;
		var isAltKeyPressed = e ? e.altKey : window.event.altKey;
		var isCtrlKeyPressed = e ? e.ctrlKey : window.event.ctrlKey;
		if (isAltKeyPressed) {
			// Ctrl + Alt + '~'
			if (isCtrlKeyPressed && keyCode === 192) {
				forwardPostDomMapEvent();
			}
			else 
			// Alt + '~'
			if (keyCode === 192) {
				sendData();
			} 
		} 
	}
	
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
	
	function postData(url, object) {
		if (object) {
			var data = 'element=' + encodeURIComponent(JSON.stringify(object));
			if (detectChrome()) {
				chrome.runtime.sendMessage({
					method: 'POST',
					action: 'xhttp',
					url: url,
					data: data
				}, function() {
					flashElement();
				});
			} else if (detectIE()) {
				if (window.httpRequestExtension) {
					var response = window.httpRequestExtension.postRequest(data, url);
					if (response === '200') {
						flashElement();
					} else {
						alert(response);
					}
				}
			} else {
				self.port.emit("postData", { url : url, data : data });
			}
		}
	}
	
	function setParentJson(object, parentJson) {
		if ('parent' in object) {
			setParentJson(object['parent'], parentJson);
		} else {
			object['parent'] = parentJson;
		}
	}
	
	function processObject(object) {
		if (window.location !== window.parent.location) {
			var event = {};
			event['name'] = 'forwardObject';
			event['data'] = object;
			window.parent.postMessage(JSON.stringify(event), "*");
		} else {
			postData(qAutomate_server_url, object);
		}
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
		} else {
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
			if (childFrame) {
				currentEventOrigin = event.origin;
				var eventObject = JSON.parse(event.data);
				if (eventObject['name'] == 'forwardObject') {
					var object = eventObject['data'];
					var json = mapDOM(childFrame, window);
					if (json) {
						setParentJson(object, json);
					}
					processObject(object);
				} else if (eventObject['name'] == 'loadCompleted') {
					var object = eventObject['data'];
					childFrame.domData = object;
					sendDomMap();
				} else if (eventObject['name'] == 'postDomMap') {
					var object = eventObject['data'];
					childFrame.domData = object;
					forwardPostDomMapEvent();
				}
			}
		}
	}
	
	if (window.addEventListener) {
		window.addEventListener("message", receiveMessage, false);
	} else {
		window.attachEvent("onmessage", receiveMessage);
	}
})();

