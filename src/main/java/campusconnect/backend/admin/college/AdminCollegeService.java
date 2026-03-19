package campusconnect.backend.admin.college;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.User;
import campusconnect.backend.entity.VerificationStatus;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminCollegeService {

    @Autowired
    public CollegeRepository collegeRepo;

    @Autowired
    private NotificationFacade notificationFacade;

    public AdminCollegeDTO mapToDTO(College college){
        return AdminCollegeDTO.builder()
                .id(college.getId())
                .name(college.getName())
                .universityName(college.getUniversityname())
                .city(college.getCity())
                .website(college.getWebsite())
                .officialLetterUrl(college.getOfficialLetterUrl())
                .naacCertificateUrl(college.getNaacCertificateUrl())
                .logoUrl(college.getLogoUrl())
                .verificationStatus(college.getVerificationStatus())
                .userId(college.getUser().getId())
                .userEmail(college.getUser().getEmail())
                .userEnabled(college.getUser().isEnabled())
                .build();
    }

    public List<AdminCollegeDTO> getAllColleges(VerificationStatus status){

        List<College> colleges;
        if(status != null)
            colleges = collegeRepo.findByVerificationStatus(status);
        else
            colleges = collegeRepo.findAll();

        return colleges.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AdminCollegeDTO getUserById(Long id){
        College college = collegeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("'college not found"));
        return mapToDTO(college);
    }

    public AdminCollegeDTO verifyStatus(Long id, VerificationStatus status){

        College college = collegeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("college not found"));

        college.setVerificationStatus(status);

        collegeRepo.save(college);

        // 🔔 ADD NOTIFICATION + EMAIL
        User user = college.getUser();

        Map<String, Object> vars = new HashMap<>();
        vars.put("collegeName", college.getName()); // or use "name"

        if(status == VerificationStatus.APPROVED){
            notificationFacade.notifyUser(
                    user,
                    "Your college has been verified successfully ✅",
                    NotificationType.COLLEGE_APPROVED,
                    vars,
                    true
            );
        }
        else if(status == VerificationStatus.REJECTED){
            notificationFacade.notifyUser(
                    user,
                    "Your college verification was rejected ❌",
                    NotificationType.COLLEGE_REJECTED,
                    vars,
                    true
            );
        }
        return mapToDTO(college);
    }


}
