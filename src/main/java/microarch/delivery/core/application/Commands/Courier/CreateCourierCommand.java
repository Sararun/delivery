package microarch.delivery.core.application.Commands.Courier;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCourierCommand {
    private final String name;

    public static Result<CreateCourierCommand, Error> create(String name) {
        var validation = Guard.combine(Guard.againstNullOrEmpty(name, "name"));

        if (validation != null) {
            return Result.failure(validation);
        }

        return Result.success(new CreateCourierCommand(name));
    }
}
