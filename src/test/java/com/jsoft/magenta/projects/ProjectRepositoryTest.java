package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.security.model.AccessPermission;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest
{
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Get all projects by association and permission level")
    public void getAllProjectsByAssociationAndPermissionLevel()
    {
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Page<Project> projectPage = new PageImpl<>(List.of(new Project(), new Project()), pageRequest, 2);

        Page<Project> result = this.projectRepository
                .findAllByAssociationsUserIdAndAssociationsPermission(1L, AccessPermission.WRITE, pageRequest);

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(projectPage)
                .extracting("totalElements")
                .isEqualTo("2");
    }
    
}
