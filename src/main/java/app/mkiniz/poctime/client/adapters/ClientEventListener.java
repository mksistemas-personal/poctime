package app.mkiniz.poctime.client.adapters;

import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.client.domain.ClientAddedEvent;
import app.mkiniz.poctime.client.domain.ClientDeletedEvent;
import app.mkiniz.poctime.client.domain.ClientUpdatedEvent;
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
class ClientEventListener {

    private final StreamBridge streamBridge;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClientCreated(ClientAddedEvent event) {
        Message<ClientAddedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(ClientConstants.CLIENT_EXCHANGE_OUT, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClientUpdated(ClientUpdatedEvent event) {
        Message<ClientUpdatedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(ClientConstants.CLIENT_EXCHANGE_OUT, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClientDeleted(ClientDeletedEvent event) {
        Message<ClientDeletedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(ClientConstants.CLIENT_EXCHANGE_OUT, message);
    }
}
