;(function () {
  
  window.navigate = function (url) {
    var showProject = url.match(/\/project\/(\d+)/);
    var page = document.find('body > article');
    if (showProject) {
      // Clear the current page
      page.innerHTML = '<div class=spinner></div>';
      // Load the project data
      ajax.getJSON(routes.controllers.Projects.projectJson(showProject[1]).url, function (data) {
        // Create the project object graph
        var project = init.Project(data);
        
        // Create the DOM
        var dom = views.projects.project(project.dataView());
        page.innerHTML = '';
        page.appendChild(dom);
        
        // Bind the DOM on the object graph
        views.bindAll(dom);
        
        // Push in history
        history.pushState({ ur: url }, "show-project", url)
      })
    } else { // Project list
      
    }
  };
})();