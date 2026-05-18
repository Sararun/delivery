package microarch.delivery.core.domain.model.kernel.Courier;

import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final Volume VOLUME = Volume.create(5).getValue();
    private static final Location LOCATION = Location.create(5, 5).getValue();

    @Test
    void create_ShouldSucceed_WhenAllParamsAreValid() {
        var result = Assignment.create(ORDER_ID, VOLUME, LOCATION);

        assertTrue(result.isSuccess());
        var assignment = result.getValue();
        assertNotNull(assignment.getUuid());
        assertEquals(ORDER_ID, assignment.getOrderId());
        assertEquals(VOLUME, assignment.getVolume());
        assertEquals(LOCATION, assignment.getLocation());
        assertEquals(Status.Assigned, assignment.getStatus());
    }

    @Test
    void create_ShouldFail_WhenOrderIdIsNull() {
        var result = Assignment.create(null, VOLUME, LOCATION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenVolumeIsNull() {
        var result = Assignment.create(ORDER_ID, null, LOCATION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenLocationIsNull() {
        var result = Assignment.create(ORDER_ID, VOLUME, null);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldAlwaysHaveAssignedStatus() {
        var assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();

        assertEquals(Status.Assigned, assignment.getStatus());
    }

    @Test
    void create_ShouldGenerateUniqueIds() {
        var first = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        var second = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();

        assertNotEquals(first.getUuid(), second.getUuid());
    }

    @Test
    void finish_ShouldSucceed_WhenCourierIsAtSameLocation() {
        var assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();

        var result = assignment.finish(LOCATION);

        assertTrue(result.isSuccess());
        assertEquals(Status.Completed, assignment.getStatus());
    }

    @Test
    void finish_ShouldSucceed_WhenCourierIsOneStepAway() {
        var assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        var nearLocation = Location.create(5, 6).getValue();

        var result = assignment.finish(nearLocation);

        assertTrue(result.isSuccess());
        assertEquals(Status.Completed, assignment.getStatus());
    }

    @Test
    void finish_ShouldFail_WhenCourierIsTooFarAway() {
        var assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        var farLocation = Location.create(1, 1).getValue();

        var result = assignment.finish(farLocation);

        assertTrue(result.isFailure());
        assertEquals(Status.Assigned, assignment.getStatus());
    }
}
