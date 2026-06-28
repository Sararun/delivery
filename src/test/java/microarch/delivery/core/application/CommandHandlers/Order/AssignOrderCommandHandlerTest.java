package microarch.delivery.core.application.CommandHandlers.Order;

import microarch.delivery.core.application.Commands.Order.AssignOrderCommand;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.service.DistributionOrdersToCouriersService;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignOrderCommandHandlerTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private CourierRepositoryPort courierRepositoryPort;

    @Mock
    private UnitOfWork unitOfWork;

    private final DistributionOrdersToCouriersService distributionService = new DistributionOrdersToCouriersService();

    @Test
    void handle_ShouldAssignOrderToNearestCourier_AndCommitUnitOfWork() {
        var order = Order.create(UUID.randomUUID(), Location.create(5, 5).getValue(), Volume.create(5).getValue())
                .getValue();
        var courier = Courier.create("Ivan", Location.create(5, 6).getValue()).getValue();
        when(orderRepositoryPort.getFirstInCreatedStatus()).thenReturn(Optional.of(order));
        when(courierRepositoryPort.getAll()).thenReturn(List.of(courier));
        var handler = new AssignOrderCommandHandler(orderRepositoryPort, courierRepositoryPort, distributionService,
                unitOfWork);

        var result = handler.handle(AssignOrderCommand.INSTANCE);

        assertTrue(result.isSuccess());
        assertEquals(Status.Assigned, order.getStatus());
        verify(unitOfWork).commit();
    }

    @Test
    void handle_ShouldFail_WhenNoUnassignedOrderExists() {
        when(orderRepositoryPort.getFirstInCreatedStatus()).thenReturn(Optional.empty());
        var handler = new AssignOrderCommandHandler(orderRepositoryPort, courierRepositoryPort, distributionService,
                unitOfWork);

        var result = handler.handle(AssignOrderCommand.INSTANCE);

        assertTrue(result.isFailure());
        verify(courierRepositoryPort, never()).getAll();
        verify(unitOfWork, never()).commit();
    }

    @Test
    void handle_ShouldFail_WhenNoCourierCanAcceptOrder() {
        var order = Order.create(UUID.randomUUID(), Location.create(5, 5).getValue(), Volume.create(5).getValue())
                .getValue();
        when(orderRepositoryPort.getFirstInCreatedStatus()).thenReturn(Optional.of(order));
        when(courierRepositoryPort.getAll()).thenReturn(List.of());
        var handler = new AssignOrderCommandHandler(orderRepositoryPort, courierRepositoryPort, distributionService,
                unitOfWork);

        var result = handler.handle(AssignOrderCommand.INSTANCE);

        assertTrue(result.isFailure());
        verify(unitOfWork, never()).commit();
    }
}
