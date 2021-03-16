package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.annotations.projects.ProjectWritePermission;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.subprojects.SubProjectSearchResult;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import com.jsoft.magenta.util.validation.annotations.ValidPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;

@Validated
@RestController
@RequestMapping("${application.url}projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ProjectWritePermission
    public Project createProject(
            @PathVariable Long accountId,
            @RequestBody @Valid Project project
    ) {
        return this.projectService.createProject(accountId, project);
    }

    @PostMapping("{projectId}/association/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ProjectWritePermission
    public void createAssociation(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody @ValidPermission String permission
    ) {
        AccessPermission accessPermission = AccessPermission.valueOf(permission.toUpperCase());
        this.projectService.createAssociation(userId, projectId, accessPermission);
    }

    @PutMapping
    @ProjectWritePermission
    public Project updateProject(@RequestBody @Valid Project project) {
        return this.projectService.updateProject(project);
    }

    @PatchMapping("name/{projectId}")
    @ProjectWritePermission
    public Project updateProjectName(
            @PathVariable Long projectId,
            @RequestBody @ValidName String newName
    ) {
        return this.projectService.updateProjectName(projectId, newName);
    }

    @PatchMapping("{projectId}/association/{userId}")
    @ProjectWritePermission
    public void updateAssociation(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody @ValidPermission String permission
    ) {
        AccessPermission accessPermission = AccessPermission.valueOf(permission.toUpperCase());
        this.projectService.updateAssociation(userId, projectId, accessPermission);
    }

    @GetMapping
    public Page<Project> getAllProjects(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = PROJECTS_DEFAULT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    ) {
        return this.projectService.getAllProjects(pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("search")
    public List<ProjectSearchResult> getAllProjectsResultsByNameExample(
            @RequestParam String nameExample,
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
    ) {
        return this.projectService.getAllProjectsResultsByNameExample(nameExample, resultsCount);
    }

    @GetMapping("{projectId}/sp")
    public Page<SubProject> getProjectSubProjects(
            @PathVariable Long projectId,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = PROJECTS_DEFAULT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    ) {
        return this.projectService.getAllProjectSubProjects(projectId, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("{projectId}/sp/results")
    public List<SubProjectSearchResult> getProjectSubProjects(
            @PathVariable Long projectId,
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount,
            @RequestParam(required = false) String nameExample
    ) {
        if (nameExample == null)
            return this.projectService.getProjectSubProjectResults(projectId, resultsCount);
        return this.projectService.getProjectSubProjectResultsByNameExample(projectId, nameExample, resultsCount);
    }

    @DeleteMapping("{projectId}/association/{userId}")
    @ProjectWritePermission
    public void removeAssociation(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        this.projectService.removeAssociation(userId, projectId);
    }

    @DeleteMapping("{projectId}/association")
    @ProjectWritePermission
    public void removeAllAssociations(@PathVariable Long projectId) {
        this.projectService.removeAllAssociations(projectId);
    }

    @DeleteMapping("{projectId}")
    @ProjectWritePermission
    public void deleteProject(@PathVariable Long projectId) {
        this.projectService.deleteProject(projectId);
    }

}
