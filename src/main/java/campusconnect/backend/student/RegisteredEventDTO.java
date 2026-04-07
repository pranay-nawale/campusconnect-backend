package campusconnect.backend.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredEventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
}
