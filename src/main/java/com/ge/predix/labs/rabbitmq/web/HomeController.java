package com.ge.predix.labs.rabbitmq.web;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.AmqpServiceInfo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer/Producer Model with RabbitMQ CF Service
 * 
 * info - smoke test send message in a queue and read the message from the queue with configuration rabbitMQ service details
 * test - smoke test send message in a queue and read the message from the queue 
 * barbershop - consumer/producer model simulator of Barber Shop
 * 
 * @author Sergey.Vyatkin@ge.com
 */
@ComponentScan
@RestController
public class HomeController {
	
	private static final String QUEUE_NAME = "TestSV";
	private String result = "";
	
    @Autowired private RabbitTemplate rabbitTemplate;
	
	public HomeController() {
		super();
	}

	/**
	 * @param echo
	 *            - the string to echo back
	 * @return - RabbitMQ service info with round trip messages from a queue 
	 */
	
	@SuppressWarnings("nls")
	@RequestMapping("/info")
	public String index(
			@RequestParam(value = "echo", defaultValue = "echo") String echo) {
		
		String result = "Services available in application <br>";

		CloudFactory cloudFactory = new CloudFactory();
		Cloud cloud = cloudFactory.getCloud();
		List<ServiceInfo> listServices = cloud.getServiceInfos();
		for (ServiceInfo si : listServices){
			   result += "service :"+ si.getId() + "<br>" ;
		}
		
		AmqpServiceInfo serviceInfo = (AmqpServiceInfo) cloud.getServiceInfo(cloud.getServiceInfos().get(0).getId());
		
		result +=  "<br> Service " + serviceInfo.getId() + "<br>" ;
        result += "Host        :" + serviceInfo.getHost() +  "<br>" ;
        result += "Port        :"+serviceInfo.getPort() +  "<br>" ;
        result += "Path        :"+serviceInfo.getPath() + "<br>" ;
        result += "URI         :"+serviceInfo.getUri() +  "<br>" ;
        result += "Virtual Host:"+ serviceInfo.getVirtualHost() + "<br>" ;
        result += "User Name :" + serviceInfo.getUserName() + "<br>" ;
        result += "Password    :"+ serviceInfo.getPassword() + "<br>" ;
        result += "Id          :"+ serviceInfo.getId() + "<br>" ;
        result += "Query       :"+ serviceInfo.getQuery() + "<br>" ;
        result += "Scheme      :"+ serviceInfo.getScheme() + "<br>" ;
        
        String message = (new Date()) + ":Test Message:" + UUID.randomUUID();
        
        result += message + "<br>";
        
    	rabbitTemplate.convertAndSend(QUEUE_NAME, message);
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
		}
		
		result += (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME) + " - " + (new Date());
		
		return result;
	}
	
	/**
	 * @param echo
	 *            - the string to echo back
	 * @return - round trip messages from a queue 
	 */
	
	@SuppressWarnings("nls")
	@RequestMapping("/test")
	public String test(
			@RequestParam(value = "echo", defaultValue = "echo") String echo) {
		
		String result = "";
		
		String message = (new Date()) + ":Test Message:" + UUID.randomUUID();
		
		result += "-> " + message + "<br>";
		
		rabbitTemplate.convertAndSend(QUEUE_NAME, message);
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
		}
		
		result += "<- " +  (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
		
		return result;
	}
	
	/***
	 * 
	 * in Edsger W. Dijkstra memory and his Sleeping Barber problem
	 * 
	 * Barber Shop 
	 * Customer arrives in waiting room aka a queue where two barbers Jovanni and Marchello serve them. 
	 * When a line of customers is empty the barbers stop to work.
	 * It's standard producer (line of customers)/consumer (two barbers) model.
	 * 
	 * @param echo
	 * @return log of events
	 * 
	 */
	
	@SuppressWarnings("nls")
	@RequestMapping("/barbershop")
	public String barberShop(
			@RequestParam(value = "echo", defaultValue = "echo") String echo) {
		
		// create a barber shop with two barbers: Jovanni and Marchello
		CountDownLatch latch = new CountDownLatch(2);
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// start consumers in thread pool executor design pattern
		executor.submit(new Barber("Jovanni", latch));
		result = new Date().getTime() + ": " + "Jovanni start to work" + "<br>";
		executor.submit(new Barber("Marchello", latch));
		result += new Date().getTime() + ": " + "Marchello start to work" + "<br>";

		/**
		 * Producer
		 * 
		 * Create new customers and put then in a queue
		 * 
		 */

		for (int i = 1; i < 8; i++) {
			String clientMessage = "client_" + i;
			result += new Date().getTime() + ": " + clientMessage + " is comming in a queue" + "<br>";
			rabbitTemplate.convertAndSend(QUEUE_NAME, clientMessage);

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		return result;
	}
	/**
	 * 
	 * @author 212396313
	 * 
	 * Barber class - each barber works in a thread.
	 * Consumer
	 *
	 */

	class Barber extends Thread {
		private String name;
		private CountDownLatch latch;

		public Barber(String name, CountDownLatch latch) {
			this.name = name;
			this.latch = latch;
		}

		@Override
		public void run() {
			String message = (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
			
			// waiting when queue has a first message 
			
			while (message == null) {
				message = (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			
			while (true) {
				if (message == null) {
					result += new Date().getTime() + ": " + name + " END " + "<br>";
					break;
				} else {
					result += new Date().getTime() + ": " + name + " start to cut -> " + message + "<br>";
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
					}
				}
					
				result += new Date().getTime() + ": " + name + " done -> " + message + "<br>";
				message = (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
			}
			latch.countDown();
		}

	}

}