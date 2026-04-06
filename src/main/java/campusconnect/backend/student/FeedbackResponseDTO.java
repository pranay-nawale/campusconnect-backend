package campusconnect.backend.student;

public class FeedbackResponseDTO {

    private Long eventId;
    private String eventTitle;
    private int rating;
    private String message;

    public FeedbackResponseDTO(Long eventId, String eventTitle, int rating, String message) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.rating = rating;
        this.message = message;
    }
}
