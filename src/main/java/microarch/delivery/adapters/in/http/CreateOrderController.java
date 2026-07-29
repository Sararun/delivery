package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.CommandHandlers.Order.CreateOrderCommandHandler;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        var address = newOrder.getAddress();
        var commandRes = CreateOrderCommand.create(newOrder.getId(), address.getCountry(), address.getCity(),
                address.getStreet(), address.getHouse(), address.getApartment(), newOrder.getVolume());
        if (commandRes.isFailure()) {
            return ErrorResponseMapper.toResponse(commandRes.getError());
        }

        var orderRes = this.createOrderCommandHandler.handle(commandRes.getValue());
        if (orderRes.isFailure()) {
            return ErrorResponseMapper.toResponse(orderRes.getError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(orderRes.getValue().getId()));
    }
}
