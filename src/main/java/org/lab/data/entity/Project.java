package org.lab.data.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Project(
        long projectId,
        @NotNull
        String name,
        long projectManagerId,
        Long teamLeadId
) {

    public Optional<Long> getTeamLead() {
        return Optional.ofNullable(teamLeadId);
    }

}
