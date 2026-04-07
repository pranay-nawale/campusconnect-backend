package campusconnect.backend.student;

import campusconnect.backend.entity.EventCategory;
import campusconnect.backend.entity.EventStatus;
import campusconnect.backend.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDTO {
    private Long id;

    private String title;

    private String description;

    private LocalDateTime eventDate;

    private int maxParticipants;

    private String bannerUrl;

    private String bannerPublicId;

    private EventCategory category;

    private EventStatus eventStatus;

    private boolean isPaid;

    private Double price;

    private Long collegeId;     // Just send the college ID
    private String collegeName;// Optional: send college name too

    private String studentStatus;

    private String msg;
}
