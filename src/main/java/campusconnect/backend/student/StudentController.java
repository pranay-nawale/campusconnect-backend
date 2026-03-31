package campusconnect.backend.student;

import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.Student;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")
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