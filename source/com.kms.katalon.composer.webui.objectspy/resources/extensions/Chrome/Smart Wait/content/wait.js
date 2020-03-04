if (window === window.top && (typeof window.katalonWaiter == "undefined")) {
  const KatalonWaiter = function() {
    this.domModifiedTime = "";
    this.ajaxObjects = [];
  };

  KatalonWaiter.prototype.katalon_smart_waiter_do_dom_wait = function(
    callback
  ) {
    var domCount = 0;
    var domTime = "";
    function katalon_smart_waiter_do_dom_wait() {
      setTimeout(() => {
        if (domTime && (Date.now() - domTime > 30000)) {
          domTime = "";
          callback(true);
        } else if (
          window.katalonWaiter.domModifiedTime &&
          (Date.now() - window.katalonWaiter.domModifiedTime < 400)
        ) {
          domCount++;
          if (domCount === 1) {
            domTime = Date.now();
          }
          return katalon_smart_waiter_do_dom_wait();
        } else {
          domTime = "";
          callback(true);
        }
      }, 100);
    }
    return katalon_smart_waiter_do_dom_wait();
  };

  KatalonWaiter.prototype.katalon_smart_waiter_do_ajax_wait = function(
    callback
  ) {
    var ajaxCount = 0;
    var ajaxTime = "";

    function notServerSentEvent(ajaxObject) {	
      var contentType = ajaxObject.getResponseHeader("Content-Type");	
      return !(contentType && contentType.includes('text/event-stream'));	
    }

    function katalon_smart_waiter_do_ajax_wait() {
      function isAjaxDone() {
        if (window.katalonWaiter.ajaxObjects) {
          if (window.katalonWaiter.ajaxObjects.length === 0) {
            return true;
          } else {
            for (var index in window.katalonWaiter.ajaxObjects) {
              if (
                (window.katalonWaiter.ajaxObjects[index].readyState) !== 4 &&
                (window.katalonWaiter.ajaxObjects[index].readyState) !==
                  undefined &&
                (window.katalonWaiter.ajaxObjects[index].readyState) !== 0 &&
                notServerSentEvent(window.katalonWaiter.ajaxObjects[index]))
              {
                return false;
              }
            }
            return true;
          }
        } else {
          if (window.katalonOriginXMLHttpRequest) {
            window.katalonOriginXMLHttpRequest = "";
          }
          return true;
        }
      }

      setTimeout(() => {
        if (ajaxTime && (Date.now() - ajaxTime > 30000)) {
          ajaxCount = 0;
          ajaxTime = "";
          callback(true);
        } else if (isAjaxDone()) {
          ajaxCount = 0;
          ajaxTime = "";
          callback(true);
        } else {
          ajaxCount++;
          if (ajaxCount === 1) {
            ajaxTime = Date.now();
          }
          return katalon_smart_waiter_do_ajax_wait();
        }
      }, 100);
    }

    return katalon_smart_waiter_do_ajax_wait();
  };

  (function katalon_smart_waiter_do_prewait() {
    window.katalonWaiter = new KatalonWaiter();
    console.log("content script: Katalon Waiter v.2 is up and running !");
    var document = window.document;
    function katalon_smart_waiter_setDOMModifiedTime() {
      window.katalonWaiter.domModifiedTime = Date.now();
    }
    document.addEventListener(
      "DOMNodeInserted",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    document.addEventListener(
      "DOMNodeInsertedIntoDocument",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    document.addEventListener(
      "DOMNodeRemoved",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    document.addEventListener(
      "DOMNodeRemovedFromDocument",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    document.addEventListener(
      "DOMSubtreeModified",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    document.addEventListener(
      "DOMContentLoaded",
      katalon_smart_waiter_setDOMModifiedTime,
      false
    );
    if (window.XMLHttpRequest) {
      if (!window.katalonOriginXMLHttpRequest || !window.katalonWaiter.ajaxObjects) {
        window.katalonWaiter.ajaxObjects = [];
        window.katalonOriginXMLHttpRequest = window.XMLHttpRequest;
        window.XMLHttpRequest = function() {
          var xhr = new window.katalonOriginXMLHttpRequest();
          window.katalonWaiter.ajaxObjects.push(xhr);
          return xhr;
        };
        // Restore properties
        for (var key of Object.keys(window.katalonOriginXMLHttpRequest)) {
          window.XMLHttpRequest[key] = window.katalonOriginXMLHttpRequest[key];
        }
        window.XMLHttpRequest.prototype = window.katalonOriginXMLHttpRequest.prototype;
      }
    }
  })();
}
