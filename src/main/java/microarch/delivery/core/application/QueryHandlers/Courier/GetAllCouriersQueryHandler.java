package microarch.delivery.core.application.QueryHandlers.Courier;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Queries.Courier.CourierDto;
import microarch.delivery.core.application.Queries.Courier.GetAllCouriersQuery;
import microarch.delivery.core.ports.CourierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCouriersQueryHandler {

    private final CourierRepositoryPort courierRepository;

    @Transactional(readOnly = true)
    public List<CourierDto> handle(GetAllCouriersQuery query) {
        return courierRepository.getAll().stream()
                .map(c -> new CourierDto(c.getId(), c.getName(), c.getCurrentLocation())).toList();
    }
}
