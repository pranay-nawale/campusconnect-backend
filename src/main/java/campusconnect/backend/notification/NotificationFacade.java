package campusconnect.backend.notification;

import campusconnect.backend.entity.Role;
import campusconnect.backend.entity.User;
import campusconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final UserRepository  userRepository;

    public void notifyUser(
            User user,
            String message,
            NotificationType type,
            Map<String, Object> vars,
            boolean sendEmail,
            byte[] attachment // optional (for invoice)
    ) {

        // 🔔 Save notification (optional for now)
        notificationService.createNotification(
                null,          // sender (system)
                user,          // receiver
                type.name(),   // title
                message,
                type
        );

        if (sendEmail) {
            try {

                if (vars == null) {
                    vars = new HashMap<>();
                }

                vars.put("name", user.getName());

                String subject;
                String template;

                switch (type) {

                    // 👤 STUDENT FLOW
                    case STUDENT_REGISTERED -> {
                        subject = "Profile Under Review";
                        template = "email/student-registered";
                    }

                    case STUDENT_APPROVED -> {
                        subject = "Account Approved";
                        template = "email/student-approved";
                    }

                    case STUDENT_REJECTED -> {
                        subject = "Account Rejected";
                        template = "email/student-rejected";
                    }

                    // 🎟 EVENT FLOW
                    case EVENT_REGISTERED -> {
                        subject = "Event Registration Successful";
                        template = "email/event-registered";
                    }

                    case PAYMENT_SUCCESS -> {
                        subject = "Payment Successful - Invoice Attached";
                        template = "email/payment-invoice";
                    }

                    case EVENT_APPROVED -> {
                        subject = "Event Approved";
                        template = "email/event-approved";
                    }

                    case EVENT_REJECTED -> {
                        subject = "Event Rejected";
                        template = "email/event-rejected";
                    }

                    case EVENT_PLAN_RECEIVED -> {
                        subject = "Event Plan Received";
                        template = "email/event-plan-received";
                    }


                    // 🧑‍🔧 VENDOR FLOW
                    case VENDOR_REGISTERED -> {
                        subject = "Vendor Registration Successful";
                        template = "email/vendor-registered";
                    }

                    case VENDOR_ASSIGNED -> {
                        subject = "Service Assigned";
                        template = "email/vendor-assigned";
                    }

                    case VENDOR_REJECTED -> {
                        subject = "Service Rejected";
                        template = "email/vendor-rejected";
                    }

                    case VENDOR_APPROVED -> {
                        subject = "Service Approved";
                        template = "email/vendor-approved";
                    }


//                    college flow
                    case COLLEGE_REGISTERED -> {
                        subject = "College Profile Under Review";
                        template = "email/college-registered";
                    }

                    case COLLEGE_APPROVED -> {
                        subject = "College Verified Successfully";
                        template = "email/college-approved";
                    }

                    case COLLEGE_REJECTED -> {
                        subject = "College Verification Rejected";
                        template = "email/college-rejected";
                    }

                    default -> {
                        subject = "CampusConnect Notification";
                        template = "email/default";
                    }
                }

                // Render HTML
                Context context = new Context();
                context.setVariables(vars);
                String html = templateEngine.process(template, context);

                // 📎 With attachment (invoice)
                if (attachment != null) {
                    emailService.sendEmailWithAttachment(
                            user.getEmail(),
                            subject,
                            html,
                            attachment
                    );
                } else {
                    emailService.sendHtmlEmail(
                            user.getEmail(),
                            subject,
                            html
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void notifyAdmin(String message,
                            Map<String, Object> vars) {

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        if(admins.isEmpty()){
            throw new RuntimeException("No admin found");
        }

        for (User admin : admins) {
            notifyUser(
                    admin,
                    message,
                    NotificationType.ADMIN_ALERT,
                    vars,
                    false,
                    null
            );
        }
    }
}

//package campusconnect.backend.notification;
//
//import campusconnect.backend.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationFacade {
//
//    private final NotificationService notificationService;
//    private final EmailService emailService;
//
//    public void notifyUser(
//            User user,
//            String message,
//            NotificationType type,
//            Map<String, Object> vars,
//            boolean sendEmail
//    ){
//
//        // 🔔 Save notification
//        notificationService.createNotification(user, message, type);
//
//        if(sendEmail){
//            try {
//
//                vars.put("name", user.getName());
//
//                String template = switch(type){
//                    case EVENT_CONFIRMED -> "email/event-confirmed";
//                    case EVENT_REJECTED -> "email/event-rejected";
//                    case EVENT_REGISTERED -> "email/student-registered";
//                    case PAYMENT_SUCCESS -> "email/payment-invoice";
//                    case COLLEGE_APPROVED -> "email/college-approved";
//                    case VENDOR_APPROVED -> "email/vendor-approved";
//                    case VENDOR_ASSIGNED -> "email/vendor-assigned";
//                    case FEEDBACK_REQUEST -> "email/feedback-request";
//                    default -> "email/event-request";
//                };
//
//                emailService.sendTemplateEmail(
//                        user.getEmail(),
//                        "CampusConnect Notification",
//                        template,
//                        vars
//                );
//
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
//}