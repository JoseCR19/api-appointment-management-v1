package pe.nttdata.apps.appointment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_appointment_event_log")
public class AppointmentEventLog extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    public Appointment appointment;

    @Column(name = "event_type", nullable = false)
    public String eventType;

    @Column(name = "event_status", nullable = false)
    public String eventStatus;

    @Column(name = "payload")
    public String payload;

    @Column(name = "error_message")
    public String errorMessage;

    @Column(name = "processed_at")
    public LocalDateTime processedAt;

    @PrePersist
    void prePersist() {
        processedAt = LocalDateTime.now();
    }
}