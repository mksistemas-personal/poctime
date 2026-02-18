package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.RemoveOrganizationUseCase;
import app.mkiniz.poctime.organization.domain.OrganizationDeletedEvent;
import app.mkiniz.poctime.shared.adapter.ValidateHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EconomicGroupMessageListener {

    private final RemoveOrganizationUseCase removeOrganizationUseCase;
    private final ValidateHelper validator;

    @Bean
    public Consumer<Message<OrganizationDeletedEvent>> economicGroupOrganizationDeleted() {
        return message -> {
            OrganizationDeletedEvent event = message.getPayload();
            RemoveOrganizationUseCase.RemoveOrganizationRequest request =
                    new RemoveOrganizationUseCase.RemoveOrganizationRequest(List.of(event.organizationId()));
            validator.validate(request);
            removeOrganizationUseCase.execute(
                    new RemoveOrganizationUseCase.RemoveOrganizationRequest(List.of(event.organizationId())
                    ));
        };
    }
}
