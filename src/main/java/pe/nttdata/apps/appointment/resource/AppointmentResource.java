package pe.nttdata.apps.appointment.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import pe.nttdata.apps.appointment.dto.AppointmentResponse;
import pe.nttdata.apps.appointment.dto.CreateAppointmentRequest;
import pe.nttdata.apps.appointment.dto.UpdateAppointmentRequest;
import pe.nttdata.apps.appointment.service.AppointmentService;

import java.util.List;
import java.util.UUID;

@Path("/appointments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityScheme(
        securitySchemeName = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class AppointmentResource {

    @Inject
    AppointmentService appointmentService;

    @GET
    public List<AppointmentResponse> findMyAppointments(
            @HeaderParam("Authorization") String authorization
    ) {
        return appointmentService.findMyAppointments(authorization);
    }

    @POST
    public AppointmentResponse create(
            @HeaderParam("Authorization") String authorization,
            CreateAppointmentRequest request
    ) {
        return appointmentService.create(authorization, request);
    }

    @PUT
    @Path("/{id}")
    public AppointmentResponse update(
            @HeaderParam("Authorization") String authorization,
            @PathParam("id") UUID id,
            UpdateAppointmentRequest request
    ) {
        return appointmentService.update(authorization, id, request);
    }

    @DELETE
    @Path("/{id}")
    public AppointmentResponse cancel(
            @HeaderParam("Authorization") String authorization,
            @PathParam("id") UUID id
    ) {
        return appointmentService.cancel(authorization, id);
    }
}