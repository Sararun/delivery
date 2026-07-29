package microarch.delivery.core.application.CommandHandlers.Order;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.GeoPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderCommandHandlerTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private GeoPort geoPort;

    @Test
    void handle_ShouldCreateAndSaveOrder_WhenCommandIsValid() {
        var handler = new CreateOrderCommandHandler(orderRepositoryPort, unitOfWork, geoPort);
        var command = CreateOrderCommand.create(UUID.randomUUID(), "Russia", "Moscow", "Tverskaya", "1", "1", 5)
                .getValue();
        var geoLocation = Location.create(2, 3).getValue();
        when(geoPort.getLocation(command.getStreet())).thenReturn(Result.success(geoLocation));

        var result = handler.handle(command);

        assertTrue(result.isSuccess());
        assertEquals(geoLocation, result.getValue().getLocation());
        verify(geoPort).getLocation("Tverskaya");
        verify(orderRepositoryPort).add(result.getValue());
        verify(unitOfWork).commit();
    }

    @Test
    void handle_ShouldReturnFailureAndNotSaveOrder_WhenGeoServiceFails() {
        var handler = new CreateOrderCommandHandler(orderRepositoryPort, unitOfWork, geoPort);
        var command = CreateOrderCommand.create(UUID.randomUUID(), "Russia", "Moscow", "Tverskaya", "1", "1", 5)
                .getValue();
        var geoError = Error.of("geo.service.unavailable", "Geo service is unavailable");
        when(geoPort.getLocation(command.getStreet())).thenReturn(Result.failure(geoError));

        var result = handler.handle(command);

        assertTrue(result.isFailure());
        assertEquals(geoError, result.getError());
        verifyNoInteractions(orderRepositoryPort, unitOfWork);
    }
}
