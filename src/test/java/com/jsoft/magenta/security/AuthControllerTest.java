package com.jsoft.magenta.security;

import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.service.AuthService;
import com.jsoft.magenta.security.service.PrivilegesGroupService;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.Stringify;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class AuthControllerTest
{
    @MockBean
    private PrivilegesGroupService privilegesGroupService;

    @MockBean
    private AuthService authService;

    @Autowired
    private AuthController authController;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Auth creation tests")
    class AuthCreationTests
    {
        @Test
        @DisplayName("Create privileges group")
        public void createPrivilegesGroup() throws Exception
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setName("group");

            Mockito.when(privilegesGroupService.createPrivilegesGroup(privilegesGroup))
                    .thenReturn(privilegesGroup);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "auth/pg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(privilegesGroup)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(privilegesGroupService).createPrivilegesGroup(privilegesGroup);
        }

        @Test
        @DisplayName("Create privileges group with invalid name")
        public void createPrivilegesGroupWithInvalidName() throws Exception
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setName("g");

            Mockito.when(privilegesGroupService.createPrivilegesGroup(privilegesGroup))
                    .thenReturn(privilegesGroup);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "auth/pg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(privilegesGroup)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
                        .value(AppConstants.NAME_LENGTH_MESSAGE));

            Mockito.verify(privilegesGroupService, Mockito.never()).createPrivilegesGroup(privilegesGroup);
        }

        @Test
        @DisplayName("Authenticate using password")
        public void authenticateUsingPassword() throws Exception
        {
            Mockito.when(authService.authenticate("password"))
                    .thenReturn(true);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("password"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().string("true"));

            Mockito.verify(authService).authenticate("password");
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Auth update tests")
    class AuthUpdateTests
    {
        @Test
        @DisplayName("Update privileges group")
        public void updatePrivilegesGroup() throws Exception
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setName("group");

            Mockito.when(privilegesGroupService.updatePrivilegesGroup(privilegesGroup))
                    .thenReturn(privilegesGroup);

            mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "auth/pg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(privilegesGroup)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(privilegesGroupService).updatePrivilegesGroup(privilegesGroup);
        }

        @Test
        @DisplayName("Update user password")
        public void updateUserPassword() throws Exception
        {
            Mockito.when(authService.updatePassword(1L, "newPassword"))
                    .thenReturn(new User());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "auth/{userId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("newPassword"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(authService).updatePassword(1L, "newPassword");
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Auth get tests")
    class AuthGetTests
    {
        @Test
        @DisplayName("Get privileges group")
        public void updatePrivilegesGroup() throws Exception
        {
            PrivilegesGroup privilegesGroup = new PrivilegesGroup();
            privilegesGroup.setName("group");

            Mockito.when(privilegesGroupService.getPrivilegesGroup(1L))
                    .thenReturn(privilegesGroup);

            mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "auth/pg/{groupId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(privilegesGroupService).getPrivilegesGroup(1L);
        }

        @Test
        @DisplayName("Get all privileges groups")
        public void getAllPrivilegesGroups() throws Exception
        {
            Sort sort = Sort.by("name").descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);

            Mockito.when(privilegesGroupService.getAllPrivilegesGroups(0, 5, "name", false))
                    .thenReturn(new PageImpl<>(List.of(), pageRequest, 1));

            mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "auth/pg")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(privilegesGroupService).getAllPrivilegesGroups(0, 5, "name", false);
        }

        @Test
        @DisplayName("Get all privileges groups results")
        public void getAllPrivilegesGroupsResults() throws Exception
        {
            Sort sort = Sort.by("name").descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);

            Mockito.when(privilegesGroupService.getAllPrivilegesGroupsResults(5))
                    .thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "auth/pg/results")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(privilegesGroupService).getAllPrivilegesGroupsResults(5);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Auth delete tests")
    class AuthDeleteTests
    {
        @Test
        @DisplayName("Delete privileges group")
        public void deletePrivilegesGroup() throws Exception
        {
            Mockito.doNothing().when(privilegesGroupService).deletePrivilegesGroup(1L);

            mockMvc.perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "auth/pg/{groupId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk());

            Mockito.verify(privilegesGroupService).deletePrivilegesGroup(1L);
        }
    }

}
