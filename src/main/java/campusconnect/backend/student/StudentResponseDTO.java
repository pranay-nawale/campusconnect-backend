package campusconnect.backend.student;

import lombok.*;
import jakarta.validation.constraints.*;
import campusconnect.backend.entity.VerificationStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long userId;
    private String userName;
    private String userEmail;

    private Long collegeId;
    private String collegeName;

    @Size(max = 300)
    private String bio;

    private List<String> skills;

    private String hobbies;

    private String linkedinUrl;

    private String githubUrl;

    private String rollNumber;

    @NotBlank
    private String department;

    @Min(1)
    @Max(4)
    private int year;

    private String profilePhoto;

    private String idCardUrl;

    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
}