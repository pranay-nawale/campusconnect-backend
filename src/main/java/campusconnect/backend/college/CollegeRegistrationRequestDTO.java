package campusconnect.backend.college;

import lombok.Data;

@Data
public class CollegeRegistrationRequestDTO {

    private String name;
    private String universityname;
    private String city;
    private String website;
    private String officialLetterUrl;
    private String naacCertificateUrl;
    private String logoUrl;

}