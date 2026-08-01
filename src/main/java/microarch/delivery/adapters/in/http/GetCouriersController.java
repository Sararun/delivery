package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.core.application.QueryHandlers.Courier.GetAllCouriersQueryHandler;
import microarch.delivery.core.application.Queries.Courier.GetAllCouriersQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {

    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;

    @Override
    public ResponseEntity<java.util.List<Courier>> getCouriers() {
        var couriers = this.getAllCouriersQueryHandler.handle(GetAllCouriersQuery.create()).stream()
                .map(dto -> new Courier(dto.id(), dto.name(),
                        new Location(dto.location().getX_Horizontal(), dto.location().getY_Vertical())))
                .toList();

        return ResponseEntity.ok(couriers);
    }
}
