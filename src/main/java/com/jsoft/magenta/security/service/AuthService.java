package com.jsoft.magenta.security.service;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public boolean authenticate(String passwordToMatch)
    {
        User user = UserEvaluator.currentUser();
        String userPassword = user.getPassword();
        return passwordEncoder.matches(passwordToMatch, userPassword);
    }

    public User updatePassword(Long userId, String newPassword)
    {
        User user = findUser(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.userRepository.save(user);
    }

    private User findUser(Long userId)
    {
        return this.userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
