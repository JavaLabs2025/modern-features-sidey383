package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.User;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserRepository {

    @SqlQuery("SELECT * FROM users WHERE user_id = :userId AND active = true")
    Optional<User> findById(long userId);

    @SqlQuery("SELECT * FROM users WHERE username = :username AND active = true")
    User findByUsername(String username);

    @SqlUpdate("""
            INSERT INTO users (username, password, user_type, active)
            VALUES (:user.username, :user.password, :user.userType, :user.active) RETURNING user_id
            """)
    Long createUser(User user);

    @SqlUpdate("""
            UPDATE users SET username = :user.username, password = :user.password,
            user_type = :user.userType, active = :user.active WHERE user_id = :user.userId
            """)
    void updateUser(User user);

    @SqlQuery("""
            SELECT * FROM users WHERE active = true
            """)
    List<User> findAllActiveUsers();

}
