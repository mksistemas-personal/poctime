package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupAddedEvent;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupDeletedEvent;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupUpdatedEvent;
import app.mkiniz.poctime.shared.adapter.MessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
class EconomicGroupEventListener {

    private final StreamBridge streamBridge;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupCreated(EconomicGroupAddedEvent event) {
        Message<EconomicGroupAddedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(EconomicGroupConstants.ECONOMIC_GROUP_EXCHANGE_OUT, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupUpdated(EconomicGroupUpdatedEvent event) {
        Message<EconomicGroupUpdatedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(EconomicGroupConstants.ECONOMIC_GROUP_EXCHANGE_OUT, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEconomiGroupDeleted(EconomicGroupDeletedEvent event) {
        Message<EconomicGroupDeletedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(EconomicGroupConstants.ECONOMIC_GROUP_EXCHANGE_OUT, message);
    }
}
