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
public class Volume extends ValueObject<Volume> {

    private final int value;

    public static Result<Volume, Error> create(int value) {
        if (value <= 0) {
            return Result.failure(GeneralErrors.valueMustBeGreaterThan("value", value, 0));
        }
        return Result.success(new Volume(value));
    }

    public static Volume zero() {
        return new Volume(0);
    }

    public Volume plus(Volume other) {
        return new Volume(this.value + other.value);
    }

    public boolean isGreaterThan(Volume other) {
        return this.value > other.value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }
}
