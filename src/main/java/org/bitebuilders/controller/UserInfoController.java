package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.ManagerInfoDTO;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInfoController {

    @Autowired
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/all-managers")
    public ResponseEntity<List<ManagerInfoDTO>> getAllManagers() {
        List<UserInfo> foundedManagers = userInfoService.getAllManagers();

        if (foundedManagers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ManagerInfoDTO> result = foundedManagers
                .stream()
                .map(UserInfo::toManagerInfoDTO)
                .toList();

        return ResponseEntity.ok(result);
    }
}
