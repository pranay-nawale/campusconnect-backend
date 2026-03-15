package campusconnect.backend.repository;

import campusconnect.backend.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    // Check if a student already registered for the event
    Optional<EventRegistration> findByStudentIdAndEventId(Long studentId, Long eventId);
}