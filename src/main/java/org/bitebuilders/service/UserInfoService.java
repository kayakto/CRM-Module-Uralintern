package org.bitebuilders.service;

import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventCuratorRepository;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.EventStudentRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository, EventRepository eventRepository, EventStudentRepository eventStudentRepository, EventCuratorRepository eventCuratorRepository) {
        this.userInfoRepository = userInfoRepository;
        this.eventRepository = eventRepository;
        this.eventStudentRepository = eventStudentRepository;
        this.eventCuratorRepository = eventCuratorRepository;
    }

    public Optional<UserInfo> getUserInfo(Long id) {
        return userInfoRepository.findById(id);
    }
    // TODO сделать контроллер для получения данных пользователя

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
        return userInfoRepository.save(user);
    }

    public List<UserInfo> getAllManagers() {
        return userInfoRepository.findAllManagers();
    }

    public List<Event> getMyEvents(Long userId) {
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new EventUserNotFoundException("User not found with ID: " + userId));

        switch (user.getRole_enum()) {
            case ADMIN -> {
                return eventRepository.findAllByAdminId(userId);
            }
            case MANAGER -> {
                return eventRepository.findAllByManagerId(userId);
            }
            case CURATOR -> {
                return eventCuratorRepository.findAcceptedEventsByCurator(userId);
            }
            case STUDENT -> {
                return eventStudentRepository.findAcceptedEventsByStudent(userId);
            }
        }

        return Collections.emptyList();
    }
}
