package pe.nttdata.apps.appointment.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof BadRequestException) {
            return build(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof NotAuthorizedException) {
            return build(Response.Status.UNAUTHORIZED, exception.getMessage());
        }

        if (exception instanceof NotFoundException) {
            return build(Response.Status.NOT_FOUND, exception.getMessage());
        }

        return build(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private Response build(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), message))
                .build();
    }
}