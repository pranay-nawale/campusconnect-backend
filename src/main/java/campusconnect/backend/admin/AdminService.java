//package campusconnect.backend.admin;
//
//import campusconnect.backend.entity.College;
//import campusconnect.backend.entity.Event;
//import campusconnect.backend.entity.EventRequest;
//import campusconnect.backend.entity.VerificationStatus;
//import campusconnect.backend.repository.CollegeRepository;
//import campusconnect.backend.repository.EventRepository;
//import campusconnect.backend.repository.EventRequestRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AdminService {
//
//    private final CollegeRepository collegeRepository;
//    private final EventRequestRepository eventRequestRepository;
//    private final EventRepository eventRepository;
//
//    // View all colleges
//    public List<College> getAllColleges(){
//        return collegeRepository.findAll();
//    }
//
//    // Approve college
//    public College approveCollege(Long id){
//
//        College college = collegeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("College not found"));
//
//        college.setVerificationStatus(VerificationStatus.APPROVED);
//
//        return collegeRepository.save(college);
//    }
//
//    // Reject college
//    public College rejectCollege(Long id){
//
//        College college = collegeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("College not found"));
//
//        college.setVerificationStatus(VerificationStatus.REJECTED);
//
//        return collegeRepository.save(college);
//    }
//
//    // View event requests
//    public List<EventRequest> getAllEventRequests(){
//        return eventRequestRepository.findAll();
//    }
//
//    // Approve event request
//    public Event approveEventRequest(Long id){
//
//        EventRequest request = eventRequestRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Event request not found"));
//
//        if(request.getStatus() == VerificationStatus.APPROVED){
//            throw new RuntimeException("Event request already approved");
//        }
//
//        request.setStatus(VerificationStatus.APPROVED);
//        eventRequestRepository.save(request);
//
//        Event event = new Event();
//
//        event.setTitle(request.getTitle());
//        event.setDescription(request.getDescription());
//        event.setEventDate(request.getEventDate());
//        event.setMaxParticipants(request.getMaxParticipants());
//        event.setCategory(request.getCategory());
//        event.setCollege(request.getCollege());
//
//        return eventRepository.save(event);
//    }
//
//    // Reject event request
//    public EventRequest rejectEventRequest(Long id){
//
//        EventRequest request = eventRequestRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Event request not found"));
//
//        request.setStatus(VerificationStatus.REJECTED);
//
//        return eventRequestRepository.save(request);
//    }
//
//
//}