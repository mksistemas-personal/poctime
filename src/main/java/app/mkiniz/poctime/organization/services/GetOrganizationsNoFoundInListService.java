package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.GetOrganizationsNoFoundInListUseCase;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetOrganizationsNoFoundInListService implements GetOrganizationsNoFoundInListUseCase {
    private OrganizationRepository organizationRepository;

    @Override
    public List<String> execute(List<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return List.of();
        }

        List<Long> longIds = ids.stream()
                .map(TsidGenerator::fromStringToLong)
                .toList();

        return organizationRepository.findIdsByNotInList(longIds).stream()
                .map(TsidGenerator::fromLongToString)
                .toList();
    }
}
