package campusconnect.backend.admin.vendor;

import campusconnect.backend.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminVendorDTO {

    private Long id;

    private String businessName;

    private String category;

    private String phone;

    private String gstNumber;

    private String businessLicenseUrl;

    private String brochureUrl;

    private VerificationStatus verificationStatus;

    private Long userId;

    private String userName;

    private String userEmail;

    private boolean userEnabled;

}
