package campusconnect.backend.repository;

import campusconnect.backend.entity.Event;
import campusconnect.backend.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCollege(College college);

}