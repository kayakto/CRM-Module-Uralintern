package org.bitebuilders.service;

import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.UserNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventCuratorRepository;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.EventStudentRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    private final EventRepository eventRepository;

    private final EventStudentRepository eventStudentRepository;

    private final EventCuratorRepository eventCuratorRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository, EventRepository eventRepository, EventStudentRepository eventStudentRepository, EventCuratorRepository eventCuratorRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.eventRepository = eventRepository;
        this.eventStudentRepository = eventStudentRepository;
        this.eventCuratorRepository = eventCuratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserInfo> getUserInfo(Long id) {
        return userInfoRepository.findById(id);
    }

    public String getFullNameById(Long userId) {
        if (userId == null) return "Unknown User";

        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id " + userId + " not found"));

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String surname = user.getSurname();

        if (surname == null) {
            return lastName + " " + firstName;
        }

        return lastName + " " + firstName + " " + surname;
    } // на будущее

    public Boolean existsByEmail(String email) {
        return userInfoRepository.findByEmail(email).isPresent();
    }

    public UserInfo getByEmail(String email) {
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UsernameNotFoundException("User with email " + email + " not found");
    }

    public UserInfo addOrUpdateUser(UserInfo user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return userInfoRepository.save(user);
    }

    public List<UserInfo> getAllManagers() {
        return userInfoRepository.findAllManagers();
    }



    public UserInfo updatePassword(UserInfo user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getSign())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setSign(passwordEncoder.encode(newPassword));
        return userInfoRepository.save(user);
    }

    public UserInfo updateEmail(UserInfo user, String newEmail) {
        if (newEmail == null || newEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setEmail(newEmail);
        return userInfoRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    public boolean isManager(Long managerId) {
        UserInfo user = userInfoRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager with id " + managerId + " not found"));
        return user.getRole_enum() == UserRole.MANAGER;
    }

    public boolean isAdmin(Long adminId) {
        UserInfo user = userInfoRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Admin with id " + adminId + " not found"));
        return user.getRole_enum() == UserRole.ADMIN;
    }
}
