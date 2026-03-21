package app.mkiniz.poctime.organization.config;

import app.mkiniz.poctime.organization.OrganizationConstants;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganizationRabbitMQConfig {

    @Bean
    public HeadersExchange organizationInputExchange() {
        return new HeadersExchange(OrganizationConstants.ORGANIZATION_INPUT_EXCHANGE);
    }

    @Bean
    public HeadersExchange organizationOutputExchange() {
        return new HeadersExchange(OrganizationConstants.ORGANIZATION_OUTPUT_EXCHANGE);
    }
}
