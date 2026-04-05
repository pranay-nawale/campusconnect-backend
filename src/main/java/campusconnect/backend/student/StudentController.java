package campusconnect.backend.student;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.Feedback;
import campusconnect.backend.repository.CollegeRepository;
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

    @GetMapping("/colleges")
    public ResponseEntity<List<College>> getAllColleges() {
        return ResponseEntity.ok(studentService.getAllColleges());
    }

    // ------------------- CREATE STUDENT PROFILE -------------------
    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<StudentResponseDTO> createStudentProfile(
            @Valid @ModelAttribute StudentRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        StudentResponseDTO response = studentService.createStudentProfile(request, email);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET STUDENT PROFILE -------------------
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized - token missing or invalid");
        }

        String email = authentication.getName();
        StudentResponseDTO response = studentService.getStudentProfile(email);
        return ResponseEntity.ok(response);
    }

    // ------------------- UPDATE STUDENT PROFILE -------------------
    @PatchMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(
            @ModelAttribute StudentRequestDTO request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            StudentResponseDTO response = studentService.updateStudentProfile(request, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 VERY IMPORTANT
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ------------------- GET CONFIRMED EVENTS -------------------
    @GetMapping("/events")
    public ResponseEntity<List<EventRequestDTO>> getEvents(Authentication authentication) {

        String email = authentication.getName();

        List<EventRequestDTO> events =
                studentService.getConfirmedEvents(email);

        return ResponseEntity.ok(events);
    }

    // ------------------- REGISTER FOR EVENT -------------------
    @PostMapping("/events/register/{eventId}")
    public ResponseEntity<String> registerEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) throws Exception {

        String email = authentication.getName();
        String message = studentService.registerForEvent(eventId, email);

        return ResponseEntity.ok(message);
    }

    // ------------------- GET EVENTS REGISTERED BY STUDENT -------------------
    @GetMapping("/events/registered")
    public ResponseEntity<List<EventRequest>> getRegisteredEvents(Authentication authentication) {

        String email = authentication.getName();

        // Fetch all registered events including free ones
        List<EventRequest> registeredEvents = studentService.getRegisteredEvents(email);

        return ResponseEntity.ok(registeredEvents);
    }

    //----------------------- FEEDBACK ----------------------------
    @PostMapping("/feedback")
    public ResponseEntity<?> giveFeedback(
            @RequestBody FeedbackRequestDTO dto,
            Authentication auth) {

        String response = studentService.giveFeedback(dto, auth);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET EVENT BY ID -------------------
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            EventRequest event = studentService.getEventById(id);
            return ResponseEntity.ok(event);

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
