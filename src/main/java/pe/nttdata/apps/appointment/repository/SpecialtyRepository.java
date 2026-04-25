package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.Specialty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SpecialtyRepository implements PanacheRepository<Specialty> {

    public List<Specialty> findActive() {
        return list("active", true);
    }

    public Optional<Specialty> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}