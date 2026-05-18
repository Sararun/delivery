package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VolumeTest {

    @Test
    void create_ShouldSucceed_WhenValueIsPositive() {
        var result = Volume.create(5);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getValue().getValue());
    }

    @Test
    void create_ShouldFail_WhenValueIsZero() {
        var result = Volume.create(0);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenValueIsNegative() {
        var result = Volume.create(-1);

        assertTrue(result.isFailure());
    }

    @Test
    void equals_ShouldReturnTrue_WhenValuesAreEqual() {
        var a = Volume.create(5).getValue();
        var b = Volume.create(5).getValue();

        assertEquals(a, b);
    }

    @Test
    void equals_ShouldReturnFalse_WhenValuesAreDifferent() {
        var a = Volume.create(5).getValue();
        var b = Volume.create(10).getValue();

        assertNotEquals(a, b);
    }
}
