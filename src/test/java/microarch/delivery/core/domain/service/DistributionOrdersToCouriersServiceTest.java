package microarch.delivery.core.domain.service;

import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import microarch.delivery.core.domain.model.kernel.Volume;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DistributionOrdersToCouriersServiceTest {

    private final DistributionOrdersToCouriersService service = new DistributionOrdersToCouriersService();

    private static final Location ORDER_LOCATION = Location.create(5, 5).getValue();
    private static final Volume SMALL_VOLUME = Volume.create(5).getValue();

    private static Order makeOrder() {
        return Order.create(UUID.randomUUID(), ORDER_LOCATION, SMALL_VOLUME).getValue();
    }

    private static Courier makeCourier(int x, int y) {
        return Courier.create("Courier", Location.create(x, y).getValue()).getValue();
    }

    private static Courier makeFullCourier() {
        var courier = makeCourier(1, 1);
        courier.addOrder(Order.create(UUID.randomUUID(), ORDER_LOCATION, Volume.create(20).getValue()).getValue());
        return courier;
    }

    @Test
    void handle_ShouldReturnNearestCourier_WhenMultipleCouriersAvailable() {
        var nearCourier = makeCourier(5, 6);
        var farCourier = makeCourier(1, 1);
        var order = makeOrder();

        var result = service.handle(order, List.of(farCourier, nearCourier));

        assertTrue(result.isSuccess());
        assertEquals(nearCourier, result.getValue());
    }

    @Test
    void handle_ShouldAssignOrderToCourier_WhenWinnerFound() {
        var courier = makeCourier(5, 5);
        var order = makeOrder();

        service.handle(order, List.of(courier));

        assertEquals(Status.Assigned, order.getStatus());
    }

    @Test
    void handle_ShouldFail_WhenAllCouriersAreFull() {
        var order = makeOrder();

        var result = service.handle(order, List.of(makeFullCourier(), makeFullCourier()));

        assertTrue(result.isFailure());
    }

    @Test
    void handle_ShouldFail_WhenCouriersListIsEmpty() {
        var order = makeOrder();

        var result = service.handle(order, List.of());

        assertTrue(result.isFailure());
    }

    @Test
    void handle_ShouldFail_WhenOrderStatusIsNotCreated() {
        var courier = makeCourier(5, 5);
        var order = makeOrder();
        order.assign();

        var result = service.handle(order, List.of(courier));

        assertTrue(result.isFailure());
    }

    @Test
    void handle_ShouldSkipFullCouriers_AndAssignToAvailable() {
        var fullCourier = makeFullCourier();
        var availableCourier = makeCourier(1, 1);
        var order = makeOrder();

        var result = service.handle(order, List.of(fullCourier, availableCourier));

        assertTrue(result.isSuccess());
        assertEquals(availableCourier, result.getValue());
    }

    @Test
    void handle_ShouldThrow_WhenOrderIsNull() {
        assertThrows(NullPointerException.class, () -> service.handle(null, List.of(makeCourier(1, 1))));
    }

    @Test
    void handle_ShouldThrow_WhenCouriersIsNull() {
        assertThrows(NullPointerException.class, () -> service.handle(makeOrder(), null));
    }
}
