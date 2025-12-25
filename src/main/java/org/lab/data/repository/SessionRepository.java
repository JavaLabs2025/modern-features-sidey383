package org.lab.data.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.lab.data.entity.Session;

import java.time.Instant;
import java.util.Optional;

@RegisterConstructorMapper(Session.class)
public interface SessionRepository {


    @GetGeneratedKeys({
            "session_id",
            "user_id",
            "created_at",
            "expires_at",
            "ip_address",
            "user_agent",
            "active"
    })
    @SqlUpdate("""
            INSERT INTO sessions (session_id, user_id, created_at, expires_at, ip_address, user_agent, active)
            VALUES (:sessionId, :userId, now(), :expiresAt, :ipAddress, :userAgent, true)
            RETURNING *
            """)
    Session createSession(
            @Bind("userId") long userId,
            @Bind("sessionId") String sessionId,
            @Bind("ipAddress") String ipAddress,
            @Bind("userAgent") String userAgent,
            @Bind("expiresAt") Instant expiresAt
    );

    @SqlUpdate("""
            UPDATE sessions SET expires_at = :expiresAt WHERE session_id = :sessionId
            """)
    void prolongateSession(
            @Bind("sessionId") String sessionId,
            @Bind("expiresAt") Instant expiresAt
    );

    @SqlQuery("""
            SELECT session_id, user_id, created_at, expires_at, ip_address, user_agent, active FROM sessions
            WHERE session_id = :sessionId
            """)
    Optional<Session> getSession(@Bind("sessionId") String sessionId);

    /*

    public Future<Optional<Session>> findById(String sessionId) {
        return SqlTemplate.forQuery(pool, """
                        SELECT * FROM sessions WHERE session_id = #{sessionId}
                        """)
                .mapTo(Session.class)
                .execute(Collections.singletonMap("sessionId", sessionId))
                .compose(rowSet -> {
                    if (rowSet.size() == 0) {
                        return Future.succeededFuture(Optional.empty());
                    }
                    Session session = rowSet.iterator().next();
                    // Автоматическая инвалидация просроченных сессий
                    if (Instant.now().isAfter(session.expiresAt())) {
                        return invalidateSession(sessionId)
                                .map(v -> Optional.empty());
                    }
                    return Future.succeededFuture(Optional.of(session));
                });
    }

    public Future<Void> invalidateSession(String sessionId) {
        return SqlTemplate.forQuery(pool, """
                        UPDATE sessions SET active = false WHERE session_id = #{sessionId}
                        """)
                .execute(Collections.singletonMap("sessionId", sessionId))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Void> invalidateAllUserSessions(long userId) {
        return SqlTemplate.forQuery(pool, """
                        UPDATE sessions SET active = false WHERE user_id = #{userId} AND active = true
                        """)
                .execute(Collections.singletonMap("userId", userId))
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<Void> renewSession(String sessionId, int additionalHours) {
        Map<String, Object> params = Map.of(
                "sessionId", sessionId,
                "hours", additionalHours
        );
        return SqlTemplate.forQuery(pool, """
                        UPDATE sessions SET expires_at = expires_at + INTERVAL '#{hours} hours'
                        WHERE session_id = #{sessionId} AND active = true
                        """)
                .execute(params)
                .compose(rowSet -> Future.succeededFuture());
    }

    public Future<List<Session>> findActiveSessionsByUser(long userId) {
        return SqlTemplate.forQuery(pool, """
                        SELECT * FROM sessions
                        WHERE user_id = #{userId} AND active = true AND expires_at > NOW()
                        ORDER BY created_at DESC
                        """)
                .mapTo(Session.class)
                .execute(Collections.singletonMap("userId", userId))
                .compose(rowSet -> {
                    List<Session> sessions = rowSet.stream()
                            .collect(java.util.stream.Collectors.toList());
                    return Future.succeededFuture(sessions);
                });
    }

    public Future<Integer> cleanupExpiredSessions() {
        return SqlTemplate.forQuery(pool, """
                        UPDATE sessions SET active = false
                        WHERE (expires_at <= NOW() OR NOT active)
                        AND created_at < NOW() - INTERVAL '7 days'
                        RETURNING session_id
                        """)
                .execute(Collections.emptyMap())
                .compose(rowSet -> Future.succeededFuture(rowSet.size()));
    }

    private String generateSessionId() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Future<Boolean> validateSession(String sessionId) {
        return findById(sessionId)
                .compose(optionalSession -> {
                    if (optionalSession.isEmpty()) {
                        return Future.succeededFuture(false);
                    }
                    Session session = optionalSession.get();
                    return Future.succeededFuture(session.isValid());
                });
    }

     */
}
