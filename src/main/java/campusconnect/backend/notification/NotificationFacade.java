package campusconnect.backend.notification;

import campusconnect.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public void notifyUser(
            User user,
            String message,
            NotificationType type,
            Map<String, Object> vars,
            boolean sendEmail
    ){

        // 🔔 Save notification
        notificationService.createNotification(user, message, type);

        if(sendEmail){
            try {

                vars.put("name", user.getName());

                String template = switch(type){
                    case EVENT_CONFIRMED -> "email/event-confirmed";
                    case EVENT_REJECTED -> "email/event-rejected";
                    case EVENT_REGISTERED -> "email/student-registered";
                    case PAYMENT_SUCCESS -> "email/payment-invoice";
                    case COLLEGE_APPROVED -> "email/college-approved";
                    case VENDOR_APPROVED -> "email/vendor-approved";
                    case VENDOR_ASSIGNED -> "email/vendor-assigned";
                    case FEEDBACK_REQUEST -> "email/feedback-request";
                    default -> "email/event-request";
                };

                emailService.sendTemplateEmail(
                        user.getEmail(),
                        "CampusConnect Notification",
                        template,
                        vars
                );

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}