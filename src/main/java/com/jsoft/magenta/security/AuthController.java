package com.jsoft.magenta.security;

import com.jsoft.magenta.security.annotations.users.UserWritePermission;
import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.model.PrivilegesGroupSearchResult;
import com.jsoft.magenta.security.service.AuthService;
import com.jsoft.magenta.security.service.PrivilegesGroupService;
import com.jsoft.magenta.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;
import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;

@Validated
@RestController
@RequestMapping("${application.url}auth")
@RequiredArgsConstructor
public class AuthController
{
    private final PrivilegesGroupService privilegesGroupService;
    private final AuthService authService;

    @PostMapping("pg")
    @UserWritePermission
    @ResponseStatus(HttpStatus.CREATED)
    public PrivilegesGroup createPrivilegesGroup(@RequestBody @Valid PrivilegesGroup privilegesGroup)
    {
        return this.privilegesGroupService.createPrivilegesGroup(privilegesGroup);
    }

    @PostMapping
    public boolean authenticate(@RequestBody @NotBlank String passwordToMatch)
    {
        return this.authService.authenticate(passwordToMatch);
    }

    @PutMapping("pg")
    @UserWritePermission
    public PrivilegesGroup updatePrivilegesGroup(@RequestBody @Valid PrivilegesGroup privilegesGroup)
    {
        return this.privilegesGroupService.updatePrivilegesGroup(privilegesGroup);
    }

    @PatchMapping("{userId}")
    @UserWritePermission
    public User updateUserPassword(
            @PathVariable Long userId,
            @RequestBody @NotBlank String newPassword
    )
    {
        return this.authService.updatePassword(userId, newPassword);
    }

    @GetMapping("pg/{groupId}")
    @UserWritePermission
    public PrivilegesGroup getPrivilegesGroup(@PathVariable Long groupId)
    {
        return this.privilegesGroupService.getPrivilegesGroup(groupId);
    }

    @GetMapping("pg")
    @UserWritePermission
    public Page<PrivilegesGroup> getAllPrivilegesGroups(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = PRIVILEGES_GROUP_DEFAULT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.privilegesGroupService.getAllPrivilegesGroups(pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("pg/results")
    @UserWritePermission
    public List<PrivilegesGroupSearchResult> getAllPrivilegesGroupsResults(
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
    )
    {
        return this.privilegesGroupService.getAllPrivilegesGroupsResults(resultsCount);
    }

    @DeleteMapping("pg/{groupId}")
    @UserWritePermission
    public void deletePrivilegesGroup(@PathVariable Long groupId)
    {
        this.privilegesGroupService.deletePrivilegesGroup(groupId);
    }
}
