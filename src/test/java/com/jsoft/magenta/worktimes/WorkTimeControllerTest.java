package com.jsoft.magenta.worktimes;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class WorkTimeControllerTest
{
    @MockBean
    private WorkTimeService workTimeService;

    @Autowired
    private WorkTimeController workTimeController;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time creation tests")
    class WorkTimeCreationTest
    {
        @Test
        @DisplayName("Create work time")
        public void createWorkTime() throws Exception
        {
            WorkTime workTime = new WorkTime();
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now().plusMinutes(30));
            workTime.setNote("note");
            workTime.setDate(LocalDate.now());

            Mockito.when(workTimeService.createWorkTime(1L, workTime))
                    .thenReturn(workTime);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "wt/{spId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workTime)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).createWorkTime(1L, workTime);
        }

        @Test
        @DisplayName("Create supervised work time")
        public void createSupervisedWorkTime() throws Exception
        {
            WorkTime workTime = new WorkTime();
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now().plusMinutes(30));
            workTime.setNote("note");
            workTime.setDate(LocalDate.now());

            Mockito.when(workTimeService.createWorkTime(1L, 1L, workTime))
                    .thenReturn(workTime);

            mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "wt/{userId}/sp/{wtId}", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Stringify.asJsonString(workTime)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).createWorkTime(1L, 1L, workTime);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time update tests")
    class WorkTimeUpdateTests
    {
        @Test
        @DisplayName("Update work time note")
        public void updateWorkTimeNote() throws Exception
        {
            String newNote = "new note";

            Mockito.when(workTimeService.updateWorkTimeNote(1L, newNote))
                    .thenReturn(new WorkTime());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wt/{wtId}/note", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newNote))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).updateWorkTimeNote(1L, newNote);
        }

        @Test
        @DisplayName("Update work time sub-project")
        public void updateWorkTimeSubProject() throws Exception
        {
            Mockito.when(workTimeService.updateWorkTimeSubProject(1L, 1L))
                    .thenReturn(new WorkTime());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wt/{wtId}/sp/{spId}", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).updateWorkTimeSubProject(1L, 1L);
        }

        @Test
        @DisplayName("Update work time start time")
        public void updateWorkTimeStartTime() throws Exception
        {
            Mockito.when(workTimeService.updateWorkTimeStartTime(1L, LocalTime.of(10, 10)))
                    .thenReturn(new WorkTime());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wt/{wtId}/start", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("newStartTime", LocalTime.of(10, 10).toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).updateWorkTimeStartTime(1L, LocalTime.of(10, 10));
        }

        @Test
        @DisplayName("Update work time end time")
        public void updateWorkTimeEndTime() throws Exception
        {
            Mockito.when(workTimeService.updateWorkTimeEndTime(1L, LocalTime.of(10, 10)))
                    .thenReturn(new WorkTime());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wt/{wtId}/end", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("newEndTime", LocalTime.of(10, 10).toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).updateWorkTimeEndTime(1L, LocalTime.of(10, 10));
        }

        @Test
        @DisplayName("Update work time amount")
        public void updateWorkTimeAmount() throws Exception
        {
            Mockito.when(workTimeService.updateWorkTimeAmount(1L, 10D))
                    .thenReturn(new WorkTime());

            mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "wt/{wtId}/amount", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("10"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

            Mockito.verify(workTimeService).updateWorkTimeAmount(1L, 10D);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time get tests")
    class WorkTimeGetTests
    {
        @Test
        @DisplayName("Get all work times by date")
        public void getAllWorkTimesByDate() throws Exception
        {
            Mockito.when(workTimeService.getAllWorkTimesByDate(LocalDate.of(2021, 02, 02)))
                    .thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "wt")
                    .queryParam("date", LocalDate.of(2021, 02, 02).toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk());

            Mockito.verify(workTimeService).getAllWorkTimesByDate(LocalDate.of(2021, 02, 02));
        }

        @Test
        @DisplayName("Get all work times of user by date")
        public void getAllWorkTimesOfUserByDate() throws Exception
        {
            Mockito.when(workTimeService.getAllWorkTimesByUserAndDate(1L, LocalDate.of(2021, 02, 02)))
                    .thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "wt/{userId}", 1L)
                    .queryParam("date", LocalDate.of(2021, 02, 02).toString()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk());

            Mockito.verify(workTimeService).getAllWorkTimesByUserAndDate(1L, LocalDate.of(2021, 02, 02));
        }
    }

    @Test
    @DisplayName("Delete work time")
    public void deleteWorkTime() throws Exception
    {
        Mockito.doNothing().when(workTimeService).deleteWorkTime(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "wt/{wtId}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(workTimeService).deleteWorkTime(1L);
    }
}
