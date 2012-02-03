;(function () {
  
  var registry = (function () {
    var entries = []; // No HashMap in JavaScript, yeah operations will be O(n)…
    return {
      put: function (k, v) {
        entries = entries.filter(function (entry) {
          return entry.k !== k;
        });
        entries.push({
          k: k,
          v: v
        });
      },
      get: function (k) {
        var results = entries.filter(function (entry) {
          return entry.k === k
        });
        if (results.length > 0) {
          return results[0].v;
        }
      }
    }
  })();
  
  var View = Class.extend({
    
    init: function (root) {
      View.register(root, this);
    },
    
    bindEvents: function () {
      
    },
    
    on: function (elt, event, handler) {
      elt.addEventListener(event, (function (e) {
        return !(handler.bind(this)(e, View.find(event.target)));
      }).bind(this))
    },
    
    setDroppable: function () {
      this.on(this.root, 'dragenter', function (e) { e.preventDefault() });
      this.on(this.root, 'dragover', function (e) { e.preventDefault() });
    }
  });
  
  View.register = registry.put
  
  View.find = registry.get
  
  View.dnd = {
      value: {},
      update: function (e) {
        var value = View.dnd.value;
        value.x = e.clientX;
        value.y = e.clientY;
        value.deltaX = e.clientX - value.origX;
        value.deltaY = e.clientY - value.origY;
      },
      init: function (e) {
        View.dnd.value = {
            id: (new Date).getTime(),
            origX: e.clientX,
            origY: e.clientY
        };
        View.dnd.update(e);
      }
  };
  
  
  
  var ProjectView = View.extend({
    
    init: function (attrs) {
      this._super(attrs.root);
      merge(this, attrs);
    },
    
    renderConflicts: function (conflicts) {
      var conflicts = views.projects.conflicts(conflicts);
      this.conflicts.parentNode.replaceChild(conflicts, this.conflicts);
      this.conflicts = conflicts;
    }
  });
  
  ProjectView.bind = function (root) {
    return {
      root: root,
      conflicts: root.find('.conflicts')
    }
  };
  
  
  var TaskView = View.extend({
    
    init: function (attrs) {
      this._super(attrs.root);
      merge(this, attrs)
    },
    
    bindEvents: function () {
      this.setDroppable();
      this.on(this.root, 'dragstart', function (e, v) {
        this.ctl.dragStarted(e, v);
      });
      this.on(this.root, 'drop', function (e, v) {
        View.dnd.update(e);
        this.ctl.dropped(e, View.dnd.value, v);
      });
      this.on(this.root, 'click', function (e, v) {
        this.ctl.clicked(e, v);
      });
    },
    
    startDnD: function (e, effect) {
      View.dnd.init(e);
      e.dataTransfer.effectAllowed = effect;
      e.dataTransfer.setData('text', View.dnd.value.id);
      return View.dnd.value
    },
    
    move: function (position) {
      this.steps.style.setProperty('top', position + 'px');
    }
  });
  
  TaskView.bind = function (root) {
    return {
      root: root,
      steps: root.find('.steps')
    }
  };
  
  
  var bindAll = function (root) {
    var findProject = ctl.Control.find(ctl.ProjectCtl),
        findTask = ctl.Control.find(ctl.TaskCtl);
    var project = findProject(root.dataset.id);
    project.view = new ProjectView(merge(ProjectView.bind(root), {
      ctl: project
    }));
    root.findAll('.task').forEach(function (root) {
      var task = findTask(root.dataset.id);
      task.view = new TaskView(merge(TaskView.bind(root), {
        ctl: task
      }));
      task.view.bindEvents();
    })
    project.view.bindEvents();
  };
  
  
  window.views = {
      bindAll: bindAll,
      ProjectView: ProjectView,
      TaskView: TaskView
  }
})();