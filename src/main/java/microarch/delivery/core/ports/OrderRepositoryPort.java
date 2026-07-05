package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.kernel.Order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    void add(Order order);

    Optional<Order> getById(UUID orderId);

    Optional<Order> getFirstInCreatedStatus();

    List<Order> getAllInAssignedStatus();

    List<Order> getAllNotCompleted();
}
