package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.UserRole;

public class BarberDto {

    private Long barberId;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String bio;
    private UserRole role;

    public BarberDto() {}

    public BarberDto(Long barberId, String name, String email, String phone, String specialization, String bio, UserRole role) {
        this.barberId = barberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.bio = bio;
        this.role = role;
    }

    public Long getBarberId() { return barberId; }
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}