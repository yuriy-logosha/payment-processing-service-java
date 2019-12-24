package com.company.batchprocessing;

import com.company.payment.PAYMENT_TYPE;
import com.company.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PaymentNotifier implements ItemProcessor<Payment, Payment> {

	private static final Logger log = LoggerFactory.getLogger(PaymentNotifier.class);

	@Override
	public Payment process(final Payment p) throws Exception {
		PAYMENT_TYPE type = PAYMENT_TYPE.getType(p);

		log.info("Notifying (" + type + ") " + p.getId());

		return p;
	}

}
