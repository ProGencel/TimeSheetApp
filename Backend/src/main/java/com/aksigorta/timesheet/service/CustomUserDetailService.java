package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.UserRepository;
import com.aksigorta.timesheet.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new CustomUserDetails(user);

    }
}
