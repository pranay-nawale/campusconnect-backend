package campusconnect.backend.entity;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rollNumber;

    private String department;

    private int year;

    private String idCardUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @OneToOne
    private User user;

    @ManyToOne
    private College college;

}