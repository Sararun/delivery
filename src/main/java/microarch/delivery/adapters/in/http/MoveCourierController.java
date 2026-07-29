package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.MoveCourierApi;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.core.application.CommandHandlers.Courier.MoveCourierCommandHandler;
import microarch.delivery.core.application.Commands.Courier.MoveCourierCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MoveCourierController implements MoveCourierApi {

    private final MoveCourierCommandHandler moveCourierCommandHandler;

    @Override
    public ResponseEntity<Void> moveCourier(UUID courierId, Location location) {
        var commandRes = MoveCourierCommand.create(courierId, location.getX(), location.getY());
        if (commandRes.isFailure()) {
            return ErrorResponseMapper.toResponse(commandRes.getError());
        }

        var result = this.moveCourierCommandHandler.handle(commandRes.getValue());
        if (result.isFailure()) {
            return ErrorResponseMapper.toResponse(result.getError());
        }

        return ResponseEntity.ok().build();
    }
}
