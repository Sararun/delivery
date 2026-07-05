package microarch.delivery.core.application.Queries.Courier;

public final class GetAllCouriersQuery {
    private static final GetAllCouriersQuery INSTANCE = new GetAllCouriersQuery();

    private GetAllCouriersQuery() {
    }

    public static GetAllCouriersQuery create() {
        return INSTANCE;
    }
}
