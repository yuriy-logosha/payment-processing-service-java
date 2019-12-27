package com.company.payment.service;

import com.company.payment.Payment;
import com.company.payment.exception.PaymentCanNotBeCancelledException;
import com.company.payment.exception.PaymentNotFoundException;
import com.company.payment.repository.PaymentRepository;
import com.company.payment.specification.PaymentSpecification;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PaymentServiceTest {

    @MockBean
    private PaymentRepository repository;

    @Test
    void findAll() {
        PaymentService paymentService = new PaymentService(repository);
        paymentService.findAll();
        verify(repository, only()).findAll(any(PaymentSpecification.class));
    }

    @Test
    void findById() {
        final long testId = 1L;
        when(repository.findById(testId)).thenReturn(buildPaymentType1(testId));
        PaymentService paymentService = new PaymentService(repository);
        paymentService.findById(testId);
        verify(repository, only()).findById(testId);
    }

    @Test
    void findByIdNotFound() {
        final long testId = 1L;
        PaymentService paymentService = new PaymentService(repository);
        Exception exception = assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.findById(testId);
        });

        assertTrue(exception.getMessage().contains("Could not found payment " + testId));
        verify(repository, only()).findById(testId);
    }

    @Test
    void save() {
        final Payment payment = buildPaymentType1(1L).get();
        PaymentService paymentService = new PaymentService(repository);
        paymentService.save(payment);
        verify(repository, only()).saveAndFlush(payment);
    }

    @Test
    void cancelPayment() {
        final long testId = 1L;
        final Optional<Payment> op = buildPaymentType1(testId);
        final Payment payment = op.get();
        when(repository.findById(testId)).thenReturn(op);
        when(repository.saveAndFlush(payment)).thenReturn(payment);
        PaymentService paymentService = new PaymentService(repository);
        final Payment result = paymentService.cancelPayment(testId);

        assertTrue(result != null);
        assertEquals(Double.valueOf(0), result.getCancellation_fee());
    }

    @Test
    void cancelPaymentRejected() {
        final long testId = 1L;
        final Optional<Payment> op = buildPaymentType1(testId, LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        final Payment payment = op.get();
        when(repository.findById(testId)).thenReturn(op);
        when(repository.saveAndFlush(payment)).thenReturn(payment);
        PaymentService paymentService = new PaymentService(repository);
        Exception exception = assertThrows(PaymentCanNotBeCancelledException.class, () -> {
            paymentService.cancelPayment(testId);
        });

        assertTrue(exception.getMessage().contains("Could not cancel payment " + testId));
    }

    //Helpers

    private Optional<Payment> buildPaymentType1(final long id, final LocalDateTime creation){
        Payment p = new Payment();
        p.setId(id);
        p.setCreation_date(creation);
        p.setCurrency("EUR");
        p.setDetails("test");
        return Optional.of(p);
    }

    private Optional<Payment> buildPaymentType1(final long id){
        return buildPaymentType1(id, LocalDateTime.now());
    }

}