package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.shared.AddBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddEconomicGroupServiceTest {
    @Mock
    private EconomicGroupRepository economicGroupRepository;

    @Mock
    private OrganizationProvider organizationProvider;

    private AddBaseBusinessTest<EconomicGroupRequest, EconomicGroupResponse> baseTest;

    @InjectMocks
    private AddEconomicGroupService service;
    private EconomicGroup economicGroup;

    @BeforeEach
    void setUp() {
        this.baseTest = AddBaseBusinessTest.<EconomicGroupRequest, EconomicGroupResponse>of();
        this.baseTest.when(request -> service.execute(request));

        this.economicGroup = EconomicGroup.builder()
                .id(123L)
                .name("Test Economic Group")
                .description("Test Description")
                .organizationIds(Set.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                .build();

    }

    @Test
    void shouldAddEconomicGroup() {
        baseTest.given(() -> {
                    when(organizationProvider.getOrganizationsNotFound(anyList())).thenReturn(List.of());
                    when(economicGroupRepository.save(any())).thenReturn(economicGroup);
                    when(economicGroupRepository.existsByName(anyString())).thenReturn(false);
                    return EconomicGroupRequest.builder()
                            .name("Test Economic Group")
                            .description("Test Description")
                            .organizationIds(List.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                            .build();
                })
                .then((request, response) -> {
                    assertNotNull(response);
                    assertEquals(TsidGenerator.fromLongToString(economicGroup.getId()), response.id());
                    assertEquals(economicGroup.getName(), response.name());
                    assertEquals(economicGroup.getDescription(), response.description());
                    assertEquals(economicGroup.getOrganizationIds().stream().toList(), response.organizationIds());
                    verify(economicGroupRepository, times(1)).save(any());
                    verify(organizationProvider, times(1)).getOrganizationsNotFound(anyList());
                    verify(economicGroupRepository, times(1)).existsByName(anyString());
                })
                .execute();
    }

    @Test
    void shouldAddEconomicGroupExisted() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest.given(() -> {
                    when(economicGroupRepository.existsByName(anyString())).thenReturn(true);
                    return EconomicGroupRequest.builder()
                            .name("Test Economic Group")
                            .description("Test Description")
                            .organizationIds(List.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                            .build();
                })
                .then((request, response) -> {
                    fail("Should not be called");
                })
                .execute());
        assertThat(exception.getMessage()).isEqualTo(EconomicGroupConstants.ECONOMIC_GROUP_ALREADY_EXISTS);
    }

    @Test
    void shouldAddEconomicGroupWithOrganizationsNotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> baseTest.given(() -> {
                    when(organizationProvider.getOrganizationsNotFound(anyList())).thenReturn(List.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"));
                    when(economicGroupRepository.existsByName(anyString())).thenReturn(false);
                    return EconomicGroupRequest.builder()
                            .name("Test Economic Group")
                            .description("Test Description")
                            .organizationIds(List.of("0PB1TNFCV3D2C", "0PB1TQ7C33CZS"))
                            .build();
                })
                .then((request, response) -> {
                    fail("Should not be called");
                })
                .execute());
        assertThat(exception.getMessage()).isEqualTo(String.format(EconomicGroupConstants.ORGANIZATIONS_NOT_FOUND, "0PB1TNFCV3D2C,0PB1TQ7C33CZS"));
    }


}