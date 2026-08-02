package microarch.delivery.adapters.in.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.CommandHandlers.Order.CreateOrderCommandHandler;
import microarch.delivery.core.application.Commands.Order.CreateOrderCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import queues.basket.events.BasketEventsProto.BasketConfirmedIntegrationEvent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BasketConfirmedKafkaConsumer {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @KafkaListener(topics = "${app.kafka.basket-events-topic}")
    public void consume(byte[] message) {
        var event = parseEvent(message);
        var address = event.getAddress();
        var commandResult = CreateOrderCommand.create(UUID.fromString(event.getBasketId()), address.getCountry(),
                address.getCity(), address.getStreet(), address.getHouse(), address.getApartment(), event.getVolume());

        if (commandResult.isFailure()) {
            throw new IllegalArgumentException("Cannot create order command: " + commandResult.getError().getMessage());
        }

        var orderResult = createOrderCommandHandler.handle(commandResult.getValue());
        if (orderResult.isFailure()) {
            throw new IllegalStateException("Cannot create order: " + orderResult.getError().getMessage());
        }
    }

    private BasketConfirmedIntegrationEvent parseEvent(byte[] message) {
        try {
            return BasketConfirmedIntegrationEvent.parseFrom(message);
        } catch (InvalidProtocolBufferException exception) {
            throw new IllegalArgumentException("Cannot parse basket confirmation event", exception);
        }
    }
}
