package org.lab.api.dto;

import org.lab.data.entity.ProjectRole;

public record ProjectWithRoleAnswer(
        ProjectAnswer project,
        ProjectRole projectRole
) {
}
