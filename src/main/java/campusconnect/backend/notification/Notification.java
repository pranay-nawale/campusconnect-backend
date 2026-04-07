package campusconnect.backend.notification;

import campusconnect.backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;// better than only message
    private String message;


    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean isRead;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private User receiver;

    @ManyToOne
    private User sender;

}