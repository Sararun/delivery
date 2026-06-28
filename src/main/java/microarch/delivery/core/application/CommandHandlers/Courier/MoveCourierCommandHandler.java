package microarch.delivery.core.application.CommandHandlers.Courier;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.Commands.Courier.MoveCourierCommand;
import microarch.delivery.core.ports.CourierRepositoryPort;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoveCourierCommandHandler {
    private final CourierRepositoryPort courierRepository;
    private final UnitOfWork unitOfWork;

    @Transactional
    public UnitResult<Error> handle(MoveCourierCommand command) {
        var courierRes = this.courierRepository.getById(command.getCourierId());
        if (courierRes.isEmpty()) {
            return UnitResult.failure(Error.of("404", "Courier not found"));
        }
        var courier = courierRes.get();
        courier.move(command.getLocation());
        this.unitOfWork.commit();
        return UnitResult.success();
    }
}
