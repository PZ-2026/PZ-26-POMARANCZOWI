package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "barber")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barber_id")
    private Long barberId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String specialization;
    private String bio;

    public Barber() {}

    public Barber(Long barberId, User user, String specialization, String bio) {
        this.barberId = barberId;
        this.user = user;
        this.specialization = specialization;
        this.bio = bio;
    }

    public Long getBarberId() { return barberId; }
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}