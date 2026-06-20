package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourierJpaRepositoryContract extends JpaRepository<Courier, UUID> {
}
