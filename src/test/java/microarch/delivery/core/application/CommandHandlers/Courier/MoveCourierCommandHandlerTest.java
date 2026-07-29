package microarch.delivery.core.application.CommandHandlers.Courier;

import microarch.delivery.core.application.Commands.Courier.MoveCourierCommand;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoveCourierCommandHandlerTest {

    @Mock
    private CourierRepositoryPort courierRepository;

    @Mock
    private UnitOfWork unitOfWork;

    @Test
    void handle_ShouldMoveCourier_AndCommitUnitOfWork() {
        var courier = Courier.create("Ivan", Location.create(1, 1).getValue()).getValue();
        when(courierRepository.getById(courier.getId())).thenReturn(Optional.of(courier));
        var newLocation = Location.create(5, 5).getValue();
        var command = MoveCourierCommand.create(courier.getId(), 5, 5).getValue();
        var handler = new MoveCourierCommandHandler(courierRepository, unitOfWork);

        var result = handler.handle(command);

        assertTrue(result.isSuccess());
        assertEquals(newLocation, courier.getCurrentLocation());
        verify(unitOfWork).commit();
    }

    @Test
    void handle_ShouldFail_WhenCourierNotFound() {
        var courierId = UUID.randomUUID();
        when(courierRepository.getById(courierId)).thenReturn(Optional.empty());
        var command = MoveCourierCommand.create(courierId, 5, 5).getValue();
        var handler = new MoveCourierCommandHandler(courierRepository, unitOfWork);

        var result = handler.handle(command);

        assertTrue(result.isFailure());
        verify(unitOfWork, never()).commit();
    }
}
