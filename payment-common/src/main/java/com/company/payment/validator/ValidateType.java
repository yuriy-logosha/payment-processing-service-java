package com.company.payment.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TypeValidator.class})
public @interface ValidateType {

    String message() default "Incompatible payment type.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
