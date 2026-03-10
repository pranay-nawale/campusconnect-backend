package campusconnect.backend.entity;

import jakarta.persistence.*;

@Entity
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName;

    private String category;

    private String phone;

    private String gstNumber;

    private String businessLicenseUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @OneToOne
    private User user;

}