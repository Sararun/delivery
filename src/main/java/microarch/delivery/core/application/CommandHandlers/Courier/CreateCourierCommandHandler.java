package microarch.delivery.core.application.CommandHandlers.Courier;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Commands.Courier.CreateCourierCommand;
import microarch.delivery.core.domain.model.kernel.Courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandler {

    private final CourierRepositoryPort courierRepository;
    private final UnitOfWork unitOfWork;

    @Transactional
    public Result<Courier, Error> handle(CreateCourierCommand command) {
        var locationRes = Location.create(1, 1);
        if (locationRes.isFailure()) {
            return Result.failure(locationRes.getError());
        }
        var courierRes = Courier.create(command.getName(), locationRes.getValue());
        if (courierRes.isFailure()) {
            return Result.failure(courierRes.getError());
        }
        this.courierRepository.add(courierRes.getValue());
        this.unitOfWork.commit();
        return courierRes;
    }
}
