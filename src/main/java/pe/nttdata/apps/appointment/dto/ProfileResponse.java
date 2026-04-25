package pe.nttdata.apps.appointment.dto;
import java.util.UUID;

public class ProfileResponse {

    public String documentNumber;
    public String fullName;
    public String email;
    public boolean enabled;
    public boolean enabledForAppointment;
    public UUID id;
}