package com.ge.predix.labs.rabbitmq.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/***
 * 
 * @author Sergey.Vyatkin@ge.com 
 *         Configuration for RabbitMQ Please see details
 *         http://docs.spring.io/autorepo/docs/spring-cloud/1.0.0.RC3/api/org/springframework/cloud/config/java/AbstractCloudConfig.html
 *
 */

@Configuration
@ComponentScan
public class RabbitConfig extends AbstractCloudConfig {

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory().rabbitConnectionFactory());
	}
}