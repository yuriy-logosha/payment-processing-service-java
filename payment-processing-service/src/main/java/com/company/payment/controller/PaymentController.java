package com.company.payment.controller;

import com.company.payment.Payment;
import com.company.payment.exception.PaymentCanNotBeCancelledException;
import com.company.payment.service.PaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping("/payments")
    @ResponseStatus(HttpStatus.OK)
    List<Payment> all() {
        return service.findAll();
    }

    @DeleteMapping("/payments")
    void delete() {
        service.deleteAll();
    }

    @DeleteMapping("/payments/{id}")
    void deleteOne(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    Payment newPayment(@Valid @RequestBody Payment newPayment) {
        return service.save(newPayment);
    }

    @GetMapping("/payments/{id}")
    @ResponseStatus(HttpStatus.OK)
    Payment one(@PathVariable Long id) {
        return service.findById(id);
    }

    //TODO: Should be removed. Created only for testing needs.
    @PatchMapping("/payments/{id}")
    Payment updatePayment(@PathVariable Long id, @RequestParam @DateTimeFormat(pattern="dd.MM.yyyy'T'HH:mm:ss.SSSXXX") java.util.Date creation_date) {
        return service.updateCreationDate(id, creation_date);
    }

    @PatchMapping("/payments/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    Payment cancelPayment(@PathVariable Long id) {
        try {
            return service.cancelPayment(id);
        } catch (PaymentCanNotBeCancelledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
        }
    }

}
