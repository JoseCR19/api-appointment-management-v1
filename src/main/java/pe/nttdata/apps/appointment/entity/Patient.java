package pe.nttdata.apps.appointment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_patient")
public class Patient extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "user_account_id", nullable = false, unique = true)
    public UUID userAccountId;

    @Column(name = "document_number", nullable = false, unique = true)
    public String documentNumber;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(name = "email")
    public String email;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}