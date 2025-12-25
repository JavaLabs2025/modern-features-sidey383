package org.lab.api.dto;

import org.lab.data.entity.UserType;

public record UserAnswer(
        String name,
        UserType userType
) {
}
