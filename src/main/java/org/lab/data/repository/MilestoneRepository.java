package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.Milestone;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(Milestone.class)
public interface MilestoneRepository {

    @SqlQuery("""
            SELECT * FROM milestones WHERE milestone_id = :milestoneId
            """)
    Optional<Milestone> findById(long milestoneId);

    @SqlUpdate("""
            INSERT INTO milestones (project_id, start_time, end_time, status, name, description) VALUES
            (:milestone.projectId, :milestone.startTime, :milestone.endTime , :milestone.status, :milestone.name, :milestone.description)
            RETURNING milestone_id
            """)
    long createMilestone(@BindMethods("milestone") Milestone milestone);

    @SqlUpdate("""
            UPDATE milestones SET
            start_time = :milestone.startTime AND
            end_time = :milestone.startTime AND
            status = :milestone.status AND
            name = :milestone.name AND
            description :milestone.description
            active :milestone.active
            WHERE
            milestoneId = :milestone.milestoneId
            """)
    void updateMilestone(@BindMethods("milestone") Milestone milestone);


    @SqlQuery("""
            SELECT * FROM milestones WHERE project_id = :projectId AND status = 'ACTIVE'
            """)
    Optional<Milestone> findActiveByProject(@Bind("projectId") long projectId);

    @SqlQuery("""
            SELECT * FROM milestones WHERE project_id = :projectId
            """)
    List<Milestone> findAllByProject(@Bind("projectId") long projectId);

/*

    public Future<Milestone> findById(long milestoneId) {
        String sql = "SELECT * FROM milestones WHERE milestone_id = #{milestoneId}";
        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Milestone.class)
                .execute(Collections.singletonMap("milestoneId", milestoneId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.failedFuture("Milestone not found");
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                });
    }

    public Future<Long> createMilestone(Milestone milestone) {
        String sql = "INSERT INTO milestones (start_time, end_time, status, active) " +
                "VALUES (#{startTime}, #{endTime}, #{status}, #{active}) RETURNING milestone_id";

        Map<String, Object> params = Map.of(
                "startTime", milestone.startTime(),
                "endTime", milestone.endTime(),
                "status", milestone.status().name(),
                "active", milestone.active()
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture(rowSet.iterator().next().getLong("milestone_id")));
    }

    public Future<Void> updateMilestone(Milestone milestone) {
        String sql = "UPDATE milestones SET start_time = #{startTime}, end_time = #{endTime}, " +
                "status = #{status}, active = #{active} WHERE milestone_id = #{milestoneId}";

        Map<String, Object> params = Map.of(
                "milestoneId", milestone.milestoneId(),
                "startTime", milestone.startTime(),
                "endTime", milestone.endTime(),
                "status", milestone.status().name(),
                "active", milestone.active()
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Milestone> findActiveMilestoneByProject(long projectId) {
        String sql = "SELECT m.* FROM milestones m " +
                "JOIN project_milestones pm ON m.milestone_id = pm.milestone_id " +
                "WHERE pm.project_id = #{projectId} AND m.active = true";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Milestone.class)
                .execute(Collections.singletonMap("projectId", projectId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.failedFuture("Active milestone not found");
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                });
    }

    public Future<Void> setMilestoneStatus(long milestoneId, MilestoneStatus status) {
        String sql = "UPDATE milestones SET status = #{status} WHERE milestone_id = #{milestoneId}";

        return SqlTemplate.forQuery(pool, sql)
                .execute(Map.of("milestoneId", milestoneId, "status", status.name()))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Void> linkMilestoneToProject(long milestoneId, long projectId) {
        String sql = "INSERT INTO project_milestones (project_id, milestone_id) " +
                "VALUES (#{projectId}, #{milestoneId})";

        return SqlTemplate.forQuery(pool, sql)
                .execute(Map.of("projectId", projectId, "milestoneId", milestoneId))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<List<Milestone>> findMilestonesByProject(long projectId) {
        String sql = "SELECT m.* FROM milestones m " +
                "JOIN project_milestones pm ON m.milestone_id = pm.milestone_id " +
                "WHERE pm.project_id = #{projectId}";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(Milestone.class)
                .execute(Collections.singletonMap("projectId", projectId))
                .compose(rowSet -> {
                    List<Milestone> milestones = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(milestones);
                });
    }

 */
}
