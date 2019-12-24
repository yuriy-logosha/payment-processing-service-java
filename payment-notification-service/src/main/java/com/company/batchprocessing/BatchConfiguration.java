package com.company.batchprocessing;

import javax.sql.DataSource;

import com.company.payment.Payment;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Override
	public void setDataSource(DataSource dataSource) {
		// override to do not set datasource even if a datasource exist.
		// initialize will use a Map based JobRepository (instead of database)
	}
	// end::setup[]

	// tag::readerwriterprocessor[]
//	@Bean
//	public FlatFileItemReader<Payment> reader() {
//		return new FlatFileItemReaderBuilder<Payment>()
//			.name("personItemReader")
//			.resource(new ClassPathResource("sample-data.csv"))
//			.delimited()
//			.names(new String[]{"id", "amount", "currency", "debtor_iban", "creditor_iban", "details"})
//			.fieldSetMapper(new BeanWrapperFieldSetMapper<Payment>() {{
//				setTargetType(Payment.class);
//			}})
//			.build();
//	}
	@Bean
	public JdbcCursorItemReader<Payment> reader() {
		return new JdbcCursorItemReaderBuilder<Payment>()
				.dataSource(dataSource)
				.name("jdbc-reader")
				.sql("select id, amount, currency, debtor_iban, creditor_iban, details, bic from payment where notified = false")
				.rowMapper((rs, i) -> {Payment p = new Payment();
					p.setId(rs.getLong(1));
					p.setAmount(rs.getDouble(2));
					p.setCurrency(rs.getString(3));
					p.setDebtor_iban(rs.getString(4));
					p.setCreditor_iban(rs.getString(5));
					p.setDetails(rs.getString(6));
					p.setBic(rs.getString(7));
					return p;})
				.build();
	}

	@Bean
	public PaymentNotifier processor() {
		return new PaymentNotifier();
	}

	@Bean
	public JdbcBatchItemWriter<Payment> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Payment>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("update payment set notified = true where id = :id")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job notificationPaymentJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("notificationPaymentJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Payment> writer) {
		return stepBuilderFactory.get("step1")
			.<Payment, Payment> chunk(10)
			.reader(reader())
			.processor(processor())
			.writer(writer)
			.build();
	}
	// end::jobstep[]
}
