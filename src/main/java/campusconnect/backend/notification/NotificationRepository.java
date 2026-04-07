package campusconnect.backend.notification;

import campusconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {


    long countByUserAndIsReadFalse(User user);
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);

    long countByReceiverAndIsReadFalse(User receiver);
}