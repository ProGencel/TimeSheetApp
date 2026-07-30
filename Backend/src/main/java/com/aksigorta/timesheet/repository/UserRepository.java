package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.user.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM users WHERE email = ?1 OR username = ?2",nativeQuery = true)
    Optional<User> findByMailOrUsername(String email, String username);

    @Query(value = "SELECT * FROM users WHERE username = ?1",nativeQuery = true)
    Optional<User> findByMail(String email);

    @NullMarked
    @Query(value = "SELECT * FROM users WHERE id = ?1",nativeQuery = true)
    Optional<User> findById(Long id);

    Page<User> findByUsernameContainsIgnoreCaseOrEmailEquals(String username, String email, Pageable pageable);

    List<User> findByUsernameContainsIgnoreCaseOrEmailEquals(String username, String email, Sort sort);


}
