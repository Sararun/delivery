package microarch.delivery.core.domain.model.kernel.Courier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.github.f4b6a3.uuid.UuidCreator;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "couriers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {

    private String name;

    @Embedded
    private Location currentLocation;

    @Embedded
    private Volume maxVolume;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "courier_id")
    private List<Assignment> assignments;

    private Courier(UUID id, String name, Location currentLocation, Volume maxVolume, List<Assignment> assignments) {
        super(id);
        this.name = name;
        this.currentLocation = currentLocation;
        this.maxVolume = maxVolume;
        this.assignments = assignments;
    }

    public static Result<Courier, Error> create(String name, Location currentLocation) {
        Error error = Guard.combine(Guard.againstNullOrEmpty(name, "name"),
                currentLocation == null ? GeneralErrors.valueIsRequired("location") : null);
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(new Courier(UuidCreator.getTimeOrderedEpoch(), name, currentLocation,
                Volume.create(20).getValueOrThrow(), new ArrayList<>()));
    }

    public boolean canAccept(Order order) {
        if (order == null)
            return false;
        var totalVolume = assignments.stream().map(Assignment::getVolume).reduce(Volume.zero(), Volume::plus);
        return !totalVolume.plus(order.getVolume()).isGreaterThan(maxVolume);
    }

    public UnitResult<Error> addOrder(Order order) {
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        if (!canAccept(order)) {
            var newTotal = assignments.stream().map(Assignment::getVolume).reduce(Volume.zero(), Volume::plus)
                    .plus(order.getVolume());
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
