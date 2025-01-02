package org.bitebuilders.controller;

import org.bitebuilders.component.UserContext;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.controller.requests.UserUpdateRequest;

import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInfoController {

    @Autowired
    private final UserInfoService userInfoService;

    @Autowired
    private final UserContext userContext;

    public UserInfoController(UserInfoService userInfoService, UserContext userContext) {
        this.userInfoService = userInfoService;
        this.userContext = userContext;
    }

    /**
     * Обновление данных текущего пользователя
     * @param updateRequest данные пользователя с учетом изменений
     * @return Обновленные данные пользователя
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequest updateRequest) {
        String email = userContext.getCurrentUserEmail();

        UserInfo user = userInfoService.getByEmail(email);

        // Обновление данных пользователя
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setSurname(updateRequest.getSurname());
        user.setCompetencies(updateRequest.getCompetencies());
        user.setTelegramUrl(updateRequest.getTelegramUrl());
        user.setVkUrl(updateRequest.getVkUrl());

        UserInfo updatedUser = userInfoService.addOrUpdateUser(user);

        return ResponseEntity.ok(updatedUser.toUserDTO());
    }

    /**
     * Получение данных текущего пользователя
     * @return Данные пользователя
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String email = userContext.getCurrentUserEmail();

        UserInfo user = userInfoService.getByEmail(email);

        return ResponseEntity.ok(user.toUserDTO());
    }

    /**
     * Получение всех руководителей, зарегистрированных в системе
     * Доступен только администраторам
     * @return Список всех руководителей
     */
    @GetMapping("/all-managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllManagers() {
        List<UserInfo> foundedManagers = userInfoService.getAllManagers();

        if (foundedManagers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<UserDTO> result = foundedManagers
                .stream()
                .map(UserInfo::toUserDTO)
                .toList();

        return ResponseEntity.ok(result);
    }
}
