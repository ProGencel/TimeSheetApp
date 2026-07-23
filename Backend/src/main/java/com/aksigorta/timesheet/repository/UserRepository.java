package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM users WHERE email = ?1 OR username = ?2",nativeQuery = true)
    Optional<User> findByMailOrUsername(String email, String username);

}
