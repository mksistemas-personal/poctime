package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupAddedEvent;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupDeletedEvent;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupUpdatedEvent;
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
class EconomicGroupEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupCreated(EconomicGroupAddedEvent event) {
        Message<EconomicGroupAddedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(EconomicGroupConstants.ECONOMIC_GROUP_OUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupUpdated(EconomicGroupUpdatedEvent event) {
        Message<EconomicGroupUpdatedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(EconomicGroupConstants.ECONOMIC_GROUP_OUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupDeleted(EconomicGroupDeletedEvent event) {
        Message<EconomicGroupDeletedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(EconomicGroupConstants.ECONOMIC_GROUP_OUT_EXCHANGE, "", message);
    }
}
