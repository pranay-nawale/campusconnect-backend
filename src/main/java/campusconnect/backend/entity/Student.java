package campusconnect.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rollNumber;

    private String department;

    private int year;
    
    private String bio;
    
    private List<String> skills;
    
    private String hobbies;

    private String linkedinUrl;
    
    private String githubUrl;

    private String profilePhoto;
    private String profilePhotoPublicId;  // ADD

    private String idCardUrl;
    private String idCardPublicId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @OneToOne
    private User user;

    @ManyToOne
    private College college;

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

    public void setSkills(List<String> skills) {

    }
}