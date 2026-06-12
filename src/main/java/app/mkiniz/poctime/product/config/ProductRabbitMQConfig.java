package app.mkiniz.poctime.product.config;

import app.mkiniz.poctime.product.ProductConstants;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitMQConfig {

    @Bean
    public HeadersExchange productInputExchange() {
        return new HeadersExchange(ProductConstants.PRODUCT_INPUT_EXCHANGE);
    }

    @Bean
    public HeadersExchange productOutputExchange() {
        return new HeadersExchange(ProductConstants.PRODUCT_OUTPUT_EXCHANGE);
    }

}
