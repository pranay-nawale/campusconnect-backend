package campusconnect.backend.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendEmailWithAttachment(
            String to,
            String subject,
            String html,
            byte[] pdf
    ) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        helper.addAttachment(
                "invoice.pdf",
                new ByteArrayResource(pdf)
        );

        mailSender.send(message);
    }
    public void sendTemplateEmail(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) throws Exception {

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        sendHtmlEmail(to, subject, html);
    }

}