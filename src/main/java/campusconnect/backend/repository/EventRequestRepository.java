package campusconnect.backend.repository;

import campusconnect.backend.entity.EventRegistration;
import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.College;
import campusconnect.backend.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    List<EventRequest> findByCollege(College college);

    boolean existsByTitleAndCollege(String title, College college);

    long countByEventStatus(EventStatus eventStatus);

    List<EventRequest> findByEventStatus(EventStatus eventStatus);

    Optional<EventRequest> findByIdAndEventStatus(Long id, EventStatus eventStatus);

    List<EventRequest> findByEventDateBetween(LocalDateTime tomorrowStart, LocalDateTime tomorrowEnd);


}
