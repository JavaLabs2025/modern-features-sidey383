package org.lab.api.dto;

import org.lab.data.entity.ProjectRole;

public record ProjectUserAnswer(
        UserAnswer user,
        ProjectRole role
) {
}
