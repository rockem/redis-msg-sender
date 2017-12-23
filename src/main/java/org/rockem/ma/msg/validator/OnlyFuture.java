package org.rockem.ma.msg.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OnlyFutureValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyFuture {
    String message() default "Invalid time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
