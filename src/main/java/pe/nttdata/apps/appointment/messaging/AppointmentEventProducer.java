package pe.nttdata.apps.appointment.messaging;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import pe.nttdata.apps.appointment.avro.AppointmentEvent;

@ApplicationScoped
public class AppointmentEventProducer {

    @Inject
    @Channel("appointment-events")
    Emitter<Record<String, AppointmentEvent>> emitter;

    public void publish(AppointmentEvent event) {
        emitter.send(Record.of(event.getAppointmentId().toString(), event));
    }
}