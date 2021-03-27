package com.jsoft.magenta.events.projects;

import com.jsoft.magenta.events.ApplicationEvent;

public class ProjectRelatedEntityEvent extends ApplicationEvent<Long> {

  public ProjectRelatedEntityEvent(Long projectId) {
    super(projectId);
  }
}
