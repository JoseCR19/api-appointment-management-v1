package pe.nttdata.apps.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentResponse {

    public UUID id;
    public String patientName;
    public String therapyServiceName;
    public String physiotherapistName;
    public LocalDateTime appointmentDate;
    public String status;
    public String notes;

    public AppointmentResponse(UUID id, String patientName, String therapyServiceName,
                               String physiotherapistName, LocalDateTime appointmentDate,
                               String status, String notes) {
        this.id = id;
        this.patientName = patientName;
        this.therapyServiceName = therapyServiceName;
        this.physiotherapistName = physiotherapistName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
    }
}