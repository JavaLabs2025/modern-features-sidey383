package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.User;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(User.class)
public interface UserRepository {

    @SqlQuery("SELECT * FROM users WHERE user_id = :userId AND active = true")
    Optional<User> findById(@Bind("userId") long userId);

    @SqlQuery("SELECT * FROM users WHERE username = :username AND active = true")
    Optional<User> findByUsername(@Bind("username") String username);

    @SqlUpdate("""
            INSERT INTO users (username, password, user_type, active)
            VALUES (:user.username, :user.password, :user.userType, :user.active) RETURNING user_id
            """)
    Long createUser(@BindMethods("user") User user);

    @SqlUpdate("""
            UPDATE users SET username = :user.username, password = :user.password,
            user_type = :user.userType, active = :user.active WHERE user_id = :user.userId
            """)
    void updateUser(@BindMethods("user") User user);

    @SqlQuery("""
            SELECT * FROM users WHERE active = true
            """)
    List<User> findAllActiveUsers();

}
