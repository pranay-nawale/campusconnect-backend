package campusconnect.backend.admin.event;

import campusconnect.backend.entity.EventCategory;
import campusconnect.backend.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminEventDTO {
    private Long id;

    private String title;

    private String description;

    private LocalDateTime eventDate;

    private int maxParticipants;

    private EventCategory category;

    private EventStatus status;

    private Long collegeId;

    private String collegeName;

    private String bannerUrl;
    private String eventPlanUrl;


}
