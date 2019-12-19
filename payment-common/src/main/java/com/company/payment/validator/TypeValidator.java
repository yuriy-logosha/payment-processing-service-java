package com.company.payment.validator;

import com.company.payment.PAYMENT_TYPE;
import com.company.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TypeValidator implements ConstraintValidator<ValidateType, Object> {
    private static final Logger log = LoggerFactory.getLogger(TypeValidator.class);

    private String message;

    @Override
    public void initialize(ValidateType requiredIfChecked) {
        message = requiredIfChecked.message();
    }

    @Override
    public boolean isValid(Object objectToValidate, ConstraintValidatorContext context) {

        Boolean valid = false;
        try {
            if (objectToValidate == null) {
                return false;
            }

            Payment payment = (Payment) objectToValidate;
            PAYMENT_TYPE type = PAYMENT_TYPE.getType(payment);
            valid = type != null;
            if(!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                log.error("Not valid type: " + payment);
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error while validating payment.", e);
            return false;
        }

        return valid;
    }
}