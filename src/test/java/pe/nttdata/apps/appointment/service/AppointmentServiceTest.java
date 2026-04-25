package pe.nttdata.apps.appointment.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.nttdata.apps.appointment.dto.*;
import pe.nttdata.apps.appointment.entity.*;
import pe.nttdata.apps.appointment.messaging.AppointmentEventProducer;
import pe.nttdata.apps.appointment.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    AppointmentService service;

    @Mock PatientRepository patientRepository;
    @Mock TherapyServiceRepository therapyServiceRepository;
    @Mock PhysiotherapistRepository physiotherapistRepository;
    @Mock AppointmentRepository appointmentRepository;
    @Mock AppointmentEventProducer appointmentEventProducer;
    @Mock pe.nttdata.apps.appointment.client.AuthProfileClient authProfileClient;

    ProfileResponse profile;
    Patient patient;
    TherapyService therapyService;
    Physiotherapist physiotherapist;

    @BeforeEach
    void setUp() {
        service = new AppointmentService();
        service.patientRepository = patientRepository;
        service.therapyServiceRepository = therapyServiceRepository;
        service.physiotherapistRepository = physiotherapistRepository;
        service.appointmentRepository = appointmentRepository;
        service.appointmentEventProducer = appointmentEventProducer;
        service.authProfileClient = authProfileClient;

        profile = new ProfileResponse();
        profile.id = UUID.randomUUID();
        profile.documentNumber = "70584920";
        profile.fullName = "Mary Peralta";
        profile.email = "mary@test.com";
        profile.enabled = true;
        profile.enabledForAppointment = true;

        patient = new Patient();
        patient.id = UUID.randomUUID();
        patient.userAccountId = profile.id;
        patient.documentNumber = profile.documentNumber;
        patient.fullName = profile.fullName;
        patient.email = profile.email;

        therapyService = new TherapyService();
        therapyService.id = UUID.randomUUID();
        therapyService.name = "Terapia lumbar";
        therapyService.price = BigDecimal.valueOf(80);
        therapyService.active = true;

        physiotherapist = new Physiotherapist();
        physiotherapist.id = UUID.randomUUID();
        physiotherapist.fullName = "Dr. Juan Pérez";
        physiotherapist.active = true;
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.therapyServiceId = therapyService.id;
        request.physiotherapistId = physiotherapist.id;
        request.appointmentDate = LocalDateTime.now().plusDays(1);
        request.notes = "Dolor lumbar";

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(therapyServiceRepository.findByIdOptional(therapyService.id)).thenReturn(Optional.of(therapyService));
        when(physiotherapistRepository.findByIdOptional(physiotherapist.id)).thenReturn(Optional.of(physiotherapist));
        when(appointmentRepository.existsActiveAppointmentByPhysiotherapistAndDate(physiotherapist.id, request.appointmentDate))
                .thenReturn(false);

        doAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.id = UUID.randomUUID();
            return null;
        }).when(appointmentRepository).persist(any(Appointment.class));

        AppointmentResponse response = service.create("Bearer token", request);

        assertNotNull(response);
        assertEquals("Mary Peralta", response.patientName);
        assertEquals("Terapia lumbar", response.therapyServiceName);
        assertEquals("Dr. Juan Pérez", response.physiotherapistName);
        assertEquals("SCHEDULED", response.status);

        verify(appointmentRepository).persist(any(Appointment.class));
        verify(appointmentEventProducer).publish(any());
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() {
        UUID appointmentId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.id = appointmentId;
        appointment.patient = patient;
        appointment.therapyService = therapyService;
        appointment.physiotherapist = physiotherapist;
        appointment.appointmentDate = LocalDateTime.now().plusDays(1);
        appointment.status = "SCHEDULED";
        appointment.notes = "Dolor lumbar";

        UpdateAppointmentRequest request = new UpdateAppointmentRequest();
        request.therapyServiceId = therapyService.id;
        request.physiotherapistId = physiotherapist.id;
        request.appointmentDate = LocalDateTime.now().plusDays(2);
        request.notes = "Dolor cervical";

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByIdOptional(appointmentId)).thenReturn(Optional.of(appointment));
        when(therapyServiceRepository.findByIdOptional(therapyService.id)).thenReturn(Optional.of(therapyService));
        when(physiotherapistRepository.findByIdOptional(physiotherapist.id)).thenReturn(Optional.of(physiotherapist));


        AppointmentResponse response = service.update("Bearer token", appointmentId, request);

        assertNotNull(response);
        assertEquals("Dolor cervical", response.notes);
        assertEquals("SCHEDULED", response.status);
        verify(appointmentEventProducer).publish(any());
    }

    @Test
    void shouldCancelAppointmentSuccessfully() {
        UUID appointmentId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.id = appointmentId;
        appointment.patient = patient;
        appointment.therapyService = therapyService;
        appointment.physiotherapist = physiotherapist;
        appointment.appointmentDate = LocalDateTime.now().plusDays(1);
        appointment.status = "SCHEDULED";

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByIdOptional(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentResponse response = service.cancel("Bearer token", appointmentId);

        assertNotNull(response);
        assertEquals("CANCELLED", response.status);
        verify(appointmentEventProducer).publish(any());
    }

    @Test
    void shouldFindMyAppointmentsSuccessfully() {
        Appointment appointment = new Appointment();
        appointment.id = UUID.randomUUID();
        appointment.patient = patient;
        appointment.therapyService = therapyService;
        appointment.physiotherapist = physiotherapist;
        appointment.appointmentDate = LocalDateTime.now().plusDays(1);
        appointment.status = "SCHEDULED";

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient.id)).thenReturn(List.of(appointment));

        List<AppointmentResponse> response = service.findMyAppointments("Bearer token");

        assertEquals(1, response.size());
        assertEquals("Mary Peralta", response.get(0).patientName);
    }

    @Test
    void shouldFailWhenAuthorizationIsMissing() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.therapyServiceId = UUID.randomUUID();
        request.physiotherapistId = UUID.randomUUID();
        request.appointmentDate = LocalDateTime.now().plusDays(1);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.create(null, request)
        );

        assertEquals("Authorization header is required", exception.getMessage());
        verifyNoInteractions(authProfileClient);
    }

    @Test
    void shouldFailWhenPhysiotherapistHasAppointmentAtSameTime() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.therapyServiceId = therapyService.id;
        request.physiotherapistId = physiotherapist.id;
        request.appointmentDate = LocalDateTime.now().plusDays(1);

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(therapyServiceRepository.findByIdOptional(therapyService.id)).thenReturn(Optional.of(therapyService));
        when(physiotherapistRepository.findByIdOptional(physiotherapist.id)).thenReturn(Optional.of(physiotherapist));
        when(appointmentRepository.existsActiveAppointmentByPhysiotherapistAndDate(physiotherapist.id, request.appointmentDate))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.create("Bearer token", request)
        );

        assertEquals("Physiotherapist already has an appointment at this time", exception.getMessage());
        verify(appointmentRepository, never()).persist(any(Appointment.class));
        verify(appointmentEventProducer, never()).publish(any());
    }

    @Test
    void shouldFailWhenAppointmentDoesNotExistOnUpdate() {
        UUID appointmentId = UUID.randomUUID();

        UpdateAppointmentRequest request = new UpdateAppointmentRequest();
        request.therapyServiceId = therapyService.id;
        request.physiotherapistId = physiotherapist.id;
        request.appointmentDate = LocalDateTime.now().plusDays(1);

        when(authProfileClient.getProfile("Bearer token")).thenReturn(profile);
        when(patientRepository.findByDocumentNumber(profile.documentNumber)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByIdOptional(appointmentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.update("Bearer token", appointmentId, request)
        );

        assertEquals("Appointment not found", exception.getMessage());
    }
}