package microarch.delivery.core.domain.service;

import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import libs.errs.Error;

@Component
public class DistributionOrdersToCouriersService {
    public Result<Courier, Error> handle(Order order, List<Courier> couriers) {
        Objects.requireNonNull(order);
        Objects.requireNonNull(couriers);

        if (order.getStatus() != Status.Created) {
            return Result.failure(GeneralErrors.valueIsInvalid("order", order));
        }

        var nearestCourier = couriers.stream().filter(courier -> courier.canAccept(order)).min(Comparator
                .comparingInt(courier -> courier.getCurrentLocation().calculateDistance(order.getLocation())));

        if (nearestCourier.isEmpty()) {
            return Result.failure(GeneralErrors.valueIsInvalid("couriers", couriers));
        }

        var winner = nearestCourier.get();

        var addResult = winner.addOrder(order);
        if (addResult.isFailure()) {
            return Result.failure(addResult.getError());
        }

        var assignResult = order.assign();
        if (assignResult.isFailure()) {
            return Result.failure(assignResult.getError());
        }

        return Result.success(winner);
    }
}
