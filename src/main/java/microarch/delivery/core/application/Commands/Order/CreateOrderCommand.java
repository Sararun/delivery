package microarch.delivery.core.application.Commands.Order;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {
    private final UUID orderId;
    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String apartment;
    private final int volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderId, String country, String city, String street,
            String house, String apartment, int volume) {
        var validation = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                Guard.againstNullOrEmpty(country, "country"), Guard.againstNullOrEmpty(city, "city"),
                Guard.againstNullOrEmpty(street, "street"), Guard.againstNullOrEmpty(house, "house"),
                Guard.againstNullOrEmpty(apartment, "apartment"));

        if (validation != null) {
            return Result.failure(validation);
        }

        return Result.success(new CreateOrderCommand(orderId, country, city, street, house, apartment, volume));
    }
}
