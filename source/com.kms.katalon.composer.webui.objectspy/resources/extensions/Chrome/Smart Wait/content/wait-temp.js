if (window === window.top && typeof window.katalonWaiter === 'undefined') {
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
        if (domTime && Date.now() - domTime > 30000) {
          domTime = "";
          callback(true);
        } else if (
          window.katalonWaiter.domModifiedTime &&
          Date.now() - window.katalonWaiter.domModifiedTime < 500
        ) {
          domCount++;
          if (domCount === 1) {
            domTime = Date.now();
          }
          return katalon_smart_waiter_do_dom_wait();
        } else {
          console.log(
            "content script: " +
              Date.now() +
              " is now, no changes since " +
              window.katalonWaiter.domModifiedTime
          );
          console.log("content script: Dom waiting is done !");
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
    try {
      var ajaxCount = 0;
      var ajaxTime = "";

      function katalon_smart_waiter_do_ajax_wait() {
        function isAjaxDone() {
          if (window.katalonWaiter.ajaxObjects) {
            if (window.katalonWaiter.ajaxObjects.length === 0) {
              console.log("no ajax objects");
              return true;
            } else {
              for (var index in window.katalonWaiter.ajaxObjects) {
                console.log(window.katalonWaiter.ajaxObjects[index]);
                if (
                  window.katalonWaiter.ajaxObjects[index].readyState !== 4 &&
                  window.katalonWaiter.ajaxObjects[index].readyState !==
                    undefined &&
                  window.katalonWaiter.ajaxObjects[index].readyState !== 0
                ) {
                  return false;
                }
              }
              return true;
            }
          } else {
            console.log("no ajax array defined yet");
            if (window.katalonOriginXMLHttpRequest) {
              window.katalonOriginXMLHttpRequest = "";
            }
            return true;
          }
        }

        setTimeout(() => {
          if (ajaxTime && Date.now() - ajaxTime > 30000) {
            ajaxCount = 0;
            ajaxTime = "";
            callback(true);
          } else if (isAjaxDone()) {
            console.log("content script: Ajax waiting is done !");
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
    } catch (e) {
      console.log(e);
    }
    return katalon_smart_waiter_do_ajax_wait();
  };

  (function katalon_smart_waiter_do_prewait() {
    window.katalonWaiter = new KatalonWaiter();
    console.log("content script: Katalon Waiter is up and running !");
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
          console.log("A new XMLHttpRequest was created ");
          var xhr = new window.katalonOriginXMLHttpRequest();
          window.katalonWaiter.ajaxObjects.push(xhr);
          return xhr;
        };
        window.XMLHttpRequest.prototype = window.katalonOriginXMLHttpRequest.prototype;

      }
    }
  })();
//   window.onbeforeunload = function() {
//     debugger
//   };
}
