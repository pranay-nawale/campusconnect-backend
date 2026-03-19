package campusconnect.backend.admin.student;

import campusconnect.backend.entity.Student;
import campusconnect.backend.entity.User;
import campusconnect.backend.entity.VerificationStatus;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminStudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private NotificationFacade notificationFacade;

    public AdminStudentDTO mapToDTO(Student student) {
        return AdminStudentDTO.builder()
                .id(student.getId())
                .rollNumber(student.getRollNumber())
                .department(student.getDepartment())
                .year(student.getYear())
                .bio(student.getBio())
                .skills(student.getSkills())
                .hobbies(student.getHobbies())
                .linkedinUrl(student.getLinkedinUrl())
                .githubUrl(student.getGithubUrl())
                .profilePhoto(student.getProfilePhoto())
                .idCardUrl(student.getIdCardUrl())
                .verificationStatus(student.getVerificationStatus())
                .userId(student.getUser().getId())
                .userName(student.getUser().getName())
                .userEmail(student.getUser().getEmail())
                .userEnabled(student.getUser().isEnabled())
                .CollegeId(student.getCollege() != null ? student.getCollege().getId() : null)
                .CollegeName(student.getCollege() != null ? student.getCollege().getName() : null)
                .build();
    }

    public List<AdminStudentDTO> getStudents(VerificationStatus status){

        List<Student> students;

        if(status != null)
            students = studentRepo.findByVerificationStatus(status);
        else
            students = studentRepo.findAll();

        return students.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AdminStudentDTO getStudentById(Long id){
        Student student = studentRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Student not found"));
        return mapToDTO(student);
    }

    public AdminStudentDTO verifyStatus(Long id, VerificationStatus status){
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setVerificationStatus(status);
        studentRepo.save(student);

        // 🔔 Notification logic
// 🔔 Notification logic
        User user = student.getUser();

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());

        if(status == VerificationStatus.APPROVED){
            notificationFacade.notifyUser(
                    user,
                    "Your student account has been verified ✅",
                    NotificationType.STUDENT_APPROVED, // ✅ better
                    vars,
                    true
            );
        }
        else if(status == VerificationStatus.REJECTED){
            notificationFacade.notifyUser(
                    user,
                    "Your student verification was rejected ❌",
                    NotificationType.STUDENT_REJECTED, // ✅ better
                    vars,
                    false
            );
        }

        return mapToDTO(student);
    }
}
