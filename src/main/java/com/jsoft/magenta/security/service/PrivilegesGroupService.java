package com.jsoft.magenta.security.service;

import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.dao.PrivilegesGroupRepository;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.model.PrivilegesGroup;
import com.jsoft.magenta.security.model.PrivilegesGroupSearchResult;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.pagination.PageRequestBuilder;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivilegesGroupService {

  private final PrivilegesGroupRepository privilegesGroupRepository;

  public PrivilegesGroup createPrivilegesGroup(PrivilegesGroup privilegesGroup) {
    verifyUniqueName(privilegesGroup.getName());
    return this.privilegesGroupRepository.save(privilegesGroup);
  }

  public PrivilegesGroup createPrivilegesGroupInheritFrom(PrivilegesGroup newGroup,
      Long superGroupId) {
    verifyUniqueName(newGroup.getName());
    PrivilegesGroup privilegesGroup = findGroup(superGroupId);
    Set<Privilege> privileges = privilegesGroup.getPrivileges();
    newGroup.addAll(privileges);
    return this.privilegesGroupRepository.save(newGroup);
  }

  public PrivilegesGroup updatePrivilegesGroup(PrivilegesGroup privilegesGroup) {
    PrivilegesGroup groupToUpdate = findGroup(privilegesGroup.getId());
    if (!privilegesGroup.getName().equalsIgnoreCase(groupToUpdate.getName())) {
      verifyUniqueName(privilegesGroup.getName());
    }
    groupToUpdate.setName(privilegesGroup.getName());
    groupToUpdate.setPrivileges(privilegesGroup.getPrivileges());
    return this.privilegesGroupRepository.save(groupToUpdate);
  }

  public PrivilegesGroup updateGroupName(Long groupId, String newName) {
    PrivilegesGroup privilegesGroup = findGroup(groupId);
    verifyUniqueName(newName);
    privilegesGroup.setName(newName);
    return this.privilegesGroupRepository.save(privilegesGroup);
  }

  public PrivilegesGroup getPrivilegesGroup(Long groupId) {
    return findGroup(groupId);
  }

  public Page<PrivilegesGroup> getAllPrivilegesGroups(int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Page<PrivilegesGroup> results = this.privilegesGroupRepository.findAll(pageRequest);
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  public List<PrivilegesGroupSearchResult> getAllPrivilegesGroupsResults(int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.PRIVILEGES_GROUP_DEFAULT_SORT, false);
    return this.privilegesGroupRepository.findAllResultsBy(pageRequest);
  }

  public void deletePrivilegesGroup(Long groupId) {
    findGroup(groupId);
    this.privilegesGroupRepository.deleteById(groupId);
  }

  private PrivilegesGroup findGroup(Long groupId) {
    return this.privilegesGroupRepository
        .findById(groupId)
        .orElseThrow(() -> new NoSuchElementException("Group not found"));
  }

  private void verifyUniqueName(String name) {
    boolean exist = this.privilegesGroupRepository
        .existsByName(name);
    if (exist) {
      throw new DuplicationException("Privileges group with same name already exists");
    }
  }

}
