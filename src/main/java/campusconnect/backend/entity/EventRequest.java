package campusconnect.backend.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;


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
    private VerificationStatus status;

    @ManyToOne
    private College college;
}