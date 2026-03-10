package campusconnect.backend.entity;

import jakarta.persistence.*;

@Entity
public class College
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String universityname;

    private String city;

    private String website;
    private String officialLetterUrl;


    private String naacCertificateUrl;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;
}
