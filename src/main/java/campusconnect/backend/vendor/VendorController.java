package campusconnect.backend.vendor;

import campusconnect.backend.entity.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/register")
    public Vendor registerVendor(@RequestBody VendorProfileRequest request,
                                 Authentication authentication) {

        return vendorService.registerVendor(authentication.getName(), request);
    }

    @GetMapping("/profile")
    public Vendor getVendorProfile(Authentication authentication) {

        return vendorService.getVendorProfile(authentication.getName());
    }

    @PutMapping("/profile")
    public Vendor updateVendorProfile(@RequestBody VendorProfileRequest request,
                                      Authentication authentication) {

        return vendorService.updateVendorProfile(authentication.getName(), request);
    }

    @GetMapping("/status")
    public String getVerificationStatus(Authentication authentication) {

        return vendorService.getVerificationStatus(authentication.getName());
    }
}