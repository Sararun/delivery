package microarch.delivery.core.domain.model.kernel.Courier;

import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CourierTest {

    private static final String NAME = "Ivan";
    private static final Location LOCATION = Location.create(5, 5).getValue();

    private static Order makeOrder(int volume) {
        return Order.create(UUID.randomUUID(), LOCATION, Volume.create(volume).getValue()).getValue();
    }

    private static Courier makeCourier() {
        return Courier.create(NAME, LOCATION).getValue();
    }

    @Test
    void create_ShouldSucceed_WhenAllParamsAreValid() {
        var result = Courier.create(NAME, LOCATION);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
    }

    @Test
    void create_ShouldFail_WhenNameIsNull() {
        var result = Courier.create(null, LOCATION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenNameIsEmpty() {
        var result = Courier.create("", LOCATION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenLocationIsNull() {
        var result = Courier.create(NAME, null);

        assertTrue(result.isFailure());
    }

    @Test
    void addOrder_ShouldSucceed_WhenVolumeIsWithinLimit() {
        var courier = makeCourier();
        var order = makeOrder(10);

        var result = courier.addOrder(order);

        assertTrue(result.isSuccess());
    }

    @Test
    void addOrder_ShouldSucceed_WhenVolumeIsExactlyAtLimit() {
        var courier = makeCourier();
        var order = makeOrder(20);

        var result = courier.addOrder(order);

        assertTrue(result.isSuccess());
    }

    @Test
    void addOrder_ShouldFail_WhenVolumeExceedsLimit() {
        var courier = makeCourier();
        var order = makeOrder(21);

        var result = courier.addOrder(order);

        assertTrue(result.isFailure());
    }

    @Test
    void addOrder_ShouldFail_WhenTotalVolumeExceedsLimit() {
        var courier = makeCourier();
        courier.addOrder(makeOrder(15));

        var result = courier.addOrder(makeOrder(10));

        assertTrue(result.isFailure());
    }

    @Test
    void addOrder_ShouldFail_WhenOrderIsNull() {
        var courier = makeCourier();

        var result = courier.addOrder(null);

        assertTrue(result.isFailure());
    }

    @Test
    void completeAssignment_ShouldSucceed_WhenCourierIsAtOrderLocation() {
        var courier = makeCourier();
        var order = makeOrder(5);
        courier.addOrder(order);

        var result = courier.completeAssignment(order.getId());

        assertTrue(result.isSuccess());
    }

    @Test
    void completeAssignment_ShouldFail_WhenOrderIdIsNull() {
        var courier = makeCourier();

        var result = courier.completeAssignment(null);

        assertTrue(result.isFailure());
    }

    @Test
    void completeAssignment_ShouldFail_WhenAssignmentNotFound() {
        var courier = makeCourier();

        var result = courier.completeAssignment(UUID.randomUUID());

        assertTrue(result.isFailure());
    }

    @Test
    void completeAssignment_ShouldFail_WhenCourierIsTooFar() {
        var orderLocation = Location.create(1, 1).getValue();
        var order = Order.create(UUID.randomUUID(), orderLocation, Volume.create(5).getValue()).getValue();
        var courier = Courier.create(NAME, Location.create(5, 5).getValue()).getValue();
        courier.addOrder(order);

        var result = courier.completeAssignment(order.getId());

        assertTrue(result.isFailure());
    }

    @Test
    void move_ShouldUpdateLocation_WhenLocationIsValid() {
        var courier = makeCourier();
        var newLocation = Location.create(6, 5).getValue();

        courier.move(newLocation);

        assertEquals(newLocation, courier.getCurrentLocation());
    }

    @Test
    void move_ShouldThrow_WhenLocationIsNull() {
        var courier = makeCourier();

        assertThrows(NullPointerException.class, () -> courier.move(null));
    }

    @Test
    void canAccept_ShouldReturnTrue_WhenCourierHasCapacity() {
        var courier = makeCourier();
        var order = makeOrder(10);

        assertTrue(courier.canAccept(order));
    }

    @Test
    void canAccept_ShouldReturnTrue_WhenVolumeExactlyFits() {
        var courier = makeCourier();
        var order = makeOrder(20);

        assertTrue(courier.canAccept(order));
    }

    @Test
    void canAccept_ShouldReturnFalse_WhenCourierIsFull() {
        var courier = makeCourier();
        courier.addOrder(makeOrder(20));

        assertFalse(courier.canAccept(makeOrder(1)));
    }

    @Test
    void canAccept_ShouldReturnFalse_WhenOrderIsNull() {
        var courier = makeCourier();

        assertFalse(courier.canAccept(null));
    }
}
