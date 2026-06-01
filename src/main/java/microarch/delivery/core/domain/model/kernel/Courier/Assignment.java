package microarch.delivery.core.domain.model.kernel.Courier;

import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.kernel.Location;

import com.github.f4b6a3.uuid.UuidCreator;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Assignment extends BaseEntity<UUID> {

    private UUID uuid;
    private UUID orderId;
    private Volume volume;
    private Location location;
    private Status status;

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Error error = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                volume == null ? GeneralErrors.valueIsRequired("volume") : null,
                location == null ? GeneralErrors.valueIsRequired("location") : null);
        if (error != null) {
            return Result.failure(error);
        }

        return Result
                .success(new Assignment(UuidCreator.getTimeOrderedEpoch(), orderId, volume, location, Status.Assigned));
    }

    public UnitResult<Error> finish(Location location) {
        if (!this.location.isNear(location)) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("location", location));
        }
        this.status = Status.Completed;
        return UnitResult.success();
    }

}
