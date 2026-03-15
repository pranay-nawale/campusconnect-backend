package campusconnect.backend.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime eventDate;

    private int maxParticipants;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPaid = false; // true = paid, false = free

    @Column(nullable = false, columnDefinition = "double precision default 0")
    private double price = 0; // applicable only if isPaid = true

    @ManyToOne
    private College college;

    @OneToMany(mappedBy = "eventRequest", cascade = CascadeType.ALL)
    private List<EventService> services;
}