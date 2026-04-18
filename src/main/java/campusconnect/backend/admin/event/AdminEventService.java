package campusconnect.backend.admin.event;

import campusconnect.backend.admin.vendor.AdminVendorDTO;
import campusconnect.backend.common.storage.dto.FileUploadResponse;
import campusconnect.backend.common.storage.service.FileUploadService;
import campusconnect.backend.entity.*;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.EventRequestRepository;
import campusconnect.backend.repository.EventServiceRepository;
import campusconnect.backend.repository.ServiceRepository;
import campusconnect.backend.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminEventService {

    @Autowired
    private EventRequestRepository eventRequestRepo;

    @Autowired
    private EventServiceRepository eventServiceRepo;

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private NotificationFacade notificationFacade;

    @Autowired
    private FileUploadService fileUploadService;


    public AdminEventDTO mapToDTO(EventRequest event){
        return AdminEventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .maxParticipants(event.getMaxParticipants())
                .category(event.getCategory())
                .status(event.getEventStatus())
                .collegeId(event.getCollege().getId())
                .collegeName(event.getCollege().getName())
                .bannerUrl(event.getBannerUrl() != null ? event.getBannerUrl() : null)
                .eventPlanUrl(event.getEventPlanUrl() != null ? event.getEventPlanUrl() : null)
                .build();
    }

    public List<AdminEventDTO> getEvents(EventStatus status){

        List<EventRequest> events;

        if(status != null)
            events = eventRequestRepo.findByEventStatus(status);
        else
            events = eventRequestRepo.findAll();

        LocalDateTime now = LocalDateTime.now();

        List<EventRequest> confirmedEvents = eventRequestRepo.findByEventStatus(EventStatus.CONFIRMED);

        List<EventRequest> updatedEvents = confirmedEvents.stream()
                .filter(event -> event.getEventDate().isBefore(now))
                .peek(event -> event.setEventStatus(EventStatus.COMPLETED))
                .collect(Collectors.toList());

        // ✅ Save only if needed
        if(!updatedEvents.isEmpty()){
            eventRequestRepo.saveAll(updatedEvents);
        }

        return events.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AdminEventDTO getEventById(Long id){
        EventRequest event = eventRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        return mapToDTO(event);
    }

    public AdminEventDTO updateStatus(Long id, EventStatus status){
        EventRequest request = eventRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        validateStatusTransition(request.getEventStatus(), status);
        request.setEventStatus(status);
        eventRequestRepo.save(request);



// 🔔 Notification logic
        User collegeUser = request.getCollege().getUser();

// 🔥 Create variables for template
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", collegeUser.getName());
        vars.put("eventName", request.getTitle());
        vars.put("eventDate", request.getEventDate());

        if(status == EventStatus.CONFIRMED){
            notificationFacade.notifyUser(
                    collegeUser,
                    "Your event has been confirmed 🎉",
                    NotificationType.EVENT_CONFIRMED,
                    vars,
                    true,
                    null
            );
        }
        else if(status == EventStatus.REJECTED){
            notificationFacade.notifyUser(
                    collegeUser,
                    "Your event has been rejected ❌",
                    NotificationType.EVENT_REJECTED,
                    vars,
                    true,
                    null
            );
        }
        else if(status == EventStatus.PLANNED){
            notificationFacade.notifyUser(
                    collegeUser,
                    "Your event plan has been prepared 📋",
                    NotificationType.EVENT_PLAN_RECEIVED,
                    vars,
                    false,
                    null
            );
        }

        return mapToDTO(request);
    }

    // Get all services of a specific event
    public List<AdminEventServiceDTO> getServicesOfEvent(Long eventId) {
        // Fetch the event
        EventRequest event = eventRequestRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Fetch all EventService records linked to this event
        List<EventService> eventServices = eventServiceRepo.findByEventRequest(event);

        // Map to DTO
        return eventServices.stream()
                .map(this::mapToEventServiceDTO)
                .collect(Collectors.toList());
    }

    //service-vendor
    public AdminEventServiceDTO assignVendor(Long eventId, Long serviceId, Long vendorId) {

        EventRequest event = eventRequestRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("event not found"));


        if(event.getEventStatus() == EventStatus.CONFIRMED ||
                event.getEventStatus() == EventStatus.COMPLETED){
            throw new RuntimeException("Cannot modify finalized event");
        }

        if(event.getEventStatus() != EventStatus.PENDING &&
                event.getEventStatus() != EventStatus.PLANNED){
            throw new RuntimeException("Vendor can only be assigned in pending or planned stage");
        }

        ServiceType service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("service not found"));

        EventService eventService = eventServiceRepo.findByEventRequestAndServiceType(event, service)
                .orElseThrow(() -> new RuntimeException("Event service not found"));

        if (vendorId != null) {
            Vendor vendor = vendorRepo.findById(vendorId)
                    .orElseThrow(() -> new RuntimeException("vendor not found"));

            eventService.setVendor(vendor);
        }
        else{
            eventService.setVendor(null);
        }

        eventServiceRepo.save(eventService);

        User collegeUser = event.getCollege().getUser();

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", collegeUser.getName());
        vars.put("eventName", event.getTitle());
        vars.put("serviceType", service.getService());
        vars.put("eventDate", event.getEventDate());

        notificationFacade.notifyUser(
                collegeUser,
                "Vendor assigned for service: " + service.getService(),
                NotificationType.VENDOR_ASSIGNED,
                vars,
                false,
                null
        );

        return mapToEventServiceDTO(eventService);

    }

    //vendors of a service
    public List<AdminVendorDTO> getServiceVendors(Long serviceId){

        ServiceType service = serviceRepo.findById(serviceId)
                .orElseThrow(()-> new RuntimeException("service not found"));

        List<Vendor> vendors = vendorRepo.findByCategory(service.getService());

        return vendors.stream()
                .map(this::mapToVendorDTO)
                .collect(Collectors.toList());
    }

    public List<AdminEventServiceDTO> getEventServices(){

        List<EventService> eventServices = eventServiceRepo.findAll();

        return eventServices.stream()
                .map(this::mapToEventServiceDTO)
                .collect(Collectors.toList());
    }

    public AdminEventServiceDTO getEventServiceById(Long id){

        EventService eventService =  eventServiceRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("service not found"));

        return mapToEventServiceDTO(eventService);
    }

    public AdminEventDTO uploadEventPlan(Long eventId, MultipartFile file){

        EventRequest event = eventRequestRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("event not found"));

        // ✅ VALIDATION (VERY IMPORTANT)
        if(event.getEventStatus() != EventStatus.PENDING &&
                event.getEventStatus() != EventStatus.RESCHEDULED){
            throw new RuntimeException("Plan can only be uploaded for pending or rescheduled events");
        }

        // ✅ DELETE OLD FILE IF EXISTS
        if(event.getEventPlanPublicId() != null){
            fileUploadService.deleteFile(event.getEventPlanPublicId());
        }

        // ✅ UPLOAD NEW FILE
        FileUploadResponse response =
                fileUploadService.uploadFile(
                        file,
                        "campusconnect/events/eventPlans"
                );

        event.setEventPlanUrl(response.getUrl());
        event.setEventPlanPublicId(response.getPublicId());

        // ✅ SET STATUS
        event.setEventStatus(EventStatus.PLANNED);

        eventRequestRepo.save(event);

        // 🔔 NOTIFY COLLEGE (IMPORTANT UX)
        User collegeUser = event.getCollege().getUser();

        notificationFacade.notifyUser(
                collegeUser,
                "Your event plan is ready 📋",
                NotificationType.EVENT_PLAN_RECEIVED,
                Map.of(
                        "eventName", event.getTitle(),
                        "eventDate", event.getEventDate()
                ),
                true,
                null
        );

        return mapToDTO(event);
    }

    private void validateStatusTransition(EventStatus current, EventStatus next) {

        switch (current) {

            case PENDING -> {
                if (next != EventStatus.PLANNED &&
                        next != EventStatus.REJECTED) {
                    throw new RuntimeException("Invalid transition from PENDING");
                }
            }

            case PLANNED -> {
                if (next != EventStatus.CONFIRMED &&
                        next != EventStatus.REJECTED &&
                        next != EventStatus.RESCHEDULED) {
                    throw new RuntimeException("Invalid transition from PLANNED");
                }
            }

            case RESCHEDULED -> {
                if (next != EventStatus.PLANNED &&
                        next != EventStatus.REJECTED) {
                    throw new RuntimeException("Invalid transition from RESCHEDULED");
                }
            }

            case CONFIRMED -> {
                if (next != EventStatus.COMPLETED) {
                    throw new RuntimeException("Invalid transition from CONFIRMED");
                }
            }

            case COMPLETED, REJECTED -> {
                throw new RuntimeException("Final state cannot be modified");
            }
        }
    }


    public AdminVendorDTO mapToVendorDTO(Vendor vendor){
        return AdminVendorDTO.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .category(vendor.getCategory())
                .phone(vendor.getPhone())
                .gstNumber(vendor.getGstNumber())
                .businessLicenseUrl(vendor.getBusinessLicenseUrl())
                .verificationStatus(vendor.getVerificationStatus())
                .userId(vendor.getUser().getId())
                .userName(vendor.getUser().getName())
                .userEmail(vendor.getUser().getEmail())
                .userEnabled(vendor.getUser().isEnabled())
                .build();
    }

    public AdminEventServiceDTO mapToEventServiceDTO(EventService eventService){
        return AdminEventServiceDTO.builder()
                .id(eventService.getId())
                .eventId(eventService.getEventRequest().getId())
                .serviceTypeId(eventService.getServiceType().getId())
                .title(eventService.getEventRequest().getTitle())
                .serviceName(eventService.getServiceType().getService())
                .vendor(eventService.getVendor() != null ? eventService.getVendor().getBusinessName() : null)
                .build();
    }

}
