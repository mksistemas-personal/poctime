package app.mkiniz.poctime.person.config;


import app.mkiniz.poctime.person.PersonConstants;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonRabbitMQConfig {
    // exchanges
    @Bean
    public HeadersExchange personInputExchange() {
        return new HeadersExchange(PersonConstants.PERSON_INPUT_EXCHANGE);
    }

    @Bean
    public HeadersExchange personOutputExchange() {
        return new HeadersExchange(PersonConstants.PERSON_OUTPUT_EXCHANGE);
    }
}
