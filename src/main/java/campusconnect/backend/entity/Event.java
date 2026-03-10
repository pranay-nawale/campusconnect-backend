package campusconnect.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime eventDate;

    private int maxParticipants;

    private boolean isPaid;

    private double price;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @ManyToOne
    private College college;

}