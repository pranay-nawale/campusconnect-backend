package campusconnect.backend.student;

import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private Long eventId;
    private int rating;
    private String message;
}
