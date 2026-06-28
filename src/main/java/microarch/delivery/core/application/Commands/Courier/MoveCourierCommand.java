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

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Location location) {
        var validation = Guard.combine(Guard.againstNullOrEmpty(courierId, "courierId"),
                Guard.againstNull(location, "location"));

        if (validation != null) {
            return Result.failure(validation);
        }

        return Result.success(new MoveCourierCommand(courierId, location));
    }

}
