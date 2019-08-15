if (window === window.top && typeof window.katalonWaiter == 'undefined') {
  const KatalonWaiter = function() {
    this.domModifiedTime = '';
    this.ajaxObjects = [];
  };

  KatalonWaiter.prototype.doDomWait = function (callback) {
    var domCount = 0;
    var domTime = '';
    function doDomWait() {
      setTimeout(() => {
        if (domTime && (Date.now() - domTime) > 30000) {
          domTime = '';
          callback(true);
        } else if (window.katalonWaiter.domModifiedTime && (Date.now() - window.katalonWaiter.domModifiedTime) < 500) {
          domCount++;
          if (domCount === 1) {
            domTime = Date.now();
          }
          return doDomWait();
        } else {
	        console.log('content script: ' + Date.now() + ' is now, no changes since ' + window.katalonWaiter.domModifiedTime);
          console.log('content script: Dom waiting is done !');
          domTime = '';
          callback(true);
        }
      }, 100);
    }
    return doDomWait();
  };

  KatalonWaiter.prototype.doAjaxWait = function (callback) {
    var ajaxCount = 0;
    var ajaxTime = '';

    function doAjaxWait() {
      function isAjaxDone() {
        if (window.katalonWaiter.ajaxObjects) {
          if (window.katalonWaiter.ajaxObjects.length === 0) {
            return true;
          } else {
            for (var index in window.katalonWaiter.ajaxObjects) {
              if (window.katalonWaiter.ajaxObjects[index].readyState !== 4
                && window.katalonWaiter.ajaxObjects[index].readyState !== undefined
                && window.katalonWaiter.ajaxObjects[index].readyState !== 0) {
                return false;
              }
            }
            return true;
          }
        } else {
          if (window.origXMLHttpRequest) {
            window.origXMLHttpRequest = '';
          }
          return true;
        }
      }

      setTimeout(() => {
        if (ajaxTime && (Date.now() - ajaxTime) > 30000) {
          ajaxCount = 0;
          ajaxTime = '';
          callback(true);
        } else if (isAjaxDone()) {
	        console.log('content script: Ajax waiting is done !');
          ajaxCount = 0;
          ajaxTime = '';
          callback(true);
        } else {
          ajaxCount++;
          if (ajaxCount === 1) {
            ajaxTime = Date.now();
          }
          return doAjaxWait();
        }
      }, 100);
    };
    return doAjaxWait();
  };

  (function doPreWait() {
    window.katalonWaiter = new KatalonWaiter();
    console.log('content script: Katalon Waiter is up and running !');
    var document = window.document;
    function  setDOMModifiedTime() {
      window.katalonWaiter.domModifiedTime = Date.now();
      console.log('content script: ' + window.katalonWaiter.domModifiedTime);
    }
    document.addEventListener("DOMNodeInserted", setDOMModifiedTime, false);
    document.addEventListener("DOMNodeInsertedIntoDocument", setDOMModifiedTime, false);
    document.addEventListener("DOMNodeRemoved", setDOMModifiedTime, false);
    document.addEventListener("DOMNodeRemovedFromDocument", setDOMModifiedTime, false);
    document.addEventListener("DOMSubtreeModified", setDOMModifiedTime, false);
    document.addEventListener("DOMContentLoaded", setDOMModifiedTime, false);
    if (window.XMLHttpRequest) {
      if (!window.originXMLHttpRequest || !window.katalonWaiter.ajaxObjects) {
        window.katalonWaiter.ajaxObjects = [];
        window.originXMLHttpRequest = window.XMLHttpRequest;
        window.XMLHttpRequest = function () {
          var xhr = new window.originXMLHttpRequest();
          window.katalonWaiter.ajaxObjects.push(xhr);
          return xhr;
        }
      }
    }
  })();
}