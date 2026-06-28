package microarch.delivery.core.application.Queries.Order;

import microarch.delivery.core.domain.model.kernel.Location;

import java.util.UUID;

public record OrderDto(UUID id, Location location) {
}
