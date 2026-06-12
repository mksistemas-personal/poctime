package app.mkiniz.poctime.client.config;

import app.mkiniz.poctime.client.ClientConstants;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientRabbitMQConfig {

    @Bean
    public HeadersExchange clientInputExchange() {
        return new HeadersExchange(ClientConstants.CLIENT_INPUT_EXCHANGE);
    }

    @Bean
    public HeadersExchange clientOutputExchange() {
        return new HeadersExchange(ClientConstants.CLIENT_OUTPUT_EXCHANGE);
    }

}
