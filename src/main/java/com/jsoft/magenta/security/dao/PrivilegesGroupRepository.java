package com.jsoft.magenta.security.dao;

import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.model.PrivilegesGroupSearchResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivilegesGroupRepository extends JpaRepository<PrivilegesGroup, Long>
{
    boolean existsByName(String name);

    List<PrivilegesGroupSearchResult> findAllResultsBy(PageRequest pageRequest);
}
