package campusconnect.backend.student;

import campusconnect.backend.common.storage.dto.FileUploadResponse;
import campusconnect.backend.common.storage.service.FileUploadService;
import campusconnect.backend.entity.*;
import campusconnect.backend.notification.*;
import campusconnect.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import campusconnect.backend.entity.VerificationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final FeedbackRepository feedbackRepository;



    private static final String UPLOAD_DIR = "uploads/";

//    @Transactional
//    public String registerForEvent(Long eventId, String email) throws Exception {
//
//        Student student = studentRepository.findByUser(
//                userRepository.findByEmail(email)
//                        .orElseThrow(() -> new RuntimeException("User not found"))
//        ).orElseThrow(() -> new RuntimeException("Student not found"));
//
//        EventRequest event = eventRequestRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//        System.out.println("🔥 REGISTER API HIT");
//        System.out.println("Event ID: " + eventId);
//        System.out.println("Student: " + student.getId());
//
//        // ✅ Already registered check
//        if(eventRegistrationRepository
//                .findByStudentIdAndEventId(student.getId(), event.getId()).isPresent()){
//            return "Already Registered";
//        }
//
//        // ✅ FREE vs PAID check
//        boolean isPaid = event.getPrice() != null && event.getPrice() > 0;

    // ================= REGISTER EVENT =================
    @Transactional
    public String registerForEvent(Long eventId, String email) throws Exception {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ Already registered check
        if (eventRegistrationRepository
                .findByStudentIdAndEventId(student.getId(), event.getId()).isPresent()) {
            return "Already Registered";
        }

        // ✅ FREE vs PAID check
        boolean isPaid = event.getPrice() != null && event.getPrice() > 0;

        // 🔥 IF PAID → DO NOT REGISTER
        if (isPaid) {
            return "PAYMENT_REQUIRED";
        }

        // ✅ FREE EVENT → REGISTER DIRECTLY
        return createRegistration(student, event, false);
    }

//        // 🔥 STEP 1 — QR DATA
//        String qrData = "EVENT:" + event.getId() +
//                "|USER:" + student.getUser().getId();
//
//        String qrBase64 = qrCodeService.generateQRCodeBase64(qrData);
//
//        // 🔥 STEP 2 — CREATE REGISTRATION
//        EventRegistration registration = EventRegistration.builder()
//                .student(student)
//                .event(event)
//                .paymentDone(!isPaid) // free = true
//                .paidAmount(isPaid ? event.getPrice() : 0.0)
//                .qrCode(qrData)
//                .build();
//
//        eventRegistrationRepository.save(registration);
//
//        // 🔥 STEP 3 — IF PAID → INVOICE + EMAIL
//        if(isPaid){
//
//            Map<String, Object> vars = new HashMap<>();
//            vars.put("name", student.getUser().getName());
//            vars.put("eventName", event.getTitle());
//            vars.put("amount", event.getPrice());
//            vars.put("date", LocalDate.now());
//            vars.put("qrCode", qrBase64);
//
//            byte[] pdf = invoiceService.generateInvoice(vars);
//
//            emailService.sendEmailWithAttachment(
//                    student.getUser().getEmail(),
//                    "Event Registration Invoice",
//                    "<p>You are successfully registered</p>",
//                    pdf
//            );
//        }
//
//        // 🔥 STEP 4 — NOTIFICATION
//        Map<String, Object> vars = new HashMap<>();
//        vars.put("name", student.getUser().getName());
//        vars.put("eventName", event.getTitle());
//
//        notificationFacade.notifyUser(
//                student.getUser(),
//                "You registered for " + event.getTitle() + " 🎉",
//                NotificationType.EVENT_REGISTERED,
//                vars,
//                false
//        );
//
//        return "Registered Successfully";
//    }

    // ================= COMMON REGISTRATION =================
    private String createRegistration(Student student, EventRequest event, boolean paymentDone) throws Exception {

        String qrData = "EVENT:" + event.getId() +
                "|USER:" + student.getUser().getId();

        String qrBase64 = qrCodeService.generateQRCodeBase64(qrData);

        EventRegistration registration = EventRegistration.builder()
                .student(student)
                .event(event)
                .paymentDone(paymentDone)
                .paidAmount(paymentDone ? event.getPrice() : 0.0)
                .qrCode(qrData)
                .build();

        eventRegistrationRepository.save(registration);

        // ✅ SEND INVOICE ONLY IF PAID
        if (paymentDone) {

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

        // ✅ NOTIFICATION
        notificationFacade.notifyUser(
                student.getUser(),
                "You registered for " + event.getTitle() + " 🎉",
                NotificationType.EVENT_REGISTERED,
                Map.of(
                        "name", student.getUser().getName(),
                        "eventName", event.getTitle()
                ),
                false
        );

        return "Registered Successfully";
    }

    // ================= PAYMENT SUCCESS =================
    @Transactional
    public String handlePaymentSuccess(Long eventId, String email) throws Exception {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ Prevent duplicate
        if (eventRegistrationRepository
                .findByStudentIdAndEventId(student.getId(), event.getId()).isPresent()) {
            return "Already Registered";
        }

        // 🔥 REGISTER AFTER PAYMENT
        return createRegistration(student, event, true);
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
    public List<EventRequestDTO> getConfirmedEvents(String email) {

        // 🔥 Get logged-in student
        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student not found"));

        VerificationStatus sts = student.getVerificationStatus();

        String msg;
        switch (sts) {
            case PENDING:
                msg = "Your profile is under review. You cannot register for events yet.";
                break;
            case REJECTED:
                msg = "Your profile was rejected. Please contact admin.";
                break;
            case APPROVED:
                msg = "You are verified. You can register for events.";
                break;
            default:
                msg = "";
        }

        List<EventRequest> events =
                eventRequestRepository.findByEventStatus(EventStatus.CONFIRMED);

        return events.stream()
                .map(event -> {

                    boolean isRegistered = eventRegistrationRepository
                            .existsByStudentIdAndEventId(student.getId(), event.getId());

                    return EventRequestDTO.builder()
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
                            .studentStatus(sts.name())
                            .msg(msg)
                            .build();
                })
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
    public List<RegisteredEventDTO> getRegisteredEvents(String email) {

        Student student = studentRepository.findByUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        ).orElseThrow(() -> new RuntimeException("Student profile not found"));

        return eventRegistrationRepository.findAll().stream()
                .filter(er -> er.getStudent().getId().equals(student.getId()))
                .map(er -> {
                    EventRequest event = er.getEvent();

                    return new RegisteredEventDTO(
                            event.getId(),
                            event.getTitle(),
                            event.getDescription(),
                            event.getEventDate()
                    );
                })
                .toList();
    }

    public EventRequest getEventById(Long id) {

        return eventRequestRepository
                .findByIdAndEventStatus(id, EventStatus.CONFIRMED)
                .orElseThrow(() -> new RuntimeException("Event not found or not confirmed"));
    }

    //------------------------FEEDBACK--------------
    public String giveFeedback(FeedbackRequestDTO dto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 2. Fetch student from DB
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ✅ 2. Get event
        EventRequest event = eventRequestRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ 3. Check if student registered
        Optional<EventRegistration> registration =
                eventRegistrationRepository
                        .findByStudentIdAndEventId(student.getId(), event.getId());

        if (registration.isEmpty()) {
            throw new RuntimeException("You are not registered for this event");
        }

        // ✅ 4. Check event completed
        if (event.getEventDate().isAfter(LocalDate.now().atStartOfDay())) {
            throw new RuntimeException("Feedback allowed only after event is over");
        }

        // ✅ 5. Prevent duplicate feedback
        boolean exists = feedbackRepository
                .existsByStudentAndEvent(student, event);

        if (exists) {
            throw new RuntimeException("You already submitted feedback");
        }

        // ✅ 6. Save feedback
        Feedback feedback = Feedback.builder()
                .message(dto.getMessage())
                .rating(dto.getRating())
                .student(student)
                .event(event)
                .role("STUDENT")
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRepository.save(feedback);

        return "Feedback submitted successfully";
    }

    //---------GET STUDENTS FEEDBACK (COMPLETED EVENTS)--------------
    public List<FeedbackResponseDTO> getMyFeedback(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ✅ Get all registrations of this student
        List<EventRegistration> registrations =
                eventRegistrationRepository.findByStudentId(student.getId());

        return registrations.stream()
                // ✅ Only COMPLETED events
                .filter(reg -> reg.getEvent().getEventStatus() == EventStatus.COMPLETED)

                .map(reg -> {
                    EventRequest event = reg.getEvent();

                    // ✅ Check if feedback already exists
                    Optional<Feedback> feedbackOpt =
                            feedbackRepository.findByStudentAndEvent(student, event);

                    if (feedbackOpt.isPresent()) {
                        Feedback f = feedbackOpt.get();

                        return new FeedbackResponseDTO(
                                event.getId(),
                                event.getTitle(),
                                f.getRating(),
                                f.getMessage(),
                                true // ✅ already given
                        );
                    } else {
                        return new FeedbackResponseDTO(
                                event.getId(),
                                event.getTitle(),
                                null,
                                null,
                                false // ❌ not given yet
                        );
                    }
                })
                .toList();
    }
}