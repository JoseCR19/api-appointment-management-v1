package pe.nttdata.apps.appointment.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import pe.nttdata.apps.appointment.dto.ProfileResponse;

@Path("/auth")
@RegisterRestClient(configKey = "auth-profile-api")
public interface AuthProfileClient {

    @GET
    @Path("/profile")
    ProfileResponse getProfile(@HeaderParam("Authorization") String authorization);
}