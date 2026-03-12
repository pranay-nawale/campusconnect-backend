package campusconnect.backend.college;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRequestResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private int maxParticipants;
    private String category;
    private String status;
    private String collegeName;

}