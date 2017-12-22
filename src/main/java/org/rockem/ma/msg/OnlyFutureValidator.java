package org.rockem.ma.msg;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class OnlyFutureValidator implements ConstraintValidator<OnlyFuture, Long> {


    @Override
    public void initialize(OnlyFuture constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC).isAfter(now());
    }

    private LocalDateTime now() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
