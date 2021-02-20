package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.users.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class WorkTimeServiceTest
{
    @InjectMocks
    private WorkTimeService workTimeService;

    @Mock
    private WorkTimeRepository workTimeRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    private void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    private static MockedStatic<UserEvaluator> mockedStatic;

    @BeforeAll
    private static void initStatic()
    {
        mockedStatic = Mockito.mockStatic(UserEvaluator.class);
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time creation tests")
    class WorkTimeCreationTests
    {
        @Test
        @DisplayName("Create work time")
        public void createWorkTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now().plusMinutes(30));
            User user = new User();
            user.setId(1L);

            mockedStatic.when(UserEvaluator::currentUser)
                    .thenReturn(user);
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.createWorkTime(1L, workTime);

            Assertions.assertThat(workTime)
                    .extracting("amount")
                    .isNotNull()
                    .isEqualTo(0.5);

            Assertions.assertThat(workTime)
                    .extracting("user")
                    .isNotNull()
                    .extracting("id")
                    .isNotNull()
                    .isEqualTo(1L);

            Assertions.assertThat(workTime)
                    .extracting("subProject")
                    .isNotNull()
                    .extracting("id")
                    .isNotNull()
                    .isEqualTo(1L);

            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Create supervised work time")
        public void createSupervisedWorkTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now().plusMinutes(30));
            User user = new User();
            user.setId(1L);

            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.createWorkTime(user.getId(), 1L, workTime);

            Assertions.assertThat(workTime)
                    .extracting("amount")
                    .isNotNull()
                    .isEqualTo(0.5);

            Assertions.assertThat(workTime)
                    .extracting("user")
                    .isNotNull()
                    .extracting("id")
                    .isNotNull()
                    .isEqualTo(1L);

            Assertions.assertThat(workTime)
                    .extracting("subProject")
                    .isNotNull()
                    .extracting("id")
                    .isNotNull()
                    .isEqualTo(1L);

            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Create work time without start date with end date")
        public void createWorkTimeWithoutStartDateWithEndDate()
        {
            WorkTime workTime = new WorkTime();
            workTime.setEndTime(LocalTime.now().plusMinutes(30));

            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            Assertions.assertThatThrownBy(() -> workTimeService.createWorkTime(1L, workTime))
                    .isInstanceOf(DateTimeException.class);

            Mockito.verify(workTimeRepository, Mockito.never()).save(workTime);
        }

        @Test
        @DisplayName("Create work time with start date without end date")
        public void createWorkTimeWithStartDateWithoutEndDate()
        {
            WorkTime workTime = new WorkTime();
            workTime.setStartTime(LocalTime.now());

            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            Assertions.assertThatThrownBy(() -> workTimeService.createWorkTime(1L, workTime))
                    .isInstanceOf(DateTimeException.class);

            Mockito.verify(workTimeRepository, Mockito.never()).save(workTime);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time update tests")
    class WorkTimeUpdateTests
    {
        @Test
        @DisplayName("Update work time note")
        public void updateWorkTimeNote()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);

            Mockito.when(workTimeRepository.getOne(workTime.getId()))
                    .thenReturn(workTime);
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.updateWorkTimeNote(1L, "new note");

            Assertions.assertThat(workTime)
                    .extracting("note")
                    .isNotNull()
                    .isEqualTo("new note");

            Mockito.verify(workTimeRepository).getOne(workTime.getId());
            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Update work time sub-project")
        public void updateWorkTimeSubProject()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);

            Mockito.when(workTimeRepository.getOne(workTime.getId()))
                    .thenReturn(workTime);
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.updateWorkTimeSubProject(1L, 1L);

            Assertions.assertThat(workTime)
                    .extracting("subProject")
                    .isNotNull()
                    .extracting("id")
                    .isEqualTo(1L);

            Mockito.verify(workTimeRepository).getOne(workTime.getId());
            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Update work time start time")
        public void updateWorkTimeStartTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now());
            LocalTime newStartTime = LocalTime.of(10, 0);

            Mockito.when(workTimeRepository.findById(workTime.getId()))
                    .thenReturn(Optional.of(workTime));
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.updateWorkTimeStartTime(1L, newStartTime);

            Assertions.assertThat(workTime)
                    .extracting("startTime")
                    .isNotNull()
                    .isEqualTo(newStartTime);

            Assertions.assertThat(workTime)
                    .extracting("amount")
                    .isNotNull();

            Mockito.verify(workTimeRepository).findById(workTime.getId());
            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Update work time start time that is after end time - should throw exception")
        public void updateWorkTimeStartTimeThatIsAfterEndTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now());
            LocalTime newStartTime = LocalTime.now().plusMinutes(30);

            Mockito.when(workTimeRepository.findById(workTime.getId()))
                    .thenReturn(Optional.of(workTime));
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            Assertions.assertThatThrownBy(() -> workTimeService.updateWorkTimeStartTime(1L, newStartTime))
                    .isInstanceOf(DateTimeException.class)
                    .hasMessage("Start time of a work time cannot be after its end time");

            Mockito.verify(workTimeRepository).findById(workTime.getId());
            Mockito.verify(workTimeRepository, Mockito.never()).save(workTime);
        }

        @Test
        @DisplayName("Update work time end time")
        public void updateWorkTimeEndTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now());
            LocalTime newEndTime = LocalTime.of(14, 0);

            Mockito.when(workTimeRepository.findById(workTime.getId()))
                    .thenReturn(Optional.of(workTime));
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.updateWorkTimeEndTime(1L, newEndTime);

            Assertions.assertThat(workTime)
                    .extracting("endTime")
                    .isNotNull()
                    .isEqualTo(newEndTime);

            Mockito.verify(workTimeRepository).findById(workTime.getId());
            Mockito.verify(workTimeRepository).save(workTime);
        }

        @Test
        @DisplayName("Update work time end time with end time before start time - should throw exception")
        public void updateWorkTimeEndTimeThatIsBeforeStartTime()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);
            workTime.setStartTime(LocalTime.now());
            workTime.setEndTime(LocalTime.now());
            LocalTime newEndTime = LocalTime.of(10, 0);

            Mockito.when(workTimeRepository.findById(workTime.getId()))
                    .thenReturn(Optional.of(workTime));
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            Assertions.assertThatThrownBy(() -> workTimeService.updateWorkTimeEndTime(1L, newEndTime))
                    .isInstanceOf(DateTimeException.class)
                    .hasMessage("Start time of a work time cannot be after its end time");


            Mockito.verify(workTimeRepository).findById(workTime.getId());
            Mockito.verify(workTimeRepository, Mockito.never()).save(workTime);
        }

        @Test
        @DisplayName("Update work time amount")
        public void updateWorkTimeAmount()
        {
            WorkTime workTime = new WorkTime();
            workTime.setId(1L);
            workTime.setAmount(10D);

            Mockito.when(workTimeRepository.findById(workTime.getId()))
                    .thenReturn(Optional.of(workTime));
            Mockito.when(workTimeRepository.save(workTime))
                    .thenReturn(workTime);

            workTimeService.updateWorkTimeAmount(1L, 15D);

            Assertions.assertThat(workTime)
                    .extracting("amount")
                    .isNotNull()
                    .isEqualTo(15D);

            Mockito.verify(workTimeRepository).findById(workTime.getId());
            Mockito.verify(workTimeRepository).save(workTime);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time get tests")
    class WorkTimeGetTests
    {
        @Test
        @DisplayName("Get work times by date")
        public void getWorkTimesByDate()
        {
            User user = new User(1L);
            LocalDate localDate = LocalDate.of(2021, 1, 2);

            Mockito.when(workTimeRepository.findAllByUserIdAndDate(user.getId(), localDate))
                    .thenReturn(List.of());
            mockedStatic.when(UserEvaluator::currentUserId)
                    .thenReturn(1L);

            workTimeService.getAllWorkTimesByDate(localDate);

            Mockito.verify(workTimeRepository).findAllByUserIdAndDate(user.getId(), localDate);
        }

        @Test
        @DisplayName("Get work times by user and date")
        public void getWorkTimesByUserAndDate()
        {
            User user = new User(1L);
            LocalDate localDate = LocalDate.of(2021, 1, 2);

            Mockito.when(workTimeRepository.findAllByUserIdAndDate(user.getId(), localDate))
                    .thenReturn(List.of());

            workTimeService.getAllWorkTimesByUserAndDate(user.getId(), localDate);

            Mockito.verify(workTimeRepository).findAllByUserIdAndDate(user.getId(), localDate);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work time delete tests")
    class WorkTimeDeleteTests
    {
        @Test
        @DisplayName("Delete work time note")
        public void updateWorkTimeNote()
        {
            Mockito.when(workTimeRepository.existsById(1L)).thenReturn(true);
            Mockito.doNothing().when(workTimeRepository).deleteById(1L);

            workTimeService.deleteWorkTime(1L);

            Mockito.verify(workTimeRepository).existsById(1L);
            Mockito.verify(workTimeRepository).deleteById(1L);
        }
    }
}
