var elementForInjectingScript = document.createElement("script");
elementForInjectingScript.src = browser.runtime.getURL("content/wait.js");
(document.head || document.documentElement).appendChild(elementForInjectingScript);
