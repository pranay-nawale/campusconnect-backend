package campusconnect.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProfileDTO {

    private String bio;
    private String skills;
    private String hobbies;

    private String linkedinUrl;
    private String githubUrl;

    private String department;
    private int year;

}