# predix-rabbitmq-cf
This is a Predix Labs Java example on how to run the RabbitMq Cloud Foundry service using RabbitTemplate API.
The example contains HomeController.java to run the smoke tests and the consumer/producer barber shop simulator.  The RabbitMq template bean configuration uses two libraries:
- spring-boot-starter-amqp
- spring-cloud-cloudfoundry-connector

######Please see details in the Developer notes.

##Project structure
  ```
├── LICENSE.md
├── README.md
├── manifest.yml
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── ge
    │               └── predix
    │                   └── labs
    │                       └── rabbitmq
    │                           ├── Application.java
    │                           ├── config
    │                           │   └── RabbitConfig.java
    │                           └── web
    │                               └── HomeController.java
    └── test
  ```

Code (RabbitConfig.java):
 -  extends AbstractCloudConfig class to get the ConnectionFactory object associated with the only RabbitMQ service bound to our application
 - and the Rabbit Template that we use in the example
 
  ```
@Configuration
@ComponentScan
public class RabbitConfig extends AbstractCloudConfig {

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory().rabbitConnectionFactory());
	}
	
   ``` 
   
## Installation
 - clone repository  
    `>git clone https://github.com/PredixDev/predix-rabbitmq-cf.git`
 - check that you have on your market space a RabbitMQ service 
 
    `>cf m`
   
   ``` 
   Getting services from the marketplace in org sergey.vyatkin@ge.com / space dev as sergey.vyatkin@ge.com...
   OK

   service                    plans       description   
   business-operations        beta        Upgrade your service using a subscription-based business model.   
   logstash-3                 free        Logstash 1.4 service for application development and testing   
   p-rabbitmq                 standard    RabbitMQ is a robust and scalable high-performance multi-protocol messaging broker.  
   postgres                   shared      Reliable PostgrSQL Service   
   ...
   ```
 - create a new service for rabbit MQ like 
     `>cf cs p-rabbitmq standard rabbitmq_sv` 
###### application dynamically detects bound rabbit mq service.  You do not need to change a code. 

 - deploy application 
 
  ```
    >cd predix-rabbitmq-cf
    
    >mvn clean package
    
    >cf push 
    
  ```
 - use browser to test app: [http://rqbbitmq-sv.run.aws-usw02-pr.ice.predix.io/info] (http://rqbbitmq-sv.run.aws-usw02-pr.ice.predix.io/info)
 - 
``` 
Services available in application 
service :rabbitmq_sv

Service rabbitmq_sv
Host :10.72.6.27
Port :5672
Path :f3d47985-2ba9-45c9-b597-f829c0db5286
URI :amqp://a9c94ee2-f9d6-4872-a17c-b4a054d945f3:k6rqn8206qn8rocaios0qt4no7@10.72.6.27:5672/f3d47985-2ba9-45c9-b597-f829c0db5286
Virtual Host:f3d47985-2ba9-45c9-b597-f829c0db5286
User Name :a9c94ee2-f9d6-4872-a17c-b4a054d945f3
Password :k6rqn8206qn8rocaios0qt4no7
Id :rabbitmq_sv
Query :null
Scheme :amqp
Fri Nov 06 18:20:33 UTC 2015:Test Message:8ae636c7-3f66-4cfb-94d8-4fb7d67901e0
Fri Nov 06 18:20:33 UTC 2015:Test Message:8ae636c7-3f66-4cfb-94d8-4fb7d67901e0 - Fri Nov 06 18:20:34 UTC 2015
``` 

- smoke test: [http://rqbbitmq-sv.run.aws-usw02-pr.ice.predix.io/test] (http://rqbbitmq-sv.run.aws-usw02-pr.ice.predix.io/test)

``` 
-> Tue Nov 10 19:06:53 UTC 2015:Test Message:3808ca7d-5f73-4f72-a173-5447471a0f0c
<- Tue Nov 10 19:06:53 UTC 2015:Test Message:3808ca7d-5f73-4f72-a173-5447471a0f0c 
``` 

- The Barber shop test for Consumer/Producer model where barbers Jovanni and Marchello serve customers coming in barber's shop from a queue <br>
   test: http://rqbbitmq-sv.run.aws-usw02-pr.ice.predix.io/barbershop <br>
   returns log with event series like below <br>
```   
1446587208351: Jovanni starts to work  
1446587208351: Marchello starts to work  
1446587208351: client_1 is coming in a queue  
1446587208456: Marchello start to cut -> client_1  
1446587208556: client_2 is coming in a queue  
1446587208756: Jovanni start to cut -> client_2  
1446587208756: client_3 is coming in a queue     
1446587208956: client_4 is coming in a queue      
1446587209057: Marchello done -> client_1    
1446587209057: Marchello start to cut -> client_3  
1446587209157: client_5 is coming in a queue  
1446587209356: Jovanni done -> client_2   
1446587209357: Jovanni start to cut -> client_4   
1446587209357: client_6 is coming in a queue   
1446587209557: client_7 is coming in a queue  
1446587209657: Marchello done -> client_3  
1446587209658: Marchello start to cut -> client_5  
1446587209957: Jovanni done -> client_4   
1446587209957: Jovanni start to cut -> client_6   
1446587210258: Marchello done -> client_5   
1446587210259: Marchello start to cut -> client_7   
1446587210557: Jovanni done -> client_6   
1446587210558: Jovanni END   
1446587210859: Marchello done -> client_7   
1446587210860: Marchello END    
```  

#### Developer notes:

 - To load in eclipse you may use [SpringSource Tool Suite - STS](https://spring.io/tools/sts/all)  
  ```
  >mvn eclipse:clean eclipse:eclipse  
  
  open eclipse and use the following commands:
  File/Import/General/Existing Projects/Browse to predix-rabbitmq-cf dir   
  ```
 - Maven library dependency for RabbitMQ service:
    ```
       <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-amqp</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-cloudfoundry-connector</artifactId>
		</dependency>
    ```
    
    
  
  
