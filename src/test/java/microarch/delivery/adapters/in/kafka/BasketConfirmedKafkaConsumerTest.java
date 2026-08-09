package microarch.delivery.adapters.in.kafka;

import libs.errs.Result;
import microarch.delivery.core.application.CommandHandlers.Order.CreateOrderCommandHandler;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import microarch.delivery.core.domain.model.kernel.Order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import queues.basket.events.BasketEventsProto.Address;
import queues.basket.events.BasketEventsProto.BasketConfirmedIntegrationEvent;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketConfirmedKafkaConsumerTest {

    @Mock
    private CreateOrderCommandHandler createOrderCommandHandler;

    @Mock
    private Order order;

    @Captor
    private ArgumentCaptor<CreateOrderCommand> commandCaptor;

    @Test
    void consume_ShouldCreateOrderFromBasketConfirmation() {
        var orderId = UUID.randomUUID();
        var event = BasketConfirmedIntegrationEvent
                .newBuilder().setBasketId(orderId.toString()).setAddress(Address.newBuilder().setCountry("Russia")
                        .setCity("Novosibirsk").setStreet("Lenina").setHouse("1").setApartment("2"))
                .setVolume(3).build();
        when(createOrderCommandHandler.handle(any())).thenReturn(Result.success(order));
        var consumer = new BasketConfirmedKafkaConsumer(createOrderCommandHandler);

        consumer.consume(event.toByteArray());

        verify(createOrderCommandHandler).handle(commandCaptor.capture());
        var command = commandCaptor.getValue();
        assertEquals(orderId, command.getOrderId());
        assertEquals("Russia", command.getCountry());
        assertEquals("Novosibirsk", command.getCity());
        assertEquals("Lenina", command.getStreet());
        assertEquals("1", command.getHouse());
        assertEquals("2", command.getApartment());
        assertEquals(3, command.getVolume());
    }
}
