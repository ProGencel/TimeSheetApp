package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.timeSheet.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSheetRepository extends JpaRepository<TimeSheet,Long> {

}
