package app.mkiniz.poctime.economicgroup.config;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EconomicGroupRabbitMQConfig {

    public static final String ECONOMIC_GROUP_QUEUE = "economic-group-service";

    @Value("${rabbitmq.dlx-exchange}")
    private String dlxExchange;

    @Value("${rabbitmq.dlx-routing-key}")
    private String dlqRoutingKey;

    @Bean
    public HeadersExchange economicGroupInputExchange() {
        return new HeadersExchange(EconomicGroupConstants.ECONOMIC_GROUP_IN_EXCHANGE);
    }

    @Bean
    public HeadersExchange economicGroupOutputExchange() {
        return new HeadersExchange(EconomicGroupConstants.ECONOMIC_GROUP_OUT_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue economicGroupQueue() {
        return QueueBuilder.durable(ECONOMIC_GROUP_QUEUE)
                .quorum()
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    // Bindings
    @Bean
    public Binding economicGroupOrganizationBinding(HeadersExchange economicGroupInputExchange) {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("x-match", "any");
        headerValues.put("event_type", "OrganizationDeletedEvent");

        return BindingBuilder.bind(economicGroupQueue())
                .to(economicGroupInputExchange)
                .whereAny(headerValues)
                .match();
    }

    @Bean
    public Binding economicGroupOrganizationExchangeBinding(HeadersExchange organizationOutputExchange) {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("x-match", "any");

        return BindingBuilder.bind(economicGroupInputExchange())
                .to(organizationOutputExchange)
                .whereAny(headerValues)
                .match();
    }


}
