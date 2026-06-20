package microarch.delivery.core.domain.model.kernel.Order;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {
    @Embedded
    private Location location;

    @Embedded
    private Volume volume;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Order(UUID id, Location location, Volume volume, Status status) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = status;
    }

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
