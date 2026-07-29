package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateCourierApi;
import microarch.delivery.adapters.in.http.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.model.NewCourier;
import microarch.delivery.core.application.CommandHandlers.Courier.CreateCourierCommandHandler;
import microarch.delivery.core.application.Commands.Courier.CreateCourierCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateCourierController implements CreateCourierApi {

    private final CreateCourierCommandHandler createCourierCommandHandler;

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(NewCourier newCourier) {
        var commandRes = CreateCourierCommand.create(newCourier.getName());
        if (commandRes.isFailure()) {
            return ErrorResponseMapper.toResponse(commandRes.getError());
        }

        var courierRes = this.createCourierCommandHandler.handle(commandRes.getValue());
        if (courierRes.isFailure()) {
            return ErrorResponseMapper.toResponse(courierRes.getError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCourierResponse(courierRes.getValue().getId()));
    }
}
