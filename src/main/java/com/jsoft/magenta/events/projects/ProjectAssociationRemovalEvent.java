package com.jsoft.magenta.events.projects;

import com.jsoft.magenta.events.AssociationEvent;
import com.jsoft.magenta.projects.domain.Project;

public class ProjectAssociationRemovalEvent extends AssociationEvent<Project> {
    public ProjectAssociationRemovalEvent(Project project, Long associatedUserId) {
        super(project, associatedUserId);
    }
}
