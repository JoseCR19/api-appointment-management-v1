package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.AppointmentEventLog;

@ApplicationScoped
public class AppointmentEventLogRepository implements PanacheRepository<AppointmentEventLog> {
}