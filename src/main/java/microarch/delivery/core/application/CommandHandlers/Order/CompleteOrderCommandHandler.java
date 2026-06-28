package microarch.delivery.core.application.CommandHandlers.Order;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;

import microarch.delivery.core.application.Commands.Order.CompleteOrderCommand;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.OrderRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompleteOrderCommandHandler {
    private final CourierRepositoryPort courierRepository;
    private final OrderRepositoryPort orderRepository;
    private final UnitOfWork unitOfWork;

    @Transactional
    public UnitResult<Error> handle(CompleteOrderCommand completeOrderCommand) {
        var courierRes = this.courierRepository.getById(completeOrderCommand.getCourierId());
        if (courierRes.isEmpty()) {
            return UnitResult.failure(Error.of("404", "Courier Not Found"));
        }
        var orderRes = this.orderRepository.getById(completeOrderCommand.getOrderId());
        if (orderRes.isEmpty()) {
            return UnitResult.failure(Error.of("404", "Order Not Found"));
        }
        var courier = courierRes.get();
        var order = orderRes.get();
        var completeAssigmentRes = courier.completeAssignment(order.getId());
        this.unitOfWork.commit();
        return completeAssigmentRes;
    }
}
