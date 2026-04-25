package pe.nttdata.apps.appointment.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pe.nttdata.apps.appointment.entity.Physiotherapist;
import pe.nttdata.apps.appointment.entity.Specialty;
import pe.nttdata.apps.appointment.entity.TherapyService;
import pe.nttdata.apps.appointment.repository.PhysiotherapistRepository;
import pe.nttdata.apps.appointment.repository.SpecialtyRepository;
import pe.nttdata.apps.appointment.repository.TherapyServiceRepository;

import java.util.List;

@Path("/catalogs")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {

    @Inject
    SpecialtyRepository specialtyRepository;

    @Inject
    TherapyServiceRepository therapyServiceRepository;

    @Inject
    PhysiotherapistRepository physiotherapistRepository;

    @GET
    @Path("/specialties")
    public List<Specialty> specialties() {
        return specialtyRepository.findActive();
    }

    @GET
    @Path("/therapy-services")
    public List<TherapyService> therapyServices() {
        return therapyServiceRepository.findActive();
    }

    @GET
    @Path("/physiotherapists")
    public List<Physiotherapist> physiotherapists() {
        return physiotherapistRepository.findActive();
    }
}