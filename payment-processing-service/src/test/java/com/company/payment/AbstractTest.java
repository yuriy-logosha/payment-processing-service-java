package com.company.payment;

import com.company.payment.builder.PaymentBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class AbstractTest {
    public static Payment buildPaymentType1() throws JsonProcessingException {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("EUR")
                .withCreditorIban("EE411217896463868358")
                .withDebtorIban("EE061256142117251146")
                .withDetails("details")
                .build();
    }

    public static Payment buildPaymentType1NegativeAmount() {
        return (new PaymentBuilder())
                .withAmount(-100)
                .withCurrency("EUR")
                .withCreditorIban("EE411217896463868358")
                .withDebtorIban("EE061256142117251146")
                .withDetails("details")
                .build();
    }

    public static Payment buildPaymentType2() {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("USD")
                .withCreditorIban("EE411217896463868358")
                .withDebtorIban("EE061256142117251146")
                .build();
    }

    public static Payment buildPaymentType2WithoutDetails() {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("USD")
                .withCreditorIban("EE411217896463868358")
                .withDebtorIban("EE061256142117251146")
                .build();
    }

    public static Payment buildPaymentType3() {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("USD")
                .withCreditorIban("EE411217896463868358")
                .withDebtorIban("EE061256142117251146")
                .withBIC("bic")
                .build();
    }

    public static Payment buildPaymentWrongType() {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("EUR")
                .build();
    }

    public static Payment buildPaymentWrongCurrency() {
        return (new PaymentBuilder())
                .withAmount(100)
                .withCurrency("UAH")
                .build();
    }
}
