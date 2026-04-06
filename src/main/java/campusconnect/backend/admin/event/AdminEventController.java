package campusconnect.backend.admin.event;

import campusconnect.backend.admin.vendor.AdminVendorDTO;
import campusconnect.backend.entity.EventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    @Autowired
    private AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<List<AdminEventDTO>> getEvents(@RequestParam(required = false)
                                                         EventStatus status)
    {
        return ResponseEntity.ok(adminEventService.getEvents(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminEventDTO> getEventById(@PathVariable Long id)
    {
        return ResponseEntity.ok(adminEventService.getEventById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminEventDTO> updateStatus(@PathVariable Long id,
                                                      @RequestParam EventStatus status)
    {
        return ResponseEntity.ok(adminEventService.updateStatus(id, status));
    }

    @PostMapping("/{id}/event-plan")
    public ResponseEntity<AdminEventDTO> uploadEventPlan(@PathVariable("id") Long eventId,
                                                         @RequestParam MultipartFile file)
    {
        return ResponseEntity.ok(adminEventService.uploadEventPlan(eventId,file));
    }

    @GetMapping("/{eventId}/services")
    public List<AdminEventServiceDTO> getServicesOfEvent(@PathVariable Long eventId) {
        return adminEventService.getServicesOfEvent(eventId);
    }

    @PatchMapping("/service-vendor/{eventId}/{serviceId}")
    public ResponseEntity<AdminEventServiceDTO> assignVendor(@PathVariable Long eventId,
                                                     @PathVariable Long serviceId,
                                                     @RequestParam(required = false) Long vendorId)
    {
        return  ResponseEntity.ok(adminEventService.assignVendor(eventId, serviceId, vendorId));
    }

    @GetMapping("/service/{serviceId}/vendors")
    public ResponseEntity<List<AdminVendorDTO>> getServiceVendors(@PathVariable Long serviceId)
    {
        return  ResponseEntity.ok(adminEventService.getServiceVendors(serviceId));
    }

    @GetMapping("/event-services")
    public ResponseEntity<List<AdminEventServiceDTO>> getEventServices(){
        return ResponseEntity.ok(adminEventService.getEventServices());
    }

    @GetMapping("/event-services/{id}")
        public ResponseEntity<AdminEventServiceDTO> getEventServiceById(@PathVariable Long id){
            return ResponseEntity.ok(adminEventService.getEventServiceById(id));
        }

}
