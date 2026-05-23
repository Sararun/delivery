package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    @Test
    void create_ShouldReturnLocation_WhenValuesAreValid() {
        var result = Location.create(2, 3);
        assertTrue(result.isSuccess());
    }

    @Test
    void create_ShouldFail_WhenXIsLessThanMin() {
        var result = Location.create(0, 5);
        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenXIsGreaterThanMax() {
        var result = Location.create(11, 5);
        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenYIsLessThanMin() {
        var result = Location.create(5, 0);
        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenYIsGreaterThanMax() {
        var result = Location.create(5, 11);
        assertTrue(result.isFailure());
    }

    @Test
    void calculateDistance_ShouldReturnCorrectDistance() {
        var from = Location.create(1, 1).getValue();
        var to = Location.create(3, 4).getValue();

        assertEquals(5, from.calculateDistance(to));
    }

    @Test
    void calculateDistance_ShouldBeSymmetric() {
        var a = Location.create(1, 1).getValue();
        var b = Location.create(3, 4).getValue();

        assertEquals(a.calculateDistance(b), b.calculateDistance(a));
    }

    @Test
    void calculateDistance_ShouldReturnZero_WhenSameLocation() {
        var location = Location.create(5, 5).getValue();

        assertEquals(0, location.calculateDistance(location));
    }

    @Test
    void equals_ShouldReturnTrue_WhenXAndYAreEqual() {
        var a = Location.create(3, 7).getValue();
        var b = Location.create(3, 7).getValue();

        assertEquals(a, b);
    }

    @Test
    void equals_ShouldReturnFalse_WhenXDiffers() {
        var a = Location.create(3, 7).getValue();
        var b = Location.create(4, 7).getValue();

        assertNotEquals(a, b);
    }

    @Test
    void equals_ShouldReturnFalse_WhenYDiffers() {
        var a = Location.create(3, 7).getValue();
        var b = Location.create(3, 8).getValue();

        assertNotEquals(a, b);
    }
}
