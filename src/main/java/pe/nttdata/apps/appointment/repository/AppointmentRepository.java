package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AppointmentRepository implements PanacheRepositoryBase<Appointment, UUID> {

    public List<Appointment> findByPatient(UUID patientId) {
        return list("patient.id", patientId);
    }

    public List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return list("appointmentDate between ?1 and ?2", start, end);
    }
    
    public boolean existsActiveAppointmentByPhysiotherapistAndDate(UUID physiotherapistId, LocalDateTime appointmentDate) {
    return count(
            "physiotherapist.id = ?1 and appointmentDate = ?2 and status <> ?3",
            physiotherapistId,
            appointmentDate,
            "CANCELLED"
    ) > 0;
}
}