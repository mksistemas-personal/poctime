package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.config.RabbitMQConfig;
import app.mkiniz.poctime.economicgroup.RemoveOrganizationUseCase;
import app.mkiniz.poctime.organization.domain.OrganizationDeletedEvent;
import app.mkiniz.poctime.shared.adapter.ValidateHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EconomicGroupMessageListener {

    private final RemoveOrganizationUseCase removeOrganizationUseCase;
    private final ValidateHelper validator;

    @RabbitListener(queues = RabbitMQConfig.ECONOMIC_GROUP_QUEUE)
    public void handleEconomicGroupOrganizationDeleted(Message<OrganizationDeletedEvent> message) {
        OrganizationDeletedEvent event = message.getPayload();
        log.info("Received OrganizationDeletedEvent for organizationId: {}", event.organizationId());

        RemoveOrganizationUseCase.RemoveOrganizationRequest request =
                new RemoveOrganizationUseCase.RemoveOrganizationRequest(List.of(event.organizationId()));

        validator.validate(request);
        removeOrganizationUseCase.execute(request);
    }
}
