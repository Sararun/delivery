package microarch.delivery.core.application.CommandHandlers.Courier;

import microarch.delivery.core.application.Commands.Courier.CreateCourierCommand;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateCourierCommandHandlerTest {

    @Mock
    private CourierRepositoryPort courierRepository;

    @Mock
    private UnitOfWork unitOfWork;

    @Test
    void handle_ShouldCreateAndSaveCourier_WhenCommandIsValid() {
        var handler = new CreateCourierCommandHandler(courierRepository, unitOfWork);
        var command = CreateCourierCommand.create("Ivan").getValue();

        var result = handler.handle(command);

        assertTrue(result.isSuccess());
        assertEquals("Ivan", result.getValue().getName());
        verify(courierRepository).add(result.getValue());
        verify(unitOfWork).commit();
    }
}
