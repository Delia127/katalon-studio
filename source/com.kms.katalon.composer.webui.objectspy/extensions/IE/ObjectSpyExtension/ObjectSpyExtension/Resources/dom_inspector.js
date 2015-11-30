(function () {
    //GLOBALS
    var gHoverElement;		//whatever element the mouse is over

    //extended	
    var infoDiv;		//currently just container for InfoDivHover, might add more here
    var infoDivHover;	//container for hoverText text node.
    var hoverText;		//show information about current element that the mouse is over

    var currentEventOrigin;

    if (!window.console) console = {
        log: function () {
        }
    };
    //START
    SetupDOMSelection();

    //(Section 1) Element Selection
    function SetupDOMSelection() {
        {
            //setup event listeners
            document.onkeyup = keyUp;
            document.onmouseover = mouseOver;
            document.onmouseout = mouseOut;
        }
        {
            //setup informational div to show which element the mouse is over.
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
            s = infoDivHover.style;
            s.fontWeight = 'bold';
            s.padding = '2px';
            s.Opacity = '0.8';
            s.borderWidth = 'thin';
            s.borderStyle = 'solid';
            s.borderColor = 'white';
            s.backgroundColor = 'black';
            s.color = 'white';
            infoDiv.appendChild(infoDivHover);
            hoverText = document.createTextNode('selecting');
            infoDivHover.appendChild(hoverText);

            // create instruction div to guide user how to capture object
            var instructionDiv = document.createElement('div');
            instructionDiv.style.position = 'fixed';
            instructionDiv.style.zIndex = '99999999';
            instructionDiv.style.display = 'none';
            instructionDiv.style.opacity = '0.9';
            instructionDiv.innerHTML = '<img src="data:image/png;base64,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" />';

            document.body.appendChild(instructionDiv);
            window.onmousemove = function(e) {
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
            };
            window.onmouseout = function() {
                instructionDiv.style.display = 'none';
            };
        }
    }

    function InfoMSG(text, color, bgcolor, border) {
        var s = infoDivHover.style;
        if (color) s.color = color;
        if (bgcolor) s.backgroundColor = bgcolor;
        if (border) s.borderColor = border;
        if (text) hoverText.data = text;
    }

    function ElementInfo(element) {
        var txt = '';
        if (element) {
            txt = element.nodeName.toLowerCase();
            txt = attrib(txt, element, 'id');
            txt = attrib(txt, element, 'class');
            txt = '//' + txt;
        }
        return txt;

        function attrib(t, e, a) {
            if ((e.getAttribute(a) != null) && (e.getAttribute(a) !== '')) {
                t += "[@" + a + "='" + e.getAttribute(a) + "']";
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
            InfoMSG(ElementInfo(gHoverElement), 'yellow', 'blue', 'yellow');
        }
    }

    function mouseOut(e) {
        var selectedElement = e ? e.target : window.event.srcElement;
        if (gHoverElement != selectedElement) {
            return;
        }
        gHoverElement.style.outline = '';
        InfoMSG('-', 'white', 'black', 'white');
        gHoverElement = null;
    }

    function flashElement() {
        if (gHoverElement) {
            $(gHoverElement).css({
                outline: "2px solid #0F0"
            }).animate({ outlineColor: "#FFF" }, 100)
				.animate({ outlineColor: "#0F0" }, 100)
				.animate({ outlineColor: "#FFF" }, 100)
				.animate({ outlineColor: "#0F0" }, 100)
				.animate({ outlineColor: "#F00" }, 100, function () {
				    if (gHoverElement && (gHoverElement.nodeName.toLowerCase() == 'iframe' || gHoverElement.nodeName.toLowerCase() == 'frame')) {
				        var iframeContentWindow = gHoverElement.contentWindow;
				        if (iframeContentWindow && currentEventOrigin) {
				            iframeContentWindow.postMessage("responseSuccess", currentEventOrigin);
				        }
				    }
				});
        }
    }

    function sendData() {
        if (gHoverElement) {
            if (gHoverElement.nodeName.toLowerCase() == 'iframe') {
                gHoverElement.contentWindow.postMessage("keyboardTriggerEvent", "*");
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
        self.on('message', function (message) {
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
                }, function () {
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
                self.port.emit("postData", { url: url, data: data });
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

