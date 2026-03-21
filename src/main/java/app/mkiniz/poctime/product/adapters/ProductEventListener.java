package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.ProductAddedEvent;
import app.mkiniz.poctime.product.domain.ProductDeletedEvent;
import app.mkiniz.poctime.product.domain.ProductUpdatedEvent;
import app.mkiniz.poctime.shared.adapter.PocTimeEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ProductEventListener extends PocTimeEventListener<ProductAddedEvent, ProductUpdatedEvent, ProductDeletedEvent> {

    public ProductEventListener(RabbitTemplate rabbitTemplate) {
        super(ProductConstants.PRODUCT_EXCHANGE, rabbitTemplate);
    }

}
