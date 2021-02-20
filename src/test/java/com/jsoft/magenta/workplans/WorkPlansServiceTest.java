package com.jsoft.magenta.workplans;

import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WorkPlansServiceTest
{
    @InjectMocks
    private WorkPlanService workPlanService;

    @Mock
    private WorkPlanRepository workPlanRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    private void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    private static MockedStatic mockedStatic;

    @BeforeAll
    private static void initStatic()
    {
        mockedStatic = Mockito.mockStatic(UserEvaluator.class);
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work plans creation tests")
    class WorkPlansCreationTests
    {
        @Test
        @DisplayName("Create work plan")
        public void createWorkPlan()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));
            User user = new User(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.USER_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));
            User supervised = new User(1L);
            user.setSupervisedUsers(Set.of(supervised));

            mockedStatic.when(UserEvaluator::currentUser)
                    .thenReturn(user);
            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            workPlanService.createWorkPlan(1L, workPlan);

            Mockito.verify(workPlanRepository).save(workPlan);
        }

        @Test
        @DisplayName("Create work plan with start date after end date - should throw exception")
        public void createWorkPlanWithStartDateAfterEndDate()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now().plusHours(1));
            workPlan.setEndDate(LocalDateTime.now());
            User user = new User(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.USER_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));
            User supervised = new User(1L);
            user.setSupervisedUsers(Set.of(supervised));

            mockedStatic.when(UserEvaluator::currentUser)
                    .thenReturn(user);

            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            Assertions.assertThatThrownBy(() -> workPlanService.createWorkPlan(1L, workPlan))
                    .isInstanceOf(DateTimeException.class);

            Mockito.verify(workPlanRepository, Mockito.never()).save(workPlan);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work plans update tests")
    class WorkPlansUpdateTests
    {
        @Test
        @DisplayName("Update work plan")
        public void updateWorkPlan()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setId(1L);
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanRepository.findById(workPlan.getId()))
                    .thenReturn(Optional.of(workPlan));
            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            workPlanService.updateWorkPlan(workPlan);

            Mockito.verify(workPlanRepository).save(workPlan);
        }

        @Test
        @DisplayName("Update work plan with start date after end date - should throw exception")
        public void updateWorkPlanWithStartDateAfterEndDate()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setId(1L);
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now().plusHours(1));
            workPlan.setEndDate(LocalDateTime.now());

            Mockito.when(workPlanRepository.findById(workPlan.getId()))
                    .thenReturn(Optional.of(workPlan));
            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            Assertions.assertThatThrownBy(() -> workPlanService.updateWorkPlan(workPlan))
                    .isInstanceOf(DateTimeException.class);

            Mockito.verify(workPlanRepository, Mockito.never()).save(workPlan);
        }

        @Test
        @DisplayName("Update work plan start date")
        public void updateWorkPlanStartDate()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanRepository.findById(1L))
                    .thenReturn(Optional.of(workPlan));
            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            workPlanService.updateWorkPlanStartDate(1L, LocalDateTime.now());

            Mockito.verify(workPlanRepository).save(workPlan);
        }

        @Test
        @DisplayName("Update work plan end date")
        public void updateWorkPlanEndDate()
        {
            WorkPlan workPlan = new WorkPlan();
            workPlan.setTitle("title");
            workPlan.setStartDate(LocalDateTime.now());
            workPlan.setEndDate(LocalDateTime.now().plusHours(1));

            Mockito.when(workPlanRepository.findById(1L))
                    .thenReturn(Optional.of(workPlan));
            Mockito.when(workPlanRepository.save(workPlan))
                    .thenReturn(workPlan);

            workPlanService.updateWorkPlanEndDate(1L, LocalDateTime.now().plusHours(1));

            Mockito.verify(workPlanRepository).save(workPlan);
        }
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Work plans get tests")
    class WorkPlansGetTests
    {
        @Test
        @DisplayName("Get work plans of user")
        public void getWorkPlansOfUser()
        {
            Sort sort = Sort.by("title").descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Mockito.when(workPlanRepository.findAllByUserId(1L, pageRequest))
                    .thenReturn(List.of());

            workPlanService.getAllWorkPlansByUserId(1L, 0, 5, "title", false);

            Mockito.verify(workPlanRepository).findAllByUserId(1L, pageRequest);
        }
    }

    @Test
    @DisplayName("Delete work plan")
    public void deleteWorkPlan()
    {
        Mockito.doNothing().when(workPlanRepository).deleteById(1L);

        workPlanService.deleteWorkPlan(1L);

        Mockito.verify(workPlanRepository).deleteById(1L);
    }
}
