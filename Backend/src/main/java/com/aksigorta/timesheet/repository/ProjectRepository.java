package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    Optional<Project> findByName(String name);

    Page<Project> findByNameContainsIgnoreCase(String name,
                                               Pageable pageable);

    @Query(value = """
        SELECT * FROM project p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
        AND (p.user_id = :userId OR :userId = ANY(p.member_ids))
        """,
            countQuery = """
        SELECT COUNT(*) FROM project p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
        AND (p.user_id = :userId OR :userId = ANY(p.member_ids))
        """,
            nativeQuery = true)
    Page<Project> findByNameContainsIgnoreCaseAndMember(@Param("q") String q, @Param("userId") Long userId, Pageable pageable);
}
