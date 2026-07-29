package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CompleteOrderApi;
import microarch.delivery.core.application.CommandHandlers.Order.CompleteOrderCommandHandler;
import microarch.delivery.core.application.Commands.Order.CompleteOrderCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompleteOrderController implements CompleteOrderApi {

    private final CompleteOrderCommandHandler completeOrderCommandHandler;

    @Override
    public ResponseEntity<Void> completeOrder(UUID courierId, UUID orderId) {
        var commandRes = CompleteOrderCommand.create(orderId, courierId);
        if (commandRes.isFailure()) {
            return ErrorResponseMapper.toResponse(commandRes.getError());
        }

        var result = this.completeOrderCommandHandler.handle(commandRes.getValue());
        if (result.isFailure()) {
            return ErrorResponseMapper.toResponse(result.getError());
        }

        return ResponseEntity.ok().build();
    }
}
