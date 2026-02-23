package app.mkiniz.poctime.config;

import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.person.PersonConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String DLX_EXCHANGE = "dlx.poctime";
    public static final String DLQ_QUEUE = "queue.poctime.dlq";
    public static final String DLQ_ROUTING_KEY = "poctime.error";

    public static final String ECONOMIC_GROUP_QUEUE = "economic-group-service";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Dead Letter Setup
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DLQ_ROUTING_KEY);
    }

    // Main Exchanges
    @Bean
    public HeadersExchange personExchange() {
        return new HeadersExchange(PersonConstants.PERSON_EXCHANGE);
    }

    @Bean
    public HeadersExchange organizationExchange() {
        return new HeadersExchange(OrganizationConstants.ORGANIZATION_EXCHANGE);
    }

    @Bean
    public HeadersExchange economicGroupExchange() {
        return new HeadersExchange(EconomicGroupConstants.ECONOMIC_GROUP_EXCHANGE);
    }

    @Bean
    public HeadersExchange clientExchange() {
        return new HeadersExchange(ClientConstants.CLIENT_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue economicGroupQueue() {
        return QueueBuilder.durable(ECONOMIC_GROUP_QUEUE)
                .quorum()
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    // Bindings
    @Bean
    public Binding economicGroupOrganizationBinding() {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("x-match", "any");
        headerValues.put("event_type", "OrganizationDeletedEvent");

        return BindingBuilder.bind(economicGroupQueue())
                .to(organizationExchange())
                .whereAny(headerValues)
                .match();
    }
}
