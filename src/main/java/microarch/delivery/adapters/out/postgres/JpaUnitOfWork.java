package microarch.delivery.adapters.out.postgres;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaUnitOfWork implements UnitOfWork {

    private final EntityManager entityManager;

    @Override
    public void commit() {
        entityManager.flush();
    }
}
