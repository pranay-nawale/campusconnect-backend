package campusconnect.backend.notification;

import campusconnect.backend.entity.User;
import campusconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createNotification(
            User sender,
            User receiver,
            String title,
            String message,
            NotificationType type
    ){

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sender(sender)
                .receiver(receiver)
                .build();

        notificationRepository.save(notification);

        NotificationDTO dto = NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(false)
                .createdAt(notification.getCreatedAt())
                .build();

        // ✅ FIX HERE
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                dto
        );

        // ✅ FIX HERE
        long unreadCount =
                notificationRepository.countByReceiverAndIsReadFalse(receiver);

        messagingTemplate.convertAndSend(
                "/topic/unread/" + receiver.getId(),
                unreadCount
        );
    }
    public List<NotificationDTO> getUserNotifications(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository
                .findByReceiverOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> NotificationDTO.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }
    public long getUnreadCount(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAsRead(Long notificationId){

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}