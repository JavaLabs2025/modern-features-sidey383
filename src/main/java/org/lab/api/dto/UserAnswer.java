package org.lab.api.dto;

import org.lab.data.entity.UserType;

public record UserAnswer(
        long id,
        String name,
        UserType userType
) {
}
