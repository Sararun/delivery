package microarch.delivery.core.application.CommandHandlers.Order;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.GeoPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler {

    private final OrderRepositoryPort orderRepositoryPort;
    private final UnitOfWork unitOfWork;
    private final GeoPort geoPort;

    @Transactional
    public Result<Order, Error> handle(CreateOrderCommand command) {
        var locationRes = geoPort.getLocation(command.getStreet());
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
