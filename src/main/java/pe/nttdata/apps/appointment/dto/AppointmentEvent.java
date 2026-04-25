package pe.nttdata.apps.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentEvent {

    public UUID appointmentId;
    public String eventType;
    public String status;
    public LocalDateTime occurredAt;

    public AppointmentEvent(UUID appointmentId, String eventType, String status, LocalDateTime occurredAt) {
        this.appointmentId = appointmentId;
        this.eventType = eventType;
        this.status = status;
        this.occurredAt = occurredAt;
    }
}