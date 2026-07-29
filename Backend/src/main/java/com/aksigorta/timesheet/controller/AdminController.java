package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.user.UserResponseDto;
import com.aksigorta.timesheet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("search_user")
    public Page<UserResponseDto> searchUser(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "") String q)
    {
        return adminService.searchUser(page,q);
    }

}
