package campusconnect.backend.admin.vendor;

import campusconnect.backend.entity.*;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.EventServiceRepository;
import campusconnect.backend.repository.ServiceRepository;
import campusconnect.backend.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminVendorService {

    private final VendorRepository vendorRepo;
    private final ServiceRepository serviceRepo;
    private final NotificationFacade notificationFacade;
    private final EventServiceRepository eventServiceRepo;

    public AdminVendorDTO mapToDTO(Vendor vendor) {

        return AdminVendorDTO.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .category(vendor.getCategory())
                .phone(vendor.getPhone())
                .gstNumber(vendor.getGstNumber())
                .businessLicenseUrl(vendor.getBusinessLicenseUrl())
                .brochureUrl(vendor.getBrochureUrl())
                .verificationStatus(vendor.getVerificationStatus())
                .userId(vendor.getUser() != null ? vendor.getUser().getId() : null)
                .userName(vendor.getUser() != null ? vendor.getUser().getName() : null)
                .userEmail(vendor.getUser() != null ? vendor.getUser().getEmail() : null)
                .userEnabled(vendor.getUser() != null ? vendor.getUser().isEnabled() : null)
                .build();
    }

    public List<AdminVendorDTO> getVendors(VerificationStatus status) {

        List<Vendor> vendors;

        if (status != null) {
            vendors = vendorRepo.findByVerificationStatus(status);
        } else {
            vendors = vendorRepo.findAll();
        }

        return vendors.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AdminVendorDTO getVendorById(Long id) {

        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return mapToDTO(vendor);
    }

    public AdminVendorDTO verifyStatus(Long id, VerificationStatus status) {

        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setVerificationStatus(status);

        // If admin approves vendor
        if (status == VerificationStatus.APPROVED) {

            Optional<ServiceType> existingService =
                    serviceRepo.findByService(vendor.getCategory());

            if (existingService.isEmpty()) {

                ServiceType serviceType = ServiceType.builder()
                        .service(vendor.getCategory())
                        .build();

                serviceRepo.save(serviceType);
            }
        }

        vendorRepo.save(vendor);

        // 🔔 ADD NOTIFICATION HERE
        User user = vendor.getUser();

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());

        if (status == VerificationStatus.APPROVED) {
            notificationFacade.notifyUser(
                    user,
                    "Your vendor account has been approved ✅",
                    NotificationType.VENDOR_APPROVED,
                    vars,
                    true,
                    null
            );
        }
        else if (status == VerificationStatus.REJECTED) {
            notificationFacade.notifyUser(
                    user,
                    "Your vendor verification was rejected ❌",
                    NotificationType.VENDOR_REJECTED,
                    vars,
                    true,
                    null
            );
        }

        return mapToDTO(vendor);
    }

    //get services allocated to a vendor
    public List<EventServiceDTO> vendorServices(Long vendorId){
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(()-> new RuntimeException("vendor not found"));
        List<EventService> services = eventServiceRepo.findByVendorAndEventRequest_EventStatusNot(vendor, EventStatus.COMPLETED);

        return services.stream()
                .map(this::mapToEventService)
                .collect(Collectors.toList());
    }

    public EventServiceDTO mapToEventService(EventService service){
        return EventServiceDTO.builder()
                .id(service.getId())
                .eventId(service.getEventRequest().getId())
                .eventTitle(service.getEventRequest().getTitle())
                .serviceId(service.getServiceType().getId())
                .serviceName(service.getServiceType().getService())
                .vendorId(service.getVendor().getId())
                .vendorName(service.getVendor().getBusinessName())
                .build();
    }
}