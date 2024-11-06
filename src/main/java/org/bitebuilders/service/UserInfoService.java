package org.bitebuilders.service;

import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService {

    @Autowired
    private final UserInfoRepository userInfoRepository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public Optional<UserInfo> getUserInfo(Long id) {
        return userInfoRepository.findById(id);
    }
    // TODO сделать контроллер для получения данных пользователя
}
