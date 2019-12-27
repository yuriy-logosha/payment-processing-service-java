package com.company.payment.builder;

import com.company.payment.Payment;

public class PaymentBuilder {

    private Payment payment = new Payment();

    public Payment build() {
        return payment;
    }

    public PaymentBuilder withCurrency(final String currency) {
        payment.setCurrency(currency);
        return this;
    }

    public PaymentBuilder withAmount(final double amount) {
        payment.setAmount(amount);
        return this;
    }

    public PaymentBuilder withDetails(final String details) {
        payment.setDetails(details);
        return this;
    }

    public PaymentBuilder withCreditorIban(final String iban) {
        payment.setCreditor_iban(iban);
        return this;
    }

    public PaymentBuilder withDebtorIban(final String iban) {
        payment.setDebtor_iban(iban);
        return this;
    }

    public PaymentBuilder withBIC(final String bic) {
        payment.setBic(bic);
        return this;
    }

}