package campusconnect.backend.admin;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.Event;
import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.VerificationStatus;
import campusconnect.backend.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {

    private final CollegeRepository collegeRepository;
    private final AdminService adminService;

    @PutMapping("/verify-college/{id}")
    public String verifyCollege(@PathVariable Long id) {

        College college = collegeRepository.findById(id).orElseThrow();

        college.setVerificationStatus(VerificationStatus.APPROVED);

        collegeRepository.save(college);

        return "College verified successfully";
    }


    // View colleges
    @GetMapping("/colleges")
    public List<College> getAllColleges(){
        return adminService.getAllColleges();
    }

    // Approve college
    @PutMapping("/colleges/{id}/approve")
    public College approveCollege(@PathVariable Long id){
        return adminService.approveCollege(id);
    }

    // Reject college
    @PutMapping("/colleges/{id}/reject")
    public College rejectCollege(@PathVariable Long id){
        return adminService.rejectCollege(id);
    }

    // View event requests
    @GetMapping("/event-requests")
    public List<EventRequest> getAllEventRequests(){
        return adminService.getAllEventRequests();
    }

    // Approve event request
    @PutMapping("/event-requests/{id}/approve")
    public Event approveEventRequest(@PathVariable Long id){
        return adminService.approveEventRequest(id);
    }

    // Reject event request
    @PutMapping("/event-requests/{id}/reject")
    public EventRequest rejectEventRequest(@PathVariable Long id){
        return adminService.rejectEventRequest(id);
    }



    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin API Working";
    }


}