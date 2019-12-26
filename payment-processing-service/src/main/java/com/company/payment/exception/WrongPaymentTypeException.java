package com.company.payment.exception;

public class WrongPaymentTypeException extends RuntimeException {
    public WrongPaymentTypeException() {
        super("Wrong payment type.");
    }
}
