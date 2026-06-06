package microarch.delivery.core.domain.model.kernel.Order;

import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Location LOCATION = Location.create(5, 5).getValue();
    private static final Volume VOLUME = Volume.create(5).getValue();

    private static Order makeOrder() {
        return Order.create(ID, LOCATION, VOLUME).getValue();
    }

    @Test
    void create_ShouldSucceed_WhenAllParamsAreValid() {
        var result = Order.create(ID, LOCATION, VOLUME);

        assertTrue(result.isSuccess());
        var order = result.getValue();
        assertEquals(ID, order.getId());
        assertEquals(LOCATION, order.getLocation());
        assertEquals(VOLUME, order.getVolume());
        assertEquals(Status.Created, order.getStatus());
    }

    @Test
    void create_ShouldFail_WhenIdIsNull() {
        var result = Order.create(null, LOCATION, VOLUME);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenLocationIsNull() {
        var result = Order.create(ID, null, VOLUME);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenVolumeIsNull() {
        var result = Order.create(ID, LOCATION, null);

        assertTrue(result.isFailure());
    }

    @Test
    void assign_ShouldSucceed_WhenStatusIsCreated() {
        var order = makeOrder();

        var result = order.assign();

        assertTrue(result.isSuccess());
        assertEquals(Status.Assigned, order.getStatus());
    }

    @Test
    void assign_ShouldFail_WhenStatusIsAlreadyAssigned() {
        var order = makeOrder();
        order.assign();

        var result = order.assign();

        assertTrue(result.isFailure());
        assertEquals(Status.Assigned, order.getStatus());
    }

    @Test
    void assign_ShouldFail_WhenStatusIsCompleted() {
        var order = makeOrder();
        order.assign();
        order.finish();

        var result = order.assign();

        assertTrue(result.isFailure());
    }

    @Test
    void finish_ShouldSucceed_WhenStatusIsAssigned() {
        var order = makeOrder();
        order.assign();

        var result = order.finish();

        assertTrue(result.isSuccess());
        assertEquals(Status.Completed, order.getStatus());
    }

    @Test
    void finish_ShouldFail_WhenStatusIsCreated() {
        var order = makeOrder();

        var result = order.finish();

        assertTrue(result.isFailure());
        assertEquals(Status.Created, order.getStatus());
    }

    @Test
    void finish_ShouldFail_WhenStatusIsAlreadyCompleted() {
        var order = makeOrder();
        order.assign();
        order.finish();

        var result = order.finish();

        assertTrue(result.isFailure());
    }
}
