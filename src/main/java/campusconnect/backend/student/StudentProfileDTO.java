package campusconnect.backend.student;

import jakarta.persistence.ElementCollection;
import lombok.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {

    @Size(max = 300)
    private String bio;

    @ElementCollection
    private List<String> skills;

    private String hobbies;

    private String linkedinUrl;
    private String githubUrl;

    @NotBlank
    private String department;

    @Min(1)
    @Max(4)
    private int year;

    private MultipartFile profilePhoto;
}