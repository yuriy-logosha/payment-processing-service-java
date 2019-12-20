package com.company.payment;

public enum PAYMENT_TYPE {
    TYPE1("TYPE1"), TYPE2("TYPE2"), TYPE3("TYPE3");

    public final String name;

    PAYMENT_TYPE(String name) {
        this.name = name;
    }

    public static PAYMENT_TYPE getType(final Payment p) {
        try {
            if (p.getBic() != null && !p.getBic().isEmpty()) {
                return TYPE3;
            } else if (p.getCurrency() != null && CURRENCY.USD.equals(CURRENCY.valueOf(p.getCurrency()))) {
                return TYPE2;
            } else if (p.getCurrency() != null && CURRENCY.EUR.equals(CURRENCY.valueOf(p.getCurrency())) && p.getDetails() != null && !p.getDetails().isEmpty()) {
                return TYPE1;
            } else return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
