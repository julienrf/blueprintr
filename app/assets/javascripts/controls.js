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
    },
    
    updateConflicts: function () {
      ajax.getJSON(routes.controllers.Projects.resourcesConflicts(this.id).url, (function (conflicts) {
        this.view.renderConflicts(conflicts)
      }).bind(this));
    }
  });
  
  
  var TaskCtl = Control.extend({
    
    init: function (attrs) {
      this._super(attrs.id);
      this.model = new models.Task(attrs)
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
          this.project.updateConflicts();
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
  
  window.ctl = {
    Control: Control,
    ProjectCtl: ProjectCtl,
    TaskCtl: TaskCtl
  }
})();