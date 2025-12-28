package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.ProjectUser;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ProjectUser.class)
public interface ProjectUserRepository {

    @SqlUpdate("""
            INSERT INTO project_users (project_id, user_id, project_role, active)
            VALUES (:user.projectId, :user.userId, :user.projectRole, :user.active)
            ON CONFLICT (project_id, user_id) DO UPDATE SET
            project_role = :user.projectRole, active = :user.active
            """)
    void addUserToProject(@BindMethods("user") ProjectUser user);

    @SqlQuery("""
            SELECT * from project_users where user_id = :userId
            """)
    List<ProjectUser> getProjectUsersByUser(@Bind("userId") long userId);

    @SqlQuery("""
            SELECT * from project_users where project_id = :projectId
            """)
    List<ProjectUser> getProjectUsersByProject(@Bind("projectId") long projectId);

    @SqlQuery("""
            SELECT * from project_users where project_id = :projectId AND user_id = :userId
            """)
    Optional<ProjectUser> findProjectUser(@Bind("projectId") long projectId, @Bind("userId") long userId);

    @SqlUpdate("""
            UPDATE project_users SET project_role = 'DEVELOPER'
            WHERE project_role = 'TEAM_LEADER' and project_id = :projectId
            """)
    void removeTeamLead(@Bind("projectId") long projectId);

    @SqlUpdate("""
            DELETE FROM project_users WHERE
            project_id = :projectId AND user_id = :userId
            """)
    void remove(@Bind("projectId") long projectId, @BindMethods("userId") long userId);

/*

    public Future<Void> addUserToProject(ProjectUser projectUser) {
        String sql = "INSERT INTO project_users (project_id, user_id, project_role, active) " +
                "VALUES (#{projectId}, #{userId}, #{projectRole}, #{active}) " +
                "ON CONFLICT (project_id, user_id) DO UPDATE SET " +
                "project_role = #{projectRole}, active = #{active}";

        Map<String, Object> params = Map.of(
                "projectId", projectUser.projectId(),
                "userId", projectUser.userId(),
                "projectRole", projectUser.projectRole().name(),
                "active", projectUser.active()
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<ProjectUser> findProjectUser(long projectId, long userId) {
        String sql = "SELECT * FROM project_users WHERE project_id = #{projectId} AND user_id = #{userId}";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(ProjectUser.class)
                .execute(Map.of("projectId", projectId, "userId", userId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.failedFuture("Project user not found");
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                });
    }

    public Future<List<ProjectUser>> findUsersByProject(long projectId) {
        String sql = "SELECT * FROM project_users WHERE project_id = #{projectId} AND active = true";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(ProjectUser.class)
                .execute(Collections.singletonMap("projectId", projectId))
                .compose(rowSet -> {
                    List<ProjectUser> users = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(users);
                });
    }

    public Future<List<ProjectUser>> findProjectsByUser(long userId) {
        String sql = "SELECT * FROM project_users WHERE user_id = #{userId} AND active = true";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(ProjectUser.class)
                .execute(Collections.singletonMap("userId", userId))
                .compose(rowSet -> {
                    List<ProjectUser> projects = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(projects);
                });
    }

    public Future<Void> removeUserFromProject(long projectId, long userId) {
        String sql = "UPDATE project_users SET active = false WHERE project_id = #{projectId} AND user_id = #{userId}";

        return SqlTemplate.forQuery(pool, sql)
                .execute(Map.of("projectId", projectId, "userId", userId))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Void> updateUserRole(long projectId, long userId, ProjectRole role) {
        String sql = "UPDATE project_users SET project_role = #{role} " +
                "WHERE project_id = #{projectId} AND user_id = #{userId}";

        return SqlTemplate.forQuery(pool, sql)
                .execute(Map.of("projectId", projectId, "userId", userId, "role", role.name()))
                .compose(rowSet -> Future.succeededFuture());
    }

 */
}