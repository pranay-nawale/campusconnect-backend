package campusconnect.backend.admin.vendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventServiceDTO {

   private Long id;

   private Long eventId;
   private String eventTitle;

   private Long serviceId;
   private String serviceName;

   private Long vendorId;
   private String vendorName;
}
