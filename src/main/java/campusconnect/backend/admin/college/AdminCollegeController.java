package campusconnect.backend.admin.college;

import campusconnect.backend.college.EventRequestResponseDTO;
import campusconnect.backend.entity.VerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/colleges")
public class AdminCollegeController {

    @Autowired
    private AdminCollegeService adminCollegeService;

    @GetMapping
    public ResponseEntity<List<AdminCollegeDTO>> getALlColleges(
            @RequestParam(required = false)VerificationStatus status)
    {
        return ResponseEntity.ok(adminCollegeService.getAllColleges(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminCollegeDTO> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(adminCollegeService.getUserById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminCollegeDTO> verifyStatus(@PathVariable Long id,
                                                        @RequestParam VerificationStatus status){
        return ResponseEntity.ok(adminCollegeService.verifyStatus(id,status));
    }

    @PutMapping("/events/{id}/approve-reschedule")
    public ResponseEntity<String> approveReschedule(@PathVariable Long id) {
        return ResponseEntity.ok(adminCollegeService.approveReschedule(id));
    }

    @PutMapping("/events/{id}/reject-reschedule")
    public ResponseEntity<String> rejectReschedule(@PathVariable Long id) {
        return ResponseEntity.ok(adminCollegeService.rejectReschedule(id));
    }

    @GetMapping("/events/reschedule-requests")
    public List<EventRequestResponseDTO> getRescheduledEvents() {
        return adminCollegeService.getRescheduledEvents();
    }

}
