package microarch.delivery.adapters.in.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.application.CommandHandlers.Order.AssignOrderCommandHandler;
import microarch.delivery.core.application.Commands.Order.AssignOrderCommand;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssignOrderJob {

    private final AssignOrderCommandHandler assignOrderCommandHandler;

    @Scheduled(fixedRate = 1000)
    public void execute() {
        var result = this.assignOrderCommandHandler.handle(AssignOrderCommand.INSTANCE);
        if (result.isFailure()) {
            log.debug("Assign order skipped: {}", result.getError().getMessage());
        }
    }
}
