package pe.nttdata.apps.appointment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_therapy_service")
public class TherapyService extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    public Specialty specialty;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "price", nullable = false)
    public BigDecimal price;

    @Column(name = "active", nullable = false)
    public boolean active = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt;
}