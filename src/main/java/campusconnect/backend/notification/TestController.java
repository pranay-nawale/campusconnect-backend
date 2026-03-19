package campusconnect.backend.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @GetMapping("/email")
    public String testEmail() throws Exception {

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Prachi");
        vars.put("eventName", "AI Hackathon");
        vars.put("eventDate", "20 March");

        emailService.sendTemplateEmail(
                "prachi.nikam24@aiml.sce.edu.in", // 👈 replace with your email
                "Test Email",
                "email/event-confirmed",
                vars
        );

        return "Email sent successfully!";
    }
}