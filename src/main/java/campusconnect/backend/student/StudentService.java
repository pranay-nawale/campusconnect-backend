package campusconnect.backend.student;

import campusconnect.backend.entity.*;
import campusconnect.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EventRequestRepository eventRequestRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // ------------------- EVENT REGISTRATION -------------------
    @Transactional
    public String registerForEvent(Long eventId, String email) {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student profile not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Duplicate check
        if(eventRegistrationRepository.findByStudentIdAndEventId(student.getId(), event.getId()).isPresent()){
            return "Already Registered";
        }

        // Payment check
        if(event.isPaid()){
            return "Payment Required"; // 🔒 Don't register yet
        }

        EventRegistration registration = EventRegistration.builder()
                .student(student)
                .event(event)
                .paymentDone(!event.isPaid()) // free = true, paid = false
                .paidAmount(event.isPaid() ? 0.0 : 0.0)
                .build();

        eventRegistrationRepository.save(registration);

        return "Registered Successfully";
    }

    // ------------------- STUDENT PROFILE -------------------
    @Transactional
    public StudentProfileDTO createStudentProfile(StudentProfileDTO request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (studentRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Student profile already exists");
        }

        Student student = new Student();
        student.setUser(user);

        updateStudentFields(student, request);

        studentRepository.save(student);

        return mapToDTO(student);
    }

    public StudentProfileDTO getStudentProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return mapToDTO(student);
    }

    @Transactional
    public StudentProfileDTO updateStudentProfile(StudentProfileDTO request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        updateStudentFields(student, request);

        studentRepository.save(student);

        return mapToDTO(student);
    }

    // ------------------- CONFIRMED EVENTS -------------------
    public List<EventRequest> getConfirmedEvents() {
        return eventRequestRepository.findByEventStatus(EventStatus.CONFIRMED);
    }

    // ------------------- HELPER METHODS -------------------
    private void updateStudentFields(Student student, StudentProfileDTO request) {

        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setBio(request.getBio());
        student.setSkills(request.getSkills());
        student.setHobbies(request.getHobbies());
        student.setLinkedinUrl(request.getLinkedinUrl());
        student.setGithubUrl(request.getGithubUrl());

        MultipartFile photo = request.getProfilePhoto();

        if (photo != null && !photo.isEmpty()) {
            try {
                File directory = new File(UPLOAD_DIR);
                if (!directory.exists()) directory.mkdir();

                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                String filePath = UPLOAD_DIR + fileName;

                photo.transferTo(new File(filePath));
                student.setProfilePhoto(filePath);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile photo");
            }
        }
    }

    private StudentProfileDTO mapToDTO(Student student) {
        return StudentProfileDTO.builder()
                .department(student.getDepartment())
                .year(student.getYear())
                .bio(student.getBio())
                .skills(Collections.singletonList(student.getSkills()))
                .hobbies(student.getHobbies())
                .linkedinUrl(student.getLinkedinUrl())
                .githubUrl(student.getGithubUrl())
                .build();
    }

    // STUDENT REGISTERED EVENTS FETCH
    public List<EventRequest> getRegisteredEvents(String email) {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student profile not found"));

        return eventRegistrationRepository.findAll().stream()
                .filter(er -> er.getStudent().getId().equals(student.getId()))
                .map(EventRegistration::getEvent)
                .toList();
    }
}