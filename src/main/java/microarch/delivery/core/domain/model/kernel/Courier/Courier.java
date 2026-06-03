package microarch.delivery.core.domain.model.kernel.Courier;

import com.github.f4b6a3.uuid.UuidCreator;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Courier extends Aggregate<UUID> {

    private UUID id;
    private String name;
    private Location currentLocation;
    private final Volume maxVolume;
    private List<Assignment> assignments;

    public static Result<Courier, Error> create(String name, Location currentLocation) {
        Error error = Guard.combine(Guard.againstNullOrEmpty(name, "name"),
                currentLocation == null ? GeneralErrors.valueIsRequired("location") : null);
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(new Courier(UuidCreator.getTimeOrderedEpoch(), name, currentLocation,
                Volume.create(20).getValueOrThrow(), new ArrayList<>()));
    }

    public UnitResult<Error> addOrder(Order order) {
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        var totalVolume = assignments.stream().map(Assignment::getVolume).reduce(Volume.zero(), Volume::add);
        var newTotal = totalVolume.add(order.getVolume());

        if (newTotal.isGreaterThan(maxVolume)) {
            return UnitResult
                    .failure(GeneralErrors.valueMustBeLessOrEqual("volume", newTotal.getValue(), maxVolume.getValue()));
        }

        var assignment = Assignment.create(order.getId(), order.getVolume(), order.getLocation());
        if (assignment.isFailure())
            return UnitResult.failure(assignment.getError());

        assignments.add(assignment.getValue());
        return UnitResult.success();
    }

    public UnitResult<Error> completeAssignment(UUID orderId) {
        var error = Guard.againstNullOrEmpty(orderId, "orderId");
        if (error != null) {
            return UnitResult.failure(error);
        }

        var assignment = assignments.stream().filter(a -> a.getOrderId().equals(orderId)).findFirst();

        if (assignment.isEmpty())
            return UnitResult.failure(GeneralErrors.valueIsInvalid("orderId", orderId));

        return assignment.get().finish(currentLocation);
    }

    public void move(Location location) {
        Objects.requireNonNull(location);
        this.currentLocation = location;
    }
}
