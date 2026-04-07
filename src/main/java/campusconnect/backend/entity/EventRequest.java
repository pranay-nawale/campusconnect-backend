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

    private String bannerUrl;       // ADD
    private String bannerPublicId;  // ADD

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPaid = false; // true = paid, false = free

    @Column(nullable = true, columnDefinition = "double precision default 0")
    private Double price; // applicable only if isPaid = true

    private String eventPlanUrl;
    private String eventPlanPublicId;

    @ManyToOne
    private College college;

    @OneToMany(mappedBy = "eventRequest", cascade = CascadeType.ALL)
    private List<EventService> services;

}