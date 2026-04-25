package pe.nttdata.apps.appointment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_physiotherapist")
public class Physiotherapist extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "phone")
    public String phone;

    @Column(name = "active", nullable = false)
    public boolean active = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt;
}