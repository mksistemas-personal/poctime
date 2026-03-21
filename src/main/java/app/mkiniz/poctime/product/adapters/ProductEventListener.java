package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.ProductAddedEvent;
import app.mkiniz.poctime.product.domain.ProductDeletedEvent;
import app.mkiniz.poctime.product.domain.ProductUpdatedEvent;
import app.mkiniz.poctime.shared.adapter.MessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
class ProductEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductCreated(ProductAddedEvent event) {
        Message<ProductAddedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(ProductConstants.PRODUCT_OUTPUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductUpdated(ProductUpdatedEvent event) {
        Message<ProductUpdatedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(ProductConstants.PRODUCT_OUTPUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductDeleted(ProductDeletedEvent event) {
        Message<ProductDeletedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(ProductConstants.PRODUCT_OUTPUT_EXCHANGE, "", message);
    }

}
