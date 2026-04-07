package campusconnect.backend.college;

import campusconnect.backend.common.storage.dto.FileUploadResponse;
import campusconnect.backend.common.storage.service.FileUploadService;
import campusconnect.backend.entity.*;
import campusconnect.backend.notification.*;
import campusconnect.backend.repository.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import campusconnect.backend.dto.EventPaymentDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final EventRequestRepository eventRequestRepository;
    private final ServiceRepository serviceRepository;
    private final EventServiceRepository eventServiceRepository;
    private final EventPaymentRepository eventPaymentRepository;
    private final EmailService emailService;
    private final FileUploadService  fileUploadService;
    private final NotificationFacade notificationFacade;
    private final QRCodeService qrCodeService;
    private final InvoiceService invoiceService;

    public String registerCollege(CollegeRegistrationRequestDTO request, String email) throws MessagingException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = College.builder()
                .name(request.getName())
                .universityname(request.getUniversityname())
                .city(request.getCity())
                .website(request.getWebsite())
                .officialLetterUrl(request.getOfficialLetterUrl())
                .naacCertificateUrl(request.getNaacCertificateUrl())
                .logoUrl(request.getLogoUrl())
                .verificationStatus(VerificationStatus.PENDING)
                .user(user)
                .build();

        collegeRepository.save(college);

        String html = """
            <html>
            <body style="font-family:Arial;background:#f4f6f8;padding:30px;">
            <table width="100%" style="max-width:600px;margin:auto;background:white;padding:30px;border-radius:8px;">
            
            <tr>
            <td align="center">
            <h2 style="color:#4CAF50;">CampusConnect</h2>
            </td>
            </tr>
            
            <tr>
            <td>
            
            <p>Hello,</p>
            
            <p>Your college registration has been submitted successfully.</p>
            
            <p style="background:#f1f1f1;padding:15px;border-radius:6px;">
            Our admin team is reviewing your documents.
            </p>
            
            <hr>
            
            <p style="font-size:12px;color:gray;">
            CampusConnect Team
            </p>
            
            </td>
            </tr>
            
            </table>
            </body>
            </html>
            """;

        emailService.sendHtmlEmail(
                user.getEmail(),
                "College Registration Submitted",
                html
        );

        return "College registration submitted successfully";
    }


    public College uploadDocument(
            String email,
            MultipartFile file,
            String type   // LOGO / NAAC / LETTER
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College profile not found"));

        // 🔥 decide folder based on type
        String folder = switch (type.toUpperCase()) {
            case "LOGO" -> "campusconnect/colleges/logo";
            case "NAAC" -> "campusconnect/colleges/documents";
            case "LETTER" -> "campusconnect/colleges/documents";
            default -> throw new RuntimeException("Invalid document type");
        };

        // 🔥 delete old file
        switch (type.toUpperCase()) {
            case "LOGO" -> {
                if (college.getLogoPublicId() != null) {
                    fileUploadService.deleteFile(college.getLogoPublicId());
                }
            }
            case "NAAC" -> {
                if (college.getNaacCertificatePublicId() != null) {
                    fileUploadService.deleteFile(college.getNaacCertificatePublicId());
                }
            }
            case "LETTER" -> {
                if (college.getOfficialLetterPublicId() != null) {
                    fileUploadService.deleteFile(college.getOfficialLetterPublicId());
                }
            }
        }

        // 🔥 upload
        FileUploadResponse response =
                fileUploadService.uploadFile(file, folder);

        // 🔥 set values
        switch (type.toUpperCase()) {
            case "LOGO" -> {
                college.setLogoUrl(response.getUrl());
                college.setLogoPublicId(response.getPublicId());
            }
            case "NAAC" -> {
                college.setNaacCertificateUrl(response.getUrl());
                college.setNaacCertificatePublicId(response.getPublicId());
            }
            case "LETTER" -> {
                college.setOfficialLetterUrl(response.getUrl());
                college.setOfficialLetterPublicId(response.getPublicId());
            }
        }

        return collegeRepository.save(college);
    }

    public CollegeResponseDTO getCollegeByUser(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        System.out.println("COLLEGE: " + college);

        return CollegeResponseDTO.builder()
                .id(college.getId())
                .name(college.getName())
                .universityname(college.getUniversityname())
                .city(college.getCity())
                .website(college.getWebsite())
                .logoUrl(college.getLogoUrl())
                .naacCertificateUrl(college.getNaacCertificateUrl())
                .officialLetterUrl(college.getOfficialLetterUrl())
                .verificationStatus(college.getVerificationStatus().name())

                // 🔥 ADD THESE
                .email(college.getUser().getEmail())
                .createdAt(college.getUser().getCreatedAt().toLocalDate().toString())

                .build();
    }

    public CollegeResponseDTO updateCollegeProfile(
            CollegeUpdateDTO request,
            String email
    ){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        if(college.getVerificationStatus() == VerificationStatus.REJECTED){
            throw new RuntimeException("Rejected college profile cannot be updated");
        }

        college.setCity(request.getCity());
        college.setWebsite(request.getWebsite());
        college.setLogoUrl(request.getLogoUrl());

        collegeRepository.save(college);

        return CollegeResponseDTO.builder()
                .id(college.getId())
                .name(college.getName())
                .universityname(college.getUniversityname())
                .city(college.getCity())
                .website(college.getWebsite())
                .logoUrl(college.getLogoUrl())
                .verificationStatus(college.getVerificationStatus().name())
                .build();
    }

    public EventRequestResponseDTO createEventRequest(EventRequestDTO request,MultipartFile banner, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        if(college.getVerificationStatus() != VerificationStatus.APPROVED){
            throw new RuntimeException("College is not verified yet");
        }

        if(request.getEventDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Event date must be in future");
        }

//        if(request.getMaxParticipants() <= 0){
//            throw new RuntimeException("Participants must be positive");
//        }

        if(eventRequestRepository.existsByTitleAndCollege(request.getTitle(), college)){
            throw new RuntimeException("Event already requested");
        }

        FileUploadResponse bannerResponse = null;

        if (banner != null && !banner.isEmpty()) {
            bannerResponse = fileUploadService.uploadFile(
                    banner,
                    "campusconnect/events/banners"
            );
        }

        // Create EventRequest
        EventRequest eventRequest = EventRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .maxParticipants(request.getMaxParticipants())
                .category(request.getCategory())

                // ✅ FIXED HERE
                .isPaid(request.isPaid())
                .price(request.isPaid() ? request.getPrice() : 0)

                .eventStatus(EventStatus.PENDING)
                .bannerUrl(bannerResponse != null ? bannerResponse.getUrl() : null)
                .bannerPublicId(bannerResponse != null ? bannerResponse.getPublicId() : null)
                .college(college)
                .build();

        eventRequestRepository.save(eventRequest);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("eventName", eventRequest.getTitle());

        notificationFacade.notifyUser(
                user,
                "Your event request has been submitted 📩",
                NotificationType.EVENT_REGISTERED,
                vars,
                true,
                null
        );

        // Create EventService entries
        if(request.getServiceIds() != null && !request.getServiceIds().isEmpty()){

            for(Long serviceId : request.getServiceIds()){

                ServiceType service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found"));

                EventService eventService = EventService.builder()
                        .eventRequest(eventRequest)
                        .serviceType(service)
                        .vendor(null) // vendor assigned later by admin
                        .build();

                eventServiceRepository.save(eventService);
            }
        }

        return EventRequestResponseDTO.builder()
                .id(eventRequest.getId())
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .eventDate(eventRequest.getEventDate())
                .maxParticipants(eventRequest.getMaxParticipants())

                .category(eventRequest.getCategory() != null
                        ? eventRequest.getCategory().name()
                        : null)

                .status(eventRequest.getEventStatus() != null
                        ? eventRequest.getEventStatus().name()
                        : null)

                .collegeName(college.getName())
                .isPaid(eventRequest.isPaid())
                .price(eventRequest.getPrice() != null ? eventRequest.getPrice() : 0)
                .bannerUrl(eventRequest.getBannerUrl())
                .build();
    }

    public List<EventRequestResponseDTO> getCollegeEventRequests(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        return eventRequestRepository.findByCollege(college)
                .stream()
                .map(event -> EventRequestResponseDTO.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .eventDate(event.getEventDate())
                        .maxParticipants(event.getMaxParticipants())

                        .category(event.getCategory() != null
                                ? event.getCategory().name()
                                : "UNKNOWN")

                        .status(event.getEventStatus() != null
                                ? event.getEventStatus().name()
                                : "PENDING")

                        .collegeName(college.getName())

                        .isPaid(event.isPaid()) // ✅ FIXED
                        .price(event.getPrice() != null ? event.getPrice() : 0)
                        .bannerUrl(event.getBannerUrl())

                        .build())
                .toList();
    }

    public String deleteEventRequest(Long requestId, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        EventRequest request = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Event request not found"));

        if(!request.getCollege().getId().equals(college.getId())){
            throw new RuntimeException("You cannot delete this request");
        }

        if(request.getEventStatus() != EventStatus.PENDING){
            throw new RuntimeException("Only pending requests can be deleted");
        }

        eventRequestRepository.delete(request);

        return "Event request deleted successfully";
    }

    public EventRequestResponseDTO updateEventRequest(
            Long requestId,
            EventRequestDTO request,
            String email
    ){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        EventRequest eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Event request not found"));

        if(!eventRequest.getCollege().getId().equals(college.getId())){
            throw new RuntimeException("You cannot edit this request");
        }

        if(eventRequest.getEventStatus() != EventStatus.PENDING){
            throw new RuntimeException("Only pending requests can be edited");
        }

        if(request.getEventDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Event date must be in future");
        }

        if(request.getMaxParticipants() <= 0){
            throw new RuntimeException("Participants must be positive");
        }

        eventRequest.setTitle(request.getTitle());
        eventRequest.setDescription(request.getDescription());
        eventRequest.setEventDate(request.getEventDate());
        eventRequest.setMaxParticipants(request.getMaxParticipants());
        eventRequest.setCategory(request.getCategory());

        eventRequestRepository.save(eventRequest);

        return EventRequestResponseDTO.builder()
                .id(eventRequest.getId())
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .eventDate(eventRequest.getEventDate())
                .maxParticipants(eventRequest.getMaxParticipants())
                .category(eventRequest.getCategory().name())
                .status(eventRequest.getEventStatus().name())
                .collegeName(college.getName())
                .build();
    }

    public String confirmEventPlan(Long eventId,
                                   EventPaymentDTO paymentDTO,
                                   String email) throws Exception {



        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        System.out.println("Event College: " + event.getCollege().getId());
        System.out.println("User College: " + college.getId());

        if(!event.getCollege().getId().equals(college.getId())){
            throw new RuntimeException("You cannot confirm this event");
        }

        if(event.getEventStatus() != EventStatus.PLANNED){
            throw new RuntimeException("Event plan is not ready yet");
        }


        EventPayment payment = EventPayment.builder()
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionId(paymentDTO.getTransactionId())
                .paymentDate(LocalDateTime.now())
                .eventRequest(event)
                .build();

        eventPaymentRepository.save(payment);

        event.setEventStatus(EventStatus.CONFIRMED);

        eventRequestRepository.save(event);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("eventName", event.getTitle());
        vars.put("amount", payment.getAmount());
        // optional if you have it
        vars.put("transactionId", payment.getTransactionId());

        notificationFacade.notifyUser(
                user,
                "Your event has been confirmed after payment 🎉",
                NotificationType.PAYMENT_SUCCESS,
                vars,
                true,
                null
        );

        return "Event confirmed after advance payment";
    }

    public String rejectEventPlan(Long eventId, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(!event.getCollege().getId().equals(college.getId())){
            throw new RuntimeException("You cannot reject this event");
        }

        if(event.getEventStatus() != EventStatus.PLANNED){
            throw new RuntimeException("Event cannot be rejected now");
        }

        event.setEventStatus(EventStatus.REJECTED);

        eventRequestRepository.save(event);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("eventName", event.getTitle());

        notificationFacade.notifyUser(
                user,
                "Your event plan has been rejected ❌",
                NotificationType.EVENT_REJECTED,
                vars,
                false,
                null
        );

        return "Event plan rejected";
    }

    public String requestReschedule(Long eventId,
                                    LocalDateTime newDate,
                                    String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(!event.getCollege().getId().equals(college.getId())){
            throw new RuntimeException("You cannot modify this event");
        }

        if(newDate.isBefore(LocalDateTime.now())){
            throw new RuntimeException("New date must be in future");
        }

        event.setEventDate(newDate);
        event.setEventStatus(EventStatus.RESCHEDULED);

        eventRequestRepository.save(event);
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("eventName", event.getTitle());
        vars.put("eventDate", newDate); // or event.getEventDate()

        notificationFacade.notifyUser(
                user,
                "Your event reschedule request has been sent 🔄",
                NotificationType.EVENT_REMINDER,
                vars,
                false,
                null
        );

        return "Event reschedule request sent";
    }


}