package campusconnect.backend.repository;

import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    List<EventRequest> findByCollege(College college);
    boolean existsByTitleAndCollege(String title, College college);

}