package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.lab.data.entity.BugReport;

@RegisterConstructorMapper(BugReport.class)
public interface BugReportRepository {

    @SqlQuery("""
            SELECT * FROM bug_reports WHERE bug_id = :bugId
            """)
    BugReport findById(long bugId);
/*
    public Future<BugReport> findById(long bugId) {
        String sql = "SELECT * FROM bug_reports WHERE bug_id = #{bugId}";
        return SqlTemplate.forQuery(pool, sql)
                .mapTo(BugReport.class)
                .execute(Collections.singletonMap("bugId", bugId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.failedFuture("Bug report not found");
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                });
    }

    public Future<Long> createBugReport(BugReport bugReport) {
        String sql = "INSERT INTO bug_reports (project_id, name, description, status) " +
                "VALUES (#{projectId}, #{name}, #{description}, #{status}) RETURNING bug_id";

        Map<String, Object> params = Map.of(
                "projectId", bugReport.projectId(),
                "name", bugReport.name(),
                "description", bugReport.description(),
                "status", bugReport.status().name()
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture(rowSet.iterator().next().getLong("bug_id")));
    }

    public Future<Void> updateBugReport(BugReport bugReport) {
        String sql = "UPDATE bug_reports SET project_id = #{projectId}, name = #{name}, " +
                "description = #{description}, status = #{status} WHERE bug_id = #{bugId}";

        Map<String, Object> params = Map.of(
                "bugId", bugReport.id(),
                "projectId", bugReport.projectId(),
                "name", bugReport.name(),
                "description", bugReport.description(),
                "status", bugReport.status().name()
        );

        return SqlTemplate.forQuery(pool, sql)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Void> updateBugStatus(long bugId, BugReportStatus status) {
        String sql = "UPDATE bug_reports SET status = #{status} WHERE bug_id = #{bugId}";

        return SqlTemplate.forQuery(pool, sql)
                .execute(Map.of("bugId", bugId, "status", status.name()))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<List<BugReport>> findBugReportsByProject(long projectId) {
        String sql = "SELECT * FROM bug_reports WHERE project_id = #{projectId}";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(BugReport.class)
                .execute(Collections.singletonMap("projectId", projectId))
                .compose(rowSet -> {
                    List<BugReport> bugReports = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(bugReports);
                });
    }

    public Future<List<BugReport>> findBugReportsByStatus(BugReportStatus status) {
        String sql = "SELECT * FROM bug_reports WHERE status = #{status}";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(BugReport.class)
                .execute(Collections.singletonMap("status", status.name()))
                .compose(rowSet -> {
                    List<BugReport> bugReports = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(bugReports);
                });
    }

    public Future<List<BugReport>> findBugReportsByDeveloper(long userId) {
        String sql = "SELECT br.* FROM bug_reports br " +
                "JOIN bug_developers bd ON br.bug_id = bd.bug_id " +
                "WHERE bd.user_id = #{userId}";

        return SqlTemplate.forQuery(pool, sql)
                .mapTo(BugReport.class)
                .execute(Collections.singletonMap("userId", userId))
                .compose(rowSet -> {
                    List<BugReport> bugReports = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(bugReports);
                });
    }
 */
}
