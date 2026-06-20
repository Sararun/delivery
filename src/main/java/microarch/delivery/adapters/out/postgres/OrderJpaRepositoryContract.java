package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepositoryContract extends JpaRepository<Order, UUID> {
    Optional<Order> findFirstByStatus(Status status);

    List<Order> findAllByStatus(Status status);
}
