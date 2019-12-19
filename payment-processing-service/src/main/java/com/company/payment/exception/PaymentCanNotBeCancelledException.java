package com.company.payment.exception;

public class PaymentCanNotBeCancelledException extends RuntimeException {
    public PaymentCanNotBeCancelledException(Long id) {
        super("Could not cancel payment " + id);
    }
}
