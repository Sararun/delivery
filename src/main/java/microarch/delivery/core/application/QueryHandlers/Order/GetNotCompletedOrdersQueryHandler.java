package microarch.delivery.core.application.QueryHandlers.Order;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Queries.Order.GetNotCompletedOrdersQuery;
import microarch.delivery.core.application.Queries.Order.OrderDto;
import microarch.delivery.core.ports.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandler {

    private final OrderRepositoryPort orderRepository;

    @Transactional(readOnly = true)
    public List<OrderDto> handle(GetNotCompletedOrdersQuery query) {
        return orderRepository.getAllNotCompleted().stream().map(o -> new OrderDto(o.getId(), o.getLocation()))
                .toList();
    }
}
