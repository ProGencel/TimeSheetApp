package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TimeSheetRepository extends JpaRepository<TimeSheet,Long> {

   // @Query(value = "SELECT * FROM timesheets where user_id = ?1",nativeQuery = true)
    Page<TimeSheet> findByUserIdEquals(Long user_id, Pageable pageable);

    Page<TimeSheet> findByUserIdEqualsAndDateGreaterThanEqualAndDateLessThanEqual(Long userId, LocalDate date, LocalDate date1, Pageable pageable);


}
