;(function () {
  
  var namespace = function (identifier) {
    var ns = window;
    if (identifier !== '') {
      var parts = identifier.split('.')
      for (var i = 0 ; i < parts.length ; i++) {
        if (!ns[parts[i]]) {
          ns[parts[i]] = {}
        }
        ns = ns[parts[i]]
      }
    }
  };
  
  var merge = function (target, src) {
    for (var p in src) {
      target[p] = src[p]
    }
    return target
  };
  
  var ajax = {
      call: function (settings) {
        
        var parseResponse = function (type, xhr) {
          if (type === 'xml' || type === 'html') {
            return xhr.responseXml
          } else if (type === 'json') {
            return JSON.parse(xhr.responseText);
          } else {
            return xhr.responseText;
          }
        };
        
        var xhr = new XMLHttpRequest();
        xhr.open(settings.method || (settings.action && settings.action.method) || 'GET', settings.url || (settings.action && settings.action.url), true);
        xhr.onreadystatechange = function () {
          if (xhr.readyState === 4) {
            var status = Math.floor(xhr.status / 100);
            settings.complete && settings.complete(xhr);
            if (status === 2) {
              settings.success && settings.success(parseResponse(settings.type, xhr), xhr);
            } else if (status === 4) {
              settings.error && settings.error(parseResponse(settings.type, xhr), xhr);
            }
          }
        };
        var data = new FormData();
        if (settings.data) {
          for (var p in settings.data) {
            data.append(p, settings.data[p])
          }
        }
        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.send(data);
      },
      
      getJSON: function (url, callback) {
        ajax.call({
          url: url,
          method: 'GET',
          type: 'json',
          success: callback
        })
      }
  };
  
  var arrayFind = function (array, predicate) {
    return array.reduce(function (acc, elt) {
      return acc ? acc : (predicate(elt) ? elt : acc)
    }, undefined)
  };
  
  window.namespace = namespace;
  window.merge = merge;
  window.ajax = ajax;
  window.arrayFind = arrayFind;
})(this);