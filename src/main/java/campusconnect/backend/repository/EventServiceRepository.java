package campusconnect.backend.repository;

import campusconnect.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventServiceRepository extends JpaRepository<EventService, Long> {

    Optional<EventService> findByEventRequestAndServiceType(EventRequest eventRequest, ServiceType service);
    List<EventService> findByVendor(Vendor vendor);
    List<EventService> findByEventRequest(EventRequest event);
    List<EventService> findByVendorAndEventRequest_EventStatusNot(Vendor vendor, EventStatus status);
}
