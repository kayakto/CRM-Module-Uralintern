package org.bitebuilders.repository;

import org.bitebuilders.model.UserInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    // TODO пока не используется
    @Query("SELECT id, first_name, last_name, surname, telegram_url, vk_url " +
            "FROM users_info " +
            "WHERE id = :curatorId;")
    UserInfo findCuratorById(Long curatorId);

    @Query("SELECT id, first_name, last_name, surname, competencies " +
            "FROM users_info " +
            "WHERE id = :studentId;")
    UserInfo findStudentByIdForAdminOrCurator(Long studentId);

    @Query("SELECT id, first_name, last_name, surname " +
            "FROM users_info " +
            "WHERE id = :studentId;")
    UserInfo findStudentByIdForManager(Long studentId);

    @Query("SELECT id, first_name, last_name, surname, email, telegram_url, vk_url " +
            "FROM users_info " +
            "WHERE role_enum = 'MANAGER';")
    List<UserInfo> findAllManagers();
}

