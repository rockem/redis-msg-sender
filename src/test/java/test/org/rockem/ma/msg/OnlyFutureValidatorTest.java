package test.org.rockem.ma.msg;

import org.junit.Test;
import org.rockem.ma.msg.validator.OnlyFutureValidator;

import javax.validation.ConstraintValidatorContext;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class OnlyFutureValidatorTest {

    private final ConstraintValidatorContext validatorContext = mock(ConstraintValidatorContext.class);

    @Test
    public void shouldFailForTimeInThePast() throws Exception {
        long hourBefore = Instant.now(Clock.systemUTC()).minus(1, ChronoUnit.HOURS).getEpochSecond();
        assertFalse(isValid(hourBefore));
    }

    private boolean isValid(long hourBefore) {
        return new OnlyFutureValidator().isValid(hourBefore, validatorContext);
    }

    @Test
    public void shouldPassForTimeInTheFuture() throws Exception {
        long secondLater = Instant.now(Clock.systemUTC()).plus(1, ChronoUnit.SECONDS).getEpochSecond();
        assertTrue(isValid(secondLater));
    }
}