package com.jsoft.magenta.subprojects;

import com.jsoft.magenta.security.annotations.projects.ProjectWritePermission;
import com.jsoft.magenta.util.validation.annotations.PositiveNumber;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("${application.url}sp")
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping("{projectId}")
    @ProjectWritePermission
    @ResponseStatus(HttpStatus.CREATED)
    public SubProject createSubProject(
            @PathVariable Long projectId,
            @RequestBody @Valid SubProject subProject
    ) {
        return this.subProjectService.createSubProject(projectId, subProject);
    }

    @PostMapping("{subProjectId}/association/{userId}")
    @ProjectWritePermission
    public SubProject createAssociation(
            @PathVariable Long subProjectId,
            @PathVariable Long userId
    ) {
        return this.subProjectService.createAssociation(userId, subProjectId);
    }

    @PutMapping
    @ProjectWritePermission
    public SubProject updateSubProject(@RequestBody @Valid SubProject subProject) {
        return this.subProjectService.updateSubProject(subProject);
    }

    @PatchMapping("name/{subProjectId}")
    @ProjectWritePermission
    public SubProject updateSubProjectName(
            @PathVariable Long subProjectId,
            @RequestBody @ValidName String newName
    ) {
        return this.subProjectService.updateSubProjectName(subProjectId, newName);
    }

    @PatchMapping("{subProjectId}/amount/{newAmount}")
    @ProjectWritePermission
    public SubProject updateSubProjectAmountOfHours(
            @PathVariable Long subProjectId,
            @PathVariable @PositiveNumber Double newAmount
    ) {
        return this.subProjectService.updateSubProjectHours(subProjectId, newAmount);
    }

    @PatchMapping("{subProjectId}/increase/{amountToAdd}")
    @ProjectWritePermission
    public SubProject increaseSubProjectAmountOfHours(
            @PathVariable Long subProjectId,
            @PathVariable @PositiveNumber Double amountToAdd
    ) {
        return this.subProjectService.increaseSubProjectHours(subProjectId, amountToAdd);
    }

    @PatchMapping("{subProjectId}/decrease/{amountToRemove}")
    @ProjectWritePermission
    public SubProject decreaseSubProjectAmountOfHours(
            @PathVariable Long subProjectId,
            @PathVariable @PositiveNumber Double amountToRemove
    ) {
        return this.subProjectService.decreaseSubProjectHours(subProjectId, amountToRemove);
    }

    @DeleteMapping("{subProjectId}/association/{userId}")
    @ProjectWritePermission
    public void removeAssociation(
            @PathVariable Long subProjectId,
            @PathVariable Long userId
    ) {
        this.subProjectService.removeAssociation(userId, subProjectId);
    }

    @DeleteMapping("{subProjectId}")
    @ProjectWritePermission
    public void deleteSubProject(@PathVariable Long subProjectId) {
        this.subProjectService.deleteSubProject(subProjectId);
    }
}
