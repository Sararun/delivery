package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.CourierRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.f4b6a3.uuid.UuidCreator;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CourierRepository.class)
@Testcontainers
class CourierRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourierRepositoryPort courierRepository;

    private static Courier makeCourier() {
        return Courier.create("Ivan", Location.create(5, 5).getValue()).getValue();
    }

    @Test
    void add_ShouldPersistCourier_WhenCalled() {
        var courier = makeCourier();

        courierRepository.add(courier);

        var found = courierRepository.getById(courier.getId());
        assertTrue(found.isPresent());
        assertEquals(courier.getId(), found.get().getId());
        assertEquals("Ivan", found.get().getName());
    }

    @Test
    void getAll_ShouldReturnAllCouriers() {
        courierRepository.add(makeCourier());
        courierRepository.add(makeCourier());

        var result = courierRepository.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void update_ShouldPersistLocationChange() {
        var courier = makeCourier();
        courierRepository.add(courier);

        var newLocation = Location.create(3, 7).getValue();
        courier.move(newLocation);
        courierRepository.update(courier);

        var found = courierRepository.getById(courier.getId());
        assertTrue(found.isPresent());
        assertEquals(newLocation, found.get().getCurrentLocation());
    }

    @Test
    void add_ShouldPersistAssignments_WhenCourierHasOrders() {
        var courier = makeCourier();
        var order = Order.create(UuidCreator.getTimeOrderedEpoch(), Location.create(3, 3).getValue(),
                Volume.create(5).getValue()).getValue();
        courier.addOrder(order);

        courierRepository.add(courier);

        var found = courierRepository.getById(courier.getId());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getAssignments().size());
        assertEquals(order.getId(), found.get().getAssignments().getFirst().getOrderId());
    }
}
