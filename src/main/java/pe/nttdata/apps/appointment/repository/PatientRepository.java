package pe.nttdata.apps.appointment.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.nttdata.apps.appointment.entity.Patient;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {

    public Optional<Patient> findByUserAccountId(UUID userAccountId) {
        return find("userAccountId", userAccountId).firstResultOptional();
    }

    public Optional<Patient> findByDocumentNumber(String documentNumber) {
        return find("documentNumber", documentNumber).firstResultOptional();
    }
}