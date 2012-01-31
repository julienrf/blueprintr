;(function () {
  
  var TaskCtl = Class.extend({
    
    init: function (settings) {
      this.model = new models.Task(settings.attrs)
    },
    
    move: function (position) {
      var process = (function (position) {
        this.model.move(position);
        this.view.move(Math.floor(this.model.startTime) / 10);
      }).bind(this);
      var action = routes.controllers.Tasks.move(this.model.id, position)
      ajax.call({
        url: action.url,
        method: action.method,
        type: 'json',
        success: (function (updated) {
          if (updated.startTime !== this.model.startTime) {
            process(position);
          }
        }).bind(this)
      });
      process(position);
    },
    
    dragStarted: function (e, dnd, v) {
      this.dndId = this.view.startDnD(e, 'move').id;
    },
    
    dropped: function (e, dnd, v) {
      if (dnd.id === this.dndId) {
        delete this.dndId;
        this.move(this.model.startTime + dnd.deltaY * 10);
      }
    },
    
    clicked: function (e, v) {
    }
  });
  
  window.controls = {
    TaskCtl: TaskCtl
  }
})();