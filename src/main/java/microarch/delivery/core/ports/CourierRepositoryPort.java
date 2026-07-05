package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.kernel.Courier.Courier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierRepositoryPort {
    void add(Courier courier);

    Optional<Courier> getById(UUID courierId);

    List<Courier> getAll();
}
