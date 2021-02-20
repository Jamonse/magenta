package com.jsoft.magenta.workplans;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.Stringify;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class WorkPlanControllerTest
{
    @MockBean
    private WorkPlanService workPlanService;

    @Autowired
    private WorkPlanController workPlanController;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work plan creation tests")
    class SubProjectCreationTests
    {
        @Test
        @DisplayName("Create work plan")
        public void createSubProject() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanService.createWorkPlan(1L, workPlan)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "wp/{userId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workPlan)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workPlanService).createWorkPlan(1L, workPlan);
        }

        @Test
        @DisplayName("Create work plan with invalid title - should return 400")
        public void createSubProjectWithInvalidTitle() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("t");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanService.createWorkPlan(1L, workPlan)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "wp/{userId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workPlan)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value(AppConstants.TITLE_LENGTH_MESSAGE));

            Mockito.verify(workPlanService, Mockito.never()).createWorkPlan(1L, workPlan);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work plan update tests")
    class WorkPlanUpdateTests
    {
        @Test
        @DisplayName("Update work plan")
        public void updateWorkPlan() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanService.updateWorkPlan(workPlan)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "wp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workPlan)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workPlanService).updateWorkPlan(workPlan);
        }

        @Test
        @DisplayName("Update work plan with invalid title - should return 400")
        public void updateWorkPlanWithInvalidTitle() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("t");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanService.updateWorkPlan(workPlan)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "wp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workPlan)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());

            Mockito.verify(workPlanService, Mockito.never()).updateWorkPlan(workPlan);
        }

        @Test
        @DisplayName("Update work plan start date")
        public void updateWorkPlanStartDate() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));
            LocalDateTime localDateTime = LocalDateTime.of(2021, 02, 02, 10, 00);

            Mockito.when(workPlanService.updateWorkPlanStartDate(1L, localDateTime)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wp/{wpId}/start", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("newStartDate", localDateTime.toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workPlanService).updateWorkPlanStartDate(1L, localDateTime);
        }

        @Test
        @DisplayName("Update work plan end date")
        public void updateWorkPlanEndDate() throws Exception
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));
            LocalDateTime localDateTime = LocalDateTime.of(2021, 02, 02, 10, 00);

            Mockito.when(workPlanService.updateWorkPlanEndDate(1L, localDateTime)).thenReturn(workPlan);

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wp/{wpId}/end", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("newEndDate", localDateTime.toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workPlanService).updateWorkPlanEndDate(1L, localDateTime);
        }
    }

    @Test
    @DisplayName("Get user work plans")
    public void getUserWorkPlansByDate() throws Exception
    {
        Mockito.when(workPlanService
                .getAllWorkPlansByUserId(1L, 0, 5, AppDefaults.WORK_PLANS_DEFAULT_SORT, false))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "wp/{userId}", 1L)
                .queryParam("pageIndex", "0")
                .queryParam("pageSize", "5")
                .queryParam("sortBy", AppDefaults.WORK_PLANS_DEFAULT_SORT)
                .queryParam("asc", "false"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(workPlanService).getAllWorkPlansByUserId(1L, 0, 5, AppDefaults.WORK_PLANS_DEFAULT_SORT, false);
    }

    @Test
    @DisplayName("Delete work plan")
    public void deleteWorkPlan() throws Exception
    {
        Mockito.doNothing().when(workPlanService).deleteWorkPlan(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "wp/{wpId}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(workPlanService).deleteWorkPlan(1L);
    }
}
