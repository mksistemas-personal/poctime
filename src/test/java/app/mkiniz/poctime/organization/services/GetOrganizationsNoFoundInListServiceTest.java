package app.mkiniz.poctime.organization.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GetOrganizationsNoFoundInListServiceTest {

    @InjectMocks
    private GetOrganizationsNoFoundInListService service;

    @Test
    void shouldReturnAllIdsWhenInputListIsEmpty() {
        // When
        List<String> result = service.execute(List.of());
        // Then
        assertThat(result).isEmpty();
    }
}
