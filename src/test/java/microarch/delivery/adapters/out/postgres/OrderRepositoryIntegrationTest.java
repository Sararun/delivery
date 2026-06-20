package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import microarch.delivery.core.domain.model.kernel.Order.Status;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.ports.OrderRepositoryPort;
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
@Import(OrderRepository.class)
@Testcontainers
class OrderRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderRepositoryPort orderRepository;

    private static Order makeOrder() {
        return Order.create(UuidCreator.getTimeOrderedEpoch(), Location.create(5, 5).getValue(),
                Volume.create(5).getValue()).getValue();
    }

    @Test
    void add_ShouldPersistOrder_WhenCalled() {
        var order = makeOrder();

        orderRepository.add(order);

        var found = orderRepository.getById(order.getId());
        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getId());
        assertEquals(Status.Created, found.get().getStatus());
    }

    @Test
    void getFirstInCreatedStatus_ShouldReturnOrder_WhenCreatedOrderExists() {
        orderRepository.add(makeOrder());

        var result = orderRepository.getFirstInCreatedStatus();

        assertTrue(result.isPresent());
        assertEquals(Status.Created, result.get().getStatus());
    }

    @Test
    void getFirstInCreatedStatus_ShouldReturnEmpty_WhenNoCreatedOrders() {
        var order = makeOrder();
        order.assign();
        orderRepository.add(order);

        var result = orderRepository.getFirstInCreatedStatus();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllInAssignedStatus_ShouldReturnAssignedOrders() {
        var order1 = makeOrder();
        var order2 = makeOrder();
        order1.assign();

        orderRepository.add(order1);
        orderRepository.add(order2);

        var result = orderRepository.getAllInAssignedStatus();

        assertEquals(1, result.size());
        assertEquals(Status.Assigned, result.getFirst().getStatus());
    }

    @Test
    void update_ShouldPersistStatusChange() {
        var order = makeOrder();
        orderRepository.add(order);

        order.assign();
        orderRepository.update(order);

        var found = orderRepository.getById(order.getId());
        assertTrue(found.isPresent());
        assertEquals(Status.Assigned, found.get().getStatus());
    }
}
