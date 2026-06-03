package microarch.delivery.core.domain.model.kernel.Order;

import com.github.f4b6a3.uuid.UuidCreator;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.kernel.Courier.Assignment;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends Aggregate<UUID> {
    private UUID id;
    private Location location;
    private Volume volume;
    private Status status;

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        Error error = Guard.combine(Guard.againstNullOrEmpty(id, "id"),
                location == null ? GeneralErrors.valueIsRequired("location") : null,
                volume == null ? GeneralErrors.valueIsRequired("volume") : null);

        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(new Order(id, location, volume, Status.Created));
    }

    public UnitResult<Error> assign() {
        if (status != Status.Created) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("status", status));
        }
        this.status = Status.Assigned;
        return UnitResult.success();
    }

    public UnitResult<Error> finish() {
        if (status != Status.Assigned) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("status", status));
        }
        this.status = Status.Completed;
        return UnitResult.success();
    }
}
