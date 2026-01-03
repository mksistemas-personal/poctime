package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.OrganizationAddedEvent;
import app.mkiniz.poctime.organization.domain.OrganizationDeletedEvent;
import app.mkiniz.poctime.organization.domain.OrganizationUpdatedEvent;
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
class OrganizationEventListener {

    private final StreamBridge streamBridge;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrganizationCreated(OrganizationAddedEvent event) {
        Message<OrganizationAddedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(OrganizationConstants.ORGANIZATION_BINDING_NAME, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonUpdated(OrganizationUpdatedEvent event) {
        Message<OrganizationUpdatedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(OrganizationConstants.ORGANIZATION_BINDING_NAME, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonDeleted(OrganizationDeletedEvent event) {
        Message<OrganizationDeletedEvent> message = MessageHelper.buildMessage(event);
        streamBridge.send(OrganizationConstants.ORGANIZATION_BINDING_NAME, message);
    }
}
