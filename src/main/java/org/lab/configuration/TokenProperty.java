package org.lab.configuration;

import java.time.Duration;

public record TokenProperty(
        Duration getAccessTokenLifeTime
) {
}
