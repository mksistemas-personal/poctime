package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.shared.DeleteBaseBusinessTest;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteEconomicGroupServiceTest {
    @Mock
    private EconomicGroupRepository repository;

    @InjectMocks
    private DeleteEconomicGroupService service;

    private DeleteBaseBusinessTest<Tsid, EconomicGroupResponse> baseTest;
    private EconomicGroup economicGroup;

    @BeforeEach
    void setUp() {
        this.baseTest = DeleteBaseBusinessTest.of();
        this.economicGroup = EconomicGroup.builder()
                .id(123L)
                .name("Test Economic Group")
                .description("Test Description")
                .organizationIds(Set.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                .build();
        this.baseTest.when((id) -> service.execute(id));
    }

    @Test
    void shouldDeleteEconomicGroup() {
        baseTest.given(() -> {
                    when(repository.findById(economicGroup.getId())).thenReturn(Optional.of(economicGroup));
                    doNothing().when(repository).delete(any(EconomicGroup.class));
                    return Tsid.from(economicGroup.getId());
                })
                .then((id, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response).isEqualTo(EconomicGroupResponse.from(economicGroup));
                    verify(repository, times(1)).delete(any(EconomicGroup.class));
                    verify(repository, times(1)).findById(economicGroup.getId());
                })
                .execute();

    }
}