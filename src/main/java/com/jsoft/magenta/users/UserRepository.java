package com.jsoft.magenta.users;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  Optional<User> findByEmail(String email);

  @Query("select u.supervisedUsers from User u where u.id = :supervisorId")
  Page<User> findSupervisedUsersBySupervisorId(Long supervisorId, Pageable pageable);

  @Query("select u.supervisedUsers from User u where u.id = :supervisorId")
  List<UserSearchResult> findSupervisedUsersResultsBySupervisorId(Long supervisorId,
      Pageable pageable);

  List<UserSearchResult> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
      String firstName, String lastName, String email, Pageable pageable);

  default List<UserSearchResult> findAllByNameExample(String nameExample, PageRequest pageRequest) {
    return this
        .findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            nameExample, nameExample, nameExample, pageRequest);
  }

  List<UserSearchResult> findAllResultsBy();
}
