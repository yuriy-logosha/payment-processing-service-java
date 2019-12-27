package com.company.payment;

import static com.company.payment.mapper.PaymentToJsonMapper.toJson;
import static com.company.payment.mapper.PaymentToJsonMapper.toPayment;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class ApplicationTest extends AbstractTest {

    private final static String PAYMENTS = "/payments";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnArray() throws Exception {
        mockMvc.perform(get(PAYMENTS))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[")))
                .andExpect(content().string(containsString("]")));
    }

    @Test
    public void shouldReturnPaymentById() throws Exception {
        Payment expectedPayment = buildPaymentType1();
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(expectedPayment)))
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(result -> {
                    Long id = toPayment(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).getId();
                    mockMvc.perform(get(PAYMENTS + "/" + id)).andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(id))
                            .andExpect(jsonPath("$.creation_date").isNotEmpty())
                            .andExpect(jsonPath("$.amount").value(expectedPayment.getAmount()))
                            .andExpect(jsonPath("$.currency").value(expectedPayment.getCurrency()))
                            .andExpect(jsonPath("$.debtor_iban").value(expectedPayment.getDebtor_iban()))
                            .andExpect(jsonPath("$.creditor_iban").value(expectedPayment.getCreditor_iban()))
                            .andExpect(jsonPath("$.details").value(expectedPayment.getDetails()));
                });
    }

    @Test
    public void shouldSavePaymentType1() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType1())))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldSavePaymentType2() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType2())))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldSavePaymentType2WithoutDetails() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType2WithoutDetails())))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldSavePaymentType3() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType3())))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldNOTSavePaymentOfWrongType() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentWrongType())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNOTSavePaymentOfNegativeAmount() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType1NegativeAmount())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNOTSavePaymentOfWrongCurrency() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentWrongCurrency())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCancelPayment() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType1())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cancellation_date").isEmpty())
                .andDo(result -> {
                    Long id = toPayment(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).getId();
                    mockMvc.perform(patch(PAYMENTS + "/" + id + "/cancel")
                            .contentType(APPLICATION_JSON_VALUE))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(id))
                            .andExpect(jsonPath("$.cancellation_date").isNotEmpty())
                            .andExpect(jsonPath("$.cancellation_fee").value(0.0));
                });
    }

    @Test
    public void shouldNOTCancelPayment() throws Exception {
        mockMvc.perform(post(PAYMENTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(buildPaymentType1())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cancellation_date").isEmpty())
                .andDo(result -> {
                    final Long id = toPayment(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).getId();
                    mockMvc.perform(get(PAYMENTS + "/" + id))
                            .andDo(print())
                            .andDo(dbResult -> {
                                final Payment dbPayment = toPayment(dbResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
                                final LocalDateTime creation_date = dbPayment.getCreation_date();
                                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss.SSS");
                                final String formatDateTime = creation_date.minusDays(1).format(formatter);
                                mockMvc.perform(patch(PAYMENTS + "/" + id)
                                        .param("creation_date", formatDateTime)
                                        .contentType(APPLICATION_JSON_VALUE))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andDo(result2 -> {
                                    mockMvc.perform(patch(PAYMENTS + "/" + id + "/cancel")
                                            .contentType(APPLICATION_JSON_VALUE))
                                            .andDo(print())
                                            .andExpect(status().isBadRequest());
                                });
                            });

                });
    }

}
