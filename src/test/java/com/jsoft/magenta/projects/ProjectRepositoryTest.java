package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.projects.domain.SubProject;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest
{
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init()
    {
        User user = userRepository.findById(150L).orElse(null);

        Project project = new Project();
        project.setName("project");
        project.setAvailable(true);
        project.setCreatedAt(LocalDate.now());

        SubProject subProject = new SubProject();
        subProject.setName("sp");
        Set<SubProject> subProjects = new HashSet<>();
        subProjects.add(subProject);
        project.setSubProjects(subProjects);

        this.projectRepository.save(project);

        ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.ADMIN);
        Set<ProjectAssociation> projectAssociations = new HashSet<>();
        projectAssociations.add(projectAssociation);
        project.setAssociations(projectAssociations);

        this.projectRepository.save(project);
    }

    @Test
    @DisplayName("Saving sub project with project")
    public void saveSubProject()
    {
        Project project = new Project();
        project.setName("project");
        project.setAvailable(true);
        project.setCreatedAt(LocalDate.now());

        SubProject subProject = new SubProject();
        subProject.setName("sp");
        Set<SubProject> subProjects = new HashSet<>();
        subProjects.add(subProject);
        project.setSubProjects(subProjects);

        this.projectRepository.save(project);

        Assertions.assertThat(subProject)
                .extracting("id")
                .isNotNull();
    }

    @Test
    @DisplayName("Get all projects by association and permission level")
    public void getAllProjectsByAssociationAndPermissionLevel()
    {
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        Page<Project> result = this.projectRepository
                .findAllByAssociationsUserIdAndAssociationsPermission(150L, AccessPermission.ADMIN, pageRequest);

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("Get all projects results by association")
    public void getAllProjectsResultsByAssociation()
    {
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        List<ProjectSearchResult> result = this.projectRepository
                .findAllByAssociationsUserId(150L, pageRequest);

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    @DisplayName("Get all projects results by association and permission")
    public void getAllProjectsResultsByAssociationAndPermission()
    {
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        List<ProjectSearchResult> result = this.projectRepository
                .findAllResultsByAssociationsUserIdAndAssociationsPermission(150L, AccessPermission.ADMIN, pageRequest);

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    @DisplayName("Get all projects results")
    public void getAllProjectsResults()
    {
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);

        List<ProjectSearchResult> result = this.projectRepository
                .findAllResultsBy(pageRequest);

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }
    
}
