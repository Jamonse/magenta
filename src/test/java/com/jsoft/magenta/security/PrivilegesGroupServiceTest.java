package com.jsoft.magenta.security;

import com.jsoft.magenta.security.dao.PrivilegeRepository;
import com.jsoft.magenta.security.dao.PrivilegesGroupRepository;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.service.PrivilegesGroupService;
import com.jsoft.magenta.util.AppDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PrivilegesGroupServiceTest
{
    @Mock
    private PrivilegesGroupRepository privilegesGroupRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @InjectMocks
    private PrivilegesGroupService privilegesGroupService;

    @BeforeEach
    private void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Privileges groups creation tests")
    class PrivilegesGroupsCreationTests
    {
        @Test
        @DisplayName("Create privileges group")
        public void createPrivilegesGroup()
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            Privilege privilege = new Privilege();
            privilege.setId(1L);
            privilege.setName("user");
            privilege.setLevel(AccessPermission.MANAGE);
            Privilege privilege1 = new Privilege();
            privilege1.setId(2L);
            privilege1.setName("posts");
            privilege1.setLevel(AccessPermission.WRITE);
            privilegesGroup.setPrivileges(Set.of(privilege, privilege1));

            Mockito.when(privilegesGroupRepository.save((privilegesGroup)))
                    .thenReturn(privilegesGroup);

            privilegesGroupService.createPrivilegesGroup(privilegesGroup);

            Mockito.verify(privilegesGroupRepository).save(privilegesGroup);
        }

        @Test
        @DisplayName("Create privileges group that inherits from another group")
        public void createPrivilegesGroupThatInheritsFromAnotherGroup()
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setId(1L);
            privilege.setName("user");
            privilege.setLevel(AccessPermission.MANAGE);
            Privilege privilege1 = new Privilege();
            privilege1.setId(2L);
            privilege1.setName("posts");
            privilege1.setLevel(AccessPermission.WRITE);
            Set<Privilege> privileges = new HashSet<>();
            privileges.add(privilege);
            privileges.add(privilege1);
            privilegesGroup.setPrivileges(privileges);

            PrivilegesGroup newGroup = new PrivilegesGroup();

            Mockito.when(privilegesGroupRepository.save((newGroup)))
                    .thenReturn(newGroup);
            Mockito.when(privilegesGroupRepository.findById(privilegesGroup.getId()))
                    .thenReturn(Optional.of(privilegesGroup));

            privilegesGroupService.createPrivilegesGroupInheritFrom(newGroup, privilegesGroup.getId());

            Assertions.assertThat(newGroup.getPrivileges())
                    .isEqualTo(privilegesGroup.getPrivileges());

            Mockito.verify(privilegesGroupRepository).save(newGroup);
            Mockito.verify(privilegesGroupRepository).findById(privilegesGroup.getId());
        }
    }

    @Nested
    @DisplayName("Privileges groups update tests")
    class PrivilegesGroupsUpdateTests
    {
        @Test
        @DisplayName("Update privileges group")
        public void updatePrivilegesGroup()
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setId(1L);
            privilegesGroup.setName("group");

            Mockito.when(privilegesGroupRepository.findById(privilegesGroup.getId()))
                    .thenReturn(Optional.of(privilegesGroup));

            privilegesGroupService.updatePrivilegesGroup(privilegesGroup);

            Mockito.verify(privilegesGroupRepository).findById(privilegesGroup.getId());
        }

        @Test
        @DisplayName("Update privileges group name")
        public void updatePrivilegesGroupName()
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setId(1L);
            privilegesGroup.setName("new name");

            Mockito.when(privilegesGroupRepository.findById(privilegesGroup.getId()))
                    .thenReturn(Optional.of(privilegesGroup));
            Mockito.when(privilegesGroupRepository.save(privilegesGroup)).thenReturn(privilegesGroup);

            privilegesGroupService.updateGroupName(privilegesGroup.getId(), "new name");

            Mockito.verify(privilegesGroupRepository).findById(privilegesGroup.getId());
            Mockito.verify(privilegesGroupRepository).save(privilegesGroup);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Privileges groups get tests")
    class PrivilegesGroupsGetTests
    {
        @Test
        @DisplayName("Get group")
        public void getGroup()
        {
            Mockito.when(privilegesGroupRepository.findById(1L))
                    .thenReturn(Optional.of(new PrivilegesGroup()));

            privilegesGroupService.getPrivilegesGroup(1L);

            Mockito.verify(privilegesGroupRepository).findById(1L);
        }

        @Test
        @DisplayName("Get all groups")
        public void getAllGroups()
        {
            Sort sort = Sort.by(AppDefaults.PRIVILEGES_GROUP_DEFAULT_SORT).descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);

            Mockito.when(privilegesGroupRepository.findAll(pageRequest))
                    .thenReturn(new PageImpl<>(List.of(), pageRequest, 1));

            privilegesGroupService.getAllPrivilegesGroups(0, 5, "name", false);

            Mockito.verify(privilegesGroupRepository).findAll(pageRequest);
        }

        @Test
        @DisplayName("Get all groups results")
        public void getAllGroupsResults()
        {
            Sort sort = Sort.by(AppDefaults.PRIVILEGES_GROUP_DEFAULT_SORT).descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Mockito.when(privilegesGroupRepository.findAllResultsBy(pageRequest))
                    .thenReturn(List.of());

            privilegesGroupService.getAllPrivilegesGroupsResults(5);

            Mockito.verify(privilegesGroupRepository).findAllResultsBy(pageRequest);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Privileges groups delete tests")
    class PrivilegesGroupsDeleteTests
    {
        @Test
        @DisplayName("Delete group")
        public void deleteGroup()
        {
            Mockito.when(privilegesGroupRepository.findById(1L))
                    .thenReturn(Optional.of(new PrivilegesGroup()));
            Mockito.doNothing().when(privilegesGroupRepository).deleteById(1L);

            privilegesGroupService.deletePrivilegesGroup(1L);

            Mockito.verify(privilegesGroupRepository).deleteById(1L);
            Mockito.verify(privilegesGroupRepository).findById(1L);
        }
    }
}
