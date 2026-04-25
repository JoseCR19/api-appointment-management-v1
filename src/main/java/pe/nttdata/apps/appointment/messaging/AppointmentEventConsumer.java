package pe.nttdata.apps.appointment.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import pe.nttdata.apps.appointment.avro.AppointmentEvent;
import pe.nttdata.apps.appointment.entity.AppointmentEventLog;
import pe.nttdata.apps.appointment.repository.AppointmentEventLogRepository;
import pe.nttdata.apps.appointment.repository.AppointmentRepository;

import java.util.UUID;

@ApplicationScoped
public class AppointmentEventConsumer {

    @Inject
    AppointmentRepository appointmentRepository;

    @Inject
    AppointmentEventLogRepository eventLogRepository;

    @Incoming("appointment-events-in")
    @Transactional
    public void consume(AppointmentEvent event) {
        AppointmentEventLog log = new AppointmentEventLog();

        appointmentRepository.findByIdOptional(UUID.fromString(event.getAppointmentId().toString()))
                .ifPresent(appointment -> log.appointment = appointment);

        log.eventType = event.getEventType().toString();
        log.eventStatus = event.getStatus().toString();
        log.payload = event.toString();

        eventLogRepository.persist(log);
    }
}