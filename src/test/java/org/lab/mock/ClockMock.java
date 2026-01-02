package org.lab.mock;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClockMock extends Clock {

    private Instant time = Instant.ofEpochSecond(0);
    private ZoneId zone = ZoneOffset.UTC;

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new ClockMock(time, zone);
    }

    @Override
    public Instant instant() {
        return time;
    }
}
