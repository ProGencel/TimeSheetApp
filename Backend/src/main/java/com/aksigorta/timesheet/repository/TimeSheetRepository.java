package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TimeSheetRepository extends JpaRepository<TimeSheet,Long> {

   // @Query(value = "SELECT * FROM timesheets where user_id = ?1",nativeQuery = true)
    Page<TimeSheet> findByUserIdEquals(Long user_id, Pageable pageable);

    Page<TimeSheet> findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(Long userId, LocalDate date, LocalDate date1, Pageable pageable);

    @Query("SELECT t FROM timesheets t WHERE t.user.id = :userId " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate)")
    List<TimeSheet> findAllForExport(@Param("userId")Long userId,
                                     @Param("startDate") LocalDate date,
                                     @Param("endDate") LocalDate date1,
                                     Sort sort);

    Page<TimeSheet> findByUser_IdEqualsAndDateEquals(Long id, LocalDate date, Pageable pageable);

 Page<TimeSheet> findByUser_IdEquals(Long id, Pageable pageable);

}
