package campusconnect.backend.student;

import campusconnect.backend.common.storage.dto.FileUploadResponse;
import campusconnect.backend.common.storage.service.FileUploadService;
import campusconnect.backend.entity.*;
import campusconnect.backend.notification.*;
import campusconnect.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EventRequestRepository eventRequestRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final FileUploadService fileUploadService;
    private final NotificationFacade notificationFacade;
    private final QRCodeService qrCodeService;
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final CollegeRepository collegeRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @Transactional
    public String registerForEvent(Long eventId, String email) throws Exception {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        System.out.println("🔥 REGISTER API HIT");
        System.out.println("Event ID: " + eventId);
        System.out.println("Student: " + student.getId());

        // ✅ Already registered check
        if(eventRegistrationRepository
                .findByStudentIdAndEventId(student.getId(), event.getId()).isPresent()){
            return "Already Registered";
        }

        // ✅ FREE vs PAID check
        boolean isPaid = event.getPrice() != null && event.getPrice() > 0;

        // 🔥 STEP 1 — QR DATA
        String qrData = "EVENT:" + event.getId() +
                "|USER:" + student.getUser().getId();

        String qrBase64 = qrCodeService.generateQRCodeBase64(qrData);

        // 🔥 STEP 2 — CREATE REGISTRATION
        EventRegistration registration = EventRegistration.builder()
                .student(student)
                .event(event)
                .paymentDone(!isPaid) // free = true
                .paidAmount(isPaid ? event.getPrice() : 0.0)
                .qrCode(qrData)
                .build();

        eventRegistrationRepository.save(registration);

        // 🔥 STEP 3 — IF PAID → INVOICE + EMAIL
        if(isPaid){

            Map<String, Object> vars = new HashMap<>();
            vars.put("name", student.getUser().getName());
            vars.put("eventName", event.getTitle());
            vars.put("amount", event.getPrice());
            vars.put("date", LocalDate.now());
            vars.put("qrCode", qrBase64);

            byte[] pdf = invoiceService.generateInvoice(vars);

            emailService.sendEmailWithAttachment(
                    student.getUser().getEmail(),
                    "Event Registration Invoice",
                    "<p>You are successfully registered</p>",
                    pdf
            );
        }

        // 🔥 STEP 4 — NOTIFICATION
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", student.getUser().getName());
        vars.put("eventName", event.getTitle());

        notificationFacade.notifyUser(
                student.getUser(),
                "You registered for " + event.getTitle() + " 🎉",
                NotificationType.EVENT_REGISTERED,
                vars,
                false
        );

        return "Registered Successfully";
    }



    public void uploadDocuments(Student student,
                                MultipartFile profilePhoto,
                                MultipartFile idCard) {

//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Student student = studentRepository.findByUser(user)
//                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (profilePhoto != null && !profilePhoto.isEmpty()) {

            FileUploadResponse profile =
                    fileUploadService.uploadFile(profilePhoto,
                            "campusconnect/students/profile");

            student.setProfilePhoto(profile.getUrl());
            student.setProfilePhotoPublicId(profile.getPublicId());
        }

        if (idCard != null && !idCard.isEmpty()) {
            FileUploadResponse id =
                    fileUploadService.uploadFile(idCard,
                            "campusconnect/students/documents");

            student.setIdCardUrl(id.getUrl());
            student.setIdCardPublicId(id.getPublicId());
        }
    }

    public Student updateProfilePhoto(String email, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Profile photo is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // delete old image
        if (student.getProfilePhotoPublicId() != null) {
            fileUploadService.deleteFile(student.getProfilePhotoPublicId());
        }

        FileUploadResponse response =
                fileUploadService.uploadFile(
                        file,
                        "campusconnect/students/profile"
                );

        student.setProfilePhoto(response.getUrl());
        student.setProfilePhotoPublicId(response.getPublicId());

        return studentRepository.save(student);
    }

    public List<College> getAllColleges() {
        return collegeRepository.findAll();
    }

    // ------------------- STUDENT PROFILE -------------------
    @Transactional
    public StudentResponseDTO createStudentProfile(StudentRequestDTO request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (studentRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Student profile already exists");
        }

        Student student = new Student();
        student.setUser(user);

        updateStudentFields(student, request);

        uploadDocuments(student, request.getProfilePhoto(), request.getIdCard());

        studentRepository.save(student);

        return mapToDTO(student);
    }

    public StudentResponseDTO getStudentProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return mapToDTO(student);
    }

    @Transactional
    public StudentResponseDTO updateStudentProfile(StudentRequestDTO request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        updateStudentFields(student, request);

        uploadDocuments(student, request.getProfilePhoto(), request.getIdCard());

        studentRepository.save(student);

        return mapToDTO(student);
    }

    // ------------------- CONFIRMED EVENTS -------------------
    public List<EventRequestDTO> getConfirmedEvents() {
        List<EventRequest> events = eventRequestRepository.findByEventStatus(EventStatus.CONFIRMED);

        // Convert each EventRequest entity to DTO
        return events.stream()
                .map(event -> EventRequestDTO.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .eventDate(event.getEventDate())
                        .maxParticipants(event.getMaxParticipants())
                        .bannerUrl(event.getBannerUrl())
                        .bannerPublicId(event.getBannerPublicId())
                        .category(event.getCategory())
                        .eventStatus(event.getEventStatus())
                        .isPaid(event.isPaid())
                        .price(event.getPrice())
                        .collegeId(event.getCollege() != null ? event.getCollege().getId() : null)
                        .collegeName(event.getCollege() != null ? event.getCollege().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    // ------------------- HELPER METHODS -------------------
    private void updateStudentFields(Student student, StudentRequestDTO request) {

        student.setRollNumber(request.getRollNumber());
        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setBio(request.getBio());
        student.setSkills(request.getSkills());
        student.setHobbies(request.getHobbies());
        student.setLinkedinUrl(request.getLinkedinUrl());
        student.setGithubUrl(request.getGithubUrl());

        College college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(() -> new RuntimeException("College not found"));

        student.setCollege(college);
    }

    private StudentResponseDTO mapToDTO(Student student) {
        return StudentResponseDTO.builder()
                .userId(student.getUser() != null ? student.getUser().getId() : null)
                .userName(student.getUser() != null ? student.getUser().getName() : null)
                .userEmail(student.getUser() != null ? student.getUser().getEmail() : null)

                .collegeId(student.getCollege() != null ? student.getCollege().getId() : null)
                .collegeName(student.getCollege() != null ? student.getCollege().getName() : null)

                .bio(student.getBio())
                .skills(student.getSkills())
                .hobbies(student.getHobbies())
                .linkedinUrl(student.getLinkedinUrl())
                .githubUrl(student.getGithubUrl())
                .rollNumber(student.getRollNumber())
                .department(student.getDepartment())
                .year(student.getYear())

                .profilePhoto(student.getProfilePhoto())
                .idCardUrl(student.getIdCardUrl())

                .verificationStatus(student.getVerificationStatus())
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

    public EventRequest getEventById(Long id) {

        return eventRequestRepository
                .findByIdAndEventStatus(id, EventStatus.CONFIRMED)
                .orElseThrow(() -> new RuntimeException("Event not found or not confirmed"));
    }

}