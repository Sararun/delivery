package microarch.delivery.core.application.Commands.Courier;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.kernel.Location;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCourierCommand {
    private final UUID courierId;
    private final Location location;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, int x, int y) {
        var courierValidation = Guard.againstNullOrEmpty(courierId, "courierId");
        if (courierValidation != null) {
            return Result.failure(courierValidation);
        }

        var locationRes = Location.create(x, y);
        if (locationRes.isFailure()) {
            return Result.failure(locationRes.getError());
        }

        return Result.success(new MoveCourierCommand(courierId, locationRes.getValue()));
    }

}
