package campusconnect.backend.student;

import lombok.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @Size(max = 300)
    private String bio;

    private List<String> skills;

    private String hobbies;

    private String linkedinUrl;

    private String githubUrl;

    private String rollNumber;

    private String department;

    @Min(1)
    @Max(4)
    private int year;

    private MultipartFile profilePhoto;

    private MultipartFile idCard;

    private Long collegeId;


}