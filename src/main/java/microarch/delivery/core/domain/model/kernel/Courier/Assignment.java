package microarch.delivery.core.domain.model.kernel.Courier;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;

import com.github.f4b6a3.uuid.UuidCreator;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.UUID;

@Getter
@Entity
@Table(name = "assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id")
    private UUID orderId;

    @Embedded
    private Volume volume;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location, Status status) {
        super(id);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public UUID getUuid() {
        return getId();
    }

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
