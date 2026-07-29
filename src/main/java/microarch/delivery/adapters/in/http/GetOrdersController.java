package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.QueryHandlers.Order.GetNotCompletedOrdersQueryHandler;
import microarch.delivery.core.application.Queries.Order.GetNotCompletedOrdersQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {

    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

    @Override
    public ResponseEntity<java.util.List<Order>> getOrders() {
        var orders = this.getNotCompletedOrdersQueryHandler.handle(GetNotCompletedOrdersQuery.create()).stream()
                .map(dto -> new Order(dto.id(),
                        new Location(dto.location().getX_Horizontal(), dto.location().getY_Vertical())))
                .toList();

        return ResponseEntity.ok(orders);
    }
}
