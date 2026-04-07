package campusconnect.backend.notification;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;

    private String message;
    private String title;

    private NotificationType type;

    private boolean isRead;

    private LocalDateTime createdAt;
}