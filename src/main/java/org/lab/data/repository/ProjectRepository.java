package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.lab.data.entity.Project;

@RegisterConstructorMapper(Project.class)
public interface ProjectRepository {

    @SqlQuery("""
            SELECT * FROM projects WHERE project_id = :projectId
            """)
    Project findById(long projectId);

/*

    public Future<Project> findById(long projectId) {
        String sql = "SELECT * FROM projects WHERE project_id = #{projectId}";
        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Project.class)
                .execute(Collections.singletonMap("projectId", projectId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.failedFuture("Project not found");
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                });
    }

    public Future<Long> createProject(Project project) {
        String sql = "INSERT INTO projects (name, project_manager_id, team_lead_id) " +
                "VALUES (#{name}, #{projectManagerId}, #{teamLeadId}) RETURNING project_id";

        Map<String, Object> params = Map.of(
                "name", project.name(),
                "projectManagerId", project.projectManagerId(),
                "teamLeadId", project.getTeamLead().orElse(null)
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture(rowSet.iterator().next().getLong("project_id")));
    }

    public Future<Void> updateProject(Project project) {
        String sql = "UPDATE projects SET name = #{name}, project_manager_id = #{projectManagerId}, " +
                "team_lead_id = #{teamLeadId} WHERE project_id = #{projectId}";

        Map<String, Object> params = Map.of(
                "projectId", project.projectId(),
                "name", project.name(),
                "projectManagerId", project.projectManagerId(),
                "teamLeadId", project.getTeamLead().orElse(null)
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<List<Project>> findProjectsByUserId(long userId) {
        String sql = "SELECT p.* FROM projects p " +
                "JOIN project_users pu ON p.project_id = pu.project_id " +
                "WHERE pu.user_id = #{userId} AND pu.active = true";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Project.class)
                .execute(Collections.singletonMap("userId", userId))
                .compose(rowSet -> {
                    List<Project> projects = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(projects);
                });
    }

    public Future<List<Project>> findAllProjects() {
        String sql = "SELECT * FROM projects";
        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Project.class)
                .execute(Collections.emptyMap())
                .compose(rowSet -> {
                    List<Project> projects = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(projects);
                });
    }

 */
}
