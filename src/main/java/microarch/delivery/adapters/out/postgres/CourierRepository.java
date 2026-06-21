package microarch.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.ports.CourierRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourierRepository implements CourierRepositoryPort {

    private final CourierJpaRepositoryContract courierJpaRepository;

    @Override
    @Transactional
    public void add(Courier courier) {
        courierJpaRepository.save(courier);
    }

    @Override
    @Transactional
    public void update(Courier courier) {
        courierJpaRepository.save(courier);
    }

    @Override
    public Optional<Courier> getById(UUID courierId) {
        return courierJpaRepository.findById(courierId);
    }

    @Override
    public List<Courier> getAll() {
        return courierJpaRepository.findAll();
    }
}
