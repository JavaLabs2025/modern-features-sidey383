package org.lab.api.dto;

public record ProjectAnswer(
        long id,
        String name,
        UserAnswer manager,
        UserAnswer teamLead
) {
}
