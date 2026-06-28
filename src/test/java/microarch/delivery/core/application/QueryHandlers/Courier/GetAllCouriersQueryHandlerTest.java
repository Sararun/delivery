package microarch.delivery.core.application.QueryHandlers.Courier;

import microarch.delivery.core.application.Queries.Courier.GetAllCouriersQuery;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllCouriersQueryHandlerTest {

    @Mock
    private CourierRepositoryPort courierRepository;

    @Test
    void handle_ShouldReturnAllCouriersAsDtos() {
        var location = Location.create(3, 5).getValue();
        var courier = Courier.create("Ivan", location).getValue();
        when(courierRepository.getAll()).thenReturn(List.of(courier));

        var handler = new GetAllCouriersQueryHandler(courierRepository);
        var result = handler.handle(GetAllCouriersQuery.create());

        assertEquals(1, result.size());
        assertEquals(courier.getId(), result.get(0).id());
        assertEquals("Ivan", result.get(0).name());
        assertEquals(location, result.get(0).location());
    }

    @Test
    void handle_ShouldReturnEmptyList_WhenNoCouriers() {
        when(courierRepository.getAll()).thenReturn(List.of());

        var handler = new GetAllCouriersQueryHandler(courierRepository);
        var result = handler.handle(GetAllCouriersQuery.create());

        assertTrue(result.isEmpty());
    }
}
