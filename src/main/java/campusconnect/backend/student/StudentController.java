package campusconnect.backend.student;

import campusconnect.backend.entity.EventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ------------------- CREATE STUDENT PROFILE -------------------
    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<StudentProfileDTO> createProfile(
            @Valid @ModelAttribute StudentProfileDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        StudentProfileDTO response = studentService.createStudentProfile(request, email);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET STUDENT PROFILE -------------------
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDTO> getProfile(Authentication authentication) {

        String email = authentication.getName();
        StudentProfileDTO response = studentService.getStudentProfile(email);
        return ResponseEntity.ok(response);
    }

    // ------------------- UPDATE STUDENT PROFILE -------------------
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<StudentProfileDTO> updateProfile(
            @Valid @ModelAttribute StudentProfileDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        StudentProfileDTO response = studentService.updateStudentProfile(request, email);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET CONFIRMED EVENTS -------------------
    @GetMapping("/events")
    public ResponseEntity<List<EventRequest>> getEvents() {
        List<EventRequest> events = studentService.getConfirmedEvents();
        return ResponseEntity.ok(events);
    }

    // ------------------- REGISTER FOR EVENT -------------------
    @PostMapping("/events/register/{eventId}")
    public ResponseEntity<String> registerEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {

        String email = authentication.getName();
        String message = studentService.registerForEvent(eventId, email);

        return ResponseEntity.ok(message);
    }

    // ------------------- GET EVENTS REGISTERED BY STUDENT -------------------
    @GetMapping("/events/registered")
    public ResponseEntity<List<EventRequest>> getRegisteredEvents(
            Authentication authentication
    ) {

        String email = authentication.getName();
        List<EventRequest> registeredEvents = studentService.getRegisteredEvents(email);

        return ResponseEntity.ok(registeredEvents);
    }
}