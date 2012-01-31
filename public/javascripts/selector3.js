/* http://dev.w3.org/2006/webapi/selectors-api2/ */
;(function () {
  var augment, api;
  
  augment = function (type, methods) {
    for (var method in methods) {
      if (typeof type.prototype[method] !== 'function') {
        type.prototype[method] = methods[method];
      }
    }
  };

  api = {
      find: function (selector) {
        return this.querySelector(selector);
      },
      findAll: function (selector) {
        var nodelist = this.querySelectorAll(selector),
            n = nodelist.length,
            array = new Array(nodelist.length);
        for (var i = 0 ; i < n ; i++) {
          array[i] = nodelist[i];
        }
        return array;
      }
  };
  
  augment(Element, api);
  augment(Document, api);
})();