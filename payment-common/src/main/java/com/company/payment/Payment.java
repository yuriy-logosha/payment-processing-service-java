package com.company.payment;


import com.company.payment.validator.ValidateType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@ValidateType
public class Payment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Positive
    @NotNull(message = "Amount is mandatory.")
    private Double amount;

    @NotNull(message = "Currency is mandatory.")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotEmpty
    @NotNull(message = "debtor_iban is mandatory.")
    private String debtor_iban;

    @NotEmpty
    @NotNull(message = "creditor_iban is mandatory.")
    private String creditor_iban;

    private String details;

    private String bic;

    @JsonFormat(pattern="dd.MM.yyyy'T'HH:mm:ss.SSS")
    @Column(name = "creation_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable=false)
    private LocalDateTime creation_date;

    private LocalDateTime cancellation_date;

    private Double cancellation_fee;

    private boolean notified;

}
