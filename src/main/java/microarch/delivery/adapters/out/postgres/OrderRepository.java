package microarch.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import microarch.delivery.core.ports.OrderRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepository implements OrderRepositoryPort {

    private final OrderJpaRepositoryContract orderJpaRepository;

    @Override
    @Transactional
    public void add(Order order) {
        orderJpaRepository.save(order);
    }

    @Override
    @Transactional
    public void update(Order order) {
        orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> getById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<Order> getFirstInCreatedStatus() {
        return orderJpaRepository.findFirstByStatus(Status.Created);
    }

    @Override
    public List<Order> getAllInAssignedStatus() {
        return orderJpaRepository.findAllByStatus(Status.Assigned);
    }
}
