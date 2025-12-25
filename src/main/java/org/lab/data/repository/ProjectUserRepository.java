package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.lab.data.entity.ProjectUser;

@RegisterBeanMapper(ProjectUser.class)
public interface ProjectUserRepository {

    @SqlQuery("""
            INSERT INTO project_users (project_id, user_id, project_role, active)
            VALUES (:user.projectId, :user.userId, :user.projectRole, :user.active)
            ON CONFLICT (project_id, user_id) DO UPDATE SET " +
            project_role = :user.projectRole, active = :user.active
            """)
    void addUserToProject(ProjectUser user);

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