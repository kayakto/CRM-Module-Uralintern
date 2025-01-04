package org.bitebuilders.controller;

import org.bitebuilders.component.UserContext;
import org.bitebuilders.controller.dto.ReferralTokenDTO;
import org.bitebuilders.controller.dto.TokensDTO;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.controller.requests.EmailUpdateRequest;
import org.bitebuilders.controller.requests.PasswordUpdateRequest;
import org.bitebuilders.controller.requests.UserUpdateRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.InvitationTokenService;
import org.bitebuilders.service.JwtService;
import org.bitebuilders.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final UserContext userContext;

    private final InvitationTokenService invitationService;

    private final JwtService jwtService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService, UserContext userContext, InvitationTokenService invitationService, JwtService jwtService) {
        this.userInfoService = userInfoService;
        this.userContext = userContext;
        this.invitationService = invitationService;
        this.jwtService = jwtService;
    }

    /**
     * Получение данных текущего пользователя
     * @return Данные пользователя
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserInfo user = userContext.getCurrentUser();

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

    /**
     * Обновление данных текущего пользователя
     * @param updateRequest данные пользователя с учетом изменений
     * @return Обновленные данные пользователя
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequest updateRequest) {
        UserInfo user = userContext.getCurrentUser();
//        List<UserRole> cantSetCompetencies = new ArrayList<>() TODO student can`t set null to competencies

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

    @PostMapping("/update-password")
    public ResponseEntity<TokensDTO> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        String email = userContext.getCurrentUserEmail();
        UserInfo user = userInfoService.getByEmail(email);

        try {
            userInfoService.updatePassword(
                    user,
                    passwordUpdateRequest.getOldPassword(),
                    passwordUpdateRequest.getNewPassword());

            String newAccessToken = jwtService.generateToken(
                    email, user.getRole_enum().name());
            String newRefreshToken = jwtService.generateRefreshToken(
                    email, user.getRole_enum().name());

            return ResponseEntity.ok(
                    new TokensDTO(newAccessToken, newRefreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update-email")
    public ResponseEntity<TokensDTO> updateEmail(@RequestBody EmailUpdateRequest emailUpdateRequest) {
        String currentEmail = userContext.getCurrentUserEmail();
        UserInfo user = userInfoService.getByEmail(currentEmail);

        try {
            userInfoService.updateEmail(user, emailUpdateRequest.getNewEmail());
            String newAccessToken = jwtService.generateToken(
                    emailUpdateRequest.getNewEmail(), user.getRole_enum().name());
            String newRefreshToken = jwtService.generateRefreshToken(
                    emailUpdateRequest.getNewEmail(), user.getRole_enum().name());

            // Возвращаем новые токены
            return ResponseEntity.ok(new TokensDTO(newAccessToken, newRefreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invite-manager")
    public ResponseEntity<ReferralTokenDTO> createReferralToken() {
        String email = userContext.getCurrentUserEmail();

        Long authorId = userInfoService.getByEmail(email).getId();

        String token = invitationService.generateToken(
                UserRole.MANAGER,
                authorId
        );

        return ResponseEntity.ok(new ReferralTokenDTO(token));
    }
}
