package campusconnect.backend.vendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProfileRequest {
    private String businessName;
    private String category;
    private String phone;
    private String gstNumber;
    private String businessLicenseUrl;
}
