package microarch.delivery.core.application.QueryHandlers.Order;

import microarch.delivery.core.application.Queries.Order.GetNotCompletedOrdersQuery;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.OrderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotCompletedOrdersQueryHandlerTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Test
    void handle_ShouldReturnNotCompletedOrdersAsDtos() {
        var location = Location.create(2, 4).getValue();
        var volume = Volume.create(5).getValue();
        var orderId = UUID.randomUUID();
        var order = Order.create(orderId, location, volume).getValue();
        when(orderRepository.getAllNotCompleted()).thenReturn(List.of(order));

        var handler = new GetNotCompletedOrdersQueryHandler(orderRepository);
        var result = handler.handle(GetNotCompletedOrdersQuery.create());

        assertEquals(1, result.size());
        assertEquals(orderId, result.getFirst().id());
        assertEquals(location, result.getFirst().location());
    }

    @Test
    void handle_ShouldReturnEmptyList_WhenAllOrdersCompleted() {
        when(orderRepository.getAllNotCompleted()).thenReturn(List.of());

        var handler = new GetNotCompletedOrdersQueryHandler(orderRepository);
        var result = handler.handle(GetNotCompletedOrdersQuery.create());

        assertTrue(result.isEmpty());
    }
}
