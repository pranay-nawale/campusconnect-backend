package campusconnect.backend.student;

import campusconnect.backend.entity.EventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

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
    public ResponseEntity<StudentResponseDTO> getProfile(Authentication authentication) {

        String email = authentication.getName();
        StudentResponseDTO response = studentService.getStudentProfile(email);
        return ResponseEntity.ok(response);
    }

    // ------------------- UPDATE STUDENT PROFILE -------------------
    @PatchMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<StudentResponseDTO> updateProfile(
            @Valid @ModelAttribute StudentRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        StudentResponseDTO response = studentService.updateStudentProfile(request, email);
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
    ) throws Exception {

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

    //---------------  STUDENT FEEDBACK  ---------------------
//    @PostMapping("/feedback")
//    public String giveFeedback(
//            @RequestParam Long studentId,
//            @RequestParam Long eventId,
//            @RequestParam String message,
//            @RequestParam int rating){
//
//        return studentService.submitFeedback(studentId,eventId,message,rating);
//    }
}