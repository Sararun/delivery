package microarch.delivery.core.domain.model.kernel;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    public static final int X_MIN_VALUE = 1;
    public static final int X_MAX_VALUE = 10;
    public static final int Y_MIN_VALUE = 1;
    public static final int Y_MAX_VALUE = 10;
    private final int X_Horizontal;
    private final int Y_Vertical;

    public static Result<Location, Error> create(int X_Horizontal, int Y_Vertical) {
        if (X_Horizontal < X_MIN_VALUE || X_Horizontal > X_MAX_VALUE) {
            return Result
                    .failure(GeneralErrors.valueIsOutOfRange("X_Horizontal", X_Horizontal, X_MIN_VALUE, X_MAX_VALUE));
        }

        if (Y_Vertical < Y_MIN_VALUE || Y_Vertical > Y_MAX_VALUE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("Y_Vertical", Y_Vertical, Y_MIN_VALUE, Y_MAX_VALUE));
        }
        return Result.success(new Location(X_Horizontal, Y_Vertical));
    }

    public int calculateDistance(Location other) {
        return Math.abs(this.X_Horizontal - other.X_Horizontal) + Math.abs(this.Y_Vertical - other.Y_Vertical);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(X_Horizontal, Y_Vertical);
    }
}
