package pe.nttdata.apps.appointment.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.nttdata.apps.appointment.avro.AppointmentEvent;
import pe.nttdata.apps.appointment.client.AuthProfileClient;
import pe.nttdata.apps.appointment.dto.AppointmentResponse;
import pe.nttdata.apps.appointment.dto.CreateAppointmentRequest;
import pe.nttdata.apps.appointment.dto.ProfileResponse;
import pe.nttdata.apps.appointment.dto.UpdateAppointmentRequest;
import pe.nttdata.apps.appointment.entity.Appointment;
import pe.nttdata.apps.appointment.entity.Patient;
import pe.nttdata.apps.appointment.entity.Physiotherapist;
import pe.nttdata.apps.appointment.entity.TherapyService;
import pe.nttdata.apps.appointment.messaging.AppointmentEventProducer;
import pe.nttdata.apps.appointment.repository.AppointmentRepository;
import pe.nttdata.apps.appointment.repository.PatientRepository;
import pe.nttdata.apps.appointment.repository.PhysiotherapistRepository;
import pe.nttdata.apps.appointment.repository.TherapyServiceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AppointmentService {

    private static final String STATUS_SCHEDULED = "SCHEDULED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private static final String EVENT_CREATED = "APPOINTMENT_CREATED";
    private static final String EVENT_UPDATED = "APPOINTMENT_UPDATED";
    private static final String EVENT_CANCELLED = "APPOINTMENT_CANCELLED";

    @Inject PatientRepository patientRepository;
    @Inject TherapyServiceRepository therapyServiceRepository;
    @Inject PhysiotherapistRepository physiotherapistRepository;
    @Inject AppointmentRepository appointmentRepository;
    @Inject AppointmentEventProducer appointmentEventProducer;

    @Inject
    @RestClient
    AuthProfileClient authProfileClient;

    public List<AppointmentResponse> findMyAppointments(String authorization) {
        ProfileResponse profile = getValidProfile(authorization);
        Patient patient = patientRepository.findByDocumentNumber(profile.documentNumber)
                .orElseThrow(() -> new NotFoundException("Patient does not have appointments"));

        return appointmentRepository.findByPatient(patient.id)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse create(String authorization, CreateAppointmentRequest request) {
        validateRequest(request);
        

        ProfileResponse profile = getValidProfile(authorization);
        Patient patient = findOrCreatePatient(profile);

        TherapyService therapyService = findActiveTherapyService(request.therapyServiceId);
        Physiotherapist physiotherapist = findActivePhysiotherapist(request.physiotherapistId);

        validatePhysiotherapistAvailability(request.physiotherapistId, request.appointmentDate);

        Appointment appointment = new Appointment();
        appointment.patient = patient;
        appointment.therapyService = therapyService;
        appointment.physiotherapist = physiotherapist;
        appointment.appointmentDate = request.appointmentDate;
        appointment.status = STATUS_SCHEDULED;
        appointment.notes = request.notes;

        appointmentRepository.persist(appointment);
        publishEvent(appointment, EVENT_CREATED);

        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse update(String authorization, UUID id, UpdateAppointmentRequest request) {
        validateUpdateRequest(request);

        ProfileResponse profile = getValidProfile(authorization);
        Patient patient = getExistingPatient(profile);

        Appointment appointment = appointmentRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        validateOwner(appointment, patient);

        if (STATUS_CANCELLED.equals(appointment.status)) {
            throw new BadRequestException("Cancelled appointments cannot be updated");
        }

        appointment.therapyService = findActiveTherapyService(request.therapyServiceId);
        appointment.physiotherapist = findActivePhysiotherapist(request.physiotherapistId);
        appointment.appointmentDate = request.appointmentDate;
        appointment.notes = request.notes;

        publishEvent(appointment, EVENT_UPDATED);

        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(String authorization, UUID id) {
        ProfileResponse profile = getValidProfile(authorization);
        Patient patient = getExistingPatient(profile);

        Appointment appointment = appointmentRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        validateOwner(appointment, patient);

        if (STATUS_CANCELLED.equals(appointment.status)) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        appointment.status = STATUS_CANCELLED;
        publishEvent(appointment, EVENT_CANCELLED);

        return toResponse(appointment);
    }

    private ProfileResponse getValidProfile(String authorization) {
        validateAuthorization(authorization);

        ProfileResponse profile = authProfileClient.getProfile(authorization);

        if (!profile.enabled || !profile.enabledForAppointment) {
            throw new BadRequestException("User is not enabled for appointment");
        }

        return profile;
    }

    private Patient findOrCreatePatient(ProfileResponse profile) {
        return patientRepository.findByDocumentNumber(profile.documentNumber)
                .orElseGet(() -> {
                    Patient patient = new Patient();
                    patient.userAccountId = profile.id;
                    patient.documentNumber = profile.documentNumber;
                    patient.fullName = profile.fullName;
                    patient.email = profile.email;
                    patientRepository.persist(patient);
                    return patient;
                });
    }

    private Patient getExistingPatient(ProfileResponse profile) {
        return patientRepository.findByDocumentNumber(profile.documentNumber)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    private TherapyService findActiveTherapyService(UUID id) {
        TherapyService therapyService = therapyServiceRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Therapy service not found"));

        if (!therapyService.active) {
            throw new BadRequestException("Therapy service is inactive");
        }

        return therapyService;
    }

    private Physiotherapist findActivePhysiotherapist(UUID id) {
        Physiotherapist physiotherapist = physiotherapistRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Physiotherapist not found"));

        if (!physiotherapist.active) {
            throw new BadRequestException("Physiotherapist is inactive");
        }

        return physiotherapist;
    }

    private void validateOwner(Appointment appointment, Patient patient) {
        if (!appointment.patient.id.equals(patient.id)) {
            throw new BadRequestException("Appointment does not belong to authenticated patient");
        }
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BadRequestException("Authorization header is required");
        }
    }

    private void validateRequest(CreateAppointmentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        validateAppointmentFields(request.therapyServiceId, request.physiotherapistId, request.appointmentDate);
    }

    private void validateUpdateRequest(UpdateAppointmentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        validateAppointmentFields(request.therapyServiceId, request.physiotherapistId, request.appointmentDate);
    }

    private void validateAppointmentFields(UUID therapyServiceId, UUID physiotherapistId, LocalDateTime appointmentDate) {
        if (therapyServiceId == null) {
            throw new BadRequestException("Therapy service id is required");
        }
        if (physiotherapistId == null) {
            throw new BadRequestException("Physiotherapist id is required");
        }
        if (appointmentDate == null) {
            throw new BadRequestException("Appointment date is required");
        }
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment date cannot be in the past");
        }
    }

    private void publishEvent(Appointment appointment, String eventType) {
        AppointmentEvent event = AppointmentEvent.newBuilder()
                .setAppointmentId(appointment.id.toString())
                .setEventType(eventType)
                .setStatus(appointment.status)
                .setOccurredAt(LocalDateTime.now().toString())
                .build();

        appointmentEventProducer.publish(event);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.id,
                appointment.patient.fullName,
                appointment.therapyService.name,
                appointment.physiotherapist.fullName,
                appointment.appointmentDate,
                appointment.status,
                appointment.notes
        );
    }

    private void validatePhysiotherapistAvailability(UUID physiotherapistId, LocalDateTime appointmentDate) {
        boolean existsAppointment = appointmentRepository.existsActiveAppointmentByPhysiotherapistAndDate(
                physiotherapistId,
                appointmentDate
        );

        if (existsAppointment) {
            throw new BadRequestException("Physiotherapist already has an appointment at this time");
        }
    }
}