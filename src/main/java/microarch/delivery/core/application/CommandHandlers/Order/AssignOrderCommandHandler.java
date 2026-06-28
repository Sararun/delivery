package microarch.delivery.core.application.CommandHandlers.Order;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Commands.Order.AssignOrderCommand;
import microarch.delivery.core.domain.service.DistributionOrdersToCouriersService;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignOrderCommandHandler {
    private final OrderRepositoryPort orderRepositoryPort;
    private final CourierRepositoryPort courierRepositoryPort;
    private final DistributionOrdersToCouriersService distributionService;
    private final UnitOfWork unitOfWork;

    @Transactional
    public UnitResult<Error> handle(AssignOrderCommand command) {
        var orderOpt = this.orderRepositoryPort.getFirstInCreatedStatus();
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(Error.of("404", "Order Not Found"));
        }
        var order = orderOpt.get();

        var couriers = this.courierRepositoryPort.getAll();
        var distributionRes = this.distributionService.handle(order, couriers);
        if (distributionRes.isFailure()) {
            return UnitResult.failure(distributionRes.getError());
        }

        this.unitOfWork.commit();
        return UnitResult.success();
    }
}
