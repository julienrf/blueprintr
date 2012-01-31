;(function () {
  
  var Task = Class.extend({
    
    init: function (attrs) {
      merge(this, attrs);
    },
    
    move: function (startTime) {
      this.startTime = startTime
    }
  });
  
  window.models = {
    Task: Task
  }
})();