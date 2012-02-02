;(function (init) {
  
  init.Project = function (data) {
    var tasks = data.tasks.map(init.Task),
        project = ctl.Control.create(ctl.ProjectCtl)({
      id: data.id,
      name: data.name,
      tasks: tasks,
      resources: []
    });
    tasks.forEach(function (task) {
      task.project = project
    });
    return project
  };
  
  init.Task = function (data) {
    return ctl.Control.create(ctl.TaskCtl)({
      id: data.id,
      name: data.name,
      startTime: data.startTime,
      steps: []
    })
  };
  
  init.Step = function (data) {
    return {}
  };
  
})(window.init || (window.init = {}));