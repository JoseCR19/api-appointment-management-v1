package pe.nttdata.apps.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateAppointmentRequest {

    public UUID therapyServiceId;
    public UUID physiotherapistId;
    public LocalDateTime appointmentDate;
    public String notes;
}