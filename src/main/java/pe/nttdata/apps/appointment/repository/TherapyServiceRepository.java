package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.TherapyService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TherapyServiceRepository implements PanacheRepository<TherapyService> {

    public List<TherapyService> findActive() {
        return list("active", true);
    }

    public Optional<TherapyService> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}