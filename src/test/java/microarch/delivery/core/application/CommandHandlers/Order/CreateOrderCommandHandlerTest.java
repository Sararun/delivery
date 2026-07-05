package microarch.delivery.core.application.CommandHandlers.Order;

import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateOrderCommandHandlerTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private UnitOfWork unitOfWork;

    @Test
    void handle_ShouldCreateAndSaveOrder_WhenCommandIsValid() {
        var handler = new CreateOrderCommandHandler(orderRepositoryPort, unitOfWork);
        var command = CreateOrderCommand.create(UUID.randomUUID(), "Russia", "Moscow", "Tverskaya", "1", "1", 5)
                .getValue();

        var result = handler.handle(command);

        assertTrue(result.isSuccess());
        verify(orderRepositoryPort).add(result.getValue());
        verify(unitOfWork).commit();
    }
}
