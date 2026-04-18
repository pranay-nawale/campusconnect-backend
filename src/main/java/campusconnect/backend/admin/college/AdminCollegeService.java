package campusconnect.backend.admin.college;

import campusconnect.backend.college.EventRequestResponseDTO;
import campusconnect.backend.entity.*;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.CollegeRepository;
import campusconnect.backend.repository.EventRequestRepository;
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
    private EventRequestRepository eventRequestRepository;

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
                    true,
                    null   // ✅ FIX
            );
        }
        else if(status == VerificationStatus.REJECTED){
            notificationFacade.notifyUser(
                    user,
                    "Your college verification was rejected ❌",
                    NotificationType.COLLEGE_REJECTED,
                    vars,
                    true,
                    null   // ✅ FIX
            );
        }
        return mapToDTO(college);
    }

    public String approveReschedule(Long eventId) {

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(event.getEventStatus() != EventStatus.RESCHEDULED){
            throw new RuntimeException("Not a reschedule request");
        }

        event.setEventStatus(EventStatus.PLANNED);
        eventRequestRepository.save(event);

        User user = event.getCollege().getUser();

        notificationFacade.notifyUser(
                user,
                "Your reschedule request has been approved ✅",
                NotificationType.EVENT_APPROVED,
                Map.of("eventName", event.getTitle()),
                true,
                null
        );

        return "Reschedule approved";
    }

    public String rejectReschedule(Long eventId) {

        EventRequest event = eventRequestRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(event.getEventStatus() != EventStatus.RESCHEDULED){
            throw new RuntimeException("Not a reschedule request");
        }

        event.setEventStatus(EventStatus.REJECTED);
        eventRequestRepository.save(event);

        User user = event.getCollege().getUser();

        notificationFacade.notifyUser(
                user,
                "Your reschedule request was rejected ❌",
                NotificationType.EVENT_REJECTED,
                Map.of("eventName", event.getTitle()),
                true,
                null
        );

        return "Reschedule rejected";
    }

    public List<EventRequestResponseDTO> getRescheduledEvents() {
        return eventRequestRepository.findByEventStatus(EventStatus.RESCHEDULED)
                .stream()
                .map(event -> EventRequestResponseDTO.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .eventDate(event.getEventDate())
                        .status(event.getEventStatus().name())
                        .collegeName(event.getCollege().getName())
                        .build())
                .toList();
    }


}
