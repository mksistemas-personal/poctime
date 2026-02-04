package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.shared.UpdateBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEconomicGroupServiceTest {
    @Mock
    private EconomicGroupRepository economicGroupRepository;

    @Mock
    private OrganizationProvider organizationProvider;

    private UpdateBaseBusinessTest<Tsid, EconomicGroupRequest, EconomicGroupResponse> baseTest;

    @InjectMocks
    private UpdateEconomicGroupService service;
    private EconomicGroup economicGroup;
    private EconomicGroup economicGroupUpdated;

    @BeforeEach
    void setUp() {
        this.baseTest = UpdateBaseBusinessTest.of();
        this.baseTest.when((key, request) -> service.execute(key, request));

        this.economicGroup = EconomicGroup.builder()
                .id(123L)
                .name("Test Economic Group")
                .description("Test Description")
                .organizationIds(Set.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                .build();
        this.economicGroupUpdated = EconomicGroup.builder()
                .id(123L)
                .name("Test Economic Group Updated")
                .description("Test Description Updated")
                .organizationIds(Set.of("0PB1TNFCV3DAA", "0PB1TQ7C33CAA"))
                .build();
    }

    @Test
    void shouldUpdateEconomicGroup() {
        baseTest.given(() -> {
                    when(organizationProvider.getOrganizationsNotFound(anyList())).thenReturn(List.of());
                    when(economicGroupRepository.save(any())).thenReturn(economicGroup);
                    when(economicGroupRepository.findById(anyLong())).thenReturn(Optional.of(economicGroup));
                    when(economicGroupRepository.existsByName(anyString())).thenReturn(false);
                    return EconomicGroupRequest.builder()
                            .id(Tsid.from(economicGroup.getId()))
                            .name("Test Economic Group Updated")
                            .description("Test Description Updated")
                            .organizationIds(List.of("0PB1TNFCV3DAA", "0PB1TQ7C33CAA"))
                            .build();
                })
                .then((key, request, response) -> {
                    assertNotNull(response);
                    assertEquals(TsidGenerator.fromLongToString(economicGroupUpdated.getId()), response.id());
                    assertEquals(economicGroupUpdated.getName(), response.name());
                    assertEquals(economicGroupUpdated.getDescription(), response.description());
                    assertEquals(
                            economicGroupUpdated.getOrganizationIds().stream().sorted().toList(),
                            response.organizationIds().stream().sorted().toList());

                    ArgumentCaptor<EconomicGroup> captor = ArgumentCaptor.forClass(EconomicGroup.class);
                    verify(economicGroupRepository, times(1)).save(captor.capture());

                    EconomicGroup savedEconomicGroup = captor.getValue();
                    assertEquals(request.name(), savedEconomicGroup.getName());
                    assertEquals(request.description(), savedEconomicGroup.getDescription());
                    assertEquals(
                            economicGroupUpdated.getOrganizationIds().stream().sorted().toList(),
                            request.organizationIds().stream().sorted().toList());

                    verify(organizationProvider, times(1)).getOrganizationsNotFound(anyList());
                    verify(economicGroupRepository, times(1)).findById(anyLong());
                    verify(economicGroupRepository, times(1)).existsByName(anyString());
                })
                .execute(Tsid.from(economicGroup.getId()));
    }

}