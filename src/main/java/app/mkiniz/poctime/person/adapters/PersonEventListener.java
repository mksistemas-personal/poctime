package app.mkiniz.poctime.person.adapters;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.PersonAddedEvent;
import app.mkiniz.poctime.person.domain.PersonDeletedEvent;
import app.mkiniz.poctime.person.domain.PersonUpdatedEvent;
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
class PersonEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonCreated(PersonAddedEvent event) {
        Message<PersonAddedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(PersonConstants.PERSON_OUTPUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonUpdated(PersonUpdatedEvent event) {
        Message<PersonUpdatedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(PersonConstants.PERSON_OUTPUT_EXCHANGE, "", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonDeleted(PersonDeletedEvent event) {
        Message<PersonDeletedEvent> message = MessageHelper.buildMessage(event);
        rabbitTemplate.convertAndSend(PersonConstants.PERSON_OUTPUT_EXCHANGE, "", message);
    }
}
