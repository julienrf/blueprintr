;(function () {
  
  var Control = Class.extend({
    init: function (id) {
      this.id = id
    }
  });
  merge(Control, {
      create: function (Kind) {
        var F = function (args) {
          return Kind.apply(this, args)
        };
        F.prototype = Kind.prototype;
        Kind._instances || (Kind._instances = {});
        return function () {
          var instance = new F(arguments);
          Kind._instances[instance.id] = instance;
          return instance
        }
      },
      find: function (Kind) {
        return function (id) {
          return Kind._instances[id]
        }
      }
  });
  
  
  
  var ProjectCtl = Control.extend({
    
    init: function (attrs) {
      this._super(attrs.id);
      this.model = new models.Project(attrs);
      this.connect(routes.controllers.Projects.updates(this.id));
    },
    
    updateConflicts: function () {
      ajax.getJSON(routes.controllers.Projects.resourcesConflicts(this.id).url, (function (conflicts) {
        this.view.renderConflicts(conflicts)
      }).bind(this));
    },
    
    connect: function (updatesAction) {
      var updates = new WebSocket('ws://' + location.hostname + ':9000' + updatesAction.url);
      updates.onmessage = (function (e) {
        var update = JSON.parse(e.data);
        var task = Control.find(TaskCtl)(update.id);
        task.updatePosition(update.startTime);
      }).bind(this);
    }
  });
  
  
  var TaskCtl = Control.extend({
    
    init: function (attrs) {
      this._super(attrs.id);
      this.model = new models.Task(attrs)
    },
    
    move: function (position) {
      var action = 
      ajax.call({
        action: routes.controllers.Tasks.move(this.project.id, this.model.id, position),
        error: function () {
          // TODO
        }
      });
      this.model.move(position);
      this.view.move(Math.floor(this.model.startTime) / 10);
    },
    
    updatePosition: function (position) {
      this.model.move(position);
      this.view.move(Math.floor(this.model.startTime) / 10);
      this.project.updateConflicts();
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
  
  window.ctl = {
    Control: Control,
    ProjectCtl: ProjectCtl,
    TaskCtl: TaskCtl
  }
})();