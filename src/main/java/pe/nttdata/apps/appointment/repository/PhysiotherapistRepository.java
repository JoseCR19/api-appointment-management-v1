package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.Physiotherapist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PhysiotherapistRepository implements PanacheRepository<Physiotherapist> {

    public List<Physiotherapist> findActive() {
        return list("active", true);
    }

    public Optional<Physiotherapist> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}