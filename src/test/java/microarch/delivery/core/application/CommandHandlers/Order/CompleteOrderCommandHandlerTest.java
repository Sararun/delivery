package microarch.delivery.core.application.CommandHandlers.Order;

import microarch.delivery.core.application.Commands.Order.CompleteOrderCommand;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteOrderCommandHandlerTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private CourierRepositoryPort courierRepository;

    @Mock
    private UnitOfWork unitOfWork;

    @Test
    void handle_ShouldCompleteAssignment_WhenCourierIsNearOrder() {
        var location = Location.create(1, 1).getValue();
        var order = Order.create(UUID.randomUUID(), location, Volume.create(3).getValue()).getValue();
        var courier = Courier.create("Ivan", location).getValue();
        courier.addOrder(order);
        order.assign();

        var command = CompleteOrderCommand.create(order.getId(), courier.getId()).getValue();
        when(courierRepository.getById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.getById(order.getId())).thenReturn(Optional.of(order));

        var handler = new CompleteOrderCommandHandler(courierRepository, orderRepository, unitOfWork);
        var result = handler.handle(command);

        assertTrue(result.isSuccess());
        verify(unitOfWork).commit();
    }

    @Test
    void handle_ShouldFail_WhenCourierNotFound() {
        var courierId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var command = CompleteOrderCommand.create(orderId, courierId).getValue();
        when(courierRepository.getById(courierId)).thenReturn(Optional.empty());

        var handler = new CompleteOrderCommandHandler(courierRepository, orderRepository, unitOfWork);
        var result = handler.handle(command);

        assertTrue(result.isFailure());
        verify(unitOfWork, never()).commit();
    }

    @Test
    void handle_ShouldFail_WhenOrderNotFound() {
        var location = Location.create(2, 2).getValue();
        var courier = Courier.create("Maria", location).getValue();
        var orderId = UUID.randomUUID();
        var command = CompleteOrderCommand.create(orderId, courier.getId()).getValue();
        when(courierRepository.getById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.getById(orderId)).thenReturn(Optional.empty());

        var handler = new CompleteOrderCommandHandler(courierRepository, orderRepository, unitOfWork);
        var result = handler.handle(command);

        assertTrue(result.isFailure());
        verify(unitOfWork, never()).commit();
    }
}
