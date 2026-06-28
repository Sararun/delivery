package microarch.delivery.core.application.Queries.Order;

public final class GetNotCompletedOrdersQuery {
    private static final GetNotCompletedOrdersQuery INSTANCE = new GetNotCompletedOrdersQuery();

    private GetNotCompletedOrdersQuery() {
    }

    public static GetNotCompletedOrdersQuery create() {
        return INSTANCE;
    }
}
