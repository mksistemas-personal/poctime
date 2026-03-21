package app.mkiniz.poctime.config;

import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.product.ProductConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.dlx-exchange}")
    private String dlxExchange;

    @Value("${rabbitmq.dlx-queue}")
    private String dlqQueue;

    @Value("${rabbitmq.dlx-routing-key}")
    private String dlqRoutingKey;


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Dead Letter Setup
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchange);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqQueue).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }

    // Main Exchanges


    @Bean
    public HeadersExchange clientExchange() {
        return new HeadersExchange(ClientConstants.CLIENT_EXCHANGE);
    }

    @Bean
    public HeadersExchange productExchange() {
        return new HeadersExchange(ProductConstants.PRODUCT_EXCHANGE);
    }


}
