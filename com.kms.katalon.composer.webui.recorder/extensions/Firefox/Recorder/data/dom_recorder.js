(function() {
	if (typeof console === 'undefined') {
		console = { log: function() {} };
	}
	
	var lastEvent;
	
	var gHoverElement;		//whatever element the mouse is over
	var infoDiv;		//currently just container for InfoDivHover, might add more here
	var infoDivHover;	//container for hoverText text node.
	var hoverText;		//show information about current element that the mouse is over
	//START
	SetupDOMEvent();
	
	function SetupDOMEvent()
	{
		{
			//setup event listeners
			document.onchange = change;
			document.onmouseup = mouseUp;
			document.onmouseover = mouseOver;
			document.onmouseout = mouseOut;
			document.ondblclick = dblClick;
			document.onkeydown = keyDown;
			var forms = document.getElementsByTagName('form');
			for (i = 0; i < forms.length; i++) { 
				var f = forms[i];
				forms[i].onsubmit = submit;
			}
			var selects = document.getElementsByTagName('select');
			for (i = 0; i < selects.length; i++) { 
				selects[i].onfocus = focus;
			}
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
		var s=infoDivHover.style;
		if(color)s.color=color;
		if(bgcolor)s.backgroundColor=bgcolor;
		if(border)s.borderColor=border;
		if(text)hoverText.data=text;
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
		var doc = selectedElement.ownerDocument;
		var win = doc.defaultView || doc.parentWindow;
		win.focus();
		if (selectedElement != gHoverElement) {	
			gHoverElement = selectedElement;
			gHoverElement.style.outline = '2px solid #f00';
			InfoMSG(ElementInfo(gHoverElement),'yellow','blue','yellow');
		}
	}
	
	function mouseOut(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		if (selectedElement.nodeName.toLowerCase() == 'iframe') {
			window.focus();
		}
		if (gHoverElement != selectedElement) {
			return;
		}
		gHoverElement.style.outline = '';
		InfoMSG('-','white','black','white');	
		gHoverElement=null;
	}
	
	function getSelectValues(select) {
		var result = [];
		var options = select && select.options;
		var opt;

		for (var i=0, iLen=options.length; i<iLen; i++) {
			opt = options[i];
			if (opt.selected) {
				result.push(opt.value || opt.text);
			}
		}
		return result;
	}
	
	function focus(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		if (selectedElement.tagName.toLowerCase() == 'select') {
			selectedElement.oldValue = getSelectValues(selectedElement);
			selectedElement.onfocus = null;
		}
	}
	
	function change(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		
		var isRecorded = false;
		var action = {};
		if (selectedElement.tagName.toLowerCase() == 'input') {
			if (selectedElement.type.toLowerCase() != 'radio' && selectedElement.type.toLowerCase() != 'checkbox') {	
				isRecorded = true;
			}
		} else {
			isRecorded = true;
		}
		if (isRecorded) {
			action["actionName"] = 'inputChange';
			if (selectedElement.tagName.toLowerCase() == 'select') {
				action["actionData"] = {};
				action["actionData"]["oldValue"] = selectedElement.oldValue
				action["actionData"]["newValue"] = getSelectValues(selectedElement);
				selectedElement.oldValue = action["actionData"]["newValue"];
			} else {
				action["actionData"] = selectedElement.value;
			}
			sendData(action, selectedElement);
		}
	}
	
	function mouseUp(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		var clickType = 'left';
		if (e.which) {
			if (e.which == 3) clickType = 'right';
			if (e.which == 2) clickType = 'middle';
		} else if (e.button) {
			if (e.button == 2) clickType = 'right';
			if (e.button == 4) clickType = 'middle';
		}
		var isRecorded = false;
		var elementTag = selectedElement.tagName.toLowerCase();
		if (clickType == 'left') {
			if (elementTag == 'input') {
				var elementInputType = selectedElement.type.toLowerCase();
				if (elementInputType == 'button' || elementInputType == 'submit' || elementInputType == 'radio' || elementInputType == 'image' || elementInputType == 'checkbox') {
					isRecorded = true;
				}
			} else if (elementTag != 'select' && elementTag != 'option' && elementTag != 'textarea') {
				isRecorded = true;
			}
		} else {
			isRecorded = true;
		}
		if (isRecorded) {
			var action = {};
			action["actionName"] = 'click';
			action["actionData"] = clickType;
			sendData(action, selectedElement);
		}
		lastEvent = 'click';
	}
	
	function dblClick(e) {
		var selectedElement = e ? e.target : window.event.srcElement;
		var action = {};
		action["actionName"] = 'doubleClick';
		action["actionData"] = '';
		sendData(action, selectedElement);
	}
	
	function keyDown(e) {	
		var keycode = (e) ? e.which : window.event.keyCode;
		if (keycode == 13) {
			lastEvent = 'enter';
		}
	}
	
	function submit() {
		if (lastEvent == 'enter') {
			var selectedElement = this;
			var action = {};
			action["actionName"] = 'submit';
			action["actionData"] = '';
			sendData(action, selectedElement);
		}
	}
	
	function sendData(action, element) {
		if (element) {
			var jsonObject = mapDOM(action, element, window);
			processObject(jsonObject);
		}
	}
	
	function setParentJson(object, parentJson) {
		if ('parent' in object) {
			setParentJson(object['parent'], parentJson);
		} else {
			object['parent'] = parentJson;
		}
	}
	
	// for Firefox
	if (!detectChrome() && !detectIE() && !(typeof self === 'undefined')) {
		self.on('message', function(message) {
			if (message.kind == "postSuccess") {
				console.log("POST success");
			} else if (message.kind && message.kind == "updateServerUrl") {
				qAutomate_server_url = message.url;
				if (window.location === window.parent.location) {
					var action = {};
					action["actionName"] = "navigate";
					action["actionData"] = window.document.URL;
					sendData(action, document);
				}
			}
		});
	}
	
	function postData(url, object) {
		if (object) {
			var data = 'element=' + encodeURIComponent(JSON.stringify(object));
			if ((typeof chrome !== 'undefined') && (typeof chrome.extension !== 'undefined')) {
				chrome.runtime.sendMessage({
					method: 'POST',
					action: 'xhttp',
					url: url,
					data: object
				}, function() {
					console.log("POST success");
				});
			} else if (detectIE() != false) {
				if (window.httpRequestExtension) {
					var response = window.httpRequestExtension.postRequest(data, url);
					if (response === '200') {
						console.log("POST success");
					} else {
						console.log(response);
					}
				}
			} else {
				self.port.emit("postData", { url : url, data : object });
			}
		}
	}
	
	function processObject(object) {
		if (window.location !== window.parent.location) {
			window.parent.postMessage(JSON.stringify(object), "*");
		} else {
			postData(qAutomate_server_url, object);
		}
	}
	
	function receiveMessage(event) {
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
			var object = JSON.parse(event.data);
			var action = {};
			action["actionName"] = "goIntoFrame";
			action["actionData"] = "";
			var json = mapDOM(action, childFrame, window);
			if (json) {
				setParentJson(object, json);
			}
			processObject(object);
		}
	}
	if (window.addEventListener) {
		window.addEventListener("message", receiveMessage, false);
	} else {
		window.attachEvent("onmessage", receiveMessage);
	}
})();

