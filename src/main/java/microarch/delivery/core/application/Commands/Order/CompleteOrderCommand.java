package microarch.delivery.core.application.Commands.Order;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteOrderCommand {
    private final UUID orderId;
    private final UUID courierId;

    public static Result<CompleteOrderCommand, Error> create(UUID orderId, UUID courierId) {
        var validation = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                Guard.againstNullOrEmpty(courierId, "courierId"));

        if (validation != null) {
            return Result.failure(validation);
        }

        return Result.success(new CompleteOrderCommand(orderId, courierId));
    }
}
