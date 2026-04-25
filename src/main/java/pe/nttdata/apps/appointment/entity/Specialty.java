package pe.nttdata.apps.appointment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_specialty")
public class Specialty extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "active", nullable = false)
    public boolean active = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt;
}