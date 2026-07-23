package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.model.UserLoginDto;
import com.aksigorta.timesheet.model.UserRegisterDto;
import com.aksigorta.timesheet.repository.UserRepository;
import com.aksigorta.timesheet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto userRegisterDto)
    {
        return userService.register(userRegisterDto);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto)
    {
        return userService.login(userLoginDto);
    }

}
