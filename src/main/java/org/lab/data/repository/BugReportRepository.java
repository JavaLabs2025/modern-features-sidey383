package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.BugReport;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(BugReport.class)
public interface BugReportRepository {

    @SqlQuery("""
            SELECT * FROM bug_reports WHERE bug_id = :bugId
            """)
    Optional<BugReport> findById(@Bind("bugId") long bugId);

    @SqlQuery("""
            SELECT * FROM bug_reports WHERE project_id = :projectId
            """)
    List<BugReport> findByProjectId(@Bind("projectId") long projectId);

    @SqlUpdate("""
            INSERT INTO bug_reports (project_id, name, description, status)
            VALUES (:bugReport.projectId, :bugReport.name, :bugReport.description, :bugReport.status)
            RETURNING bug_id
            """)
    long createBugReport(@BindMethods("bugReport") BugReport bugReport);

    @SqlUpdate("""
            UPDATE bug_reports SET
            project_id = :bugReport.projectId AND
            name = :bugReport.name AND
            description = :bugReport.description AND
            status = :bugReport.status
            updated_at = NOW()
            WHERE bug_id = :bugReport.bugId
            """)
    void updateBugReport(@BindMethods("bugReport") BugReport bugReport);
}
