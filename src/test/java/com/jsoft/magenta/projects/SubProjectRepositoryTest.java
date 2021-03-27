package com.jsoft.magenta.projects;

import com.jsoft.magenta.subprojects.SubProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubProjectRepositoryTest {

  @Autowired
  private SubProjectRepository subProjectRepository;
}
