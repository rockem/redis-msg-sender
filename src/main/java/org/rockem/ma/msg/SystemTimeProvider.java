package org.rockem.ma.msg;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class SystemTimeProvider implements TimeProvider {

    @Override
    public long now() {
        return Instant.now(Clock.systemUTC()).getEpochSecond();
    }
}
