package campusconnect.backend.student;

import lombok.Data;

@Data
public class FeedbackResponseDTO {

    private Long eventId;
    private String eventTitle;
    private Integer rating; // ✅ Use Integer (nullable)
    private String message;
    private boolean submitted;

    // ✅ Full constructor
    public FeedbackResponseDTO(Long eventId, String eventTitle,
                               Integer rating, String message,
                               boolean submitted) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.rating = rating;
        this.message = message;
        this.submitted = submitted;
    }
}
