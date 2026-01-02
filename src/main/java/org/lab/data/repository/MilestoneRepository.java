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
}
