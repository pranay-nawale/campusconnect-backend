package campusconnect.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student; // nullable

    @ManyToOne
    @JoinColumn(name = "college_id")
    private College college; // nullable

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventRequest event;

    private String role; // "STUDENT" or "COLLEGE"

    private LocalDateTime createdAt;
}