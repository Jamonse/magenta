package com.jsoft.magenta.security.dao;

import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.model.PrivilegesGroupSearchResult;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegesGroupRepository extends JpaRepository<PrivilegesGroup, Long> {

  boolean existsByName(String name);

  List<PrivilegesGroupSearchResult> findAllResultsBy(PageRequest pageRequest);
}
