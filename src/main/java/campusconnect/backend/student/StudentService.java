package campusconnect.backend.student;

import org.springframework.stereotype.Service;

import campusconnect.backend.dto.StudentProfileDTO;
import campusconnect.backend.entity.Student;
import campusconnect.backend.entity.User;
import campusconnect.backend.repository.StudentRepository;
import campusconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentProfileDTO getStudentProfile(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return StudentProfileDTO.builder()
                .department(student.getDepartment())
                .year(student.getYear())
                .bio(student.getBio())
                .skills(student.getSkills())
                .hobbies(student.getHobbies())
                .linkedinUrl(student.getLinkedinUrl())
                .githubUrl(student.getGithubUrl())
                .build();
    }

    public StudentProfileDTO updateStudentProfile(
            StudentProfileDTO request,
            String email
    ){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setBio(request.getBio());
        student.setSkills(request.getSkills());
        student.setHobbies(request.getHobbies());
        student.setLinkedinUrl(request.getLinkedinUrl());
        student.setGithubUrl(request.getGithubUrl());

        studentRepository.save(student);

        return StudentProfileDTO.builder()
                .department(student.getDepartment())
                .year(student.getYear())
                .bio(student.getBio())
                .skills(student.getSkills())
                .hobbies(student.getHobbies())
                .linkedinUrl(student.getLinkedinUrl())
                .githubUrl(student.getGithubUrl())
                .build();
    }
}