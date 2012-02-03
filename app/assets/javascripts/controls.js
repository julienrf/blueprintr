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
    },
    
    dataView: function () {
      return {
        id: this.model.id,
        name: this.model.name,
        resources: this.model.resources.map(function (resource) {
          return {
            id: resource.id,
            name: resource.name,
            color: resource.color
          }
        }),
        tasks: this.model.tasks.map(function (task) { return task.dataView() }),
        conflicts: this.model.conflicts
      }
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
    },
    
    dataView: function () {
      var _this = this;
      return {
        id: this.model.id,
        name: this.model.name,
        cssWidth: this.model.resources.length * 50,
        cssTop: this.model.startTime / 10,
        startTime: (new Date(this.model.startTime * 1000)).toString(),
        steps: this.model.steps.map(function (step) {
          return {
            id: step.id,
            name: step.name,
            cssHeight: step.duration / 10,
            resources: step.resources.map(function (resource) {
              return {
                cssLeft: _this.model.resources.indexOf(arrayFind(_this.model.resources, function (r) { return r.id === resource.id })),
                color: resource.color
              }
            })
          }
        })
      }
    }
  });
  
  window.ctl = {
    Control: Control,
    ProjectCtl: ProjectCtl,
    TaskCtl: TaskCtl
  }
})();