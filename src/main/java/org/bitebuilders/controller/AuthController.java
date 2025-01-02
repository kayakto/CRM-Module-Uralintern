package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.controller.requests.LoginRequest;
import org.bitebuilders.controller.requests.UserRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.InvitationToken;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.InvitationTokenService;
import org.bitebuilders.service.JwtService;
import org.bitebuilders.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final UserInfoService userInfoService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final InvitationTokenService tokenService;

    public AuthController(UserInfoService userInfoService, PasswordEncoder passwordEncoder, JwtService jwtService, InvitationTokenService tokenService) {
        this.userInfoService = userInfoService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    // Регистрация без токена
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRequest userRequest) {
        // Проверка обязательных полей
        UserRole requestRole = userRequest.getRole();
        if (!(requestRole == UserRole.STUDENT || requestRole == UserRole.CURATOR)) {
            return ResponseEntity.badRequest().build();
        }

        if (userRequest.getCompetencies() == null || userRequest.getCompetencies().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Проверка существующего пользователя
        if (userInfoService.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Создание пользователя
        UserInfo userInfo = userRequest.toUserInfo(
                requestRole,
                passwordEncoder.encode(userRequest.getSign())
        );

        UserInfo savedUser = userInfoService.addOrUpdateUser(userInfo);

        // Генерация URI для созданного пользователя
        URI location = URI.create("/users/" + savedUser.getId());

        return ResponseEntity.created(location).body(savedUser.toUserDTO());
    }

    // Регистрация с токеном
    @PostMapping("/register-with-token")
    public ResponseEntity<UserDTO> registerWithToken(@RequestParam String token,
                                                     @RequestBody UserRequest userRequest) {
        Optional<InvitationToken> optionalToken = tokenService.validateToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        InvitationToken invitationToken = optionalToken.get();

        // Проверка существующего пользователя
        if (userInfoService.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Проверка на наличие запрещенных полей
        if (userRequest.getRole() != null || userRequest.getCompetencies() != null) {
            return ResponseEntity.badRequest().build();
        }

        // Создание пользователя
        UserInfo userInfo = userRequest.toUserInfo(
                invitationToken.getRole(),
                passwordEncoder.encode(userRequest.getSign())
        );

        UserInfo savedUser = userInfoService.addOrUpdateUser(userInfo);

        // Отметка токена как использованного
        tokenService.markTokenAsUsed(invitationToken);

        // Генерация URI для созданного пользователя
        URI location = URI.create("/users/" + savedUser.getId());

        return ResponseEntity.created(location).body(savedUser.toUserDTO());
    }

//    @Operation(summary = "Login", description = "Authenticate user and retrieve tokens", security = {})
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        UserInfo user = userInfoService.getByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getSign())) {
            throw new BadCredentialsException("Invalid password");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole_enum().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole_enum().name());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtService.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtService.getUsernameFromToken(refreshToken);
        String role = jwtService.getRoleFromToken(refreshToken);
        String newAccessToken = jwtService.generateToken(username, role);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken); // Возвращаем refresh токен как есть

        return ResponseEntity.ok(tokens);
    }
}

