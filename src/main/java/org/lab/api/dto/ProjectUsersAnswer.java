package org.lab.api.dto;

import java.util.Collection;

public record ProjectUsersAnswer(
        Collection<ProjectUserAnswer> users,
        ProjectAnswer project
) {
}
