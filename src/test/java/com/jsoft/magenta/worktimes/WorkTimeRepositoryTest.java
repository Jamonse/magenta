package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.users.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WorkTimeRepositoryTest
{
    @Autowired
    private WorkTimeRepository workTimeRepository;

    @Test
    @DisplayName("Create work time")
    public void createWorkTime()
    {
        WorkTime workTime = new WorkTime();
        workTime.setDate(LocalDate.now());
        workTime.setNote("");
        workTime.setAmount(10D);

        WorkTime workTime1 = workTimeRepository.getOne(1L);
        System.out.println(workTime1.getId());

        workTime.setUser(new User(1L));
        workTime.setSubProject(new SubProject(1L));

        WorkTime saved = workTimeRepository.save(workTime);
        System.out.println(saved.getId());
    }
}
