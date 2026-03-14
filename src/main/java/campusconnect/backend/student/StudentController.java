package campusconnect.backend.student;

import campusconnect.backend.dto.StudentProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Get student profile
    @GetMapping("/profile")
    public StudentProfileDTO getProfile(@RequestParam String email){
        return studentService.getStudentProfile(email);
    }

    // Update student profile
    @PutMapping("/profile")
    public StudentProfileDTO updateProfile(
            @RequestBody StudentProfileDTO request,
            @RequestParam String email
    ){
        return studentService.updateStudentProfile(request, email);
    }

}