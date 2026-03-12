package campusconnect.backend.college;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.User;
import campusconnect.backend.entity.VerificationStatus;
import campusconnect.backend.repository.CollegeRepository;
import campusconnect.backend.repository.EventRequestRepository;
import campusconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final EventRequestRepository eventRequestRepository;

    public String registerCollege(CollegeRegistrationRequestDTO request, String email) {

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

        return "College registration submitted successfully";
    }

    public CollegeResponseDTO getCollegeByUser(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        College college = collegeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("College not found"));

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

    public EventRequestResponseDTO createEventRequest(EventRequestDTO request, String email){

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

        if(request.getMaxParticipants() <= 0){
            throw new RuntimeException("Participants must be positive");
        }

        if(eventRequestRepository.existsByTitleAndCollege(request.getTitle(), college)){
            throw new RuntimeException("Event already requested");
        }

        EventRequest eventRequest = EventRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .maxParticipants(request.getMaxParticipants())
                .category(request.getCategory())
                .status(VerificationStatus.PENDING)
                .college(college)
                .build();

        eventRequestRepository.save(eventRequest);

        return EventRequestResponseDTO.builder()
                .id(eventRequest.getId())
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .eventDate(eventRequest.getEventDate())
                .maxParticipants(eventRequest.getMaxParticipants())
                .category(eventRequest.getCategory().name())
                .status(eventRequest.getStatus().name())
                .collegeName(college.getName())
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
                        .category(event.getCategory().name())
                        .status(event.getStatus().name())
                        .collegeName(college.getName())
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

        if(request.getStatus() != VerificationStatus.PENDING){
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

        if(eventRequest.getStatus() != VerificationStatus.PENDING){
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
                .status(eventRequest.getStatus().name())
                .collegeName(college.getName())
                .build();
    }
}