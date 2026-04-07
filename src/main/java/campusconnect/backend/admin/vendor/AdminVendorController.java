package campusconnect.backend.admin.vendor;

import campusconnect.backend.entity.EventService;
import campusconnect.backend.entity.VerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vendors")
public class AdminVendorController {

    @Autowired
    private AdminVendorService adminVendorService;

    @GetMapping
    public ResponseEntity<List<AdminVendorDTO>> getVendors(@RequestParam(required = false)
                                                               VerificationStatus status)
    {
        return ResponseEntity.ok(adminVendorService.getVendors(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminVendorDTO> getVendorById(@PathVariable Long id){
        return ResponseEntity.ok(adminVendorService.getVendorById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminVendorDTO> verifyStatus(@PathVariable Long id,
                                                       @RequestParam VerificationStatus status)
    {
        return ResponseEntity.ok(adminVendorService.verifyStatus(id,status));
    }

    @GetMapping("{id}/services")
    public ResponseEntity<List<EventServiceDTO>> getVendorServices(@PathVariable("id") Long vendorId)
    {
        return ResponseEntity.ok(adminVendorService.vendorServices(vendorId));
    }
}
