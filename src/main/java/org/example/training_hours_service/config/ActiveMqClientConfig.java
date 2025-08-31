package org.example.training_hours_service.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class ActiveMqClientConfig {

    @Bean
    @Primary
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
                "tcp://localhost:61616");
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);           // 3 re-deliveries (4 tries overall)
        redeliveryPolicy.setInitialRedeliveryDelay(2000);     // 2s
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setBackOffMultiplier(2.0);           // 2s -> 4s -> 8s
        redeliveryPolicy.setMaximumRedeliveryDelay(10000);    // max 10s
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        return connectionFactory;
    }
}
