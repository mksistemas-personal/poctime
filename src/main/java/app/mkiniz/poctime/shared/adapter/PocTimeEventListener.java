package app.mkiniz.poctime.shared.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
public abstract class PocTimeEventListener<TAddEvent, TUpdateEvent, TDeleteEvent> {
    private final String exchangeName;
    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreated(TAddEvent event) {
        Message<TAddEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(this.exchangeName, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdated(TUpdateEvent event) {
        Message<TUpdateEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(this.exchangeName, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePOrganizationDeleted(TDeleteEvent event) {
        Message<TDeleteEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(this.exchangeName, "", message);
    }

}
