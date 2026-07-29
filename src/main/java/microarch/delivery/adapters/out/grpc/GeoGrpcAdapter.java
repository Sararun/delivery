package microarch.delivery.adapters.out.grpc;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto.GetGeolocationRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.ApplicationProperties;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.GeoPort;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GeoGrpcAdapter implements GeoPort {

    private static final long REQUEST_TIMEOUT_SECONDS = 5;

    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub geoClient;

    public GeoGrpcAdapter(ApplicationProperties properties) {
        var geoService = properties.getGrpc().getGeoService();
        this.channel = ManagedChannelBuilder.forAddress(geoService.getHost(), geoService.getPort()).usePlaintext()
                .build();
        this.geoClient = GeoGrpc.newBlockingStub(channel);
    }

    @Override
    public Result<Location, Error> getLocation(String street) {
        var request = GetGeolocationRequest.newBuilder().setStreet(street).build();

        try {
            var response = geoClient.withDeadlineAfter(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .getGeolocation(request);
            var location = response.getLocation();
            return Location.create(location.getX(), location.getY());
        } catch (StatusRuntimeException exception) {
            return Result.failure(Error.of("geo.service.unavailable",
                    "Failed to get location from Geo service: " + exception.getStatus().getCode()));
        }
    }

    @PreDestroy
    public void close() {
        channel.shutdown();
    }
}
