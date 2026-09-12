package com.oleksandr.financeplatform.service;

import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.exception.ResourceNotFoundException;
import com.oleksandr.financeplatform.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User"));
    }
}
