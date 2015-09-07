//Recursively loop through DOM elements and assign properties to object
function treeHTML(action, element, object, currentWindow) {
	if (element) {
		object["action"] = action;
		object["type"] = element.nodeName;
		var nodeList = element.childNodes;
		if (nodeList != null) {
			if (nodeList.length) {
				object["content"] = [];
				for (var i = 0; i < nodeList.length; i++) {
					if (nodeList[i].nodeType == 3) {
						object["content"].push(nodeList[i].value);
					}
				}
			}
		}
		if (element.attributes != null) {
			if (element.attributes.length) {
				object["attributes"] = {};
				for (var i = 0; i < element.attributes.length; i++) {
					object["attributes"][element.attributes[i].nodeName] = element.attributes[i].value;
				}
			}
		}
		var xpath = createXPathFromElement(element);
		if (xpath) {
			object['xpath'] = xpath;
		}
		
		if (window.location === window.parent.location) {
			object["page"] = {};
			object["page"]['url'] = currentWindow.document.URL;
			object["page"]['title'] = currentWindow.document.title;
		}
	}
}

function mapDOM(action, element, currentWindow) {
	var treeObject = {};

	// If string convert to document Node
	if (typeof element === "string") {
		if (window.DOMParser) {
			  parser = new DOMParser();
			  docNode = parser.parseFromString(element,"text/xml");
		} else { // Microsoft strikes again
			  docNode = new ActiveXObject("Microsoft.XMLDOM");
			  docNode.async = false;
			  docNode.loadXML(element); 
		} 
		element = docNode.firstChild;
	}
	treeHTML(action, element, treeObject, currentWindow);
	return treeObject;
}

function createXPathFromElement(element) { 
	var allNodes = document.getElementsByTagName('*'); 
	for (var segs = []; element && element.nodeType == 1; element = element.parentNode) 
	{ 
		if ((element.getAttribute('id') != null) && (element.getAttribute('id') !== '')) {
				var uniqueIdCount = 0; 
				for (var n=0;n < allNodes.length;n++) { 
					if (((allNodes[n].getAttribute('id') != null) || (allNodes[n].getAttribute('id') !== '')) && allNodes[n].id == element.id) uniqueIdCount++; 
					if (uniqueIdCount > 1) break; 
				}; 
				if ( uniqueIdCount == 1) { 
					segs.unshift('id("' + element.getAttribute('id') + '")'); 
					return segs.join('/'); 
				} else if (element.nodeName) { 
					segs.unshift(element.nodeName.toLowerCase() + '[@id="' + element.getAttribute('id') + '"]'); 
				} 
		} else if ((element.getAttribute('class') != null) && (element.getAttribute('class') !== '')) {
			segs.unshift(element.nodeName.toLowerCase() + '[@class="' + element.getAttribute('class') + '"]'); 
		} else { 
			for (i = 1, sib = element.previousSibling; sib; sib = sib.previousSibling) { 
				if (sib.nodeName == element.nodeName)  i++; 
			}
			segs.unshift(element.nodeName.toLowerCase() + '[' + i + ']');
		}; 
	}; 
	return segs.length ? '/' + segs.join('/') : null; 
};

function detectIE() {
	var ua = window.navigator.userAgent;

	var msie = ua.indexOf('MSIE ');
	if (msie > 0) {
		// IE 10 or older => return version number
		return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	}

	var trident = ua.indexOf('Trident/');
	if (trident > 0) {
		// IE 11 => return version number
		var rv = ua.indexOf('rv:');
		return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	}

	var edge = ua.indexOf('Edge/');
	if (edge > 0) {
	   // IE 12 => return version number
	   return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
	}

	// other browser
	return false;
}

function detectChrome() {
	return (typeof chrome !== 'undefined') && (typeof chrome.extension !== 'undefined');
}