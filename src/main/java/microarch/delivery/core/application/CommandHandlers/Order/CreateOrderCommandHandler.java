package microarch.delivery.core.application.CommandHandlers.Order;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler {

    private final OrderRepositoryPort orderRepositoryPort;
    private final UnitOfWork unitOfWork;

    @Transactional
    public Result<Order, Error> handle(CreateOrderCommand command) {
        var random = ThreadLocalRandom.current();
        var locationRes = Location.create(random.nextInt(Location.X_MIN_VALUE, Location.X_MAX_VALUE + 1),
                random.nextInt(Location.Y_MIN_VALUE, Location.Y_MAX_VALUE + 1));
        if (locationRes.isFailure()) {
            return Result.failure(locationRes.getError());
        }
        var volumeRes = Volume.create(command.getVolume());
        if (volumeRes.isFailure()) {
            return Result.failure(volumeRes.getError());
        }
        var orderRes = Order.create(command.getOrderId(), locationRes.getValue(), volumeRes.getValue());
        if (orderRes.isFailure()) {
            return Result.failure(orderRes.getError());
        }
        this.orderRepositoryPort.add(orderRes.getValue());
        this.unitOfWork.commit();
        return orderRes;
    }

}
