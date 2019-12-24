package com.company.payment.service;

import com.company.payment.PAYMENT_TYPE;
import com.company.payment.Payment;
import com.company.payment.exception.PaymentCanNotBeCancelledException;
import com.company.payment.exception.PaymentNotFoundException;
import com.company.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository repository;

    @Value( "${type1.rate}" )
    private double type1Rate;

    @Value( "${type2.rate}" )
    private double type2Rate;

    @Value( "${type3.rate}" )
    private double type3Rate;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Payment findById(@Positive Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public void deleteById(@Positive Long id) {
        repository.deleteById(id);
    }

    public Payment save(@Valid Payment newPayment) {
        return repository.saveAndFlush(newPayment);
    }

    public Payment updateCreationDate(Long id, @Valid LocalDateTime creation_date) {
        return repository.findById(id)
                .map(p -> {p.setCreation_date(creation_date); return save(p);})
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment cancelPayment(Long id) {
        return repository.findById(id)
                .filter(p -> p.getCreation_date().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()))
                .map(this::cancelPayment)
                .orElseThrow(() -> new PaymentCanNotBeCancelledException(id));
    }

    private Payment cancelPayment(final Payment p) {
        p.setCancellation_date(LocalDateTime.now());

        PAYMENT_TYPE type = PAYMENT_TYPE.getType(p);
        if (type == null) {
            //TODO: Wrong payment type and exception should be thrown.
        }
        long hours_spent = LocalDateTime.from( p.getCreation_date() ).until( p.getCancellation_date(), ChronoUnit.HOURS);

        p.setCancellation_fee(hours_spent * (type.equals(PAYMENT_TYPE.TYPE1) ?
                type1Rate :
                type.equals(PAYMENT_TYPE.TYPE2) ?
                        type2Rate : type3Rate));

        return save(p);
    }
}
